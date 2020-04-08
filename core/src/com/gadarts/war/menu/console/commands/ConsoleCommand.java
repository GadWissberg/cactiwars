package com.gadarts.war.menu.console.commands;

import com.gadarts.shared.console.Commands;
import com.gadarts.shared.console.Console;
import com.gadarts.shared.console.ConsoleCommandResult;

import java.util.Map;

public abstract class ConsoleCommand {
    public ConsoleCommandResult run(Console console, Map<String, String> parameters) {
        return console.notifyCommandExecution(getCommandEnumValue());
    }

    protected abstract Commands getCommandEnumValue();

}
