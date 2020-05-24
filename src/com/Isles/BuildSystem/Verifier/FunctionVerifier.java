package com.Isles.BuildSystem.Verifier;

import com.Isles.BuildSystem.ApplicationCore.*;
import com.Isles.BuildSystem.Container.FunctionContainer;
import org.jetbrains.annotations.NotNull;

import static com.Isles.BuildSystem.ApplicationCore.CoreFunctions.errorFunctionForbiddenElement;
import static com.Isles.BuildSystem.ApplicationCore.CoreFunctions.errorFunctionUnexpectedEnd;

//TODO: If parameters shall be used, the function also has to set the number of arguments in the container to check the number in the expression verifier
public class FunctionVerifier implements IVerifier {
    @Override
    public void verify(@NotNull ITokenizer tokenizer,@NotNull ICore core) {

        System.out.println("Function verifier.");

        boolean isInherited = false;
        boolean isPublic = false;
        boolean isLocal = false;
        String name;
        FunctionContainer functionContainer = new FunctionContainer();

        Token t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase("function")) {
            CoreFunctions.errorFunction(core, "function", t.getTokenString());
        }
        t = tokenizer.getNextToken();
        //t now contains the name
        functionContainer.setName(t.getTokenString());
        t = tokenizer.getNextToken();
        /* test if it is local */
        if(t.getTokenString().equalsIgnoreCase("is")){
            t = tokenizer.getNextToken();
            if(t.getTokenString().equalsIgnoreCase("local")){
                functionContainer.setLocal(true);
            }else if(t.getTokenString().equalsIgnoreCase("public")){
                functionContainer.setPublicSymbol(true);
            }else if(t.getTokenString().equalsIgnoreCase("inherited")){
                isInherited = true;
            }else{
                CoreFunctions.errorFunction(core,"local|public|inherited",t.getTokenString());
            }
            t = tokenizer.getNextToken();

            /* test if it is just a declaration, that can be ignored. */
            if(t.getTokenString().equalsIgnoreCase("end") && isInherited){
                //see if this function exists
                if(!core.isFunction(functionContainer.getName())){
                    System.out.println("Function "+functionContainer.getName()+" cannot be inherited, no public definition found!");
                    core.error();
                }
                //skip end and return
                tokenizer.getNextToken();
                return;
            }else if(isInherited){
                System.out.println("Functions declared as inherited cannot have a body. They are extern declarations!");
                CoreFunctions.errorFunction(core,"end", t.getTokenString());
            }
        }

        if(!t.getTokenString().equalsIgnoreCase(":")){
            CoreFunctions.errorFunction(core, ":", t.getTokenString());
        }

        t = tokenizer.getNextToken();

        while(!t.getTokenString().equalsIgnoreCase("end")){
            if(t.getTokenString().equals("")) {
                errorFunctionUnexpectedEnd(core);
            } else if(t.getTokenString().equals("begin")) {
                errorFunctionForbiddenElement(core, t.getTokenString());
            }
            tokenizer.keepPreviousElement();
            core.subverify(t.getTokenString());
            t = tokenizer.getNextToken();
        }
        //skip end
        tokenizer.getNextToken();

        if(!isInherited) {
            core.addFunction(functionContainer);
        }

    }
}
