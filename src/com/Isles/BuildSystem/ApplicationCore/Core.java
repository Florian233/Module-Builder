package com.Isles.BuildSystem.ApplicationCore;

import com.Isles.BuildSystem.Container.IAttributeContainer;
import com.Isles.BuildSystem.Container.IFunctionContainer;
import com.Isles.BuildSystem.Container.IRuleContainer;
import com.Isles.BuildSystem.Interpreter.IInterpreter;
import com.Isles.BuildSystem.Verifier.IVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.System.exit;

/* A core object is equivalent to a module. */

public class Core implements ICore {

    private String moduleName;

    private String pathToRoot;

    private String pathToParent;

    private String path;

    private ITokenizer tokenizer;

    private Map<String, IAttributeContainer<String>> variables = new HashMap<>();

    private Map<String, IFunctionContainer> functions = new HashMap<>();

    private Map<String, IRuleContainer> rules = new HashMap<>();

    private IRegistry registry = Registry.getInstance();

    private Map<String, String> inputs;

    private List<String> rulesToBeExecuted;

    private Map<String,String> interfaces = new HashMap<>();

    private Map<String,ICore> submodules = new HashMap<>();

    private Map<String, IAttributeContainer<String>> temporaryVariables = new HashMap<>();

    public Core(String pathToRoot, Map<String, String> inputs, List<String> rulesToBeExecuted) {
        this.pathToRoot = pathToRoot;
        this.path = pathToRoot;
        this.pathToParent = "";
        this.inputs = inputs;
        this.rulesToBeExecuted = rulesToBeExecuted;

        /* TODO: buffer verwenden, das auch in Tokenizer */
        try {
            tokenizer = new Tokenizer(new String(Files.readAllBytes(Paths.get(path+"\\build.module"))));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot find the file "+path+"\\build.module.");
            System.out.println("Exiting...");
            exit(2);
        }
    }

