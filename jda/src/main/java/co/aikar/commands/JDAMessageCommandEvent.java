package co.aikar.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class JDAMessageCommandEvent extends JDACommandEvent {

    private final MessageReceivedEvent event;

    JDAMessageCommandEvent(JDACommandManager manager, MessageReceivedEvent event) {
        super(manager, event.getAuthor(), event.getMember(), event.getGuild(), event.getChannel());
        this.event = event;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MessageReceivedEvent getIssuer() {
        return event;
    }
}
