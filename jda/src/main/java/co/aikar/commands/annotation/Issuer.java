package co.aikar.commands.annotation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code Issuer} annotation is to define whether the parameter should be resolved from the event that issued the command.
 * <p>
 * Possible uses:
 * <ul>
 *     <li>Using this on a {@link User} will fetch the user instance of the user that issued the command.</li>
 *     <li>Using this on a {@link Member} will fetch the member instance of the user that issued the command.</li>
 *     <li>Using this on a {@link Message} will fetch the message from the event that triggered the command.</li>
 *     <li>Using this on a {@link Channel}/{@link TextChannel}/{@link PrivateChannel}/etc.. will fetch the channel from the event that triggered the command.</li>
 *     <li>Using this on a {@link Guild} will fetch the guild from the event that triggered the command.</li>
 * <ul>
 * </p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Issuer {
}
