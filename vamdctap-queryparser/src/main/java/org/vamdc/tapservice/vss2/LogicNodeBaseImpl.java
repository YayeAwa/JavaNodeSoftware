package org.vamdc.tapservice.vss2;

import java.util.ArrayList;
import java.util.Collection;



/**
 * Simple class to keep query restricts logic tree structure
 * @author doronin
 */
public class LogicNodeBaseImpl implements LogicNode{

	private ArrayList<LogicNode> children = new ArrayList<LogicNode>();
	private Operator operator;
	
	public LogicNodeBaseImpl(Operator operation){
		operator=operation;
	}
	
	public LogicNodeBaseImpl(){
	}
	
	public void addChild(LogicNode node){
		children.add(node);
	}
	
	@Override
	public String toString(){
		String result="(";
		if (children.size()==1){
			result=result.concat(operator.name());
		}
		for (int i=0;i<children.size();i++){
			result=result.concat(" "+children.get(i).toString()+" ");
			if (i<children.size()-1)
				result=result.concat(operator.name());
		}
		result=result.concat(")");
		return result;
	}

	@Override
	public Operator getOperator() {
		return operator;
	}
	
	protected void setOperator(Operator op){
		this.operator=op;
	}

	@Override
	public Object getValue() {
		if (children.size()>=1)
			return children.get(0);
		return null;
	}

	@Override
	public Collection<LogicNode> getValues() {
		return children;
	}

	@Override
	public boolean isValid() {
		return (operator!=null && children.size()>0);
	}

}
