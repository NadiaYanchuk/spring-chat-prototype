const url = 'http://localhost:8080'; // URL-адрес сервера, с которым будет установлено соединение

let stompClient; // Объект StompClient для обмена сообщениями по протоколу STOMP
let selectedUser; // Имя выбранного пользователя для чата
let principal; // Переменная для хранения имени текущего пользователя

registration(); // Вызов функции регистрации

let socket = new SockJS(url + '/chat'); // Создание нового WebSocket-соединения


// Функция для подключения к чату
function connectToChat(principal) {
    console.log("connecting to chat...")
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to: " + frame);
        stompClient.subscribe("/topic/messages/" + principal.id, function (response) {
            let data = JSON.parse(response.body);
            console.log("Data", data);
            if (!selectedUser) {
                incrementCounter(data.user.id);
            } else {
                if (selectedUser.username === data.user.username) {
                    liveRender(data.text, data.user.username, data.timestamp);
                } else {
                    incrementCounter(data.user.id);
                }
            }
        });
        stompClient.subscribe("/topic/newdialog/" + principal.id, function (r) {
            console.log(r)
            $.get(url + "/fetchuser?id=" + r.body, function (response) {
                users.push(response);
                appendUsers(response.id, response.username);
            });
        })
        stompClient.subscribe('/topic/updatemessage/' + principal.id, function (r) {
            console.log(r)
            const data = JSON.parse(r.body);
            console.log(selectedUser.id === Number(data.user.id))
            if (selectedUser.id === Number(data.user.id)) {
                $('#' + Date.parse(data.timestamp).valueOf()).text(data.text);
            }
        })
        stompClient.subscribe('/topic/deletemsg/' + principal.id, function (r) {
            const data = JSON.parse(r.body);

            if (!!$(`#${Date.parse(data.timestamp)}`)) {
                $(`#${Date.parse(data.timestamp)}`).parent().remove();
            }

            // Обновляем счётчик сообщений после удаления
            updateChatNumMessages();
        })
    });
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
    const message = {
        senderId: from.id,
        recipientId: selectedUser.id,
        text: text,
        timestamp: timestamp
    }
    console.log('Message ', message);
    stompClient.send("/app/chat/" + selectedUser.id, {}, JSON.stringify(message));
}

function updateMsg(text, timestamp) {
    console.log('Message ', text);

    const message = {
        recipient: selectedUser.id,
        text: text,
    };

    fetch('/updatemessage?timestamp=' + timestamp, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(message),
    })
        .then(response => {
            if (response.ok) {
                console.log('Message updated successfully');
            } else {
                throw new Error('Error updating message');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// Функция для регистрации пользователя
function registration() {
    $.get(url + "/getprincipal", function (response) {
        principal = response;

        console.log('Principal -> ', principal)

        $('#userName').text(principal.username);

        connectToChat(principal);

        fetchKnown();
    });
}

// Функция для выбора пользователя для чата
function selectUser(userId) {
    console.log("selecting users: " + userId);

    selectedUser = users.find(u => u.id === Number(userId));
    console.log('Selected user', selectedUser)

    // Сбрасываем счётчик новых сообщений для выбранного пользователя
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
}

let users;

// Функция для получения списка всех пользователей
function fetchKnown() {
    $.get(url + "/fetchknownusers", function (response) {
        users = response;
        console.log('Fetch', users)
        $('#usersList').html('');
        $('#selectedUserId').html('');
        $('#chat-history').html('Chose someone to start chatting :)').addClass('unselected');
        $('.chat-message').hide();
        $('.chat-num-messages').text('');

        selectedUser = null;

        for (let i = 0; i < users.length; i++) {
            appendUsers(users[i].id, users[i].username)
        }
    }).done(function () {
        $('#usersList').off('click', 'li').on('click', 'li', function (e) {
            const current = document.getElementsByClassName("selected");
            if (current.length > 0) {
                current[0].classList.remove("selected");
            }
            this.classList.add("selected");
        });
    });
}

function writeToUser(id) {
    $.get(url + '/writetofound?principalId=' + principal.id + '&recipientId=' + id, function (response) {
        let room = response;
        console.log('Room', room)
    })
    console.log("write to user " + id)
    console.log("write to user1 " + list.find(u => u.id === Number(id)).id)
    let user = list.find(u => u.id === Number(id));
    appendUsers(user.id, user.username)
    users.push(list.find(u => u.id === Number(id)));
    console.log('Pushed users', users)
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