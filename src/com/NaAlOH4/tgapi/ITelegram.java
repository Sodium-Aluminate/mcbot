package com.NaAlOH4.tgapi;

import org.jetbrains.annotations.NotNull;
import com.NaAlOH4.Telegram.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Telegram API Client
 */
public interface ITelegram {
    @NotNull
    static ITelegram create(@NotNull String token) {
        return new TelegramImpl(token);
    }

    @NotNull
    Me getMe() throws IOException, TelegramAPIException;

    @NotNull
    Update[] getUpdates() throws IOException, TelegramAPIException;

    @NotNull
    Message sendMessage(@NotNull Chat chat,
                        @Nullable String parseMode,
                        @Nullable Integer replyTo,
                        @NotNull String text) throws IOException, TelegramAPIException;

    @NotNull
    default Message sendMessage(@NotNull Chat chat,
                        @NotNull String text) throws IOException, TelegramAPIException {
        return sendMessage(chat, null, null, text);
    }

    @NotNull
    default Message replyMessage(@NotNull Message message,
                                 @Nullable String parseMode,
                                 @NotNull String text) throws IOException, TelegramAPIException {
        return sendMessage(message.chat, parseMode, message.message_id, text);
    }

    @NotNull
    default Message replyMessage(@NotNull Message message,
                         @NotNull String text) throws IOException, TelegramAPIException {
        return replyMessage(message, null, text);
    }

    void deleteMessage(@NotNull Message message) throws IOException, TelegramAPIException;
}
