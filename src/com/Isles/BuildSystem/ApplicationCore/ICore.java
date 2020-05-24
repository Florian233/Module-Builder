package com.Isles.BuildSystem.ApplicationCore;

import com.Isles.BuildSystem.Container.IAttributeContainer;
import com.Isles.BuildSystem.Container.IFunctionContainer;
import com.Isles.BuildSystem.Container.IRuleContainer;

public interface ICore {

    String getPath();

    String getPathToParentModule();

    String getPathToRootModule();

    void error();

    void subverify(String token);

    void subinterpret(String token);

    void subinterpret(String token, ITokenizer customTokenizer);

    String getInput(String attributeName);

    void callFunction(String functionName, String parameter);

    void addFunction(IFunctionContainer function);

    IAttributeContainer<String> getAttribute(String attributeName);

    void setAttribute(String attributeName, IAttributeContainer<String> value);

    ICore createChildCore(String path);

    void addRule(IRuleContainer rule);

    void callRule(String ruleName);

    void callSubmoduleRule(String moduleName, String ruleName);

    void start(String ruleToBeExecuted);

    void verify();

    void interpret();

    void setModuleName(String moduleName);

    String getModuleName();

    String getInterface(String module);

    void setInterface(String module, String interfacedef);

    void addSubmodule(String modulepath);

    boolean isFunction(String functionName);

    boolean isRule(String ruleName);

    boolean isVariable(String varName);

    boolean isSubmodule(String name);

    ICore getSubmodule(String name);

    boolean setTemporaryAttribute(String name, IAttributeContainer<String> value);

    void removeTemporaryAttribute(String name);
}
