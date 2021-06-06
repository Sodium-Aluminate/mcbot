package com.NaAlOH4.tgapi;

import com.NaAlOH4.Telegram.Result;
import org.jetbrains.annotations.NotNull;

public class TelegramAPIException extends Exception {
    public final String errorCode;
    public final String description;

    TelegramAPIException(@NotNull Result result) {
        this(result.errorCode, result.description);
    }

    TelegramAPIException(@NotNull String errorCode,
                         @NotNull String description) {
        super(String.format("%s (%s)", description, errorCode));
        this.errorCode = errorCode;
        this.description = description;
    }

}
