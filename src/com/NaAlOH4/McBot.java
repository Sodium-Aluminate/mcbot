package com.NaAlOH4;

import com.NaAlOH4.Telegram.*;

import java.io.IOException;

import com.NaAlOH4.tgapi.*;
import org.jetbrains.annotations.NotNull;

/**
 * /prpr@NaAlOH4
 * /prpr@Yuuta
 */
public class McBot {

    public McBot(@NotNull String token) {
        new Telegram(token,
                ((update, telegram) -> {
                    Message message = update.message;
                    if (message == null) message = update.edited_message;
                    if (message == null) return;
                    if(!messageAllowed(message))return;

                    String text = message.text;
                    Main.printDebug("message text: " + text);
                    if (text == null) return;
                    if (!text.startsWith("/")) return;
                    text = text.substring(1);
                    Me me = telegram.getMe();
                    if (me != null) text = text.replaceAll("@" + telegram.getMe().username+"$", "");

                    // handle "list" command
                    if (text.equalsIgnoreCase("list")) {
                        Main.printDebug("list users");
                        try {
                            telegram.replyMessage(message, Main.getBotOnlineInformation(), ParseMode.MARKDOWN_V2, true, null, false);
                        } catch (IOException | TelegramAPIException ignored) {
                        }
                    }

                    boolean login = false;
                    boolean logout = false;
                    // handle login command
                    if (text.startsWith("login_") || text.startsWith("login ")) {
                        text = text.substring("login_".length());
                        login = true;
                    }
                    // handle logout command
                    if (text.startsWith("logout_") || text.startsWith("logout ")) {
                        text = text.substring("logout_".length());
                        logout = true;
                    }
                    if (login || logout) {
                        Bot bot = Main.getBot(text);
                        if (bot == null) {
                            try {
                                Message toDel = telegram.replyMessage(message, "user not found", null);
                                Main.sleep(5000);
                                telegram.deleteMessage(toDel);
                            } catch (TelegramAPIException | IOException e) {
                                Main.printDebug(e);
                            }

                            return;
                        }
                        if (login) {
                            Main.printDebug("call login: " + bot.name());
                            boolean logged = bot.login();
                            try {
                                Message toDel = telegram.replyMessage(message, logged ? "ok." : "error, check the stdout.");
                                Main.sleep(5000);
                                telegram.deleteMessage(toDel);
                            } catch (TelegramAPIException | IOException ignored) {
                            }
                        }
                        if (logout) {
                            Main.printDebug("call logout: " + bot.name());
                            bot.logout();
                            try {
                                Message toDel = telegram.replyMessage(message, "ok.");
                                Main.sleep(5000);
                                telegram.deleteMessage(toDel);
                            } catch (TelegramAPIException | IOException ignored) {
                            }
                        }
                    }

                }));

    }

    private boolean messageAllowed(Message message) {
        Main.printDebug("check permission: ");
        Main.printDebug("isAllowGroup: " + Main.config.isAllowGroup(message.chat));
        Main.printDebug("idAdmin: " + Main.config.isAdmin(message.from));
        if (Main.config.isAdmin(message.from)) return true;
        return Main.config.isAllowGroup(message.chat);
    }
}
