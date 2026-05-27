let $chatHistory; // Переменная для хранения элемента истории чата
let $button; // Переменная для хранения кнопки отправки сообщения
let $textarea; // Переменная для хранения текстового поля ввода сообщения
let $chatHistoryList; // Переменная для хранения списка сообщений чата

function init() {
    cacheDOM(); // Вызов функции для кэширования элементов DOM
    bindEvents(); // Вызов функции для привязки событий
}

function bindEvents() {
    $button.on('click', addMessage.bind(this)); // Привязка события клика на кнопке отправки сообщения
    $textarea.on('keyup', addMessageEnter.bind(this)); // Привязка события нажатия клавиши Enter в текстовом поле ввода сообщения
    $deleteButton.on('click', deleteMessage.bind(this))
    $copyButton.on('click', copyMessage.bind(this))
    $editeButton.on('click', editMessage.bind(this))
    // deleteButtonCall(deleteButton);
    // copyButtonCall(copyButton);
    // editButtonCall(editeButton);
}

function cacheDOM() {
    $chatHistory = $('.chat-history'); // Кэширование элемента истории чата
    $button = $('#sendBtn'); // Кэширование кнопки отправки сообщения
    $textarea = $('#message-to-send'); // Кэширование текстового поля ввода сообщения
    $chatHistoryList = $chatHistory.find('ul'); // Кэширование списка сообщений чата
}

// Переменная для хранения текущего открытого контекстного меню
function copyMessage() {
    navigator.clipboard.writeText(messageContainer.context.innerText).catch(function (error) {
        console.error('Ошибка при копировании текста: ', error);
    });
}

function deleteMsg(messageId) {
    return fetch('/deletemessage/' + messageId, {
        method: 'DELETE',
        credentials: 'same-origin'
    })
        .then(response => {
            if (isAuthFailedResponse(response)) {
                disconnectAndRedirectToLogin();
                throw new Error('Not authenticated');
            }

            if (!response.ok) {
                throw new Error('Error deleting message');
            }

            console.log('Message deleted successfully');
            return true;
        });
}

function deleteMessage() {
    const messageId = messageContainer.find('.message').data('message-id');

    if (!messageId) {
        console.error('Message id was not found');
        return;
    }

    deleteMsg(messageId)
        .then(function () {
            contextMenu.hide();
        })
        .catch(function (error) {
            console.error('Delete failed:', error);
        });
}

function editMessage() {
    const textArea = $('#message-to-send');
    const saveBtn = $('#sendBtn');

    const messageText = messageContainer.find('.message').text().trim();

    textArea.val(messageText);
    textArea.focus();
    saveBtn.text('Save');
}

let contextMenu = $('.context-menu');
let messageContainer;
const $deleteButton = contextMenu.find('#delete-button');
const $editeButton = contextMenu.find('#edite-button');
const $copyButton = contextMenu.find('#copy-button');

function addContextMenu(messageId) {
    $('#message-' + messageId).on('contextmenu', function (e) {
        // Prevent the default context menu from appearing
        e.preventDefault();

        // Закрытие предыдущего контекстного меню, если есть
        if (contextMenu) {
            contextMenu.hide();
        }

        // Создание нового контекстного меню
        messageContainer = $(this).closest('li');

        // Позиционирование контекстного меню относительно нажатого сообщения
        const posX = e.pageX;
        const posY = e.pageY;
        contextMenu.css({top: posY, left: posX});

        console.log($(this).find('div[class]'))
        console.log($(this).hasClass('my-message'))

        if ($(this).hasClass('my-message')) {
            $editeButton.hide();
            $deleteButton.hide();
        } else {
            $editeButton.show();
            $deleteButton.show();
        }

        contextMenu.show();
    });
}

// Закрытие контекстного меню при клике вне него
$(document).ready(function () {
    $(document).on('click', function () {
        contextMenu.hide();
    });
});

function render(sender, recipient) {
    let templateResponse = Handlebars.compile($("#message-response-template").html()); // Компиляция шаблона для отображения полученных сообщений
    let template = Handlebars.compile($("#message-template").html()); // Компиляция шаблона для отображения отправленных сообщений

    console.log(sender, recipient)

    setTimeout(function () {
        getJson("/getmessages?recipient=" + encodeURIComponent(recipient.id))
            .then(function (messages) {
                console.log(messages);

                if (!Array.isArray(messages)) {
                    console.error('Messages response is not an array:', messages);
                    return;
                }

                for (let i = 0; i < messages.length; i++) {
                    if (messages[i].user.username === principal.username) {
                        $chatHistoryList.append(template({
                            messageOutput: messages[i].text,
                            time: getTime(messages[i].timestamp),
                            messageId: messages[i].id
                        }));
                        addContextMenu(messages[i].id);
                    } else {
                        $chatHistoryList.append(templateResponse({
                            response: messages[i].text,
                            time: getTime(messages[i].timestamp),
                            userName: selectedUser.username,
                            messageId: messages[i].id
                        }));
                        addContextMenu(messages[i].id);
                    }
                }

                updateChatNumMessages();
                scrollToBottom();
            })
            .catch(function (error) {
                console.error('Render messages failed:', error);
            });
    }.bind(this), 200);

}

function sendMessage(message) {
    if (!selectedUser) {
        return;
    }

    if (!message || message.trim() === '') {
        return;
    }

    let currentTime = new Date();

    sendMsg(principal, message.trim(), currentTime)
        .then(function (sent) {
            if (!sent) {
                return;
            }

            $textarea.val('');
            scrollToBottom();
        });
}

function updateMessage(val) {
    const text = val ? val.trim() : '';

    if (text === '') {
        return;
    }

    const messageId = messageContainer.find('.message').data('message-id');

    updateMsg(text, messageId)
        .then(function () {
            $('#message-' + messageId).text(text);

            $button.text('Send');
            $textarea.val('');
            contextMenu.hide();
        })
        .catch(function (error) {
            console.error('Update failed:', error);
        });
}

function liveRender(data) {
    scrollToBottom();

    const isOwnMessage = Number(data.user.id) === Number(principal.id);

    const template = Handlebars.compile(
        isOwnMessage
            ? $("#message-template").html()
            : $("#message-response-template").html()
    );

    const context = isOwnMessage
        ? {
            messageOutput: data.text,
            time: getTime(data.timestamp),
            messageId: data.id
        }
        : {
            response: data.text,
            time: getTime(data.timestamp),
            userName: data.user.username,
            messageId: data.id
        };

    setTimeout(function () {
        $chatHistoryList.append(template(context));
        addContextMenu(data.id);
        updateChatNumMessages();
        scrollToBottom();
    }, 200);
}

function scrollToBottom() {
    $chatHistory.scrollTop($chatHistory[0].scrollHeight); // Прокрутка до конца истории чата
    console.log($chatHistory.scrollTop($chatHistory[0].scrollHeight));
}

function getTime(timestamp) {
    console.log(timestamp)
    return new Date(timestamp).toLocaleTimeString().replace(/([\d]+:[\d]{2})(:[\d]{2})(.*)/, "$1$3"); // Получение текущего времени
}

function addMessage() {
    console.log($button.text().toUpperCase())
    if ($button.text().toUpperCase() === 'send'.toUpperCase()) {
        sendMessage($textarea.val()); // Добавление сообщения
    } else {
        updateMessage($textarea.val(), messageContainer);
    }
}

function addMessageEnter(event) {
    // enter was pressed
    if (event.keyCode === 13) {
        addMessage(); // Добавление сообщения при нажатии клавиши Enter
    }
}

init(); // Инициализация приложения при загрузке страницы