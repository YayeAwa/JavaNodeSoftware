package org.vamdc.tapservice.vss2.impl4;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import org.vamdc.dictionary.Requestable;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.dictionary.VSSPrefix;
import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.NodeFilter;
import org.vamdc.tapservice.vss2.Prefix;
import org.vamdc.tapservice.vss2.Query;
import org.vamdc.tapservice.vss2.RestrictExpression;
import org.vamdc.tapservice.vsssqlparser.VSS2Lexer;
import org.vamdc.tapservice.vsssqlparser.VSS2Parser;

public class QueryImpl4 extends VSS2ParseListener implements Query {

	private String query = "";

	public QueryImpl4(String query) {
		this.query = query;
		parse(query);
	}



	public QueryImpl4(String query, Collection<Restrictable> filter) {
		this.query = query;
		this.allowedRestrictables=filter;
		parse(query);
		
	}
	
	public QueryImpl4(String query, Collection<Restrictable> filter, boolean debug) {
		this.query = query;
		this.allowedRestrictables=filter;
		this.debug=debug;
		parse(query);
	}



	private void parse(String query) {
		ParseTree tree = getVSSParseTree(query);
		ParseTreeWalker walker = new ParseTreeWalker();
		//All the variables are initialized while walking the tree
		walker.walk(this, tree);
		//We should be OK by now.
	}
	

	@Override
	public boolean checkSelectBranch(Requestable branch) {
		return requestables.checkBranch(branch);
	}

	@Override
	public Collection<Prefix> getPrefixes() {
		return prefixes.getPrefixes();
	}

	@Override
	public List<RestrictExpression> getRestrictsList() {
		return restrictsList;
	}

	@Override
	public LogicNode getRestrictsTree() {
		return logicTree;
	}

	@Override
	public LogicNode getFilteredTree(Collection<Restrictable> allowedKeywords) {
		return NodeFilter.filterKeywords(logicTree, allowedKeywords);
	}

	@Override
	public LogicNode getPrefixedTree(VSSPrefix prefix, int index) {
		return NodeFilter.filterPrefix(logicTree, new Prefix(prefix,index));
	}

	@Override
	public String getQuery() {
		return this.query;
	}
	
	private ParseTree getVSSParseTree(String query) {
		CharStream inputCharStream = null;
		try {
			inputCharStream = new ANTLRInputStream(new StringReader(query));
		} catch (IOException e) {
		}
		VSS2Lexer lexer = new VSS2Lexer(inputCharStream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		VSS2Parser parser = new VSS2Parser(tokens);

		ParseTree tree = parser.parse();
		System.out.println(tree.toStringTree(parser));
		return tree;
	}

}
