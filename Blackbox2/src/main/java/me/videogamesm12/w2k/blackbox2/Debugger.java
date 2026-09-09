package me.videogamesm12.w2k.blackbox2;

import me.videogamesm12.w2k.blackbox2.gui.ConnectingWindow;

import javax.swing.*;

public class Debugger
{
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            final String entry = JOptionPane.showInputDialog("Please enter an IP address and port for a Supervisor connection");
            System.out.println(entry);
        }
        //new ConnectingWindow().setVisible(true);
    }
}
