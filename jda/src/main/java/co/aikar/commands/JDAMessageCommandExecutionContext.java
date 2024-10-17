package co.aikar.commands;

import java.util.List;
import java.util.Map;

public class JDAMessageCommandExecutionContext extends JDACommandExecutionContext<JDAMessageCommandExecutionContext, JDAMessageCommandEvent> {
    JDAMessageCommandExecutionContext(RegisteredCommand cmd, CommandParameter param, JDAMessageCommandEvent sender, List<String> args, int index, Map<String, Object> passedArgs) {
        super(cmd, param, sender, args, index, passedArgs);
    }
}
