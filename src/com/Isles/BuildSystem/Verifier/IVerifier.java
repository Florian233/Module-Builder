package com.Isles.BuildSystem.Verifier;

import com.Isles.BuildSystem.ApplicationCore.ICore;
import com.Isles.BuildSystem.ApplicationCore.ITokenizer;

public interface IVerifier {

    void verify(ITokenizer tokenizer, ICore core);
}
