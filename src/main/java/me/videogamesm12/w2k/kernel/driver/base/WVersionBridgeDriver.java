package me.videogamesm12.w2k.kernel.driver.base;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.data.PlayerEntry;
import net.minecraft.text.Text;

import java.util.List;

public interface WVersionBridgeDriver extends WDriver
{
    void disconnect();

    String getClientDebugInformation();

    void runCommand(String command);

    void scheduleSafeShutdown();

    void sendMessage(String message);

    String textToString(JsonElement text);

    List<PlayerEntry> getOnlinePlayers();
}
