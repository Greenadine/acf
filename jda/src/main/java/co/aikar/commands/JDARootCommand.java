package co.aikar.commands;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import java.util.ArrayList;
import java.util.List;

public class JDARootCommand implements RootCommand {

    private final String name;
    boolean isRegistered = false;
    private final JDACommandManager manager;
    private BaseCommand defCommand;
    private final SetMultimap<String, RegisteredCommand> subCommands = HashMultimap.create();
    private final List<BaseCommand> children = new ArrayList<>();

    JDARootCommand(JDACommandManager manager, String name) {
        this.manager = manager;
        this.name = name;
    }

    @Override
    public void addChild(BaseCommand command) {
        if (this.defCommand == null || !command.subCommands.get(BaseCommand.DEFAULT).isEmpty()) {
            this.defCommand = command;
        }
        addChildShared(children, subCommands, command);
    }

    @Override
    public CommandManager getManager() {
        return manager;
    }

    @Override
    public SetMultimap<String, RegisteredCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public List<BaseCommand> getChildren() {
        return children;
    }

    @Override
    public String getCommandName() {
        return name;
    }

    @Override
    public BaseCommand getDefCommand() {
        return defCommand;
    }

}
