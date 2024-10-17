package co.aikar.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Super class for all JDA Command Events.
 */
public abstract class JDACommandEvent implements CommandIssuer {

    private final JDACommandManager manager;
    private final User user;
    private final Member member;
    private final Guild guild;
    private final MessageChannel channel;

    protected JDACommandEvent(JDACommandManager manager, User user, Member member, Guild guild, MessageChannel channel) {
        this.manager = manager;
        this.user = user;
        this.member = member;
        this.guild = guild;
        this.channel = channel;
    }

    /**
     * Gets the user that triggered the command.
     *
     * @return the user that triggered the command.
     */
    public @NotNull User getUser() {
        return user;
    }

    /**
     * Gets the {@link Member} that triggered the command.
     *
     * @return the {@code Member} that triggered the command, or {@code null} if the command was not triggered in a
     * guild.
     */
    public @Nullable Member getMember() {
        return member;
    }

    /**
     * Gets the guild the command was triggered in.
     *
     * @return the guild the command was triggered in.
     */
    public @Nullable Guild getGuild() {
        return guild;
    }

    /**
     * Gets the channel the command was triggered in.
     *
     * @return the channel the command was triggered in.
     */
    public @Nullable MessageChannel getChannel() {
        return channel;
    }

    /**
     * Gets the type of the channel the command was triggered in.
     *
     * @return the type of the channel the command was triggered in.
     */
    public @Nullable ChannelType getChannelType() {
        return channel.getType();
    }

    /**
     * Checks if the command was triggered in a guild.
     *
     * @return {@code true} if the command was triggered in a guild, {@code false} otherwise.
     */
    public boolean isInGuild() {
        return guild != null;
    }

    /**
     * Checks if the command was triggered in a private channel.
     *
     * @return {@code true} if the command was triggered in a private channel, {@code false} otherwise.
     */
    public boolean isInPrivate() {
        return channel.getType() == ChannelType.PRIVATE;
    }

    /* CommandIssuer */

    @Override
    public JDACommandManager getManager() {
        return manager;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        // Discord id only have 64 bit width (long) while UUIDs have twice the size.
        // In order to keep it unique we use 0L for the first 64 bit.
        long authorId = user.getIdLong();
        return new UUID(0, authorId);
    }

    @Override
    public boolean hasPermission(String permission) {
        CommandPermissionResolver permissionResolver = this.manager.getPermissionResolver();
        return permissionResolver == null || permissionResolver.hasPermission(manager, this, permission);
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public void sendMessageInternal(String message) {
        channel.sendMessage(message).queue();
    }
}
