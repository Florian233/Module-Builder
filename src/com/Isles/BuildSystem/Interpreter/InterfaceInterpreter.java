package com.Isles.BuildSystem.Interpreter;

import com.Isles.BuildSystem.ApplicationCore.ICore;
import com.Isles.BuildSystem.ApplicationCore.ITokenizer;

/*
 * We don't need to do something here because the interface is set in the core by the verifier.
 * So we only need to skip the tokens.
 */

public class InterfaceInterpreter implements IInterpreter {
    @Override
    public void interpret(ITokenizer tokenizer, ICore core) {
        /* skip all tokens in this block, everything is done by the verifier */
        while(!tokenizer.getNextToken().getTokenString().equalsIgnoreCase("end"));

        //skip end
        tokenizer.getNextToken();
    }
}
