package com.Isles.BuildSystem.ApplicationCore;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;


public class Tokenizer implements ITokenizer {

    private Token previousToken;
    private boolean emitPreviousToken = false;
    private String code;
    private CharacterIterator codeCharIterator;

    private TokenizeFunction defaultTokenizeFunction =  (CharacterIterator codeCharIterator, int startLine, int startCharacter) -> {
        int character = startCharacter;
        int line = startLine;

        boolean stop = false;
        /* indicates whether a ! = > < - + or * in part of the current token */
        boolean specialChar = false;
        String currentToken = "";
        while (codeCharIterator.current() != CharacterIterator.DONE) {
            switch (codeCharIterator.current()) {
                case '\r': /* windows encodes newlines as \r\n */
                    codeCharIterator.next();
                    ++character;
                    continue;
                case '/':
                    break;
                case '|':
                case '&':
                case '%':
                case '!':
                case '=':
                case '<':
                case '>':
                case '-':
                case '+':
                case '*':
                    if (!specialChar && !(currentToken.isEmpty())) {
                        stop = true;
                        break;
                    }
                    specialChar = true;
                    break;
                case '\n':
                case '\t':
                    if (currentToken.isEmpty()) {
                        if (codeCharIterator.current() == '\n') {
                            character = 1;
                            ++line;
                        }
                        codeCharIterator.next();
                        ++character;
                        continue;
                    } else {
                        stop = true;
                    }
                    break;
                case ' ':
                    if (currentToken.isEmpty()) {
                        codeCharIterator.next();
                        ++character;
                        continue;
                    } else {
                        stop = true;
                    }
                    break;
                case ';':
                case ':':
                case '(':
                case ')':
                case '[':
                case ']':
                case '{':
                case '}':
                    if (currentToken.isEmpty()) {
                        currentToken += codeCharIterator.current();
                        codeCharIterator.next();
                        ++character;
                        if (currentToken.equals(":") && codeCharIterator.current() == '=') {
                            currentToken += codeCharIterator.current();
                            codeCharIterator.next();
                            ++character;
                        }
                    }
                    stop = true;
                    break;
                default:
                    if (specialChar) {
                        stop = true;
                    }
                    break;
            }

            if (stop) {
                break; /* while loop */
            } else {
                if(codeCharIterator.current() == '/') {
                    codeCharIterator.next();
                    if(codeCharIterator.current() == '*') {
                        ++character;
                        char previousChar = codeCharIterator.current();
                        codeCharIterator.next();
                        ++character;
                        /* skip comment */
                        while(true) {
                            if(previousChar == '*' && codeCharIterator.current() == '/') {
                                ++character;
                                codeCharIterator.next();
                                break;
                            }
                            previousChar = codeCharIterator.current();
                            if(codeCharIterator.current() == '\n') {
                                character = 1;
                                ++line;
                                codeCharIterator.next();
                            } else {
                                codeCharIterator.next();
                                ++character;
                            }
                        }
                        continue;
                    } else {
                        codeCharIterator.previous();
                    }
                }
                currentToken += codeCharIterator.current();
                codeCharIterator.next();
                ++character;
            }
        }
        return new TokenImpl(startLine, startCharacter, line, character, currentToken);
    };

    private int line = 1;
    private int character = 1;

    public Tokenizer(String code) {
        this.code = code;
        codeCharIterator = new StringCharacterIterator(this.code);
    }

    private void skipComment() {
        char previousChar = codeCharIterator.current(); /* must be the star */
        ++character; /* cannot be a newline */
        codeCharIterator.next();
        while (codeCharIterator.current() != CharacterIterator.DONE) {
            if(codeCharIterator.current() == '/' && previousChar == '*') {
               codeCharIterator.next();
               ++character; /* cannot be a new line */
               return;
            }
            previousChar = codeCharIterator.current();
            if(codeCharIterator.current() == '\n') {
                ++line;
                character = 1;
            } else {
                ++character;
            }
            codeCharIterator.next();
        }
        return; /* should not happen - only if the comment was never closed */
    }

    @Override
    public Token getNextToken() {
        return getNextToken(defaultTokenizeFunction);
    }

    @Override
    public Token getNextToken(TokenizeFunction t) {
        if (emitPreviousToken == true) {
            emitPreviousToken = false;
            return previousToken;
        }

        if(codeCharIterator.current() == CharacterIterator.DONE) {
            previousToken = new TokenImpl(0,0,0,0,"");
            return previousToken;
        }

        Token token = t.isToken(codeCharIterator, line, character);

        line = token.getEndLine();
        character = token.getEndCharacter();

        previousToken = token;

        //System.out.println("Returning token: "+token.getTokenString());
        return token;
    }

    @Override
    public void keepPreviousElement() {
        emitPreviousToken = true;
    }

    @Override
    public void reset() {
        previousToken = null;
        emitPreviousToken = false;
        codeCharIterator = new StringCharacterIterator(code);

        line = 1;
        character = 1;
    }

    @Override
    public void error() {
        System.out.println("Error occured in line:"+previousToken.getStartLine()+" character:"+previousToken.getStartCharacter()+" to line:"+previousToken.getEndLine()+" character:"+previousToken.getEndCharacter()+".");
        System.out.println("Exiting.");
        System.exit(1);
    }

}
