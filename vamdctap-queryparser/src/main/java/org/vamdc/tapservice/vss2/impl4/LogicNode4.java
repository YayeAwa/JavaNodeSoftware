package org.vamdc.tapservice.vss2.impl4;

import java.util.ArrayList;

import org.vamdc.tapservice.vss2.LogicNode;
import org.vamdc.tapservice.vss2.impl.LogicNodeImpl;

public class LogicNode4 extends LogicNodeImpl{

	public LogicNode4(Operator operation) {
		super(operation);
	}
	
	public LogicNode4(ArrayList<Object> children){
		Operator operation = Operator.AND;
		for (Object child:children){
			if (child instanceof Operator 
					&& (Operator.ADD.equals(child) 
							|| Operator.NOT.equals(child)
							|| Operator.OR.equals(child))){
				operation = (Operator) child;
				break;
			}	
		}
		this.setOperator(operation);
		
		for (Object child:children){
			if (child instanceof LogicNodeImpl && ((LogicNode)child).getOperator()==this.getOperator())
				for (LogicNode subchild:((LogicNodeImpl)child).getValues())
					this.addChild(subchild);
		
			else if (child instanceof LogicNode)
				this.addChild((LogicNode)child);
			
				
		}
	}

}
