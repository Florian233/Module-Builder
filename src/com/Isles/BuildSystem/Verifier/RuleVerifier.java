package com.Isles.BuildSystem.Verifier;

import com.Isles.BuildSystem.ApplicationCore.CoreFunctions;
import com.Isles.BuildSystem.ApplicationCore.ICore;
import com.Isles.BuildSystem.ApplicationCore.ITokenizer;
import com.Isles.BuildSystem.ApplicationCore.Token;

import static com.Isles.BuildSystem.ApplicationCore.CoreFunctions.*;

public class RuleVerifier implements IVerifier {

    @Override
    public void verify(ITokenizer tokenizer, ICore core) {

        Token t = tokenizer.getNextToken();
        if(!t.getTokenString().equals("rule")) {
            errorFunction(core, "rule", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        String varName = t.getTokenString();
        if(core.isRule(varName) || core.isSubmodule(varName) || core.isFunction(varName) || core.isVariable(varName) || CoreFunctions.isKeyword(varName) || CoreFunctions.isBultinFunction(varName)){
            System.out.println("Error: Rule name is in use.");
            core.error();
        }
        t = tokenizer.getNextToken();

        //check dependency
        if(t.getTokenString().equalsIgnoreCase("dependency")){
            t = tokenizer.getNextToken();
            while(!t.getTokenString().equalsIgnoreCase(":")) {
                checkCondition(t,tokenizer, core);
            }
        }

        if(!t.getTokenString().equals(":")) {
            errorFunction(core, ":", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        while(!t.getTokenString().equals("end")) {
            if(t.getTokenString().equals("")) {
                errorFunctionUnexpectedEnd(core);
            } else if(t.getTokenString().equals("begin")) {
                errorFunctionForbiddenElement(core, t.getTokenString());
            }
            tokenizer.keepPreviousElement();
            core.subverify(t.getTokenString());
            t = tokenizer.getNextToken();
        }

        //skip end
        tokenizer.getNextToken();
    }

    /* Fälle die hier auftreten können:
     * 1.) variable komparator variable
     * 2.) variable komparator wert
     * 3.) wert komparator variable
     * 4.) variable komparator build-in funktion
     * 5.) wert komparator build-in funktion
     * 6.) build-in funktion komparator wert
     * 7.) build-in funktion komparator variable
     */
    private void checkCondition(Token t, ITokenizer tokenizer, ICore core) {
        boolean arg1Number = false;
        boolean arg2Number = false;

        /* further checks are not necessary at the moment because all combinations are valid, even value comp value  */
        if(core.isVariable(t.getTokenString())){

        }else if(CoreFunctions.isBultinFunction(t.getTokenString())){
            String functionName = t.getTokenString();
            int numArgs = 0;
            t = tokenizer.getNextToken();
            if(t.getTokenString().equalsIgnoreCase("(")){
                CoreFunctions.errorFunction(core, "(", t.getTokenString());
            }
            t = tokenizer.getNextToken();
            while(true){
                ++numArgs;
                if(!core.isVariable(t.getTokenString())){
                    System.out.println("The parameters of built-in functions must be variables.");
                    core.error();
                }
                t = tokenizer.getNextToken();
                if(t.getTokenString().equalsIgnoreCase(",")){
                    t = tokenizer.getNextToken();
                }else if(t.getTokenString().equalsIgnoreCase(")")){
                    break;
                }else{
                    CoreFunctions.errorFunction(core, ", or )", t.getTokenString());
                }
            }
            if(!CoreFunctions.evaluateNumberOfArgsBuiltInFunctions(functionName, numArgs)){
                System.out.println("Built-in function has the wrong number of arguments.");
                core.error();
            }
            if(!t.getTokenString().equalsIgnoreCase(")")){
                CoreFunctions.errorFunction(core, ")", t.getTokenString());
            }

        }else{
            //has to be a "normal" value
            //TODO maybe this shouldn't be acceptable
        }

        t = tokenizer.getNextToken();
        //now t has to be a comparator
        if(!isComparator(t.getTokenString())){
            CoreFunctions.errorFunction(core, "comparator", t.getTokenString());
        }
        t = tokenizer.getNextToken();

        if(core.isVariable(t.getTokenString())){

        }else if(CoreFunctions.isBultinFunction(t.getTokenString())){
            String functionName = t.getTokenString();
            int numArgs = 0;
            t = tokenizer.getNextToken();
            if(t.getTokenString().equalsIgnoreCase("(")){
                CoreFunctions.errorFunction(core, "(", t.getTokenString());
            }
            t = tokenizer.getNextToken();
            while(true){
                ++numArgs;
                if(!core.isVariable(t.getTokenString())){
                    System.out.println("The parameters of built-in functions must be variables.");
                    core.error();
                }
                t = tokenizer.getNextToken();
                if(t.getTokenString().equalsIgnoreCase(",")){
                    t = tokenizer.getNextToken();
                }else if(t.getTokenString().equalsIgnoreCase(")")){
                    break;
                }else{
                    CoreFunctions.errorFunction(core, ", or )", t.getTokenString());
                }
            }
            if(!CoreFunctions.evaluateNumberOfArgsBuiltInFunctions(functionName, numArgs)){
                System.out.println("Built-in function has the wrong number of arguments.");
                core.error();
            }
            if(!t.getTokenString().equalsIgnoreCase(")")){
                CoreFunctions.errorFunction(core, ")", t.getTokenString());
            }
        }else{
            //has to be a "normal" value
            //TODO maybe this shouldn't be acceptable
        }
        t = tokenizer.getNextToken();
        if(!(t.getTokenString().equalsIgnoreCase(":") || t.getTokenString().equalsIgnoreCase(","))){
            CoreFunctions.errorFunction(core,": or ,", t.getTokenString());
        }
        if(t.getTokenString().equalsIgnoreCase(",")) {
            //skip , because another dependency follows and t should be a reference to calling function
            t = tokenizer.getNextToken();
        }
    }
}
