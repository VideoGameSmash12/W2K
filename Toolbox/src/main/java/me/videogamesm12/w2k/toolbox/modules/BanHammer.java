package me.videogamesm12.w2k.toolbox.modules;

import com.google.common.eventbus.Subscribe;
import me.videogamesm12.w2k.kernel.abstraction.inventory.ItemStackInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.ClientPlayerEntityInterface;
import me.videogamesm12.w2k.kernel.abstraction.world.EntityInterface;
import me.videogamesm12.w2k.kernel.event.entity.EntityInteractionEvent;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.BooleanSetting;
import me.videogamesm12.w2k.kernel.module.setting.ColorSetting;
import me.videogamesm12.w2k.kernel.module.setting.StringSetting;
import me.videogamesm12.w2k.kernel.util.VersionUtils;

import java.awt.*;

public class BanHammer extends WModule
{
    // TODO: Make configurable
    private final String itemName = "Ban Hammer";

    private final StringSetting banCommand = register(new StringSetting("ban_command", "Ban Command", "ban %username%"));
    private final StringSetting banIpCommand = register(new StringSetting("ban_ip_command", "Ban IP Command", "banip %uuid%"));
    private final StringSetting itemType = register(new StringSetting("item_type", "Item Type",
            VersionUtils.isNewerThanOrRunning("1.16.5") ? "minecraft:netherite_axe" : "minecraft:diamond_axe"));

    public final BooleanSetting outlineTarget = register(new BooleanSetting("outline_target", "Outline Target", true));
    public final BooleanSetting useCustomHighlightColor = register(new BooleanSetting("use_custom_highlight_color", "Use Custom Highlight Color", true));
    public final ColorSetting highlightColor = register(new ColorSetting("custom_highlight_color", "Custom Highlight Color", new Color(255, 0, 0)));

    public BanHammer()
    {
        super("Ban Hammer",
                "Repurposes an item to act as a literal ban hammer. \nThis should only be used for extreme cases where you need to \nremove a large quantity of bots in a given space. \n\nLeft click to ban regularly, right click to ban IP.");
    }

    @Subscribe
    public void onEntityHit(EntityInteractionEvent event)
    {
        final ClientPlayerEntityInterface clientPlayer = event.getClientPlayerEntity();
        final EntityInterface target = event.getTarget();

        if (!isEnabled()
                || !clientPlayer.w2k$isCreative()
                || !clientPlayer.w2k$getStackInMainHand().filter(this::isHammerActive).isPresent()
                || !target.w2k$type().equalsIgnoreCase("minecraft:player"))
        {
            return;
        }

        final String command = (event.isLeftClick() ? banCommand.get() : banIpCommand.get())
                .replaceAll("%uuid%", target.w2k$uuid().toString())
                .replaceAll("%username%", target.w2k$internalName());

        versionAbstractionLayer().networkHandler().ifPresent(handler -> handler.w2k$sendCommand(command));
    }

    public boolean isHammerActive(final ItemStackInterface stack)
    {
        return stack != null
                && stack.w2k$isNotEmpty()
                && stack.w2k$type().equalsIgnoreCase(itemType.get())
                && stack.w2k$name() != null
                && versionAbstractionLayer().text().adventureToString(stack.w2k$name()).contains(itemName);
    }
}
