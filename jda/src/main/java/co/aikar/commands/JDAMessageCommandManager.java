package co.aikar.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JDAMessageCommandManager extends JDACommandManager<
        JDAMessageCommandEvent,
        JDAMessageCommandExecutionContext,
        JDAMessageConditionContext,
        String,
        JDAMessageFormatter> {

    protected JDAMessageCommandManager(JDA jda) {
        this(jda, null);
    }

    protected JDAMessageCommandManager(JDA jda, JDAOptions options) {
        super(jda, options);

        this.contexts = new JDAMessageCommandContexts(this);
        this.configProvider = options.messageConfigProvider != null ? options.messageConfigProvider : options.configProvider != null ? options.configProvider : null;
        if (options.configProvider == null) {
            throw new IllegalArgumentException("No CommandConfigProvider provided for message commands");
        }

        jda.addEventListener(new JDAMessageCommandListener(this));  // Register message listener
    }

    @Override
    public void registerCommand(BaseCommand command) {
        command.onRegister(this);
        for (Map.Entry<String, RootCommand> entry : command.registeredCommands.entrySet()) {
            String commandName = entry.getKey().toLowerCase(Locale.ENGLISH);
            JDARootCommand cmd = (JDARootCommand) entry.getValue();
            if (!cmd.isRegistered) {
                cmd.isRegistered = true;
                commands.put(commandName, cmd);
            }
        }
    }

    public void unregisterCommand(BaseCommand command) {
        for (Map.Entry<String, RootCommand> entry : command.registeredCommands.entrySet()) {
            String jdaCommandName = entry.getKey().toLowerCase(Locale.ENGLISH);
            JDARootCommand jdaCommand = (JDARootCommand) entry.getValue();
            jdaCommand.getSubCommands().values().removeAll(command.subCommands.values());
            if (jdaCommand.isRegistered && jdaCommand.getSubCommands().isEmpty()) {
                jdaCommand.isRegistered = false;
                commands.remove(jdaCommandName);
            }
        }
    }

    @Override
    public boolean isCommandIssuer(Class<?> type) {
        return JDAMessageCommandEvent.class.isAssignableFrom(type);
    }

    @Override
    public JDAMessageCommandEvent getCommandIssuer(Object issuer) {
        if (!(issuer instanceof MessageReceivedEvent)) {
            throw new IllegalArgumentException(issuer.getClass().getName() + " is not a MessageReceivedEvent.");
        }
        return new JDAMessageCommandEvent(this, (MessageReceivedEvent) issuer);
    }

    @Override
    public CommandExecutionContext createCommandContext(RegisteredCommand command, CommandParameter parameter, CommandIssuer sender, List<String> args, int i, Map<String, Object> passedArgs) {
        return new JDAMessageCommandExecutionContext(command, parameter, (JDAMessageCommandEvent) sender, args, i, passedArgs);
    }

    @Override
    public CommandCompletionContext createCompletionContext(RegisteredCommand command, CommandIssuer sender, String input, String config, String[] args) {
        // TODO: Maybe implement this, prolly not but maybe for slash commands
        //noinspection unchecked
        return new CommandCompletionContext(command, sender, input, config, args);
    }

    void dispatchEvent(MessageReceivedEvent event) {
        final Message message = event.getMessage();
        final String msg = message.getContentRaw();

        final CommandConfig config = getCommandConfig(event);

        String prefixFound = null;
        for (String prefix : config.getCommandPrefixes()) {
            if (msg.startsWith(prefix)) {
                prefixFound = prefix;
                break;
            }
        }
        if (prefixFound == null) {
            return;
        }

        String[] args = ACFPatterns.SPACE.split(msg.substring(prefixFound.length()), -1);
        if (args.length == 0) {
            return;
        }
        final String cmd = args[0].toLowerCase(Locale.ENGLISH);
        final JDARootCommand rootCommand = this.commands.get(cmd);
        if (rootCommand == null) {
            return;
        }
        if (args.length > 1) {
            args = Arrays.copyOfRange(args, 1, args.length);
        } else {
            args = new String[0];
        }
        rootCommand.execute(this.getCommandIssuer(event), cmd, args);
    }

    private CommandConfig getCommandConfig(MessageReceivedEvent event) {
        CommandConfig config = this.defaultConfig;
        if (this.configProvider != null) {
            CommandConfig provided = this.configProvider.provide(event);
            if (provided != null) {
                config = provided;
            }
        }
        return config;
    }


    @Override
    public String getCommandPrefix(CommandIssuer issuer) {
        MessageReceivedEvent event = issuer.getIssuer();
        CommandConfig commandConfig = getCommandConfig(event);
        List<String> prefixes = commandConfig.getCommandPrefixes();
        return prefixes.isEmpty() ? "" : prefixes.get(0);
    }
}
