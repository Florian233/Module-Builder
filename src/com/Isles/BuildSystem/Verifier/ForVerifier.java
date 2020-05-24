package com.Isles.BuildSystem.Verifier;

import com.Isles.BuildSystem.ApplicationCore.*;
import com.Isles.BuildSystem.Container.AttributeContainer;
import com.Isles.BuildSystem.Container.IAttributeContainer;

public class ForVerifier implements IVerifier {
	@Override
	public void verify(ITokenizer tokenizer, ICore core) {
		Token t = tokenizer.getNextToken();
		if(!t.getTokenString().equalsIgnoreCase("for")) {
			CoreFunctions.errorFunction(core, "for", t.getTokenString());
		}
		t = tokenizer.getNextToken();
		if(!t.getTokenString().equalsIgnoreCase("var")){
			CoreFunctions.errorFunction(core, "var", t.getTokenString());
		}
		t = tokenizer.getNextToken();
		//now we process the iterator name
		String iteratorName = t.getTokenString();
		if(core.isVariable(iteratorName)) {
			System.out.println("Iterator name "+iteratorName+" already exists.");
			core.error();
		}
		t = tokenizer.getNextToken();
		if(!t.getTokenString().equalsIgnoreCase("in")) {
			CoreFunctions.errorFunction(core, "in", t.getTokenString());
		}
		t = tokenizer.getNextToken();
		String collectionName = t.getTokenString();
		if(!core.isVariable(collectionName)) {
			System.out.println("Variable "+collectionName+" doesn't exist.");
			core.error();
		}
		//just set a dummy, sufficient for verification
		IAttributeContainer<String> iterator = new AttributeContainer<>();
		iterator.setConst(true);
		core.setTemporaryAttribute(iteratorName, iterator);
		t = tokenizer.getNextToken();
		if(!t.getTokenString().equalsIgnoreCase(":")){
			CoreFunctions.errorFunction(core, ":", t.getTokenString());
		}
		t = tokenizer.getNextToken();
		while(!t.getTokenString().equalsIgnoreCase("end")){
			tokenizer.keepPreviousElement();
			core.subverify(t.getTokenString());
			t = tokenizer.getNextToken();
			// the iteratorelement is irrelevant for verification, thus is can remain a dummy
		}
		//skip end
		tokenizer.getNextToken();
	}
}
