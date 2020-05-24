package com.Isles.BuildSystem.Verifier;

import com.Isles.BuildSystem.ApplicationCore.*;
import java.text.CharacterIterator;

import static com.Isles.BuildSystem.ApplicationCore.CoreFunctions.errorFunction;
import static com.Isles.BuildSystem.ApplicationCore.CoreFunctions.errorFunctionUnexpectedEnd;

public class SubmodulesVerifier implements IVerifier {

    TokenizeFunction tokenizeFunction = (CharacterIterator codeCharIterator, int startLine, int startCharacter) -> {
        int character = startCharacter;
        int line = startLine;
        String currentToken = "";

        /* skip tabs und white spaces */
        while(true) {
            if (codeCharIterator.current() == '\t') {
                codeCharIterator.next();
                ++character;
            } else if (codeCharIterator.current() == ' ') {
                codeCharIterator.next();
                ++character;
            } else if (codeCharIterator.current() == '\r') {
                codeCharIterator.next();
            } else if (codeCharIterator.current() == '\n') {
                codeCharIterator.next();
                character = 1;
                ++line;
            } else if(codeCharIterator.current() == '/') {
                codeCharIterator.next();
                if(codeCharIterator.current() == '*') {
                    ++character;
                    /* skip comment */
                } else {
                    codeCharIterator.previous();
                    break;
                }
            } else {
                break;
            }
        }

        while (codeCharIterator.current() != CharacterIterator.DONE && codeCharIterator.current() != '\r' && codeCharIterator.current() != '\n' ) {
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
                } else {
                    codeCharIterator.previous();
                }
            }
            currentToken += codeCharIterator.current();
            codeCharIterator.next();
            ++character;
        }
        return new TokenImpl(startLine, startCharacter, line, character, currentToken);
    };

    @Override
    public void verify(ITokenizer tokenizer, ICore core) {
        Token t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase("submodules")) {
            errorFunction(core, "submodules", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase(":")) {
            errorFunction(core, ":", t.getTokenString());
        }
        //next token is the first submodule path
        t = tokenizer.getNextToken(tokenizeFunction);
        while(!t.getTokenString().equalsIgnoreCase("end")) {
            if(t.getTokenString().equalsIgnoreCase("")) {
                errorFunctionUnexpectedEnd(core);
            }
            String path = core.getPath();
            path += "\\";
            path += t.getTokenString();
            path += "\\build.module";
            if(!CoreFunctions.fileExists(path)) {
                System.out.print("Cannot find the module: ");
                System.out.print(t.getTokenString());
                System.out.print(" with the path: ");
                System.out.println(path);
                core.error();
            }
            core.addSubmodule(path);
            t = tokenizer.getNextToken(tokenizeFunction);
        }
        //skip end
        tokenizer.getNextToken();

    }
}
