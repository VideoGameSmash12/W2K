package me.videogamesm12.w2k.kernel.driver.base;

import me.videogamesm12.w2k.kernel.command.WCommand;

public interface WCommandDriver extends WDriver
{
    void executeCommand(WCommand command, String[] args);


}
