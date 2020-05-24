package com.Isles.BuildSystem.Interpreter;

import com.Isles.BuildSystem.ApplicationCore.*;
import com.Isles.BuildSystem.Container.AttributeContainer;
import com.Isles.BuildSystem.Container.IAttributeContainer;
import org.jetbrains.annotations.NotNull;

public class ForInterpreter implements IInterpreter {
    @Override
    public void interpret(@NotNull ITokenizer tokenizer,@NotNull ICore core) {
        Token t = tokenizer.getNextToken();
        //skip for
        t = tokenizer.getNextToken();
        //skip var
        t = tokenizer.getNextToken();
        //now we process the iterator name
        String iteratorName = t.getTokenString();
        t = tokenizer.getNextToken();
        //skip in
        t = tokenizer.getNextToken();
        String collectionName = t.getTokenString();
        IAttributeContainer<String> iterator = new AttributeContainer<>();
        IAttributeContainer<String> collection = core.getAttribute(collectionName);
        int collectionSize = CoreFunctions.size(collection);
        int collectionIndex = 0;
        iterator.setConst(true);
        core.setTemporaryAttribute(iteratorName, iterator);
        t = tokenizer.getNextToken();
        //skip :
        t = tokenizer.getNextToken();
        //save code to execute it several times
        String code = "";
        while(!t.getTokenString().equalsIgnoreCase("end")){
            code += t.getTokenString();
            code += " ";
            t = tokenizer.getNextToken();
        }
        code += t.getTokenString();
        //skip end
        tokenizer.getNextToken();

        //now execute for every loopbody
        ITokenizer loopbodyTokenizer = new Tokenizer(code);
        t = loopbodyTokenizer.getNextToken();
        for (String element : collection.getValue()) {//set value for the current loop body
            iterator.getValue().clear();
            iterator.getValue().add(element);

            while (!t.getTokenString().equalsIgnoreCase("end")) {
                loopbodyTokenizer.keepPreviousElement();
                core.subinterpret(t.getTokenString());
                t = loopbodyTokenizer.getNextToken();
            }
            loopbodyTokenizer.reset();
        }
    }
}
