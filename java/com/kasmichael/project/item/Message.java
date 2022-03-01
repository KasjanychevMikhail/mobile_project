package com.kasmichael.project.item;

public class Message {

    private String title;
    private String text;
    private String time;
    private int id;

    public Message(String title, String text, String time, int id) {
        this.title = title;
        this.text = text;
        this.time = time;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
    }
}
