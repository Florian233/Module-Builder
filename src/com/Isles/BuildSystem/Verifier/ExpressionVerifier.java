package com.Isles.BuildSystem.Verifier;

import com.Isles.BuildSystem.ApplicationCore.*;
import org.jetbrains.annotations.NotNull;


// TODO: if parameters are added for a function, that has be checked here, but there are none (yet).
public class ExpressionVerifier implements IVerifier {
    @Override
    public void verify(@NotNull ITokenizer tokenizer,@NotNull ICore core) {
        Token t = tokenizer.getNextToken();
        tokenizer.keepPreviousElement();

        if(core.isVariable(t.getTokenString())){
            verifyVariableAssignment(tokenizer, core);
        } else if(core.isFunction(t.getTokenString())) {
            verifyCall(tokenizer, core);
        } else if(core.isRule(t.getTokenString())) {
            verifyCall(tokenizer, core);
        } else if(CoreFunctions.isBultinFunction(t.getTokenString())) {
            verifyBuiltinFunctionCall(tokenizer, core);
        } else if(core.isSubmodule(t.getTokenString())){
            verifySubmoduleCall(tokenizer, core);
        } else {
            System.out.println("Error: Cannot evaluate the expression, unknown identifier or keyword:"+t.getTokenString());
            core.error();
        }
        tokenizer.keepPreviousElement();
    }


    private void verifyVariableAssignment(ITokenizer tokenizer, ICore core){
        Token t = tokenizer.getNextToken();
        String variableName = t.getTokenString();
        //check if var is constant, cannot be manipulated
        if(core.getAttribute(variableName).isConst()){
            System.out.println("Error: Trying to manipulate a constant.");
            core.error();
        }
        t = tokenizer.getNextToken();
        String operation = t.getTokenString();
        if(!operation.equalsIgnoreCase(":=") && !operation.equalsIgnoreCase("=") && !operation.equalsIgnoreCase("+=")){
            System.out.println("Error: Unknown operation " + operation);
            core.error();
        }
        /* if the operation is "=" the attribute container has to be set to const */
        if(operation.equalsIgnoreCase("=")){
            core.getAttribute(variableName).setConst(true);
        }

        t = tokenizer.getNextToken();
        boolean empty = true;
        while(!t.getTokenString().equalsIgnoreCase(";")){
            empty = false;
            if(CoreFunctions.isBultinFunction(t.getTokenString())) {
                tokenizer.keepPreviousElement();
                verifyBuiltinFunctionCall(tokenizer, core);
                break;
            }else if(CoreFunctions.isKeyword(t.getTokenString())) {
                CoreFunctions.errorFunctionForbiddenElement(core, t.getTokenString());
            }else if(core.isVariable(t.getTokenString())){
                break;
            }else if(core.isFunction(t.getTokenString())){
                tokenizer.keepPreviousElement();
                verifyCall(tokenizer,core);
                break;
            }else if(core.isSubmodule(t.getTokenString())){
                String submoduleName = t.getTokenString();
                t = tokenizer.getNextToken();
                if(!t.getTokenString().equalsIgnoreCase(".")){
                    CoreFunctions.errorFunction(core, ".", t.getTokenString());
                }
                t = tokenizer.getNextToken();
                if(!t.getTokenString().equalsIgnoreCase("interface")){
                    System.out.println("Only the interface of a module can be assigned to a variable!");
                    core.error();
                }
                //interface can just be skipped, is correct
            }
            /* just a string, can be skipped here, should be okay, it checks already for keywords above */
            t = tokenizer.getNextToken();
        }
        if(!t.getTokenString().equalsIgnoreCase(";")){
            /* expression should have ended here, but it doesn't terminate. */
            CoreFunctions.errorFunction(core, ";", t.getTokenString());
        }
        if(empty){
            //operation is followed directly by a ;
            if(operation.equalsIgnoreCase(":=")){
                core.getAttribute(variableName).getValue().clear();
            } else if(t.getTokenString().equalsIgnoreCase("=")){
                System.out.println("Error: Cannot clear constant.");
                core.error();
            }
            //+= can be ignored, just does nothing, means "add nothing"
        }
        //skip ;
        tokenizer.getNextToken();
    }

