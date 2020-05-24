package com.Isles.BuildSystem.ApplicationCore;

import com.Isles.BuildSystem.Interpreter.IInterpreter;
import com.Isles.BuildSystem.Verifier.IVerifier;

import java.util.HashMap;
import java.util.Map;

public class Registry implements IRegistry {

    private static Registry registryInstance = new Registry();

    private FunctionInterpreterInterface functionInterpreter = null;

    private RuleInterpreterInterface ruleInterpreter = null;

    public static Registry getInstance() {
        return registryInstance;
    }

    Map<String,IVerifier> verifierMap = new HashMap<>();
    Map<String,IInterpreter> interpreterMap = new HashMap<>();

    @Override
    public void registerKeyword(String keyword, IVerifier verifier, IInterpreter interpreter) {
        verifierMap.put(keyword.toLowerCase(),verifier);
        interpreterMap.put(keyword.toLowerCase(),interpreter);
    }

    @Override
    public IVerifier getVerifier(String keyword) {
        return verifierMap.get(keyword.toLowerCase());
    }

    @Override
    public IInterpreter getInterpreter(String keyword) {
        return interpreterMap.get(keyword.toLowerCase());
    }


    @Override
    public void registerFunctionInterpreterFunction(FunctionInterpreterInterface interpreter) {
        functionInterpreter = interpreter;
    }

    @Override
    public void registerRuleInterpreterFunction(RuleInterpreterInterface interpreter) {
        ruleInterpreter = interpreter;
    }

    @Override
    public FunctionInterpreterInterface getFunctionInterpreterFunction() {
        return functionInterpreter;
    }

    @Override
    public RuleInterpreterInterface getRuleInterpreterFunction() {
        return ruleInterpreter;
    }
}
