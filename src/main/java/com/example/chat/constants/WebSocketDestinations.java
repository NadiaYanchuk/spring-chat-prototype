package com.example.chat.constants;

public final class WebSocketDestinations {
    
    private WebSocketDestinations() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String MESSAGES_TOPIC = "/topic/messages/";
    public static final String UPDATE_MESSAGE_TOPIC = "/topic/updatemessage/";
    public static final String DELETE_MESSAGE_TOPIC = "/topic/deletemsg/";
    public static final String NEW_DIALOG_TOPIC = "/topic/newdialog/";
}
