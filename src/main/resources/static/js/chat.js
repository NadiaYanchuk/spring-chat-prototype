const url = 'http://localhost:8080'; // URL-адрес сервера, с которым будет установлено соединение

let stompClient; // Объект StompClient для обмена сообщениями по протоколу STOMP
let selectedUser; // Имя выбранного пользователя для чата
let principal; // Переменная для хранения имени текущего пользователя

registration(); // Вызов функции регистрации

let socket = new SockJS(url + '/chat'); // Создание нового WebSocket-соединения


// Функция для подключения к чату
let pendingStatuses = {};

let redirectingToLogin = false;

function disconnectAndRedirectToLogin() {
    if (redirectingToLogin) {
        return;
    }

    redirectingToLogin = true;

    try {
        if (stompClient && stompClient.connected) {
            stompClient.disconnect(function () {
                window.location.href = '/login';
            });
            return;
        }
    } catch (e) {
        console.error('Error disconnecting websocket:', e);
    }

    window.location.href = '/login';
}

function isAuthFailedResponse(response) {
    return response.status === 401
        || response.status === 403
        || response.redirected
        || response.url.includes('/login');
}

function getJson(path) {
    return fetch(url + path, {
        method: 'GET',
        credentials: 'same-origin',
        cache: 'no-store',
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(function (response) {
            if (isAuthFailedResponse(response)) {
                disconnectAndRedirectToLogin();
                throw new Error('Not authenticated');
            }

            if (!response.ok) {
                throw new Error('Request failed: ' + response.status);
            }

            const contentType = response.headers.get('content-type') || '';

            if (!contentType.includes('application/json')) {
                disconnectAndRedirectToLogin();
                throw new Error('Expected JSON, got: ' + contentType);
            }

            return response.json();
        });
}

function ensureAuthenticated() {
    return getJson('/getprincipal')
        .then(function (response) {
            principal = response;
            return true;
        })
        .catch(function (error) {
            console.error('Authentication check failed:', error);
            return false;
        });
}

function connectToChat(principal) {
    console.log("connecting to chat...") // Вывод сообщения о попытке подключения к чату
    stompClient = Stomp.over(socket); // Создание объекта StompClient для управления соединением
    stompClient.connect({}, function (frame) {
        console.log("connected to: " + frame); // Вывод сообщения об успешном подключении к чату
        stompClient.subscribe("/topic/messages/" + principal.id, function (response) {
            const data = JSON.parse(response.body);

            if (selectedUser && isMessageInSelectedChat(data)) {
                liveRender(data);
                return;
            }

            const partnerId = getMessagePartnerId(data);

            if (partnerId && partnerId !== Number(principal.id)) {
                incrementCounter(partnerId);
            }
        });
        stompClient.subscribe("/topic/newdialog/" + principal.id, function (r) {
            const userId = Number(r.body);

            const existingUser = users
                ? users.find(u => Number(u.id) === userId)
                : null;

            if (existingUser) {
                refreshUserOnlineStatus(existingUser);
                return;
            }

            getJson('/fetchuser?id=' + encodeURIComponent(userId))
                .then(function (response) {
                    addUserToKnownList(response);
                })
                .catch(function (error) {
                    console.error('Fetch new dialog user failed:', error);
                });
        });
        stompClient.subscribe('/topic/updatemessage/' + principal.id, function (r) {
            const data = JSON.parse(r.body);

            if (selectedUser && isMessageInSelectedChat(data)) {
                $('#message-' + data.id).text(data.text);
            }
        });
        stompClient.subscribe('/topic/deletemsg/' + principal.id, function (r) {
            const data = JSON.parse(r.body);

            if (selectedUser && isMessageInSelectedChat(data)) {
                $('#message-' + data.id).closest('li').remove();
                updateChatNumMessages();
            }
        });

        // Онлайн-статусы от всех
        stompClient.subscribe('/topic/status', function (r) {
            const parts = r.body.split(':');
            const username = parts[0];
            const status = parts[1];

            const user = users ? users.find(u => u.username === username) : null;
            if (user) {
                updateUserStatus(user.id, status);
            } else {
                // Пользователь ещё не загружен — запомним статус
                pendingStatuses[username] = status;
            }
        });
    });
}

function getMessagePartnerId(message) {
    if (!message || !message.room || !principal) {
        return null;
    }

    const principalId = Number(principal.id);
    const senderId = Number(message.user.id);
    const user1Id = Number(message.room.user1.id);
    const user2Id = Number(message.room.user2.id);

    if (senderId !== principalId) {
        return senderId;
    }

    return user1Id === principalId ? user2Id : user1Id;
}

function isMessageInSelectedChat(message) {
    if (!selectedUser || !principal || !message.room) {
        return false;
    }

    const principalId = Number(principal.id);
    const selectedUserId = Number(selectedUser.id);
    const user1Id = Number(message.room.user1.id);
    const user2Id = Number(message.room.user2.id);

    return (
        (user1Id === principalId && user2Id === selectedUserId) ||
        (user2Id === principalId && user1Id === selectedUserId)
    );
}

// Обновляет иконку статуса, повторяет если элемент ещё не отрисован
function updateUserStatus(userId, status) {
    const icon = $('#userNameAppender_' + userId)
        .closest('li')
        .find('.fa-circle');

    if (icon.length) {
        if (status === 'online') {
            icon.removeClass('offline').addClass('online');
        } else {
            icon.removeClass('online').addClass('offline');
        }
    } else {
        setTimeout(() => updateUserStatus(userId, status), 500);
    }
}

// Увеличивает счётчик новых сообщений у пользователя в списке
function incrementCounter(userId) {
    let counter = $('#newMessage_' + userId);
    if (counter.length) {
        let current = parseInt(counter.text().replace('+', '')) || 0;
        counter.text('+' + (current + 1));
    } else {
        $('#userNameAppender_' + userId).append(
            '<span id="newMessage_' + userId + '" style="color: red">+1</span>'
        );
    }
}

// Обновляет надпись с количеством сообщений в заголовке чата
function updateChatNumMessages() {
    const count = $('#chat-history li').length;
    $('.chat-num-messages').text(count + ' messages');
}

// Функция для отправки сообщения
function sendMsg(from, text, timestamp) {
    return ensureAuthenticated().then(function (authenticated) {
        if (!authenticated) {
            return false;
        }

        if (!stompClient || !stompClient.connected) {
            disconnectAndRedirectToLogin();
            return false;
        }

        const message = {
            senderId: from.id,
            recipientId: selectedUser.id,
            text: text,
            timestamp: timestamp
        };

        console.log('Message ', message);
        stompClient.send("/app/chat/" + selectedUser.id, {}, JSON.stringify(message));

        return true;
    });
}

function updateMsg(text, messageId) {
    const message = {
        text: text
    };

    return fetch('/updatemessage/' + messageId, {
        method: 'PUT',
        credentials: 'same-origin',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(message),
    })
        .then(response => {
            if (isAuthFailedResponse(response)) {
                disconnectAndRedirectToLogin();
                throw new Error('Not authenticated');
            }

            if (!response.ok) {
                throw new Error('Error updating message');
            }

            console.log('Message updated successfully');
            return true;
        });
}

// Функция для регистрации пользователя
function registration() {
    getJson('/getprincipal')
        .then(function (response) {
            principal = response;

            console.log('Principal -> ', principal);

            $('#userName').text(principal.username);

            socket = new SockJS(url + '/chat');
            connectToChat(principal);

            fetchKnown();
        })
        .catch(function (error) {
            console.error('Registration/auth check failed:', error);
        });
}

// Функция для выбора пользователя для чата
function selectUser(userId) {
    ensureAuthenticated().then(function (authenticated) {
        if (!authenticated) {
            return;
        }

        console.log("selecting users: " + userId);

        selectedUser = users.find(u => Number(u.id) === Number(userId));

        if (!selectedUser) {
            console.error('Selected user was not found:', userId);
            return;
        }

        console.log('Selected user', selectedUser);

        let counter = document.getElementById("newMessage_" + selectedUser.id);
        if (counter) {
            counter.parentNode.removeChild(counter);
        }

        $('#selectedUserId').html('');
        $('#selectedUserId').append('Chat with ' + selectedUser.username);
        $('#chat-history').html('').removeClass('unselected');
        $('.chat-message').show();
        $('.chat-num-messages').text('');

        render(principal, selectedUser);
    });
}

let users; //массив объектов user (тех с которыми общаеся principal)

// Функция для получения списка всех пользователей
function fetchKnown() {
    getJson('/fetchknownusers')
        .then(function (response) {
            users = response;

            console.log('Fetch', users);

            $('#usersList').html('');
            $('#selectedUserId').html('');
            $('#chat-history').html('Chose someone to start chatting :)').addClass('unselected');
            $('.chat-message').hide();
            $('.chat-num-messages').text('');

            selectedUser = null;

            for (let i = 0; i < users.length; i++) {
                appendUsers(users[i].id, users[i].username);
                refreshUserOnlineStatus(users[i]);
            }

            $('#usersList').off('click', 'li').on('click', 'li', function () {
                const current = document.getElementsByClassName("selected");

                if (current.length > 0) {
                    current[0].classList.remove("selected");
                }

                this.classList.add("selected");
            });
        })
        .catch(function (error) {
            console.error('Fetch known users failed:', error);
        });
}

function writeToUser(id) {
    getJson('/writetofound?recipientId=' + encodeURIComponent(id))
        .then(function (room) {
            const user = getRoomPartner(room);

            addUserToKnownList(user);

            $('#search-list-div').hide();
            $('#userSearchInput').val('');

            selectUser(user.id);
        })
        .catch(function (error) {
            console.error('Error creating room:', error);
        });
}

function getRoomPartner(room) {
    if (Number(room.user1.id) === Number(principal.id)) {
        return room.user2;
    }

    return room.user1;
}

function addUserToKnownList(user) {
    if (!user) { return; }

    if (!users) { users = []; }

    const existingUser = users.find(function (knownUser) {
        return Number(knownUser.id) === Number(user.id);
    });

    if (existingUser) {
        refreshUserOnlineStatus(existingUser);
        return;
    }

    users.push(user);
    appendUsers(user.id, user.username);
    refreshUserOnlineStatus(user);
}

function refreshUserOnlineStatus(user) {
    if (!user) {
        return;
    }

    const pendingStatus = pendingStatuses[user.username];

    if (pendingStatus) {
        updateUserStatus(user.id, pendingStatus);
        delete pendingStatuses[user.username];
        return;
    }

    getJson('/getonlineusers')
        .then(function (onlineUsernames) {
            if (onlineUsernames.includes(user.username)) {
                updateUserStatus(user.id, 'online');
            } else {
                updateUserStatus(user.id, 'offline');
            }
        })
        .catch(function (error) {
            console.error('Refresh online status failed:', error);
        });
}

function appendUsers(id, username) {
    let usersTemplateHTML = '<a href="#" onclick="selectUser(\'' + id + '\')"><li class="list-group-item list-group-item-action mb-2 clearfix" data-toggle="list">\n' +
        '                <div class="about">\n' +
        '                    <div id="userNameAppender_' + id + '" class="name">' + username + '</div>\n' +
        '                    <div class="status">\n' +
        '                        <i class="fa fa-circle offline"></i>\n' +
        '                    </div>\n' +
        '                </div>\n' +
        '            </li></a>';
    $('#usersList').append(usersTemplateHTML);
}