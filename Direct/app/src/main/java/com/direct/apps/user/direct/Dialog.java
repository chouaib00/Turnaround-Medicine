package com.direct.apps.user.direct;


import java.util.ArrayList;

public class Dialog {

    private int dialogId;
    private User sender;
    private User receiver;
    private ArrayList<Message> messages = new ArrayList<>();

    public Dialog() {
    }

    public int getDialogId() {
        return dialogId;
    }

    public void setDialogId(int dialogId) {
        this.dialogId = dialogId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message){
        messages.add(message);
    }
}
