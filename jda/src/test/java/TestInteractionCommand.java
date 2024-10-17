import co.aikar.commands.BaseCommand;
import co.aikar.commands.JDAInteractionCommandEvent;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.InteractionCommand;
import net.dv8tion.jda.api.interactions.commands.Command;

@InteractionCommand(Command.Type.SLASH)
public class TestInteractionCommand extends BaseCommand {

    @Default
    public void onCommand(JDAInteractionCommandEvent event) {

    }
}
