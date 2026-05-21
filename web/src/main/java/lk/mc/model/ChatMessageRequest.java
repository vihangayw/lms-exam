package lk.mc.model;

import java.io.Serializable;

public class ChatMessageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;
    private String senderName;

    public ChatMessageRequest() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
