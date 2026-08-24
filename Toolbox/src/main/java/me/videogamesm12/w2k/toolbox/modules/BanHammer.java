package me.videogamesm12.w2k.toolbox.modules;

import me.videogamesm12.w2k.kernel.data.IEntityEntry;
import me.videogamesm12.w2k.kernel.data.IItemStackEntry;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.StringSetting;
import me.videogamesm12.w2k.kernel.util.VersionUtils;

public class BanHammer extends WModule
{
    // TODO: Make configurable
    private final String itemName = "Ban Hammer";

    private final StringSetting banCommand = register(new StringSetting("ban_command", "Ban Command", "ban %username%"));
    private final StringSetting banIpCommand = register(new StringSetting("ban_ip_command", "Ban IP Command", "banip %uuid%"));
    private final StringSetting itemType = register(new StringSetting("item_type", "Item Type",
            VersionUtils.isNewerThanOrRunning("1.16.5") ? "minecraft:netherite_axe" : "minecraft:diamond_axe"));

    public BanHammer()
    {
        super("Ban Hammer",
                "Repurposes an item to act as a literal ban hammer. \nThis should only be used for extreme cases where you need to \nremove a large quantity of bots in a given space. \n\nLeft click to ban regularly, right click to ban IP.");
    }

    public boolean handleClick(final IEntityEntry entity, final IItemStackEntry stack, final boolean hit)
    {
        if (!stack.w2k$isNotEmpty()
                || !stack.w2k$name().toString().contains(itemName)
                || !stack.w2k$type().equalsIgnoreCase(itemType.get())
                || !entity.w2k$type().equalsIgnoreCase("minecraft:player"))
        {
            return false;
        }

        final String command = (hit ? banCommand.get() : banIpCommand.get())
                .replaceAll("%uuid%", entity.w2k$uuid().toString())
                .replaceAll("%username%", entity.w2k$internalName());
        versionBridge().runCommand(command);
        return true;
    }
}
