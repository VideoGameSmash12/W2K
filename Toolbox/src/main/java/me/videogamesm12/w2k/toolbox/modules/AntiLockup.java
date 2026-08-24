package me.videogamesm12.w2k.toolbox.modules;

import lombok.Getter;
import lombok.Setter;
import me.videogamesm12.w2k.kernel.W2K;
import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.BooleanSetting;
import me.videogamesm12.w2k.kernel.module.setting.LongSetting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class AntiLockup extends WModule
{
    public final BooleanSetting showAlert = register(new BooleanSetting("show_alert", "Show Alert", true));
    public final LongSetting alertInterval = register(new LongSetting("alert_interval", "Alert Inverval (in ms)", 5000, 0, Long.MAX_VALUE));
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

    public void handleAlerts()
    {
        if (showAlert.get())
        {
            packetCount++;

            if ((System.currentTimeMillis() - timeSinceLastAlert >= alertInterval.get()))
            {
                versionBridge().displayMessage(Component.translatable("w2k.toolbox.module.antilockup.blocked", Component.text(packetCount))
                        .color(NamedTextColor.YELLOW));

                timeSinceLastAlert = System.currentTimeMillis();
                packetCount = 0;
            }
        }
    }
}
