package lk.mc.model;

import java.io.Serializable;

public class AdminChatMessageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;
    private String userId;
    private String userName;

    public AdminChatMessageRequest() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
