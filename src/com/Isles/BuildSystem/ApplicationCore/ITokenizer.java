package com.Isles.BuildSystem.ApplicationCore;


public interface ITokenizer {

    Token getNextToken();

    Token getNextToken(TokenizeFunction t);

    void keepPreviousElement();

    void reset();

    void error();
}
