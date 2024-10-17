package co.aikar.commands;


import net.dv8tion.jda.api.entities.channel.concrete.Category;

import java.util.regex.Matcher;

public class JDAMessageCommandContexts extends JDACommandContexts<JDAMessageCommandExecutionContext, JDAMessageCommandEvent> {

    JDAMessageCommandContexts(JDAMessageCommandManager manager) {
        super(manager);

        registerIssuerOnlyContext(JDAMessageCommandEvent.class, CommandExecutionContext::getIssuer);
        registerIssuerAwareContext(Category.class, (c) -> {
            if (c.issuer.getGuild() == null) {
                throw new InvalidCommandArgument(JDAMessageKeys.GUILD_ONLY, false);
            }
            final String arg = c.getFirstArg();
            Category category = null;
            final Matcher matcher = JDAPatterns.CHANNEL_MENTION.matcher(arg);
            if (matcher.matches()) {
                final String id = matcher.group("id");
                category = c.issuer.getGuild().getCategoryById(id);
            }

            if (category != null) {
                c.popFirstArg();  // Consume input
            } else {
                category = c.issuer.getIssuer().getMessage().getGuildChannel().asStandardGuildChannel().getParentCategory();
                if (category == null && !c.isOptional()) {
                    throw new InvalidCommandArgument(JDAMessageKeys.COULD_NOT_FIND_CATEGORY, false);
                }
            }
            return category;
        });
        // TODO: Add all other channel types, if needed
    }
}
