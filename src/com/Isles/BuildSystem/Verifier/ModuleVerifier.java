package com.Isles.BuildSystem.Verifier;

import com.Isles.BuildSystem.ApplicationCore.CoreFunctions;
import com.Isles.BuildSystem.ApplicationCore.ICore;
import com.Isles.BuildSystem.ApplicationCore.ITokenizer;
import com.Isles.BuildSystem.ApplicationCore.Token;

import static com.Isles.BuildSystem.ApplicationCore.CoreFunctions.errorFunction;
import static com.Isles.BuildSystem.ApplicationCore.CoreFunctions.errorFunctionUnexpectedEnd;

public class ModuleVerifier implements IVerifier {

    @Override
    public void verify(ITokenizer tokenizer, ICore core) {
        Token t = tokenizer.getNextToken();
        if(!t.getTokenString().equals("module")) {
            errorFunction(core, "module", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        String varName = t.getTokenString();
        if(core.isRule(varName) || core.isSubmodule(varName) || core.isFunction(varName) || core.isVariable(varName) || CoreFunctions.isKeyword(varName) || CoreFunctions.isBultinFunction(varName)){
            System.out.println("Error: Module name is in use.");
            core.error();
        }
        core.setModuleName(t.getTokenString());
        t = tokenizer.getNextToken();
        if(!t.getTokenString().equals(":")) {
            errorFunction(core, ":", t.getTokenString());
        }
        t=tokenizer.getNextToken();
        while(!t.getTokenString().equals("end")) {
            if(t.getTokenString().equals("begin")) {
                t = tokenizer.getNextToken();
                tokenizer.keepPreviousElement();
                core.subverify(t.getTokenString());
            } else if(t.getTokenString().equals("")) {
                errorFunctionUnexpectedEnd(core);
            } else {
                errorFunction(core, "begin", t.getTokenString());
            }
            t = tokenizer.getNextToken();
        }

        //skip end
        t = tokenizer.getNextToken();
    }
}
