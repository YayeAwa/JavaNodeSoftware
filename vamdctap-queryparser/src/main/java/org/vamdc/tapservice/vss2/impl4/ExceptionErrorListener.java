package org.vamdc.tapservice.vss2.impl4;

import java.util.BitSet;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class ExceptionErrorListener extends BaseErrorListener {
	private boolean debug=false;
	
	ExceptionErrorListener(){
		
	}
	
	ExceptionErrorListener(boolean debug){
		this.debug=debug;
	}
	
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
							Object offendingSymbol,
							int line,
							int charPositionInLine,
							String msg,
							RecognitionException e)
	{
		throw new IllegalArgumentException(e);
	}
	
	@Override
	public void reportAmbiguity(Parser recognizer,
								DFA dfa,
								int startIndex,
								int stopIndex,
								boolean exact,
								BitSet ambigAlts,
								ATNConfigSet configs)
	{
		if (debug) System.out.println("ambiguity between "+startIndex+" and "+stopIndex);
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer,
											DFA dfa,
											int startIndex,
											int stopIndex,
											BitSet conflictingAlts,
											ATNConfigSet configs)
	{
		if (debug) System.out.println("FullContext between "+startIndex+" and "+stopIndex);
	}

	@Override
	public void reportContextSensitivity(Parser recognizer,
										 DFA dfa,
										 int startIndex,
										 int stopIndex,
										 int prediction,
										 ATNConfigSet configs)
	{
		if (debug) System.out.println("ContextSens between "+startIndex+" and "+stopIndex);
	}
	

}
