package com.Isles.BuildSystem.Verifier;

import com.Isles.BuildSystem.ApplicationCore.*;

import java.text.CharacterIterator;

import static com.Isles.BuildSystem.ApplicationCore.CoreFunctions.errorFunction;
import static com.Isles.BuildSystem.ApplicationCore.CoreFunctions.getmodulename;


public class InterfaceVerifier implements IVerifier {

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
            } else if (codeCharIterator.current() == '\n' ) {
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
        String moduleInterface = "";
        Token t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase("interface")) {
            errorFunction(core, "interface", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase(":")) {
            errorFunction(core, ":", t.getTokenString());
        }
        t = tokenizer.getNextToken(tokenizeFunction);
        while(!t.getTokenString().equalsIgnoreCase("end")) {
            moduleInterface = moduleInterface.concat(t.getTokenString());
            moduleInterface = moduleInterface.concat(" ");
            t = tokenizer.getNextToken(tokenizeFunction);
        }
        String path = core.getPath();
        path += "\\";
        path += moduleInterface;
        core.setInterface(getmodulename(core),path);

        //skip end
        tokenizer.getNextToken();
    }
}
