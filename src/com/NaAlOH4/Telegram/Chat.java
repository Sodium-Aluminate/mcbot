package com.NaAlOH4.Telegram;

import java.util.Objects;

public class Chat {
    public Long id;
    public String type;
    public String title;
    public String username;
    public String first_name;
    public String last_name;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        if(id==null){
            return Objects.equals(username,chat.username);
        }
        return (id.equals(chat.id));
    }
}
