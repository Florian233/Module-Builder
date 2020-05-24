package com.Isles.BuildSystem.ApplicationCore;

public interface Token {

    int getStartLine();

    int getStartCharacter();

    int getEndLine();

    int getEndCharacter();

    String getTokenString();

}
