package co.aikar.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import java.util.Collection;

/**
 * Represents a command event that was triggered by a slash command interaction.
 */
public class JDAInteractionCommandEvent extends JDACommandEvent {

    private final CommandInteraction interaction;

    public JDAInteractionCommandEvent(JDACommandManager manager, CommandInteraction interaction) {
        super(manager, interaction.getUser(), interaction.getMember(), interaction.getGuild(), interaction.getMessageChannel());
        this.interaction = interaction;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CommandInteraction getIssuer() {
        return interaction;
    }

    /* -- Reply -- */

    /**
     * Defer the reply to the interaction.
     *
     * @return the reply callback action.
     */
    @CheckReturnValue
    public @NotNull ReplyCallbackAction deferReply() {
        return interaction.deferReply();
    }

    /**
     * Defer the reply to the interaction.
     *
     * @param ephemeral {@code true} if the reply should be ephemeral, {@code false} otherwise.
     * @return the reply callback action.
     */
    @CheckReturnValue
    public @NotNull ReplyCallbackAction deferReply(boolean ephemeral) {
        return interaction.deferReply(ephemeral);
    }

    /**
     * Reply to the interaction.
     *
     * @param message the message to reply with.
     * @return the reply callback action.
     */
    @CheckReturnValue
    public @NotNull ReplyCallbackAction reply(@NotNull MessageCreateData message) {
        return interaction.reply(message);
    }

    /**
     * Reply to the interaction.
     *
     * @param content the content to reply with.
     * @return the reply callback action.
     */
    @CheckReturnValue
    public @NotNull ReplyCallbackAction reply(@NotNull String content) {
        return interaction.reply(content);
    }

    /**
     * Reply to the interaction.
     *
     * @param embeds the embeds to reply with.
     * @return the reply callback action.
     */
    @CheckReturnValue
    public @NotNull ReplyCallbackAction replyEmbeds(@NotNull Collection<? extends MessageEmbed> embeds) {
        return interaction.replyEmbeds(embeds);
    }

    /**
     * Reply to the interaction.
     *
     * @param embed  the embed to reply with.
     * @param embeds the additional embeds to reply with.
     * @return the reply callback action.
     */
    @CheckReturnValue
    public @NotNull ReplyCallbackAction replyEmbeds(@NotNull MessageEmbed embed, @NotNull MessageEmbed... embeds) {
        return interaction.replyEmbeds(embed, embeds);
    }

    /**
     * Reply to the interaction.
     *
     * @param components the components to reply with.
     * @return the reply callback action.
     */
    @CheckReturnValue
    public @NotNull ReplyCallbackAction replyComponents(@NotNull Collection<? extends LayoutComponent> components) {
        return interaction.replyComponents(components);
    }

    /**
     * Reply to the interaction.
     *
     * @param component  the component to reply with.
     * @param components the additional components to reply with.
     * @return the reply callback action.
     */
    @CheckReturnValue
    public @NotNull ReplyCallbackAction replyComponents(@NotNull LayoutComponent component, @NotNull LayoutComponent... components) {
        return interaction.replyComponents(component, components);
    }

    /**
     * Reply to the interaction.
     *
     * @param format the format of the message to reply with.
     * @param args   the arguments to apply to the format.
     * @return the reply callback action.
     */
    @CheckReturnValue
    public @NotNull ReplyCallbackAction replyFormat(@NotNull String format, @NotNull Object... args) {
        return interaction.replyFormat(format, args);
    }

    /**
     * Reply to the interaction.
     *
     * @param files the files to reply with.
     * @return the reply callback action.
     */
    @CheckReturnValue
    public @NotNull ReplyCallbackAction replyFiles(@NotNull Collection<? extends FileUpload> files) {
        return interaction.replyFiles(files);
    }

    /**
     * Reply to the interaction.
     *
     * @param files the files to reply with.
     * @return the reply callback action.
     */
    @CheckReturnValue
    public @NotNull ReplyCallbackAction replyFiles(@NotNull FileUpload... files) {
        return interaction.replyFiles(files);
    }
}
