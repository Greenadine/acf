package co.aikar.commands;

import co.aikar.commands.annotation.CrossGuild;
import co.aikar.commands.annotation.Issuer;
import co.aikar.commands.annotation.SelfUser;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Split;
import co.aikar.commands.annotation.Values;
import co.aikar.commands.contexts.ContextResolver;
import com.google.common.collect.Iterables;
import com.vdurmont.emoji.EmojiManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

public abstract class JDACommandContexts<CEC extends JDACommandExecutionContext<CEC, I>, I extends JDACommandEvent> extends CommandContexts<CEC> {

    private final JDACommandManager manager;
    protected final JDA jda;

    public JDACommandContexts(JDACommandManager manager) {
        super(manager);
        this.manager = manager;
        this.jda = this.manager.getJDA();
        this.registerIssuerOnlyContext(JDACommandEvent.class, CommandExecutionContext::getIssuer);
//        this.registerIssuerOnlyContext(MessageReceivedEvent.class, c -> c.getIssuer().getIssuer());
        this.registerIssuerOnlyContext(ChannelType.class, (c) -> c.issuer.getChannelType());
        this.registerIssuerOnlyContext(JDA.class, (c) -> jda);
        this.registerIssuerOnlyContext(Guild.class, (c) -> {
            if (c.issuer.isInPrivate() && !c.isOptional()) {
                throw new InvalidCommandArgument(JDAMessageKeys.GUILD_ONLY, false);
            } else {
                return c.issuer.getGuild();
            }
        });
        registerIssuerAwareContext(User.class, (c) -> {
            if (c.hasAnnotation(SelfUser.class)) {
                return manager.getJDA().getSelfUser();
            }
            if (c.hasAnnotation(Issuer.class)) {
                return c.issuer.getUser();
            }
            final String arg = c.isLastArg() ? String.join(" ", c.getArgs()) : c.popFirstArg();
            if (!c.isOptional() && (arg == null || arg.isEmpty())) {
                throw new InvalidCommandArgument(JDAMessageKeys.PLEASE_SPECIFY_USER, false);
            }
            User user = null;
            if (!(arg == null || arg.isEmpty())) {
                final Matcher matcher = JDAPatterns.USER_MENTION.matcher(arg);
                if (matcher.matches()) {
                    final String id = matcher.group("id");
                    user = jda.getUserById(id);
                    if (user == null) {
                        throw new InvalidCommandArgument(JDAMessageKeys.COULD_NOT_FIND_USER, false);
                    }
                    if (c.hasFlag("humanonly") && user.isBot()) {
                        throw new InvalidCommandArgument(JDAMessageKeys.USER_IS_BOT, false);
                    }
                    c.popFirstArg();  // Consume input
                } else {
                    final Collection<User> users = jda.getUsersByName(arg, c.hasFlag("ignorecase"));
                    if (users.size() > 1 && !c.isOptional()) {
                        throw new InvalidCommandArgument(JDAMessageKeys.TOO_MANY_USERS_WITH_NAME, false);
                    } else if (!users.isEmpty()) {
                        user = Iterables.get(users, 0);
                        c.popFirstArg();  // Consume input
                    }
                }
            }
            return user;
        });
        registerIssuerAwareContext(Member.class, (c) -> {
            if (c.issuer.getMember() == null) {
                throw new InvalidCommandArgument(JDAMessageKeys.GUILD_ONLY);
            }
            if (c.hasAnnotation(SelfUser.class)) {
                return c.issuer.getMember();
            }
            if (!c.hasAnnotation(Issuer.class)) {
                return c.issuer.getMember();
            }
            final String arg = c.isLastArg() ? String.join(" ", c.getArgs()) : c.popFirstArg();
            if (!c.isOptional() && (arg == null || arg.isEmpty())) {
                throw new InvalidCommandArgument(JDAMessageKeys.PLEASE_SPECIFY_USER, false);
            }
            Member member = null;
            if (!(arg == null || arg.isEmpty())) {
                final Matcher matcher = JDAPatterns.USER_MENTION.matcher(arg);
                final Guild guild = c.issuer.getGuild();
                if (matcher.matches()) {
                    final String id = matcher.group("id");
                    member = guild.getMemberById(id);
                    if (member == null) {
                        throw new InvalidCommandArgument(JDAMessageKeys.COULD_NOT_FIND_USER, false);
                    }
                    if (c.hasFlag("humanonly") && member.getUser().isBot()) {
                        throw new InvalidCommandArgument(JDAMessageKeys.USER_IS_BOT, false);
                    }
                    c.popFirstArg();  // Consume input
                } else {
                    final Collection<Member> members = guild.getMembersByName(arg, c.hasFlag("ignorecase"));
                    if (members.size() > 1 && !c.isOptional()) {
                        throw new InvalidCommandArgument(JDAMessageKeys.TOO_MANY_USERS_WITH_NAME, false);
                    } else if (!members.isEmpty()) {
                        member = Iterables.get(members, 0);
                        c.popFirstArg();  // Consume input
                    }
                }
            }
            return member;
        });
        registerOptionalContext(Role.class, (c) -> {
            final String arg = c.isLastArg() ? String.join(" ", c.getArgs()) : c.getFirstArg();
            if (!c.isOptional() && (arg == null || arg.isEmpty())) {
                throw new InvalidCommandArgument(JDAMessageKeys.PLEASE_SPECIFY_ROLE, false);
            }

            Role role = null;
            if (!(arg == null || arg.isEmpty())) {
                final Matcher matcher = JDAPatterns.ROLE_MENTION.matcher(arg);
                final boolean isCrossGuild = c.hasAnnotation(CrossGuild.class);
                if (matcher.matches()) {
                    final String id = matcher.group("id");
                    role = (isCrossGuild || c.issuer.getGuild() == null)  // If cross-guild or not issued in a guild, use JDA instance
                            ? jda.getRoleById(id)
                            : c.issuer.getGuild().getRoleById(id);  // Otherwise, use guild instance
                }
            }
            if (role != null) {
                c.popFirstArg();  // Consume input
            } else {
                if (!c.isOptional()) {
                    throw new InvalidCommandArgument(JDAMessageKeys.COULD_NOT_FIND_ROLE, false);
                }
            }
            return role;
        });
        registerIssuerAwareContext(Channel.class, (c) -> {
            if (c.hasAnnotation(Issuer.class)) {
                return c.issuer.getChannel();
            }
            final String arg = c.getFirstArg();
            Channel channel = null;
            final Matcher matcher = JDAPatterns.CHANNEL_MENTION.matcher(arg);
            if (matcher.matches()) {
                final String id = matcher.group("id");
                final boolean isCrossGuild = c.hasAnnotation(CrossGuild.class);
                final List<ChannelType> types = new ArrayList<>();

                // If channel types are specified, parse them
                if (c.hasFlag("types")) {
                    final String[] typeNames = ACFPatterns.SEMICOLON.split(c.getFlagValue("types", ""));
                    for (final String typeName : typeNames) {
                        try {
                            final ChannelType type = ChannelType.valueOf(typeName.toUpperCase(Locale.ROOT));
                            types.add(type);
                        } catch (IllegalArgumentException ex) {
                            ACFUtil.sneaky(ex);
                        }
                    }
                }
                channel = (isCrossGuild || c.issuer.getGuild() == null)  // If cross-guild or not issued in a guild, use JDA instance
                        ? jda.getChannelById(Channel.class, id)
                        : c.issuer.getGuild().getGuildChannelById(id);  // Otherwise, use guild instance

                // Only allow specified channel types if any are present
                if (!types.isEmpty() && channel != null) {
                    if (!types.contains(channel.getType())) {
                        throw new InvalidCommandArgument(JDAMessageKeys.INVALID_CHANNEL_TYPE, false);  // TODO: Add replacement for type maybe?
                    }
                }
            }
            if (channel != null) {
                c.popFirstArg();  // Consume input
            } else {
                if (!c.isOptional()) {
                    throw new InvalidCommandArgument(JDAMessageKeys.COULD_NOT_FIND_CHANNEL, false);
                }
                channel = c.issuer.getChannel();
            }
            return channel;
        });
        registerIssuerAwareContext(PrivateChannel.class, (c) ->  {
            if (!c.issuer.isInPrivate()) {
                throw new InvalidCommandArgument(JDAMessageKeys.PRIVATE_ONLY, false);
            }
            return (PrivateChannel) c.issuer.getChannel();
        });
        // TODO: Add all other channel types, if needed
        registerOptionalContext(Emoji.class, (c) -> {
            final String arg = c.popFirstArg();
            if (!c.isOptional() && (arg == null || arg.isEmpty())) {
                throw new InvalidCommandArgument(JDAMessageKeys.PLEASE_SPECIFY_EMOJI, false);
            }
            Emoji emoji = null;
            if (!(arg == null || arg.isEmpty())) {
                final Matcher matcher = JDAPatterns.CUSTOM_EMOJI.matcher(arg);
                if (matcher.matches()) {
                    final String id = matcher.group("id");
                    final boolean isCrossGuild = c.hasAnnotation(CrossGuild.class);
                    emoji = (isCrossGuild || c.issuer.getGuild() == null)  // If cross-guild or not issued in a guild, use JDA instance
                            ? jda.getEmojiById(id)
                            : c.issuer.getGuild().getEmojiById(id);  // Otherwise, use guild instance
                } else if (EmojiManager.isEmoji(arg)) {
                    emoji = new UnicodeEmojiImpl(arg);  // TODO: Check if this is correct
                }
            }
            if (emoji == null && !c.isOptional()) {
                throw new InvalidCommandArgument(JDAMessageKeys.COULD_NOT_FIND_EMOJI, false);
            }
            return emoji;
        });
        registerOptionalContext(UnicodeEmoji.class, (c) -> {
            final String arg = c.getFirstArg();
            if (!c.isOptional() && (arg == null || arg.isEmpty())) {
                throw new InvalidCommandArgument(JDAMessageKeys.PLEASE_SPECIFY_EMOJI, false);
            }
            if (!(arg == null || arg.isEmpty())) {
                if (!EmojiManager.isEmoji(arg)) {
                    throw new InvalidCommandArgument(JDAMessageKeys.COULD_NOT_FIND_UNICODE_EMOJI, false);
                }
            }
            return new UnicodeEmojiImpl(arg);
        });
        registerOptionalContext(CustomEmoji.class, (c) -> {
            final String arg = c.popFirstArg();
            if (!c.isOptional() && (arg == null || arg.isEmpty())) {
                throw new InvalidCommandArgument(JDAMessageKeys.PLEASE_SPECIFY_EMOJI, false);
            }
            CustomEmoji emoji = null;
            if (!(arg == null || arg.isEmpty())) {
                final Matcher matcher = JDAPatterns.CUSTOM_EMOJI.matcher(arg);
                if (matcher.matches()) {
                    final String id = matcher.group("id");
                    final boolean isCrossGuild = c.hasAnnotation(CrossGuild.class);
                    emoji = (isCrossGuild || c.issuer.getGuild() == null)  // If cross-guild or not issued in a guild, use JDA instance
                            ? jda.getEmojiById(id)
                            : c.issuer.getGuild().getEmojiById(id);  // Otherwise, use guild instance
                }
            }
            if (emoji == null && !c.isOptional()) {
                throw new InvalidCommandArgument(JDAMessageKeys.COULD_NOT_FIND_EMOJI, false);
            }
            return emoji;
        });

        //region Override ACF core resolvers to better fit Discord message commands
        registerContext(Long.class, Long.TYPE, (c) -> resolveNumber(c, Long.MIN_VALUE, Long.MAX_VALUE).longValue());
        registerContext(Integer.class, Integer.TYPE, (c) -> resolveNumber(c, Integer.MIN_VALUE, Integer.MAX_VALUE).intValue());
        registerContext(Short.class, Short.TYPE, (c) -> resolveNumber(c, Short.MIN_VALUE, Short.MAX_VALUE).shortValue());
        registerContext(Byte.class, Byte.TYPE, (c) -> resolveNumber(c, Byte.MIN_VALUE, Byte.MAX_VALUE).byteValue());
        registerContext(Double.class, Double.TYPE, (c) -> resolveNumber(c, Double.MIN_VALUE, Double.MAX_VALUE).doubleValue());
        registerContext(Float.class, Float.TYPE, (c) -> resolveNumber(c, Float.MIN_VALUE, Float.MAX_VALUE).floatValue());
        registerContext(Boolean.class, Boolean.TYPE, (c) -> ACFUtil.isTruthy(c.popFirstArg()));
        registerContext(Number.class, (c) -> resolveNumber(c, Double.MIN_VALUE, Double.MAX_VALUE));
        registerContext(BigDecimal.class, this::resolveBigNumber);
        registerContext(BigInteger.class, this::resolveBigNumber);
        registerContext(Character.class, Character.TYPE, (c) -> {
            final String arg = c.popFirstArg();
            if (arg.length() > 1) {
                throw new InvalidCommandArgument(MessageKeys.MUST_BE_MAX_LENGTH, false, "{max}", String.valueOf(1));
            } else {
                return arg.charAt(0);
            }
        });
        registerContext(String.class, (c) -> {
            if (c.hasAnnotation(Values.class)) {
                return c.popFirstArg();
            }
            final String val = c.isLastArg() && !c.hasAnnotation(Single.class) ? ACFUtil.join(c.getArgs()) : c.popFirstArg();
            final Integer minLen = c.getFlagValue("minlen", (Integer) null);
            final Integer maxLen = c.getFlagValue("maxlen", (Integer) null);
            if (minLen != null && val.length() < minLen) {
                throw new InvalidCommandArgument(MessageKeys.MUST_BE_MIN_LENGTH, false, "{min}", String.valueOf(minLen));
            } else if (maxLen != null && val.length() > maxLen) {
                throw new InvalidCommandArgument(MessageKeys.MUST_BE_MAX_LENGTH, false, "{max}", String.valueOf(minLen));
            } else {
                return val;
            }
        });
        registerContext(String[].class, (c) -> {
            final List<String> args = c.getArgs();
            final String val;
            if (c.isLastArg() && !c.hasAnnotation(Single.class)) {
                val = ACFUtil.join(args);
            } else {
                val = c.popFirstArg();
            }
            final String split = c.getAnnotationValue(Split.class, 8);
            if (split != null) {
                if (val.isEmpty()) {
                    throw new InvalidCommandArgument();
                } else {
                    return ACFPatterns.getPattern(split).split(val);
                }
            } else {
                if (!c.isLastArg()) {
                    ACFUtil.sneaky(new IllegalStateException("Weird Command signature... String[] should be last or @Split"));
                }

                final String[] result = args.toArray(new String[0]);
                args.clear();
                return result;
            }
        });
        registerContext(Enum.class, (c) -> {
            final String first = c.popFirstArg();
            //noinspection deprecation,unchecked
            final Class<? extends Enum<?>> enumCls = (Class<? extends Enum<?>>) c.getParam().getType();
            final Enum<?> match = ACFUtil.simpleMatch(enumCls, first);
            if (match == null) {
                final List<String> names = ACFUtil.enumNames(enumCls);
                throw new InvalidCommandArgument(MessageKeys.PLEASE_SPECIFY_ONE_OF, false, "{valid}", ACFUtil.join(names, ", "));
            } else {
                return match;
            }
        });
        //endregion
    }

