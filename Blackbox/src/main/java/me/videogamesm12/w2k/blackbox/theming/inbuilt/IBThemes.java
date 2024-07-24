/*
 * Copyright (c) 2023 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.videogamesm12.w2k.blackbox.theming.inbuilt;

import com.formdev.flatlaf.util.SystemInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.videogamesm12.w2k.blackbox.theming.ITheme;
import me.videogamesm12.w2k.kernel.W2K;

import javax.swing.*;
import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum IBThemes implements ITheme
{
    DARK_METAL("Dark Metal", "A dark metallic theme for those feeling a bit edgy.", IBThemeType.NETBEANS, "org.netbeans.swing.laf.dark.DarkMetalLookAndFeel", true),
    METAL("Metal", "A nice-looking cross-platform theme with a tint of metal thrown in.", IBThemeType.BUILT_IN, "javax.swing.plaf.metal.MetalLookAndFeel", true),
    GTK("GTK", "A theme that uses the glorious GTK library to display.", IBThemeType.BUILT_IN, Arrays.stream(UIManager.getInstalledLookAndFeels()).filter(info -> info.getName().equalsIgnoreCase("gtk+")).findAny().map(UIManager.LookAndFeelInfo::getClassName).orElse(null), Arrays.stream(UIManager.getInstalledLookAndFeels()).anyMatch(info -> info.getName().equalsIgnoreCase("gtk+"))),
    MOTIF("Motif", "A hilariously outdated theme that hasn't changed at all since the 1990s.", IBThemeType.BUILT_IN, "com.sun.java.swing.plaf.motif.MotifLookAndFeel", true),
    NIMBUS("Nimbus", "We love skeuomorphism in this joint.", IBThemeType.BUILT_IN, Arrays.stream(UIManager.getInstalledLookAndFeels()).filter(info -> info.getName().equalsIgnoreCase("nimbus")).findAny().map(UIManager.LookAndFeelInfo::getClassName).orElse(null), Arrays.stream(UIManager.getInstalledLookAndFeels()).anyMatch(info -> info.getName().equalsIgnoreCase("nimbus"))),
    SYSTEM("System", "A theme that automatically adapts to whatever operating system you are currently using.", IBThemeType.BUILT_IN, UIManager.getSystemLookAndFeelClassName(), true),
    WINDOWS("Windows", "Ah yes, good ol' Win32.", IBThemeType.BUILT_IN, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel", SystemInfo.isWindows),
    WINDOWS_CLASSIC("Windows Classic", "Perfect for those who prefer function over form.", IBThemeType.BUILT_IN, "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel", SystemInfo.isWindows);

    private final String themeName;
    private final String themeDescription;
    private final IBThemeType type;
    private final String themeClass;
    private final boolean supposedToShow;

    @Override
    public String getInternalName()
    {
        return name();
    }

    @Override
    public void apply()
    {
        Arrays.stream(UIManager.getInstalledLookAndFeels()).forEach(style ->
        {
            W2K.getLogger().info("DEBUG, NAME - {}", style.getName());
            W2K.getLogger().info("DEBUG, CLASS - {}", style.getClassName());
            W2K.getLogger().info("DEBUG, TOSTRING - {}", style.toString());
        });

        try
        {
            UIManager.setLookAndFeel(themeClass);
        }
        catch (Exception ex)
        {
            W2K.getLogger().error("Failed to apply built-in theme with class {}", themeClass, ex);
        }
    }

    @Override
    public void showOptionalMessage()
    {
        // Do nothing.
    }
}
