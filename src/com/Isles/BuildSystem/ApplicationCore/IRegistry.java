package com.Isles.BuildSystem.ApplicationCore;
import com.Isles.BuildSystem.Interpreter.IInterpreter;
import com.Isles.BuildSystem.Verifier.IVerifier;


public interface IRegistry {

    void registerKeyword(String keyword, IVerifier verifier, IInterpreter interpreter);

    IVerifier getVerifier(String keyword);

    IInterpreter getInterpreter(String keyword);

    void registerFunctionInterpreterFunction(FunctionInterpreterInterface interpreter);

    void registerRuleInterpreterFunction(RuleInterpreterInterface interpreter);

    FunctionInterpreterInterface getFunctionInterpreterFunction();

    RuleInterpreterInterface getRuleInterpreterFunction();
}
