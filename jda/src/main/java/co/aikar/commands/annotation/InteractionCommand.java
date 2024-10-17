package co.aikar.commands.annotation;

import net.dv8tion.jda.api.interactions.commands.Command.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link InteractionCommand} annotation is used to mark a command as a type of interaction command. The types of
 * interaction commands are {@link Type#SLASH slash commands}, {@link Type#USER user context commands},
 * and {@link Type#MESSAGE message context commands}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InteractionCommand {

    /**
     * The type of interaction command.
     *
     * @return the type of interaction command.
     */
    Type value();

    /**
     * The ID of the guild where the command should be registered. If defined, the command will be registered in the
     * specified guild.
     *
     * @return the ID of the guild where the command should be registered.
     */
    long guildId() default 0L;

    /**
     * The name of the guild where the command should be registered. If defined, the command will be registered in the
     * specified guild.
     */
    String guildName() default "";

    /**
     * If a name is provided, whether searching for the guild by name should be case-insensitive. Default is
     * {@code false}.
     *
     * @return {@code true} if name searching should be case-insensitive, {@code false} otherwise.
     */
    boolean ignoreCase() default false;
}
