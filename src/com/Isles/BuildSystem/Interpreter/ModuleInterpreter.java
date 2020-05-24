package com.Isles.BuildSystem.Interpreter;

import com.Isles.BuildSystem.ApplicationCore.ICore;
import com.Isles.BuildSystem.ApplicationCore.ITokenizer;
import com.Isles.BuildSystem.ApplicationCore.Token;

public class ModuleInterpreter implements IInterpreter {

    @Override
    public void interpret(ITokenizer tokenizer, ICore core) {
        Token t = tokenizer.getNextToken();
        while(!t.getTokenString().equals("end")) {
            if(t.getTokenString().equals("begin")) {
                t = tokenizer.getNextToken();
                tokenizer.keepPreviousElement();
                core.subinterpret(t.getTokenString());
            }
            t = tokenizer.getNextToken();
        }
        //skip end
        tokenizer.getNextToken();
    }
}
