package com.NaAlOH4.tgapi;

import com.NaAlOH4.Main;
import com.NaAlOH4.Telegram.*;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class Telegram {
    @NotNull
    private final TGNetwork tgNetwork;
    @NotNull
    private final BiConsumer<Update, Telegram>[] messageHandlers;
    @NotNull
    private final String uid;

    /**
     * @param token          token from @botfather
     * @param messageHandlers one or more Consumer to handle message.
     */
    @SafeVarargs
    public Telegram(@NotNull String token, BiConsumer<Update, Telegram>... messageHandlers) {
        this(token, false, messageHandlers);
    }

    /**
     * @param token          token from @botfather
     * @param singleThread   if you are 100% ensured that
     *                       your Consumer will not block thread
     *                       or throw any exception
     * @param messageHandlers one or more Consumer to handle message.
     */
    @SafeVarargs
    public Telegram(@NotNull String token, boolean singleThread, @NotNull BiConsumer<Update, Telegram>... messageHandlers) {
        tgNetwork = new TGNetwork(token);
        this.messageHandlers = messageHandlers;
        this.singleThread = singleThread;
        this.uid = token.split(":",2)[0];
        updateMeInInitialize();
        start();
    }

    /**
     * getMe will execute in Initialize, If you don't want it, override it.
     */
    protected void updateMeInInitialize() {
        updateMe();
    }

    private final boolean singleThread;
    private void start(){
        new Thread("bot-"+uid){
            @Override
            public void run() {
                while (true){
                    try {
                        Update[] updates = getUpdates();
                        for (Update update : updates) {
                            if(singleThread){
                                handleUpdate(update);
                            }else {
                                new Thread(()->handleUpdate(update)).start();
                            }
                        }
                    } catch (IOException | TelegramAPIException ignored) {
                    }
                }
            }
        }.start();
    }

    private int offset = 0;
    private Update[] getUpdates() throws IOException, TelegramAPIException {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("timeout", 120);
        if (offset != 0) parameters.put("offset", offset + 1);
        Update[] updates = readResult(
                tgNetwork.request("getUpdates", parameters),
                Update[].class);
        for (Update update : updates) {
            if (update.update_id > offset)
                offset = update.update_id;
        }
        return updates;
    }

    private void handleUpdate(Update update){
        for (BiConsumer<Update, Telegram> messageHandler : messageHandlers) {
            messageHandler.accept(update, this);
        }
    }

    /**
     * @return Me null if network error or tg api had return bad things.
     */
    @Nullable
    public Me getMe() {
        if (me == null && !getMeFailed) updateMe();
        return me;
    }

    /**
     * use getMe method even if "Me" was already got.
     */
    public void updateMe() {
        if (getMeFailed) return; // avoid call getMe too Often.
        for (int i = 0; i < 3; i++) {
            try {
                Result result = tgNetwork.request("getMe", null);
                me = readResult(result, Me.class);
                return;
            } catch (IOException ignored) {
            } catch (TelegramAPIException e) {
                Main.printDebug(String.format("getme Failed(%s), reason: %s", e.errorCode, e.description));
                return;
            }
        }
        getMeFailed = true;
    }

    private Me me;
    private boolean getMeFailed = false;


    private static final Gson gson = new Gson();

    private <T> T readResult(@NotNull Result result, @NotNull Type type) throws TelegramAPIException {
        Main.printTgJson(result);
        if (!result.ok) {
            throw new TelegramAPIException(result);
        }
        return gson.fromJson(result.result, type);
    }

    /**
     * @param parse_mode see https://core.telegram.org/bots/api#formatting-options
     * @param disable_web_page_preview Disables link previews for links in this message
     * @param disable_notification Sends the message silently. Users will receive a notification with no sound.
     * @return Message if send success.
     * @throws IOException network error?
     * @throws TelegramAPIException in case of message cannot be send
     */
    public Message sendMessage(
            @NotNull Chat chat,
            @NotNull String text,
            @Nullable ParseMode parse_mode,
            @Nullable Boolean disable_web_page_preview,
            @Nullable Boolean disable_notification)
            throws IOException, TelegramAPIException {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("chat_id", chat.id);
        parameters.put("text", text);
        parameters.put("parse_mode", parse_mode);
        parameters.put("disable_web_page_preview", disable_web_page_preview);
        parameters.put("disable_notification", disable_notification);
        return readResult(
                tgNetwork.request("sendMessage", parameters),
                Message.class);
    }

    public Message sendMessage(Chat chat, String text) throws TelegramAPIException, IOException {
        return sendMessage(chat,text,null);
    }
    public Message sendMessage(Chat chat, String text,ParseMode parse_mode) throws TelegramAPIException, IOException {
        return sendMessage(chat, text, parse_mode,null,null);
    }


    /**
     * @param message to reply
     * @param text to send
     * @param parse_mode see https://core.telegram.org/bots/api#formatting-options
     * @param disable_web_page_preview Disables link previews for links in this message
     * @param disable_notification Sends the message silently. Users will receive a notification with no sound.
     * @return Message if send success
     * @throws IOException network error?
     * @throws TelegramAPIException in case of message cannot be send
     */
    public Message replyMessage(Message message,
                                @NotNull String text,
                                @Nullable ParseMode parse_mode,
                                @Nullable Boolean disable_web_page_preview,
                                @Nullable Boolean disable_notification,
                                @Nullable Boolean allow_sending_without_reply)
            throws IOException, TelegramAPIException {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("chat_id", message.chat.id);
        parameters.put("text", text);
        parameters.put("reply_to_message_id", message.message_id);
        parameters.put("parse_mode", parse_mode);
        parameters.put("disable_web_page_preview", disable_web_page_preview);
        parameters.put("disable_notification", disable_notification);
        parameters.put("allow_sending_without_reply", allow_sending_without_reply);
        return readResult(
                tgNetwork.request("sendMessage", parameters),
                Message.class);
    }
    public Message replyMessage(@NotNull Message message, @NotNull String text) throws TelegramAPIException, IOException {
        return replyMessage(message, text, null);
    }
    public Message replyMessage(Message message,
                                @NotNull String text,
                                @Nullable ParseMode parse_mode) throws TelegramAPIException, IOException {
        return replyMessage(message, text, parse_mode,null,null,true);
    }

    /**
     * @param message to delete
     * @return true if deleted.
     * @throws IOException network error?
     * @throws TelegramAPIException for example: bot was kicked/message was already deleted/etc
     */
    public boolean deleteMessage(Message message)
            throws IOException, TelegramAPIException {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("chat_id", message.chat.id);
        parameters.put("message_id", message.message_id);
        return readResult(
                tgNetwork.request("deleteMessage", parameters),
                Boolean.TYPE);
    }

}