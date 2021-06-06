package com.NaAlOH4.Telegram;

import java.util.Objects;

public class User {
    public Integer id;
    public boolean is_bot;
    public String first_name;
    public String last_name;
    public String username;
    public String language_code;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        if(id==null){
            return Objects.equals(username,user.username);
        }
        return (id.equals(user.id));
    }

    @Override
    public int hashCode() {
        return id==null?0:id;
    }
}
