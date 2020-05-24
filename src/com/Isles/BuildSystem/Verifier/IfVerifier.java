package com.Isles.BuildSystem.Verifier;

import com.Isles.BuildSystem.ApplicationCore.*;

public class IfVerifier implements IVerifier {
    @Override
    public void verify(ITokenizer tokenizer, ICore core) {
        Token t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase("if")) {
            CoreFunctions.errorFunction(core,"if",t.getTokenString());
        }
        t = tokenizer.getNextToken();
        /* first part of condition */
	    if(!core.isVariable(t.getTokenString())){
	    	   System.out.println("Condition in an if-statement has to be a variable.");
	    	   core.error();
	    }
        t = tokenizer.getNextToken();
        /* comparator */
        switch (t.getTokenString()){
            case "!=":
            case "==":
            case "<":
            case ">":
            case "<=":
            case ">=":
                break;
            default:
                CoreFunctions.errorFunction(core,"comparator",t.getTokenString());
        }
        t = tokenizer.getNextToken();
        /* second part of the condition */
	    if(!core.isVariable(t.getTokenString())){
		    System.out.println("Condition in an if-statement has to be a variable.");
		    core.error();
	    }
        t = tokenizer.getNextToken();
        if(!t.getTokenString().equalsIgnoreCase("then")){
            CoreFunctions.errorFunction(core,"then",t.getTokenString());
        }
        t = tokenizer.getNextToken();
        while(!t.getTokenString().equalsIgnoreCase("end") && !t.getTokenString().equalsIgnoreCase("else")) {
          tokenizer.keepPreviousElement();
        	core.subverify(t.getTokenString());
        	t = tokenizer.getNextToken();
        }
        if(t.getTokenString().equalsIgnoreCase("else")) {
        	   //skip else
		   t = tokenizer.getNextToken();
		   while(!t.getTokenString().equalsIgnoreCase("end")) {
			   tokenizer.keepPreviousElement();
			   core.subverify(t.getTokenString());
			   t = tokenizer.getNextToken();
		   }
        }
        //skip end
	    tokenizer.getNextToken();
    }
}
