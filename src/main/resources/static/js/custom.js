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

function deleteMessage() {
    // Delete the message here
    $.ajax({
        url: '/deletemessage',
        type: 'DELETE',
        data: JSON.stringify({
            timestamp: messageContainer.find('[id]').attr('id'),
            recipient: selectedUser.id,
            principal: principal.id
        }),
        contentType: 'application/json',
        success: function () {
            contextMenu.hide();
        },
        error: function (error) {
            console.error('Error:', error);
        }
    });

}

function editMessage() {
    console.log('edit')
    const textArea = $('#message-to-send');
    const saveBtn = $('#sendBtn');

    textArea.val(messageContainer.context.innerText)
    textArea.focus();
    saveBtn.text('save');
}

let contextMenu = $('.context-menu');
let messageContainer;
const $deleteButton = contextMenu.find('#delete-button');
const $editeButton = contextMenu.find('#edite-button');
const $copyButton = contextMenu.find('#copy-button');

function addContextMenu(id) {
    // Attach contextmenu event handler to each message
    $(`#${id}`).on('contextmenu', function (e) {
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
        } else {
            $editeButton.show();
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
        $.get(url + "/getmessages?sender=" + sender.id + "&recipient=" + recipient.id, function (response) {
            let messages = response; // Получение списка сообщений
            console.log(messages);
            for (let i = 0; i < messages.length; i++) {
                if (messages[i].user.username === principal.username) {
                    $chatHistoryList.append(template({
                        messageOutput: messages[i].text,
                        time: getTime(messages[i].timestamp),
                        myidentifier: Date.parse(messages[i].timestamp).valueOf()
                    })); // Отображение отправленных сообщений в чате
                    addContextMenu(Date.parse(messages[i].timestamp).valueOf());
                } else {
                    $chatHistoryList.append(templateResponse({
                        response: messages[i].text,
                        time: getTime(messages[i].timestamp),
                        userName: selectedUser.username,
                        otheridentifier: Date.parse(messages[i].timestamp).valueOf()
                    })); // Отображение полученных сообщений в чате
                    addContextMenu(Date.parse(messages[i].timestamp).valueOf());
                }
            }
            scrollToBottom(); // Прокрутка до конца истории чата
        })
    }.bind(this), 200);

}

function sendMessage(message) {
    let currenTime = new Date();
    sendMsg(principal, message, currenTime); // Отправка сообщения
    scrollToBottom(); // Прокрутка до конца истории чата
    if (message.trim() !== '') {
        let template = Handlebars.compile($("#message-template").html());
        let context = {
            messageOutput: message,
            time: getTime(currenTime),
            myidentifier: currenTime.getTime()
        };

        $chatHistoryList.append(template(context)); // Отображение отправленного сообщения в чате
        addContextMenu(currenTime.valueOf());
        scrollToBottom(); // Прокрутка до конца истории чата
        $textarea.val(''); // Очистка текстового поля ввода сообщения
    }
}

function updateMessage(val) {
    $button.text('send');
    const timestamp = messageContainer.find('[id]').attr('id');
    const text = val;

    updateMsg(text, timestamp);

    $('#' + timestamp).text(text);
    $textarea.val('');
}

function liveRender(message, userName, timestamp) {
    scrollToBottom(); // Прокрутка до конца истории чата
    // responses
    let templateResponse = Handlebars.compile($("#message-response-template").html());
    let contextResponse = {
        response: message,
        time: getTime(timestamp),
        userName: userName,
        otheridentifier: Date.parse(timestamp).valueOf()
    };

    setTimeout(function () {
        $chatHistoryList.append(templateResponse(contextResponse)); // Отображение полученного сообщения в чате
        scrollToBottom(); // Прокрутка до конца истории чата
        addContextMenu(new Date(timestamp).valueOf());
    }.bind(this), 200);
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