package com.Isles.BuildSystem.ApplicationCore;

@FunctionalInterface
public interface RuleInterpreterInterface {

    void interpretRule(ICore core, String code);

}
