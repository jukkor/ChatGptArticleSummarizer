package models;

public class Message {
    private final String role;
    private final String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
