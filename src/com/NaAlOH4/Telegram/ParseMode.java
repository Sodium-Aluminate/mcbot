package com.NaAlOH4.Telegram;

import org.jetbrains.annotations.NotNull;

public class ParseMode {
    public static final ParseMode MARKDOWN = new ParseMode("Markdown");
    public static final ParseMode HTML = new ParseMode("HTML");
    public static final ParseMode MARKDOWN_V2= new ParseMode("MarkdownV2");
    @NotNull
    public final String value;
    private ParseMode(@NotNull String value){
        this.value=value;
    }

    @Override
    public String toString() {
        return value;
    }
}
