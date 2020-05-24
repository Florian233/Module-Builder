package com.Isles.BuildSystem.Interpreter;

import com.Isles.BuildSystem.ApplicationCore.*;
import com.Isles.BuildSystem.Container.FunctionContainer;
import com.Isles.BuildSystem.Container.IFunctionContainer;

import java.text.CharacterIterator;

public class FunctionInterpreter implements IInterpreter {

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
                character = 0;
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

        if(codeCharIterator.current() == ';') {
            codeCharIterator.next();
            ++character;
            return new TokenImpl(startLine, startCharacter, line, character, ";");
        }

        while (codeCharIterator.current() != CharacterIterator.DONE && codeCharIterator.current() != '\r' && codeCharIterator.current() != '\n' && codeCharIterator.current() != ';' ) {
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
                            character = 0;
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

    FunctionInterpreterInterface functionInterpreter = ((String code, String parameters, ICore core) -> {
        Tokenizer tokenizer = new Tokenizer(code);
        Token t = tokenizer.getNextToken();
        while(!t.getTokenString().equalsIgnoreCase("end")){
            core.subinterpret(t.getTokenString(),tokenizer);
            t = tokenizer.getNextToken();
        }
    });

    public void initialise() {
        Registry.getInstance().registerFunctionInterpreterFunction(functionInterpreter);
    }

    @Override
    public void interpret(ITokenizer tokenizer, ICore core) {
        Token t = tokenizer.getNextToken();
        String code = "";
        IFunctionContainer f = new FunctionContainer();
        int nesting = 1;

        /* skip function keyword */
        t = tokenizer.getNextToken();
        String functionName = t.getTokenString();
        t = tokenizer.getNextToken();
        /* now is or : */
        if(t.getTokenString().equalsIgnoreCase("is")){
            t = tokenizer.getNextToken();
            if (t.getTokenString().equalsIgnoreCase("local")){
                f.setLocal(true);
            }else if(t.getTokenString().equalsIgnoreCase("inherited")){
                /* skip inherited */
                tokenizer.getNextToken();
                /* now skip end and return */
                tokenizer.getNextToken();
                return;
            }
            t = tokenizer.getNextToken();
        }
        /* skip : */
        t = tokenizer.getNextToken();
        while(nesting != 0) {
            if(t.getTokenString().equalsIgnoreCase("if")){
                ++nesting;
            }else if(t.getTokenString().equalsIgnoreCase("end")){
                --nesting;
            }else if(t.getTokenString().equalsIgnoreCase("=") || t.getTokenString().equalsIgnoreCase("+=") || t.getTokenString().equalsIgnoreCase(":=")){
                code += t.getTokenString();
                code += " ";
                t = tokenizer.getNextToken(tokenizeFunction);
                while(!t.getTokenString().equalsIgnoreCase(";")){
                    code += t.getTokenString();
                    code += "\n";
                    t = tokenizer.getNextToken(tokenizeFunction);
                }
            }
            code += t.getTokenString();
            code += " ";
            t = tokenizer.getNextToken();
        }
        /* the end is already inserted into code and skipped by the loop */

        /* add function to the core, will die interpreted later by the function defined above. */
        f.setFunction(code);
        f.setName(functionName);
        core.addFunction(f);
    }
}
