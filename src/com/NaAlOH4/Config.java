package com.NaAlOH4;

import com.NaAlOH4.Telegram.Chat;
import com.NaAlOH4.Telegram.User;

public class Config {
    public LoginInformation[] users;
    public String server;
    public Chat allowGroup;
    public Chat[] allowGroups;
    public User admin;
    public User[] admins;
    public String pythonCommand;
    public String scriptPath;

    public boolean isAdmin(User from){
        if(admin!=null && admin.equals(from)) return true;
        if(admins!=null){
            for (User user:admins) {
                if(user.equals(from))return true;
            }
        }
        return false;
    }
    public boolean isAllowGroup(Chat from){
        if(allowGroup!=null && allowGroup.equals(from)) return true;
        if(allowGroups!=null){
            for (Chat chat:allowGroups) {
                if(chat.equals(from))return true;
            }
        }
        return false;
    }
    public String token;

    public boolean printDebugInfo;
    public boolean printTelegramInfo;

    public ProxySetting proxy;
}
