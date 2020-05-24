package com.Isles.BuildSystem.Interpreter;

import com.Isles.BuildSystem.ApplicationCore.*;
import com.Isles.BuildSystem.Container.IRuleContainer;
import com.Isles.BuildSystem.Container.RuleContainer;

import java.text.CharacterIterator;

public class RuleInterpreter implements IInterpreter {

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

    RuleInterpreterInterface ruleInterpreter = ((ICore core, String code) -> {
        Tokenizer tokenizer = new Tokenizer(code);
        Token t = tokenizer.getNextToken();

        if(t.getTokenString().equalsIgnoreCase("dependency")){
            while(!t.getTokenString().equalsIgnoreCase(":")) {
                /* evaluate expressions, stop execution when a single one is not satisfied */
                if (!evaluateCondition(tokenizer, core)) {
                    return;
                }
                t = tokenizer.getNextToken();
            }
        }
        /* skip : */
        t = tokenizer.getNextToken();

        while(t.getTokenString().equalsIgnoreCase("end")){
            core.subinterpret(t.getTokenString(), tokenizer);
            t = tokenizer.getNextToken();
        }

    });

    private boolean evaluateCondition(Tokenizer tokenizer, ICore core) {
        boolean result = false;

        String arg0;
        String arg1;
        String operator;

        Token t = tokenizer.getNextToken();

        if(CoreFunctions.isBultinFunction(t.getTokenString())) {
            String functionName = t.getTokenString();
            t = tokenizer.getNextToken();
            String parameters = "";
            /* t now points at a token containing "(" */
            while(!t.getTokenString().equalsIgnoreCase(")")){
                parameters += t.getTokenString();
                t = tokenizer.getNextToken();
            }
            arg0 = CoreFunctions.executeBuiltinFunction(core, functionName, parameters).get(0);
        }else if(core.isVariable(t.getTokenString())){
            arg0 = core.getAttribute(t.getTokenString()).getValue().get(0);
        }else{
            /* must be variable */
            arg0 = t.getTokenString();
        }

        t = tokenizer.getNextToken();
        operator = t.getTokenString();

        t = tokenizer.getNextToken();

        if(CoreFunctions.isBultinFunction(t.getTokenString())) {
            String functionName = t.getTokenString();
            t = tokenizer.getNextToken();
            String parameters = "";
            /* t now points at a token containing "(" */
            while(!t.getTokenString().equalsIgnoreCase(")")){
                parameters += t.getTokenString();
                t = tokenizer.getNextToken();
            }
            arg1 = CoreFunctions.executeBuiltinFunction(core, functionName, parameters).get(0);
        }else if(core.isVariable(t.getTokenString())){
            arg1 = core.getAttribute(t.getTokenString()).getValue().get(0);
        }else{
            /* must be variable */
            arg1 = t.getTokenString();
        }

        /* evaluate the condition */
        switch (operator){
            case "==":
                /* == and != must be string compares because true and false cannot be translate to long like this */
                result = arg0.equals(arg1);
                break;
            case "!=":
                result = !arg0.equals(arg1);
                break;
            case "<":
                result = Long.getLong(arg0) < Long.getLong(arg1);
                break;
            case "<=":
                result = Long.getLong(arg0) <= Long.getLong(arg1);
                break;
            case ">":
                result = Long.getLong(arg0) > Long.getLong(arg1);
                break;
            case ">=":
                result = Long.getLong(arg0) >= Long.getLong(arg1);
                break;
            default:
                result = false;
        }

        return result;

    }

    public void initialise() {
        Registry.getInstance().registerRuleInterpreterFunction(ruleInterpreter);
    }

    @Override
    public void interpret(ITokenizer tokenizer, ICore core) {
        Token t = tokenizer.getNextToken();
        String code = "";
        int nesting = 1;
        /* skip rule keyword*/
        t = tokenizer.getNextToken();
        String ruleName = t.getTokenString();
        t = tokenizer.getNextToken();
        /* now dependency or : */
        while(nesting != 0) {
            if(t.getTokenString().equalsIgnoreCase("if")){
                ++nesting;
            }else if(t.getTokenString().equalsIgnoreCase("end")){
                --nesting;
            } else if(t.getTokenString().equalsIgnoreCase(":=") || t.getTokenString().equalsIgnoreCase("=") || t.getTokenString().equalsIgnoreCase("+=")){
                code += t.getTokenString();
                code += " ";
                t = tokenizer.getNextToken(tokenizeFunction);
                /* we must add all tokens until a ; occurs because a end could be in this string, but this is not a end token! */
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
        /* add rule to the core, will die interpreted later by the function defined above. */
        IRuleContainer r = new RuleContainer();
        r.setRule(code);
        r.setName(ruleName);
        core.addRule(r);
    }
}
