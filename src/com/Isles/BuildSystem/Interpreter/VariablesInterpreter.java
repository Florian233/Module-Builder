package com.Isles.BuildSystem.Interpreter;

import com.Isles.BuildSystem.ApplicationCore.*;
import com.Isles.BuildSystem.Container.AttributeContainer;
import com.Isles.BuildSystem.Container.IAttributeContainer;

import java.text.CharacterIterator;

public class VariablesInterpreter implements IInterpreter {

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

    @Override
    public void interpret(ITokenizer tokenizer, ICore core) {
        Token t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase("variables")) {
            CoreFunctions.errorFunction(core, "variables", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase(":")) {
            CoreFunctions.errorFunction(core, ":", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        while(!t.getTokenString().equalsIgnoreCase("end") && !t.getTokenString().equalsIgnoreCase("")) {
            IAttributeContainer<String> container = new AttributeContainer<>();
            boolean variableWithIdentifiers = false;
            boolean addToVar = false;
            while (setIdentifier(t.getTokenString(), container)) {
                t = tokenizer.getNextToken();
                variableWithIdentifiers = true;
            }
            /* must be a name or end or empty */
            if(CoreFunctions.isKeyword(t.getTokenString())) {
                CoreFunctions.errorFunctionForbiddenElement(core, t.getTokenString());
            } else if(t.getTokenString().equalsIgnoreCase("")) {
                CoreFunctions.errorFunctionUnexpectedEnd(core);
            }
            /* must be a variable name */
            container.setName(t.getTokenString());
            t = tokenizer.getNextToken();
            /* now it must be some assignment function */
            switch (t.getTokenString()) {
                case ";":
                    /* if it is a input, set input because no other assignment is there */
                    container.getValue().add(core.getInput(container.getName()));
                    break;
                case "=":
                    container.setConst(true);
                    break;
                case ":=":
                    /* default, nothing to do */
                    break;
                case "+=":
                    addToVar = true;
                    break;
                default:
                    CoreFunctions.errorFunction(core, "assignment operation", t.getTokenString());
            }

            /* definitions shall be added to existing variable, fetch it from the core before. */
            if(!variableWithIdentifiers && core.getAttribute(container.getName()) != null && addToVar){
                container = core.getAttribute(container.getName());
            }

            t = tokenizer.getNextToken(tokenizeFunction);
            while(!t.getTokenString().equalsIgnoreCase(";")) {
                if(t.getTokenString().equalsIgnoreCase("end")){
                    CoreFunctions.errorFunctionUnexpectedEnd(core);
                } else if(CoreFunctions.isKeyword(t.getTokenString())){
                    CoreFunctions.errorFunction(core, "variable value", t.getTokenString() );
                }

                if(t.getTokenString().contains(".interface")) {
                    String tokenStr = t.getTokenString();
                    int index = tokenStr.indexOf(".interface");
                    String interfaceName = tokenStr.substring(0, index);
                    if(core.getInterface(interfaceName) == null) {
                        System.out.println("Error: Cannot find the interface for "+interfaceName);
                        core.error();
                    }
                    container.getValue().add(core.getInterface(interfaceName));
                }else if(core.isVariable(t.getTokenString())) {
                    container.getValue().addAll(core.getAttribute(t.getTokenString()).getValue());
                } else {
                    container.getValue().add(t.getTokenString());
                }
                t = tokenizer.getNextToken(tokenizeFunction);
            }
            core.setAttribute(container.getName(), container);
            if(t.getTokenString().equalsIgnoreCase(";")) {
                //skip this token because it has done its job already */
                t = tokenizer.getNextToken();
            }
        }
        //skip end
        t = tokenizer.getNextToken();
    }

    /* returns true if it was an identifier, false otherwise. */
    private boolean setIdentifier(String tokenStr, IAttributeContainer<String> container) {
        boolean returnValue = false;
        switch (tokenStr.toLowerCase()) {
            case "local":
                returnValue = true;
                container.setLocal(true);
                break;
            case "inherited":
                returnValue = true;
                container.setInherited(true);
                break;
            case "update":
                returnValue = true;
                container.setUpdateTrigger(true);
                break;
            case "input":
                returnValue = true;
                /* nothing to do here */
                break;
            case "public":
                returnValue = true;
                container.setPublicSymbol(true);
                break;
        }
        return returnValue;
    }
}
