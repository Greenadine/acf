package co.aikar.commands;

import java.util.List;
import java.util.Map;

public abstract class JDACommandExecutionContext<CEC extends JDACommandExecutionContext<CEC, I>, I extends JDACommandEvent> extends CommandExecutionContext<CEC, I> {
    JDACommandExecutionContext(RegisteredCommand cmd, CommandParameter param, I sender, List<String> args, int index, Map<String, Object> passedArgs) {
        super(cmd, param, sender, args, index, passedArgs);
    }
}
