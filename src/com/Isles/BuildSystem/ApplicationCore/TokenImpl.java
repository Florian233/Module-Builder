package com.Isles.BuildSystem.ApplicationCore;

public class TokenImpl implements Token {

    private int startLine;

    private int startCharacter;

    private int endLine;

    private int endCharacter;

    private String tokenString;


    public TokenImpl(int startLine, int startCharacter, int endLine, int endCharacter, String tokenString) {
        this.startLine = startLine;
        this.startCharacter = startCharacter;
        this.endLine = endLine;
        this.endCharacter = endCharacter;
        this.tokenString = tokenString;
    }

    @Override
    public int getStartLine() {
        return startLine;
    }

    @Override
    public int getStartCharacter() {
        return startCharacter;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public int getEndCharacter() {
        return endCharacter;
    }

    @Override
    public String getTokenString() {
        return tokenString;
    }
}
