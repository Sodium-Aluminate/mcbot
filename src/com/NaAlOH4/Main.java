package com.NaAlOH4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Main {

    public static final String ENV_HOME = System.getenv("HOME");
    private static final HashMap<String, Bot> nameMap = new HashMap<>();
    private static final HashMap<String, Bot> mailMap = new HashMap<>();
    public static Config config;

    public static void main(String[] args) {
        String config_path = String.format("%s/.config/mcbot.json", ENV_HOME);
        if(args.length == 1&& new File(args[0]).exists()) {
            config_path = args[0];
        }

        String configString;
        try {
            configString = Files.readString(Path.of(config_path));
        } catch (Exception e) {
            System.err.println("Can't read config from " + config_path);
            System.out.println("you can use an argument to force a config path.");
            System.out.println("Here is an config example: ");
            System.out.println(EXAMPLE_JSON);
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
        new McBot(config.token);
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



    public static final String EXAMPLE_JSON = "{\n" +
            "\t\"users\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"name\":\"steve\",\n" +
            "\t\t\t\"mail\":\"example@NaAlOH4.com\",\n" +
            "\t\t\t\"pass\":\"12345678\"\n" +
            "\t\t}\n" +
            "\t],\n" +
            "\t\"server\":\"example.com\",\n" +
            "\t\"token\":\"12345678:abcdefg\",\n" +
            "\t\"pythonCommand\":\"/usr/bin/python3\",\n" +
            "\t\"scriptPath\":\"/home/sodiumaluminate/pyCraft/start.py\",\n" +
            "\t\"admins\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"id\":498633413\n" +
            "\t\t}\n" +
            "\t],\n" +
            "\t\"allowGroup\":{\n" +
            "\t\t\"id\":-1001479325575\n" +
            "\t},\n" +
            "\t\"proxy\":{\n" +
            "\t\t\"type\":\"socks5\",\n" +
            "\t\t\"address\":\"127.0.0.1\",\n" +
            "\t\t\"port\":1080\n" +
            "\t},\n" +
            "\t\"printDebugInfo\":true,\n" +
            "\t\"printTelegramInfo\":true\n" +
            "}\n";
}
