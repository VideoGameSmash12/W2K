package me.videogamesm12.w2k.toolbox.util;

import com.google.gson.Gson;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class AshconUtil
{
    private static final Gson gson = new Gson();

    public static AshconResponse getAshconData(String nameOrUuid) throws IOException
    {
        // Sends the request to Mojang's servers
        URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + nameOrUuid);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        return gson.fromJson(new InputStreamReader(connection.getInputStream()), AshconResponse.class);
    }

    public static CompletableFuture<AshconResponse> getAshconDataAsync(String nameOrUuid)
    {
        final CompletableFuture<AshconResponse> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() ->
        {
            final AshconResponse response;
            try
            {
                response = getAshconData(nameOrUuid);
                future.complete(response);
            }
            catch (Throwable ex)
            {
                future.completeExceptionally(ex);
            }
        });

        return future;
    }

    @Getter
    public static class AshconResponse
    {
        private String username;

        private String uuid;
    }
}
