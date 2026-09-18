package me.videogamesm12.w2k.toolbox.modules;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.Setter;
import me.videogamesm12.w2k.kernel.event.network.packet.IncomingOpenScreenPacketEvent;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.BooleanSetting;
import me.videogamesm12.w2k.kernel.module.setting.LongSetting;
import me.videogamesm12.w2k.kernel.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class AntiLockup extends WModule
{
    private final Component lockupName = Component.text("Player");
    //--
    public final BooleanSetting showAlert = register(new BooleanSetting("show_alert", "Show Alert", true));
    public final LongSetting alertInterval = register(new LongSetting("alert_interval", "Alert Interval (in ms)", 5000, 0, Long.MAX_VALUE));
    //--
    @Getter
    @Setter
    private long timeSinceLastAlert;
    @Getter
    @Setter
    private int packetCount;

    public AntiLockup()
    {
        super("Anti Lockup",
                "Resists the effects of the TotalFreedomMod's /lockup feature.");
    }

    @Subscribe
    public void onOpenScreenPacket(IncomingOpenScreenPacketEvent event)
    {
        if (!isEnabled()
                || event.isCancelled()
                || !event.getType().equalsIgnoreCase("minecraft:generic_9x4")
                || !ComponentUtils.deserializeComponent(event.getName()).equals(lockupName))
        {
            return;
        }

        event.setCancelled(true);
        handleAlerts();
    }

    public void handleAlerts()
    {
        if (showAlert.get())
        {
            packetCount++;

            if ((System.currentTimeMillis() - timeSinceLastAlert >= alertInterval.get()))
            {
                versionAbstractionLayer().getLocalPlayer().ifPresent(player ->
                {
                    player.w2k$displayMessage(Component.translatable("w2k.toolbox.module.antilockup.blocked", Component.text(packetCount))
                            .color(NamedTextColor.YELLOW));

                    timeSinceLastAlert = System.currentTimeMillis();
                    packetCount = 0;
                });
            }
        }
    }
}
