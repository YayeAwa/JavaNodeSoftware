package org.vamdc.tapservice.vss2.impl4;

import java.util.Collection;
import java.util.List;

import org.vamdc.dictionary.Requestable;
import org.vamdc.dictionary.Restrictable;
import org.vamdc.dictionary.VSSPrefix;
import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.Prefix;
import org.vamdc.tapservice.vss2.Query;
import org.vamdc.tapservice.vss2.RestrictExpression;

public class QueryImpl4 implements Query{

	public QueryImpl4(String query) {
		
	}

	@Override
	public boolean checkSelectBranch(Requestable branch) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Prefix> getPrefixes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RestrictExpression> getRestrictsList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LogicNode getRestrictsTree() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LogicNode getFilteredTree(Collection<Restrictable> allowedKeywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LogicNode getPrefixedTree(VSSPrefix prefix, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
