package com.Isles.BuildSystem.ApplicationCore;

import java.text.CharacterIterator;

@FunctionalInterface
public interface TokenizeFunction {

    Token isToken(CharacterIterator codeCharIterator, int startLine, int startCharacter);

}
