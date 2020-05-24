package com.Isles.BuildSystem.Container;


public interface IFunctionContainer extends IContainer {

    String getFunction();

    void setFunction(String function);

    void setNumberOfArguments(int args);

    int getNumberOfArguments();
}
