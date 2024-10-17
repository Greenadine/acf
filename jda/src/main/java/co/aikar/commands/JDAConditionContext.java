package co.aikar.commands;

public abstract class JDAConditionContext<CE extends JDACommandEvent> extends ConditionContext<CE> {
    JDAConditionContext(CE issuer, String config) {
        super(issuer, config);
    }
}
