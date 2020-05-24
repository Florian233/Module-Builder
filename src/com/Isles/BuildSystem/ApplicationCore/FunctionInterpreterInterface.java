package com.Isles.BuildSystem.ApplicationCore;

@FunctionalInterface
public interface FunctionInterpreterInterface {

    void interpretFunction(String functionCode, String parameters, ICore core);

}
