package co.aikar.commands;

public class JDAMessageConditionContext extends JDAConditionContext<JDAMessageCommandEvent> {
    JDAMessageConditionContext(JDAMessageCommandEvent issuer, String config) {
        super(issuer, config);
    }
}