    /* Utility methods */

    protected <T> void registerContext(Class<? extends T> clazz1, Class<? extends T> clazz2, ContextResolver<? extends T, CEC> supplier) {
        registerContext((Class<T>) clazz1, (ContextResolver<T, CEC>) supplier);
        registerContext((Class<T>) clazz2, (ContextResolver<T, CEC>) supplier);
    }

    protected <T extends Number> T resolveBigNumber(@NotNull CommandExecutionContext c) {
        final String arg = c.popFirstArg();
        try {
            //noinspection unchecked
            final T number = (T) ACFUtil.parseBigNumber(arg, c.hasFlag("suffixes"));
            this.validateMinMax(c, number);
            return number;
        } catch (NumberFormatException ex) {
            throw new InvalidCommandArgument(MessageKeys.MUST_BE_A_NUMBER, false, "{num}", arg);
        }
    }

    private Number resolveNumber(@NotNull CommandExecutionContext c, @NotNull Number minValue, @NotNull Number maxValue) {
        final String number = c.popFirstArg();
        try {
            return this.parseAndValidateNumber(number, c, minValue, maxValue).shortValue();
        } catch (NumberFormatException ex) {
            throw new InvalidCommandArgument(MessageKeys.MUST_BE_A_NUMBER, false, "{num}", number);
        }
    }

    @NotNull
    private Number parseAndValidateNumber(String number, CommandExecutionContext c, Number minValue, Number maxValue) throws InvalidCommandArgument {
        final Number val = ACFUtil.parseNumber(number, c.hasFlag("suffixes"));
        this.validateMinMax(c, val, minValue, maxValue);
        return val;
    }

    private void validateMinMax(CommandExecutionContext c, Number val) throws InvalidCommandArgument {
        this.validateMinMax(c, val, null, null);
    }

    private void validateMinMax(CommandExecutionContext c, Number val, Number minValue, Number maxValue) throws InvalidCommandArgument {
        minValue = c.getFlagValue("min", minValue);
        maxValue = c.getFlagValue("max", maxValue);
        if (maxValue != null && val.doubleValue() > maxValue.doubleValue()) {
            throw new InvalidCommandArgument(MessageKeys.PLEASE_SPECIFY_AT_MOST, false, "{max}", String.valueOf(maxValue));
        } else if (minValue != null && val.doubleValue() < minValue.doubleValue()) {
            throw new InvalidCommandArgument(MessageKeys.PLEASE_SPECIFY_AT_LEAST, false, "{min}", String.valueOf(minValue));
        }
    }
}
