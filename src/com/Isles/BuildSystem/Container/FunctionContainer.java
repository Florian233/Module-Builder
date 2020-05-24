package com.Isles.BuildSystem.Container;


public class FunctionContainer extends Container implements IFunctionContainer{

    private String function = "";

    private int numArguments = 0;

    @Override
    public String getFunction() {
        return function;
    }

    @Override
    public void setFunction(String function) {
        this.function = function;
    }

    @Override
    public void setNumberOfArguments(int args) {
        numArguments = args;
    }

    @Override
    public int getNumberOfArguments() {
        return numArguments;
    }
}
