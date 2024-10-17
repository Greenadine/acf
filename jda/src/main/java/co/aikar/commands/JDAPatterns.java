package co.aikar.commands;

import java.util.regex.Pattern;

final class JDAPatterns {

    /**
     * A pattern which checks for mentioned users (e.g. {@code <@1234567890>}).
     * <p>
     * From Javacord's {@code org.javacord.api.util.DiscordRegexPattern}.
     * </p>
     */
    public static final Pattern USER_MENTION =
            Pattern.compile("(?x)                  # enable comment mode \n"
                    + "(?<!                # negative lookbehind \n"
                    + "                    # (do not have uneven amount of backslashes before) \n"
                    + "    (?<!\\\\)       # negative lookbehind (do not have one backslash before) \n"
                    + "    (?:\\\\{2}+)    # exactly two backslashes \n"
                    + "    {0,1000000000}+ # 0 to 1_000_000_000 times \n"
                    + "                    # (basically *, but a lookbehind has to have a maximum length) \n"
                    + "    \\\\            # the one escaping backslash \n"
                    + ")                   # \n"
                    + "<@!?+               # '<@' or '<@!' \n"
                    + "(?<id>[0-9]++)      # the user id as named group \n"
                    + ">                   # '>'");

    /**
     * A pattern which checks for mentioned roles (e.g. {@code <@&1234567890>}).
     * <p>
     * From Javacord's {@code org.javacord.api.util.DiscordRegexPattern}.
     * </p>
     */
    public static final Pattern ROLE_MENTION =
            Pattern.compile("(?x)                  # enable comment mode \n"
                    + "(?<!                # negative lookbehind \n"
                    + "                    # (do not have uneven amount of backslashes before) \n"
                    + "    (?<!\\\\)       # negative lookbehind (do not have one backslash before) \n"
                    + "    (?:\\\\{2}+)    # exactly two backslashes \n"
                    + "    {0,1000000000}+ # 0 to 1_000_000_000 times \n"
                    + "                    # (basically *, but a lookbehind has to have a maximum length) \n"
                    + "    \\\\            # the one escaping backslash \n"
                    + ")                   # \n"
                    + "<@&                 # '<@&' \n"
                    + "(?<id>[0-9]++)      # the role id as named group \n"
                    + ">                   # '>'");

    /**
     * A pattern which checks for mentioned channels (e.g. {@code <#1234567890>}).
     * <p>
     * From Javacord's {@code org.javacord.api.util.DiscordRegexPattern}.
     * </p>
     */
    public static final Pattern CHANNEL_MENTION =
            Pattern.compile("(?x)                  # enable comment mode \n"
                    + "(?<!                # negative lookbehind \n"
                    + "                    # (do not have uneven amount of backslashes before) \n"
                    + "    (?<!\\\\)       # negative lookbehind (do not have one backslash before) \n"
                    + "    (?:\\\\{2}+)    # exactly two backslashes \n"
                    + "    {0,1000000000}+ # 0 to 1_000_000_000 times \n"
                    + "                    # (basically *, but a lookbehind has to have a maximum length) \n"
                    + "    \\\\            # the one escaping backslash \n"
                    + ")                   # \n"
                    + "(?-x:<#)            # '<#' with disabled comment mode due to the # \n"
                    + "(?<id>[0-9]++)      # the channel id as named group \n"
                    + ">                   # '>'");

    /**
     * A pattern which checks for custom emojis (e.g. {@code <:my_emoji:1234567890>}).
     * <p>
     * From Javacord's {@code org.javacord.api.util.DiscordRegexPattern}.
     */
    public static final Pattern CUSTOM_EMOJI =
            Pattern.compile("(?x)                  # enable comment mode \n"
                    + "(?<!                # negative lookbehind \n"
                    + "                    # (do not have uneven amount of backslashes before) \n"
                    + "    (?<!\\\\)       # negative lookbehind (do not have one backslash before) \n"
                    + "    (?:\\\\{2}+)    # exactly two backslashes \n"
                    + "    {0,1000000000}+ # 0 to 1_000_000_000 times \n"
                    + "                    # (basically *, but a lookbehind has to have a maximum length) \n"
                    + "    \\\\            # the one escaping backslash \n"
                    + ")                   # \n"
                    + "<a?+:               # '<:' or '<a:' \n"
                    + "(?<name>\\w++)      # the custom emoji name as named group \n"
                    + ":                   # ':' \n"
                    + "(?<id>[0-9]++)      # the custom emoji id as named group \n"
                    + ">                   # '>' \n");

    public static final Pattern EQUALS_MATCHER = Pattern.compile(".*=.*");
    public static final Pattern EQUALS_SPLITTER = Pattern.compile("=");

    private JDAPatterns() {
    }
}