    private void verifyCall(ITokenizer tokenizer, ICore core){
        Token t = tokenizer.getNextToken();
        String name = t.getTokenString();
        t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase("(")){
            CoreFunctions.errorFunction(core,"(", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase(")")){
            CoreFunctions.errorFunction(core, ")", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase(";")){
            CoreFunctions.errorFunction(core,";",t.getTokenString());
        }
        //skip ;
        tokenizer.getNextToken();

    }

    private void checkNumberOfArguments(ITokenizer tokenizer, ICore core, int numberOfArguments){
        Token t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase("(")){
            CoreFunctions.errorFunction(core, "(", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        for(int i = numberOfArguments; i > 0; --i){
            if(!core.isVariable(t.getTokenString())){
                System.out.println("Input of an built-in function can only be a variable!");
                core.error();
            }
            t = tokenizer.getNextToken();
            if(i != 1){
                /* last argument, so no , afterwards. */
                if(!t.getTokenString().equalsIgnoreCase(",")){
                    CoreFunctions.errorFunction(core, ",", t.getTokenString());
                }
                t = tokenizer.getNextToken();
            }
        }
        if(!t.getTokenString().equalsIgnoreCase(")")){
            CoreFunctions.errorFunction(core, ")", t.getTokenString());
        }
        tokenizer.getNextToken();
    }

    private void verifyBuiltinFunctionCall(ITokenizer tokenizer, ICore core){
        Token t = tokenizer.getNextToken();
        if(t.getTokenString().equalsIgnoreCase("concat")){
            checkNumberOfArguments(tokenizer, core, 2);
        }else if(t.getTokenString().equalsIgnoreCase("split")){
            checkNumberOfArguments(tokenizer, core, 2);
        }else if(t.getTokenString().equalsIgnoreCase("replace")){
            checkNumberOfArguments(tokenizer, core, 2);
        }else if(t.getTokenString().equalsIgnoreCase("substring")){
            checkNumberOfArguments(tokenizer, core, 3);
        }else if(t.getTokenString().equalsIgnoreCase("indexof")){
            checkNumberOfArguments(tokenizer, core, 2);
        }else if(t.getTokenString().equalsIgnoreCase("timestamp")){
            checkNumberOfArguments(tokenizer, core, 1);
        }else if(t.getTokenString().equalsIgnoreCase("size")){
            checkNumberOfArguments(tokenizer, core, 1);
        }else if(t.getTokenString().equalsIgnoreCase("deletefile")){
            checkNumberOfArguments(tokenizer, core, 1);
        }else if(t.getTokenString().equalsIgnoreCase("createfile")){
            checkNumberOfArguments(tokenizer, core, 1);
        }else if(t.getTokenString().equalsIgnoreCase("deletedir")){
            checkNumberOfArguments(tokenizer, core, 1);
        }else if(t.getTokenString().equalsIgnoreCase("createdir")){
            checkNumberOfArguments(tokenizer, core, 1);
        }else if(t.getTokenString().equalsIgnoreCase("print")){
            checkNumberOfArguments(tokenizer, core, 1);
        }else if(t.getTokenString().equalsIgnoreCase("execute")){
            /* this function can have one or two arguments, hence it is checked here. */
            t = tokenizer.getNextToken();
            if(!t.getTokenString().equalsIgnoreCase("(")){
                CoreFunctions.errorFunction(core, "(", t.getTokenString());
            }
            t = tokenizer.getNextToken();
            if(!core.isVariable(t.getTokenString())){
                System.out.println("Input of an built-in function can only be a variable!");
                core.error();
            }
            t = tokenizer.getNextToken();
            if(t.getTokenString().equalsIgnoreCase(",")) {
                t = tokenizer.getNextToken();
                if(!core.isVariable(t.getTokenString())){
                    System.out.println("Input of an built-in function can only be a variable!");
                    core.error();
                }
                t = tokenizer.getNextToken();
            }
            if(!t.getTokenString().equalsIgnoreCase(")")){
                CoreFunctions.errorFunction(core, ")", t.getTokenString());
            }
            tokenizer.getNextToken();
        }else if(t.getTokenString().equalsIgnoreCase("path")){
            checkNumberOfArguments(tokenizer, core, 0);
        }else if(t.getTokenString().equalsIgnoreCase("modulename")){
            checkNumberOfArguments(tokenizer, core, 1);
        }else if(t.getTokenString().equalsIgnoreCase("movefile")){
            checkNumberOfArguments(tokenizer, core, 2);
        }else if(t.getTokenString().equalsIgnoreCase("copyfile")){
            checkNumberOfArguments(tokenizer, core, 2);
        }else if(t.getTokenString().equalsIgnoreCase("direxists")){
            checkNumberOfArguments(tokenizer, core, 1);
        }else if(t.getTokenString().equalsIgnoreCase("fileexists")){
            checkNumberOfArguments(tokenizer, core, 1);
        }else if(t.getTokenString().equalsIgnoreCase("MODULEINTERFACE")){
            checkNumberOfArguments(tokenizer, core, 1);
        }

        if(!t.getTokenString().equalsIgnoreCase(";")){
            CoreFunctions.errorFunction(core,";",t.getTokenString());
        }
        tokenizer.getNextToken();
    }

    private void verifySubmoduleCall(ITokenizer tokenizer, ICore core){
        Token t = tokenizer.getNextToken();
        //t should contain now the submodule name
        String submoduleName = t.getTokenString();
        t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase(".")){
            CoreFunctions.errorFunction(core, ".", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        String ruleName = t.getTokenString();
        ICore submoduleCore = core.getSubmodule(submoduleName);
        if(!submoduleCore.isRule(ruleName)){
            System.out.println("Error: Cannot find the rule "+ruleName+" of the module "+submoduleName+".");
            core.error();
        }
        verifyCall(tokenizer,core);
    }
}
