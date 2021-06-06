package com.NaAlOH4.tgapi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.NaAlOH4.Main;
import com.NaAlOH4.Telegram.Result;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.google.gson.Gson;

/**
 * Telegram network utils
 */
final class TGNetwork {
    private final String url;
    private final String token;
    private final OkHttpClient client;
    private final OkHttpClient longClient;
    private final Gson gson;

    public TGNetwork(@NotNull String token) {
        this(token, String.format("https://api.telegram.org/bot%s/", token));
    }

    TGNetwork(@NotNull String token,
              @NotNull String url) {
        this.token = token;
        this.url = url;
        @NotNull Proxy proxy = Main.config.proxy.getProxy();
        client = new OkHttpClient.Builder()
                .proxy(proxy)
                .build();
        longClient = new OkHttpClient.Builder()
                .proxy(proxy)
                .readTimeout(120, TimeUnit.SECONDS).build();
        gson = new Gson();
    }

    /**
     * @param method example: getMe, sendMessage, deleteMessage ...
     * @param args
     * @return
     */
    public Result request(@NotNull String method,
                              @Nullable Map<String, String> args,
                          boolean longPull) throws IOException {
        FormBody.Builder updateFormBody = new FormBody.Builder();
        if(args != null) {
            for(final Map.Entry<String, String> entry : args.entrySet())
                updateFormBody.add(entry.getKey(), entry.getValue());
        }
        Request request = new Request.Builder()
                .url(url + method)
                .post(updateFormBody.build())
                .build();
        Response response = (longPull ? longClient : client).newCall(request).execute();
        return gson.fromJson(response.body().string(), Result.class);
    }

    public Result request(@NotNull String method,
                          @Nullable Map<String, String> args) throws IOException {
        return request(method, args, false);
    }
}
