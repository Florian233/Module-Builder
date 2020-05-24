package com.Isles.BuildSystem.Interpreter;

import com.Isles.BuildSystem.ApplicationCore.CoreFunctions;
import com.Isles.BuildSystem.ApplicationCore.ICore;
import com.Isles.BuildSystem.ApplicationCore.ITokenizer;
import com.Isles.BuildSystem.ApplicationCore.Token;

public class IfInterpreter implements IInterpreter {
    @Override
    public void interpret(ITokenizer tokenizer, ICore core) {
        String condition1;
        String condition2;
        String comparator;
        boolean executeIf = false;
        Token t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase("if")) {
            CoreFunctions.errorFunction(core,"if",t.getTokenString());
        }
        t = tokenizer.getNextToken();
        /* first part of condition */
        condition1 = t.getTokenString();
        t = tokenizer.getNextToken();
        /* comparator */
        comparator = t.getTokenString();
        t = tokenizer.getNextToken();
        condition2 = t.getTokenString();
        t = tokenizer.getNextToken();

        // evaluate condition
        if(comparator.equals("==")) {
            if(condition1.equals(condition2)) {
                executeIf = true;
            } else {
                executeIf = false;
            }
        } else if(comparator.equals("!=")) {
            if(!condition1.equals(condition2)) {
                executeIf = true;
            } else {
                executeIf = false;
            }
        } else if(comparator.equals("<")) {
            if(Integer.decode(condition1) < Integer.decode(condition2)) {
                executeIf = true;
            } else {
                executeIf = false;
            }
        } else if(comparator.equals(">")) {
            if(Integer.decode(condition1) > Integer.decode(condition2)) {
                executeIf = true;
            } else {
                executeIf = false;
            }
        } else if(comparator.equals(">=")) {
            if(Integer.decode(condition1) >= Integer.decode(condition2)) {
                executeIf = true;
            } else {
                executeIf = false;
            }
        } else if(comparator.equals("<=")) {
            if(Integer.decode(condition1) <= Integer.decode(condition2)) {
                executeIf = true;
            } else {
                executeIf = false;
            }
        }

        // execute if block of skip it depending of the evaluation of the condition
        if(executeIf) {
            while (!t.getTokenString().equalsIgnoreCase("end") && !t.getTokenString().equalsIgnoreCase("else")) {
                tokenizer.keepPreviousElement();
                core.subinterpret(t.getTokenString());
                t = tokenizer.getNextToken();
            }
        } else {
            //we shouldn't execute this, so skip it
            while (!t.getTokenString().equalsIgnoreCase("end") && !t.getTokenString().equalsIgnoreCase("else")) {
                t = tokenizer.getNextToken();
            }
        }
        if(t.getTokenString().equalsIgnoreCase("else")) {
            t = tokenizer.getNextToken();
            if(!executeIf) {
                while(!t.getTokenString().equalsIgnoreCase("end")){
                    tokenizer.keepPreviousElement();
                    core.subinterpret(t.getTokenString());
                    t = tokenizer.getNextToken();
                }
            } else {
                //just skip the block
                while(!t.getTokenString().equalsIgnoreCase("end")) {
                    t = tokenizer.getNextToken();
                }
            }
        }
        //skip end
        tokenizer.getNextToken();
    }
}
