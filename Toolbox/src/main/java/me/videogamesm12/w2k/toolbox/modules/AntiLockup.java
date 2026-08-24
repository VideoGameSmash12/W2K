package me.videogamesm12.w2k.toolbox.modules;

import me.videogamesm12.w2k.kernel.module.WModule;
import me.videogamesm12.w2k.kernel.module.setting.BooleanSetting;
import me.videogamesm12.w2k.kernel.module.setting.LongSetting;

public class AntiLockup extends WModule
{
    public final BooleanSetting showAlert = register(new BooleanSetting("show_alert", "Show Alert", true));
    public final LongSetting alertInterval = register(new LongSetting("alert_interval", "Alert Inverval (in ms)", 5000, 0, Long.MAX_VALUE));

    public AntiLockup()
    {
        super("AntiLockup",
                "Resists the effects of the TotalFreedomMod's /lockup feature.");
    }
}
