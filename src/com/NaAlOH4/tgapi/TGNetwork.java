package com.NaAlOH4.tgapi;

import java.io.IOException;
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
    private final OkHttpClient client;
    private final OkHttpClient longClient;
    private final Gson gson;

    public TGNetwork(@NotNull String token) {
        this(token, String.format("https://api.telegram.org/bot%s/", token));
    }

    TGNetwork(@NotNull String token,
              @NotNull String url) {
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
     * @param parameter is send by http POST method.
     * @return Result should read by readResult()
     */
    public Result request(@NotNull String method,
                              @Nullable Map<String, Object> parameter,
                          boolean longPull) throws IOException {
        FormBody.Builder updateFormBody = new FormBody.Builder();
        if(parameter != null) {
            for (final Map.Entry<String, Object> entry : parameter.entrySet())
                if (entry.getValue() != null)
                    updateFormBody.add(entry.getKey(), String.valueOf(entry.getValue()));
        }
        Request request = new Request.Builder()
                .url(url + method)
                .post(updateFormBody.build())
                .build();
        Response response = (longPull ? longClient : client).newCall(request).execute();
        try {
            return gson.fromJson(response.body().string(), Result.class);
        }catch (NullPointerException e){
            throw new IOException(e);
        }
    }

    public Result request(@NotNull String method,
                          @Nullable Map<String, Object> args) throws IOException {
        return request(method, args, false);
    }
}
