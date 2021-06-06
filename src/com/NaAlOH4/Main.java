package com.NaAlOH4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Main {

    public static final String env_home = System.getenv("HOME");
    public static final HashMap<String, Bot> nameMap = new HashMap<>();
    public static final HashMap<String, Bot> mailMap = new HashMap<>();
    public static Config config;

    public static void main(String[] args) {
        String config_path = env_home + "/.config/mcbot.json";

        String configString;
        try {
            configString = Files.readString(Path.of(config_path));
        } catch (IOException e) {
            System.err.println("Can't read config from " + config_path);
            System.err.println("Here is an example: {\"users\":[{\"name\":\"example\",\"mail\":\"example@NaAlOH4.com\",\"pass\":\"prpr\"}]}");
            e.printStackTrace();
            throw new ConfigErrorException();
        }
        config = new Gson().fromJson(configString, Config.class);

        for (final LoginInformation loginInformation : config.users) {
            Bot bot = new Bot(loginInformation);

            if (nameMap.containsKey(loginInformation.name) ||
                    mailMap.containsKey(loginInformation.mail)) {
                System.err.println("Duplicated entry: \n" +
                        new GsonBuilder().setPrettyPrinting().create().toJson(loginInformation));
                throw new ConfigErrorException();
            }
            nameMap.put(loginInformation.name, bot);
            mailMap.put(loginInformation.mail, bot);
        }
        new TelegramBot(config.token).start();
    }

    public static Bot getBot(String string) {
        Bot m = Main.mailMap.get(string);
        Bot n = Main.nameMap.get(string);
        if (m != null && n != null) {
            throw new WTFException();
        }
        return m == null ? n : m;
    }

    public static void sleep(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new WTFException();
        }
    }
    public static String getBotOnlineInformation(){
        StringBuilder onlineList = new StringBuilder("__Online__:\n");
        StringBuilder offlineList = new StringBuilder("\n__Offline__:\n");
        for (Bot bot:nameMap.values()) {
            if(bot.isLogin()){
                onlineList.append("• `")
                        .append(escape(bot.name()))
                        .append("`\n");
            }else {
                offlineList.append("• `")
                        .append(escape(bot.name()))
                        .append("`\n");
            }
        }
        return onlineList.append(offlineList).toString();
    }

    public static String escape(String str){
        String[] chars = new String[]{ "_", "*", "`", "[" };
        for (String s:chars) {
            str = str.replace(s,"\\"+s);
        }
        return str;
    }
    public static void printDebug(Object o){
        if(config.printDebugInfo) System.out.println(o);
    }
    public static void printTgJson(Object o){
        if(config.printTelegramInfo)try {
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(o));
        }catch (Exception ignore){
        }
    }


}
