package com.Isles.BuildSystem.Interpreter;

import com.Isles.BuildSystem.ApplicationCore.ICore;
import com.Isles.BuildSystem.ApplicationCore.ITokenizer;

public class SubmodulesInterpreter implements IInterpreter {
    @Override
    public void interpret(ITokenizer tokenizer, ICore core) {
        /* just skip all tokens in this block, everything else is done by the verifier */
        while(!tokenizer.getNextToken().getTokenString().equalsIgnoreCase("end"));

        //skip end
        tokenizer.getNextToken();
    }
}
