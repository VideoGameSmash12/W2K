package me.videogamesm12.w2k.toolbox.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.data.IPlayerEntry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ProfileUtil
{
    private static final Gson gson = new Gson();
    private static final Cache<String, ProfileLookupResult> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build();

    public static ProfileLookupResult getAshconData(String nameOrUuid) throws IOException
    {
        ProfileLookupResult result = cache.getIfPresent(nameOrUuid);

        if (result == null)
        {
            // Sends the request to Mojang's servers
            URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + nameOrUuid);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            result = gson.fromJson(new InputStreamReader(connection.getInputStream()), ProfileLookupResult.class);
            cache.put(nameOrUuid, result);
        }

        return result;
    }

    public static CompletableFuture<ProfileLookupResult> getAshconDataAsync(String nameOrUuid)
    {
        final CompletableFuture<ProfileLookupResult> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() ->
        {
            final List<IPlayerEntry> players = W2K.getInstance().getDriverManager().getVersionBridge().getPlayerList();
            final Optional<IPlayerEntry> candidate = players.stream()
                    .filter(profile -> profile.w2k$profile().getName().equalsIgnoreCase(nameOrUuid)
                            || profile.w2k$profile().getId().toString().equalsIgnoreCase(nameOrUuid))
                    .findAny();

            if (candidate.isPresent())
            {
                future.complete(new ProfileLookupResult(candidate.get().w2k$profile().getName(),
                        candidate.get().w2k$profile().getId().toString()));
            }
            else
            {
                try
                {
                    future.complete(getAshconData(nameOrUuid));
                }
                catch (Throwable ex)
                {
                    future.completeExceptionally(ex);
                }
            }
        });

        return future;
    }

    public static CompletableFuture<ProfileLookupResult> getMojangAPIData(String nameOrUuid)
    {
        final CompletableFuture<ProfileLookupResult> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() ->
        {
            try
            {
                future.complete(getAshconData(nameOrUuid));
            }
            catch (Throwable ex)
            {
                future.completeExceptionally(ex);
            }
        });

        return future;
    }

    @Getter
    @AllArgsConstructor
    public static class ProfileLookupResult
    {
        private String username;

        private String uuid;
    }
}
