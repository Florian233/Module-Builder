package com.Isles.BuildSystem;

import com.Isles.BuildSystem.ApplicationCore.*;
import com.Isles.BuildSystem.Interpreter.*;
import com.Isles.BuildSystem.Verifier.*;

import java.util.*;

public class Main {

    private static void addVerifiersAndInterpreters() {
        IInterpreter i;
        IVerifier v;
        IRegistry registry = Registry.getInstance();


        /* module */
        v = new ModuleVerifier();
        i = new ModuleInterpreter();
        registry.registerKeyword("module", v, i);
        /* rule */
        v = new RuleVerifier();
        i = new RuleInterpreter();
        registry.registerKeyword("rule", v, i);
        ((RuleInterpreter) i).initialise();
        /* function */
        v = new FunctionVerifier();
        i = new FunctionInterpreter();
        registry.registerKeyword("function", v, i);
        ((FunctionInterpreter) i).initialise();
        /* Interface */
        v = new InterfaceVerifier();
        i = new InterfaceInterpreter();
        registry.registerKeyword("interface", v, i);
        /* Submodules */
        v = new SubmodulesVerifier();
        i = new SubmodulesInterpreter();
        registry.registerKeyword("submodules", v, i);
        /* Variables */
        v = new VariablesVerifier();
        i = new VariablesInterpreter();
        registry.registerKeyword("variables", v, i);
        /* If */
        v = new IfVerifier();
        i = new IfInterpreter();
        registry.registerKeyword("if", v, i);
        /* For */
        v = new ForVerifier();
        i = new ForInterpreter();
        registry.registerKeyword("for", v, i);
        /* Expression */
        v = new ExpressionVerifier();
        i = new ExpressionInterpreter();
        registry.registerKeyword("expression", v, i);
    }

    public static void main(String[] args) {

        Map<String,String> inputAttributes = new HashMap<>();
        /* rules must be in the same order than entered in the command line! */
        List<String> rules = new ArrayList<>();

        /* parse command line input */
        for(var s:args) {
            if(s.contains("=")) {
                /* must be attribute */
                String attribute = s.substring(0,s.indexOf("="));
                String value = s.substring(s.indexOf("="));
                /* split would also be possible */
                inputAttributes.put(attribute, value);
            } else {
                rules.add(s);
            }
        }

        addVerifiersAndInterpreters();

        /* create first Core that then starts the whole process */
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("The current working directory is " + currentDirectory);
        ICore core = new Core(currentDirectory,inputAttributes, rules);
        System.out.println("Starting the verification.");
        core.verify();
        System.out.println("Verification successful.");
        System.out.println("Now starting the interpretation.");
        core.interpret();
        for(String r: rules) {
            core.start(r);
        }
        System.out.println("Execution completed.");
    }
}
