package co.aikar.commands;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

import java.util.Locale;

public enum JDAMessageKeys implements MessageKeyProvider {

    // TODO: Add files

    OWNER_ONLY,
    GUILD_ONLY,                     // TODO: Add to file: "This command can only be executed in a Guild."
    PRIVATE_ONLY,
    GROUP_ONLY,
    TOO_MANY_USERS_WITH_NAME,
    COULD_NOT_FIND_USER,
    PLEASE_SPECIFY_USER,
    USER_IS_BOT,
    USER_NOT_MEMBER_OF_SERVER,
    USER_NOT_IN_VOICE_CHANNEL,
    COULD_NOT_FIND_CHANNEL,         // TODO: Add to file: "Couldn't find a channel with that name or ID."
    TOO_MANY_CHANNELS_WITH_NAME,    // TODO: Add to file: "Too many channels were found with the given name. Try with the `#channelname` syntax."
    COULD_NOT_FIND_CATEGORY,
    TOO_MANY_CATEGORIES_WITH_NAME,  // TODO: Add to file: "Too many categories were found with the given name. Try with the `#categoryname` syntax."
    COULD_NOT_FIND_ROLE,            // TODO: Add to file: "Could not find a role with that name or ID."
    TOO_MANY_ROLES_WITH_NAME,       // TODO: Add to file: "Too many roles were found with the given name. Try with the `@role` syntax."
    PLEASE_SPECIFY_ROLE,
    TOO_MANY_EMOJIS_WITH_NAME,
    COULD_NOT_FIND_EMOJI,
    COULD_NOT_FIND_UNICODE_EMOJI,
    PLEASE_SPECIFY_EMOJI,
    INVALID_CHANNEL_TYPE;

    private final MessageKey key = MessageKey.of("acf-jda." + this.name().toLowerCase(Locale.ENGLISH));
    public MessageKey getMessageKey() {
        return key;
    }
}
