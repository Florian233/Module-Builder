package com.Isles.BuildSystem.Interpreter;

import com.Isles.BuildSystem.ApplicationCore.*;

import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.List;

import static com.Isles.BuildSystem.ApplicationCore.CoreFunctions.executeBuiltinFunction;


public class ExpressionInterpreter implements IInterpreter {

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
        tokenizer.keepPreviousElement();

        if(core.isVariable(t.getTokenString())){
            interpretVariableAssignment(tokenizer, core);
        } else if(core.isFunction(t.getTokenString())) {
            interpretCall(tokenizer, core);
        } else if(core.isRule(t.getTokenString())) {
            interpretCall(tokenizer, core);
        } else if(CoreFunctions.isBultinFunction(t.getTokenString())) {
            interpretBuiltinFunctionCall(tokenizer, core);
        } else if(core.isSubmodule(t.getTokenString())){
            interpretSubmoduleCall(tokenizer, core);
        } else {
            /* Shouldn't exist, verifier already paniced in this case */
            core.error();
        }
        tokenizer.keepPreviousElement();
    }

    private void interpretSubmoduleCall(ITokenizer tokenizer, ICore core) {
        Token t = tokenizer.getNextToken();
        String submoduleName = t.getTokenString();
        t = tokenizer.getNextToken();
        /* skip . */
        t = tokenizer.getNextToken();
        String submoduleRule = t.getTokenString();
        t = tokenizer.getNextToken();
        /* skip ( */
        t = tokenizer.getNextToken();
        /* skip ) */
        t = tokenizer.getNextToken();
        /* skip ; */
        t = tokenizer.getNextToken();

        /* now call the submodule */
        core.callSubmoduleRule(submoduleName, submoduleRule);
    }

    private List<String> interpretBuiltinFunctionCall(ITokenizer tokenizer, ICore core) {
        Token t = tokenizer.getNextToken();
        String functionName = t.getTokenString();
        t = tokenizer.getNextToken();
        /* skip ( */
        t = tokenizer.getNextToken();
        String parameters = "";
        while(!t.getTokenString().equalsIgnoreCase(")")) {
            parameters += t.getTokenString();
        }
        /* skip ) */
        t = tokenizer.getNextToken();
        /* skip ; */
        t = tokenizer.getNextToken();

        /* now call the function */
        return executeBuiltinFunction(core, functionName, parameters);
    }

    private void interpretCall(ITokenizer tokenizer, ICore core) {
        Token t = tokenizer.getNextToken();
        String functionName = t.getTokenString();
        String parameters = "";
        t = tokenizer.getNextToken();
        /* skip ( */
        t = tokenizer.getNextToken();
        /* now write parameter string till ) - there are no parameters (yet) but this code can stay for now, maybe they will be added in the future  */
        while(!t.getTokenString().equalsIgnoreCase(")")){
            parameters += t.getTokenString();
            t = tokenizer.getNextToken();
        }
        /* skip ) */
        t = tokenizer.getNextToken();
        /* skip ; */
        t = tokenizer.getNextToken();

        /* now call the function */
        if(core.isRule(functionName)){
            core.callRule(functionName);
        } else if(core.isFunction(functionName)) {
            core.callFunction(functionName, parameters);
        }
    }

    private void interpretVariableAssignment(ITokenizer tokenizer, ICore core) {
        String operation;
        List<String> assignedValue = new ArrayList<>();

        Token t = tokenizer.getNextToken();
        String variableName = t.getTokenString();
        t = tokenizer.getNextToken();
        operation = t.getTokenString();
        t = tokenizer.getNextToken(tokenizeFunction);
        /* create the value list */
        while(!t.getTokenString().equalsIgnoreCase(";")){
            assignedValue.add(t.getTokenString());
            t = tokenizer.getNextToken(tokenizeFunction);
        }
        /* skip ; */
        t = tokenizer.getNextToken();

        if(assignedValue.isEmpty()){
            core.getAttribute(variableName).clear();
        }else {
            switch (operation) {
                case "=":
                    core.getAttribute(variableName).assign(assignedValue);
                    core.getAttribute(variableName).setConst(true);
                    break;
                case ":=":
                    core.getAttribute(variableName).assign(assignedValue);
                    break;
                case "+=":
                    core.getAttribute(variableName).add(assignedValue);
                    break;
                default:
                    /* this shouldn't happen because it already went through the verifier */
                    System.out.println("Found unknown operation during interpretation.");
                    core.error();
            }
        }
    }
}
