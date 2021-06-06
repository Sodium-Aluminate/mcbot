package com.NaAlOH4;

import com.NaAlOH4.Telegram.*;

import java.io.IOException;
import java.util.concurrent.*;

import com.NaAlOH4.tgapi.*;

/**
 * /prpr@NaAlOH4
 * /prpr@Yuuta
 */
public class TelegramBot extends Thread {
    public final ITelegram telegram;
    TaskQueue taskQueue = new TaskQueue();
    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    private Me me = null;

    public TelegramBot(String token) {
        telegram = ITelegram.create(token);
        new Thread(() -> {
            try {
                me = telegram.getMe();
            } catch (IOException e) {
                System.err.println("getMe failed");
                e.printStackTrace();
            } catch (TelegramAPIException e) {
                System.err.println("getMe failed: " + e.getMessage());
            }
        }).start();
        taskQueue.start();
    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            Update[] updates = null;
            try {
                updates = telegram.getUpdates();
            } catch (IOException e) {
                Main.printDebug("get update failed: IOException");
            } catch (TelegramAPIException e) {
                Main.printDebug("get update failed: " + e.getMessage());
            }

            if (updates == null) {
                Main.sleep(5000);
                continue;
            }
            for (Update update : updates) {
                if (update.message != null) {
                    handleMessage(update.message);
                }
            }
        }
    }

    private void handleMessage(Message message) {

        Main.printDebug("check permission: ");
        Main.printDebug("isAllowGroup: "+Main.config.isAllowGroup(message.chat));
        Main.printDebug("idAdmin: "+Main.config.isAdmin(message.from));
        if (!(Main.config.isAllowGroup(message.chat) || Main.config.isAdmin(message.from))) return;


        // check is a command
        String text = message.text;
        Main.printDebug(text);
        if (text == null) return;
        if (!text.startsWith("/")) return;
        text = text.substring(1);
        String[] args = text.split(" +");

        if (args.length == 1) {
            String arg = args[0];
            if (me != null && me.username != null && arg.contains("@")) {
                String[] s = arg.split("@");
                if (s.length != 2) return;
                if (!s[1].equals(me.username)) return;
                arg = s[0];
            }
            if (arg.equals("list")) {
                Main.printDebug("list users");
                taskQueue.execute(() -> telegram.replyMessage(message, "MarkdownV2", Main.getBotOnlineInformation()));
            }
        }
        if (args.length == 2) {
            Bot bot = Main.getBot(args[1]);
            if (bot == null) {
                taskQueue.execute(() -> {
                    Message toDel = telegram.replyMessage(message, "user not found");
                    taskQueue.execute(
                            () -> telegram.deleteMessage(toDel)
                            , 5000);
                });
                return;
            }
            if (args[0].equalsIgnoreCase("login")) {
                taskQueue.execute(() -> {
                    Main.printDebug("call login");
                    bot.login();
                    Message toDel = telegram.replyMessage(message, "ok.");
                    taskQueue.execute(() ->
                                    telegram.deleteMessage(toDel),
                            5000);
                });
            }
            if (args[0].equalsIgnoreCase("logout")) {
                taskQueue.execute(() -> {
                    bot.logout();
                    Message toDel = telegram.replyMessage(message, "ok.");
                    taskQueue.execute(() ->
                                    telegram.deleteMessage(toDel),
                            5000);
                });
            }
        }
    }
}
