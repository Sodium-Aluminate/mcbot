package com.NaAlOH4.Telegram;

public class Message {
    public int message_id;
    public User from;
    public Chat sender_chat;
    public int date;
    public Chat chat;
    public Message reply_to_message;
    public String text;
}