    private Core(String pathToRoot, String pathToParent, String path,
                 Map<String, String> inputs, Map<String, IAttributeContainer<String>> inheritedVariables,
                 Map<String, IFunctionContainer> inheritedFunctions, Map<String, IRuleContainer> rules,
                 List<String> rulesToBeExecuted, Map<String,String> interfaces)
    {
        this.pathToRoot = pathToRoot;
        this.pathToParent = pathToParent;
        this.path = path;
        this.inputs = inputs;
        this.variables.putAll(inheritedVariables);
        this.functions.putAll(inheritedFunctions);
        this.rules.putAll(rules);
        this.rulesToBeExecuted = rulesToBeExecuted;
        this.interfaces = interfaces;

        try {
            tokenizer = new Tokenizer(new String(Files.readAllBytes(Paths.get(path))));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot find the file "+path+".");
            System.out.println("Exiting...");
            exit(2);
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getPathToParentModule() {
        return pathToParent;
    }

    @Override
    public String getPathToRootModule() {
        return pathToRoot;
    }

    @Override
    public void error() {
        System.out.println("Error in file: "+path+"\\build.module");
        tokenizer.error();
    }

    @Override
    public void subverify(String token) {
        IVerifier v = registry.getVerifier(token);
        if(v == null) {
        	/* if nothing else fits, try expression, the corresponding verifier can fault. */
            v = registry.getVerifier("Expression");
        }
        v.verify(tokenizer, this);
        tokenizer.keepPreviousElement();
    }

    @Override
    public void subinterpret(String token) {
        subinterpret(token, tokenizer);
    }

    @Override
    public void subinterpret(String token, ITokenizer customTokenizer) {
        IInterpreter i = registry.getInterpreter(token);
        if (i == null) {
            /* try expressions, the corresponding interpreter can fault if nothing fits. */
            i = registry.getInterpreter("Expression");
        }
        i.interpret(customTokenizer, this);
        customTokenizer.keepPreviousElement();
    }

    @Override
    public String getInput(String attributeName) {
        return inputs.get(attributeName);
    }

    @Override
    public void setAttribute(String attributeName, IAttributeContainer<String> value) {
        //all temp variables shall not be manipulated by this function and are const here
        if((variables.containsKey(attributeName) && variables.get(attributeName).isConst()) || temporaryVariables.containsKey(attributeName)) {
            System.out.println("Manipulating the constant "+attributeName+".");
            System.out.println("Error in:"+path+"\\build.module");
            tokenizer.error();
        }
        variables.put(attributeName,value);
    }

    @Override
    public IAttributeContainer<String> getAttribute(String attributeName) {
        if(temporaryVariables.containsKey(attributeName)) {
            return temporaryVariables.get(attributeName);
        }
        return variables.get(attributeName);
    }

    @Override
    /* there are not parameters (yet) but maybe they will be added in the future, hence I will keep this for now. */
    public void callFunction(String functionName, String parameter) {
        String function = functions.get(functionName).getFunction();
        registry.getFunctionInterpreterFunction().interpretFunction(function, parameter, this);
    }

    @Override
    public void addFunction(IFunctionContainer function) {
        functions.put(function.getName(), function);
    }

    @Override
    public ICore createChildCore(String path) {
        Map<String, IAttributeContainer<String>> inheritedVariables = new HashMap<>();
        Map<String, IFunctionContainer> inheritedFunctions = new HashMap<>();
        Map<String, IRuleContainer> inheritedRules = new HashMap<>();

        for (Map.Entry<String, IAttributeContainer<String>> entry : variables.entrySet()) {
            String name = entry.getKey();
            IAttributeContainer container = entry.getValue();
            if (container.isInherited() && !container.isLocal()) {
                inheritedVariables.put(name, container);
            }
        }
        return new Core(pathToRoot, this.path, path, inputs, inheritedVariables, inheritedFunctions, inheritedRules, rulesToBeExecuted, interfaces);
    }

    @Override
    public void addRule(IRuleContainer rule) {
        rules.put(rule.getName(), rule);
    }

    @Override
    public void callRule(String ruleName) {
        if(rules.get(ruleName) == null) {
            /* don't call a rule that hasn't been defined. */
            return;
        }
        String rule = rules.get(ruleName).getRule();
        registry.getRuleInterpreterFunction().interpretRule(this, rule);
    }

    @Override
    public void callSubmoduleRule(String moduleName, String ruleName) {
        submodules.get(moduleName).callRule(ruleName);
    }


    @Override
    public void start(String ruleToBeExecuted) {
        System.out.println("Executing "+ruleToBeExecuted);
        callRule(ruleToBeExecuted);
        /* sudmodules */
        submodules.forEach((s,c)->{c.start(ruleToBeExecuted);});
    }

    @Override
    public void verify() {
        Token t = tokenizer.getNextToken();
        while(!t.getTokenString().equalsIgnoreCase("")) {
            if (t.getTokenString().equalsIgnoreCase("begin")) {
                /* skip it */
                t = tokenizer.getNextToken();
            } else {
                /* no begin, indicates error */
                System.out.println("Missing begin statement in "+path+"\\build.module.");
                tokenizer.error();
            }
            /* next token is keyword */
            IVerifier v = registry.getVerifier(t.getTokenString());
            if(v == null) {
                System.out.print("Couldn't find a verifier for ");
                System.out.print(t.getTokenString());
                System.out.println(".");
                tokenizer.error();
            }
            tokenizer.keepPreviousElement();
            v.verify(tokenizer, this);
            tokenizer.keepPreviousElement();
            /* now the tokenizer is either empty an returns empty strings or there is another construct that starts with begin */
            t = tokenizer.getNextToken();
            //System.out.println("Hier:"+t.getTokenString());
        }

        /* call submodules to verify them */
        submodules.forEach((s,c)->{c.verify();});

        tokenizer.reset();
    }

    @Override
    public void interpret() {
        Token t = tokenizer.getNextToken();

        while(!t.getTokenString().equals("")) {
            if (t.getTokenString().equalsIgnoreCase("begin")) {
                /* skip it */
                t = tokenizer.getNextToken();
            } else {
                /* no begin, indicates error */
                System.out.println("Missing begin statement.");
                tokenizer.error();
            }
            /* next token is keyword */
            IInterpreter i = registry.getInterpreter(t.getTokenString());
            if(i == null) {
                System.out.print("Couldn't find a interpreter for ");
                System.out.print(t.getTokenString());
                System.out.println(".");
                tokenizer.error();
            }
            tokenizer.keepPreviousElement();
            i.interpret(tokenizer, this);
            tokenizer.keepPreviousElement();
            /* now the tokenizer is either empty an returns empty strings or there is another construct that starts with begin */
            t = tokenizer.getNextToken();
        }

        submodules.forEach((s,c)->{c.interpret();});
    }

    @Override
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public String getInterface(String module) {
        return interfaces.get(module);
    }

    @Override
    public void setInterface(String module, String interfaceDef) {
        interfaces.put(module,interfaceDef);
    }

    @Override
    public void addSubmodule(String modulePath) {
        ICore submoduleCore = createChildCore(modulePath);
        submoduleCore.verify();
        if(submodules.containsKey(submoduleCore.getModuleName())) {
            System.out.println("Submodule '"+submoduleCore.getModuleName()+"' of the module '"+moduleName+ "' already exits.");
            error();
        }
        submodules.put(submoduleCore.getModuleName(), submoduleCore);
    }

    @Override
    public boolean isFunction(String functionName) {
        if(functions.containsKey(functionName)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isRule(String ruleName) {
        if(rules.containsKey(ruleName)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVariable(String varName) {
        if(variables.containsKey(varName) || temporaryVariables.containsKey(varName)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSubmodule(String name) {
        if(submodules.containsKey(name)){
            return true;
        }
        return false;
    }

    @Override
    public ICore getSubmodule(String name) {
        return submodules.get(name);
    }

    @Override
    public boolean setTemporaryAttribute(String name, IAttributeContainer<String> value) {
        if(variables.containsKey(name)) {
            return false;
        }
        temporaryVariables.put(name, value);
        return true;
    }

    @Override
    public void removeTemporaryAttribute(String name) {
        temporaryVariables.remove(name);
    }
}
