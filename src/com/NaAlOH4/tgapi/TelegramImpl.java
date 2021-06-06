package com.NaAlOH4.tgapi;

import com.NaAlOH4.Main;
import com.NaAlOH4.Telegram.*;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

class TelegramImpl implements ITelegram {
    private final Gson gson;
    private final TGNetwork network;
    private int offset = -1;

    public TelegramImpl(@NotNull String token) {
        this.network = new TGNetwork(token);
        this.gson = new Gson();
    }

    private <T> T convertResult(@NotNull Result result, @Nullable Type type)
            throws TelegramAPIException {
        Main.printTgJson(result);
        if(!result.ok) {
            throw new TelegramAPIException(result);
        }
        if(type == null) return null;
        return gson.fromJson(result.result, type);
    }

    @NotNull
    @Override
    public Me getMe() throws IOException, TelegramAPIException {
        return convertResult(network.request("getMe", null), Me.class);
    }

    @NotNull
    @Override
    public Message sendMessage(@NotNull Chat chat,
                               @Nullable String parseMode,
                               @Nullable Integer replyTo,
                               @NotNull String text)
            throws IOException, TelegramAPIException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("chat_id", String.valueOf(chat.id));
        parameters.put("text", text);
        if(parseMode != null)
            parameters.put("parse_mode", parseMode);
        if(replyTo != null)
            parameters.put("reply_to_message_id", String.valueOf(replyTo));
        parameters.put("disable_web_page_preview", "true");
        return convertResult(network.request("sendMessage", parameters), Message.class);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Update[] getUpdates() throws IOException, TelegramAPIException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("timeout", "110");
        if(offset > 0) parameters.put("offset", Integer.toString(offset + 1));
        final Type listType = new TypeToken<ArrayList<Update>>(){}.getType();
        final Update[] result = ((List<Update>)
                convertResult(network.request("getUpdates", parameters, true),
                        listType))
                .toArray(new Update[]{});
        for (Update update : result)
            if(update.update_id > offset) {
                offset = update.update_id;
            }
        return result;
    }

    @Override
    public void deleteMessage(@NotNull Message message)
            throws IOException, TelegramAPIException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("chat_id", String.valueOf(message.chat.id));
        parameters.put("message_id", String.valueOf(message.message_id));
        convertResult(network.request("deleteMessage", parameters), null);
    }
}
