package me.videogamesm12.w2k.blackbox.window.tool.crashpad;

import javax.swing.*;
import java.io.File;

public class Bootstrap
{
    public static void main(final String[] args)
    {
        final File file = new File(String.join(" ", args));
        if (!file.exists())
        {
            JOptionPane.showMessageDialog(null, "File not found: " + file.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final Crashpad crashpad = new Crashpad(file);
        crashpad.setVisible(true);
    }
}
