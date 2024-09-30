package co.aikar.commands;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JDAListener extends ListenerAdapter {

    private final JDACommandManager manager;

    JDAListener(JDACommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // Ignore bots and webhooks
        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }
        // Only allow messages from private channels, text channels, and public and private guild threads
        final ChannelType channelType = event.getChannelType();
        if (!(channelType == ChannelType.PRIVATE || channelType == ChannelType.TEXT
                || channelType == ChannelType.GUILD_PUBLIC_THREAD || channelType == ChannelType.GUILD_PRIVATE_THREAD)) {
            return;
        }
        manager.dispatchEvent(event);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        manager.initializeBotOwner();
    }
}
