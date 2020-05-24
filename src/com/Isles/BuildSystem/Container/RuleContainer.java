package com.Isles.BuildSystem.Container;

public class RuleContainer extends Container implements IRuleContainer {

    private String rule;

    @Override
    public String getRule() {
        return rule;
    }

    @Override
    public void setRule(String rule) {
        this.rule = rule;
    }
}
