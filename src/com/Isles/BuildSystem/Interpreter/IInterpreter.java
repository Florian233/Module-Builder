package com.Isles.BuildSystem.Interpreter;

import com.Isles.BuildSystem.ApplicationCore.ICore;
import com.Isles.BuildSystem.ApplicationCore.ITokenizer;

public interface IInterpreter {

    void interpret(ITokenizer tokenizer, ICore core);

}
