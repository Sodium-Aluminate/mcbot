package com.NaAlOH4;

import com.NaAlOH4.tgapi.TelegramAPIException;

import java.io.IOException;

public interface Runnable {
    void run() throws IOException, TelegramAPIException;
}
