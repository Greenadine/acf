package co.aikar.commands;

import co.aikar.commands.apachecommonslang.ApacheCommonsExceptionUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class JDACommandManager<
        I extends JDACommandEvent,
        CEC extends JDACommandExecutionContext<CEC, I>,
        CC extends JDAConditionContext<I>,
        C,
        MF extends MessageFormatter<C>
        >
        extends CommandManager<I, I, C, MF, CEC, CC> {

    private final JDA jda;
    protected JDACommandCompletions completions;
    protected JDACommandContexts<CEC, I> contexts;
    protected JDALocales locales;
    protected Map<String, JDARootCommand> commands = new HashMap<>();
    protected Logger logger;
    protected CommandConfig defaultConfig;
    protected CommandConfigProvider configProvider;
    protected CommandPermissionResolver permissionResolver;
    private long botOwner = 0L;

    protected JDACommandManager(JDA jda) {
        this(jda, null);
    }

    protected JDACommandManager(JDA jda, JDAOptions options) {
        if (options == null) {
            options = new JDAOptions();
        }
        this.jda = jda;
        this.defaultConfig = options.defaultConfig != null ? options.defaultConfig : new JDACommandConfig();
        this.permissionResolver = options.permissionResolver;
//        this.defaultFormatter = new JDAMessageFormatter();  TODO: Create abstract implementation or move this to the two subclasses
        this.completions = new JDACommandCompletions(this);
        this.logger = Logger.getLogger(this.getClass().getSimpleName());

        getCommandConditions().addCondition("owneronly", context -> {
            if (context.getIssuer().getUser().getIdLong() != getBotOwnerId()) {
                throw new ConditionFailedException(JDAMessageKeys.OWNER_ONLY);
            }
        });

        getCommandConditions().addCondition("guildonly", context -> {
            if (context.getIssuer().getChannelType() != ChannelType.TEXT) {
                throw new ConditionFailedException(JDAMessageKeys.GUILD_ONLY);
            }
        });

        getCommandConditions().addCondition("privateonly", context -> {
            if (context.getIssuer().getChannelType() != ChannelType.PRIVATE) {
                throw new ConditionFailedException(JDAMessageKeys.PRIVATE_ONLY);
            }
        });

        getCommandConditions().addCondition("grouponly", context -> {
            if (context.getIssuer().getChannelType() != ChannelType.GROUP) {
                throw new ConditionFailedException(JDAMessageKeys.PRIVATE_ONLY);
            }
        });
    }

    public static JDAOptions options() {
        return new JDAOptions();
    }

    void initializeBotOwner() {
        if (botOwner == 0L) {
            if (jda.getSelfUser().isBot()) {
                botOwner = jda.retrieveApplicationInfo().complete().getOwner().getIdLong();
            } else {
                botOwner = jda.getSelfUser().getIdLong();
            }
        }
    }

    public long getBotOwnerId() {
        // Just in case initialization on ReadyEvent fails.
        initializeBotOwner();
        return botOwner;
    }

    public JDA getJDA() {
        return jda;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public CommandConfig getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(@NotNull CommandConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public CommandConfigProvider getConfigProvider() {
        return configProvider;
    }

    public void setConfigProvider(CommandConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    public CommandPermissionResolver getPermissionResolver() {
        return permissionResolver;
    }

    public void setPermissionResolver(CommandPermissionResolver permissionResolver) {
        this.permissionResolver = permissionResolver;
    }

    @Override
    public CommandContexts<?> getCommandContexts() {
        return contexts;
    }

    @Override
    public CommandCompletions<?> getCommandCompletions() {
        return null;  // TODO: Maybe implement this for slash commands?
    }

    @Override
    public RootCommand createRootCommand(String cmd) {
        return new JDARootCommand(this, cmd);
    }

    @Override
    public Collection<RootCommand> getRegisteredRootCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }

    @Override
    public boolean hasRegisteredCommands() {
        return !this.commands.isEmpty();
    }

    @Override
    public Locales getLocales() {
        if (this.locales == null) {
            this.locales = new JDALocales(this);
            this.locales.loadLanguages();
        }
        return this.locales;
    }

    @Override
    public void log(LogLevel level, String message, Throwable throwable) {
        Level logLevel = level == LogLevel.INFO ? Level.INFO : Level.SEVERE;
        logger.log(logLevel, LogLevel.LOG_PREFIX + message);
        if (throwable != null) {
            for (String line : ACFPatterns.NEWLINE.split(ApacheCommonsExceptionUtil.getFullStackTrace(throwable))) {
                logger.log(logLevel, LogLevel.LOG_PREFIX + line);
            }
        }
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

    /**
     * Executes a root command.
     *
     * @param event the event that triggered the command.
     * @param cmd   the invoked command's name.
     * @param args  the invoked command arguments.
     */
    protected void executeRootCommand(@NotNull Object event, @NotNull String cmd, @NotNull String[] args) {
        RootCommand rootCommand = this.commands.get(cmd);
        if (rootCommand == null) {
            return;
        }
        rootCommand.execute(this.getCommandIssuer(event), cmd, args);
    }
}
