/* 
 * Copyright (C) 2002-2016 XimpleWare, info@ximpleware.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
/*VTD-XML is protected by US patent 7133857, 7260652, an 7761459*/
package com.ximpleware.extended;
import com.ximpleware.extended.xpath.Expr;
/**
 * The parser.java uses this class to contruct the corresponding
 * AST for XPath expression when there are two operands and one
 * operator
 * 
 */





public class BinaryExpr extends Expr {
	public final static int ADD = 0;
	public final static int SUB = 1;
	public final static int MULT = 2;
	public final static int DIV = 3;
	public final static int MOD = 4;
	public final static int OR = 5;
	public final static int AND = 6;
	public final static int EQ = 7;
	public final static int NE = 8;
	public final static int LE = 9;
	public final static int GE = 10;
	public final static int LT = 11;
	public final static int GT = 12;
	//public final static int UNION = 13;

	public final static int BUF_SZ_EXP = 7; 
	protected int op;
	boolean isNumerical;
	boolean isBoolean;
	
	protected Expr left;
	protected Expr right;
	
	protected FastIntBuffer fib1;
	/**
	 * constructor
	 * @param l
	 * @param o
	 * @param r
	 */
	public BinaryExpr ( Expr l, int o, Expr r) {
		op = o;
		left = l;
		right = r;
		fib1 =  null;
		switch(op){
		 	case ADD:
			case SUB:
			case MULT:
			case DIV:
			case MOD: isNumerical = true; isBoolean = false; break;
			case OR :
			case AND:
			case EQ:
			case NE:
			case LE:
			case GE:
			case LT:
			case GT: isNumerical = false; isBoolean = true;
			default:
		}
	}
	public String toString(){
		String os;
		switch(op){
			case ADD: os = " + "; break;
			case SUB: os = " - "; break;
			case MULT: os = " * "; break;
			case DIV: os = " / "; break;
			case MOD: os = " mod "; break;
			case OR : os = " or ";break;
			case AND: os = " and "; break;
			case EQ: os = " = "; break;
			case NE: os = " != "; break;
			case LE: os = " <= "; break;
			case GE: os = " >= "; break;
			case LT: os = " < "; break;
			default: os = " > "; break;
			 
		}
		
		return "("+ left + os + right+")";
	}

	
	public boolean evalBoolean(VTDNavHuge vn){
	    /*int i,i1=0, s1, s2;
	    int stackSize;
	    Expr e1, e2;
	    int t;
	    boolean b = false;*/
		switch(op){
			case OR: return left.evalBoolean(vn) || right.evalBoolean(vn);
			case AND:return left.evalBoolean(vn) && right.evalBoolean(vn);
			case EQ:
			case NE: 		
			case LE: 
			case GE: 
			case LT: 
			case GT: return computeComp(op,vn);	
			default: double dval = evalNumber(vn);
				 if (dval ==-0.0 || dval ==+0.0 || Double.isNaN(dval))
					 return false;
				 return true;
		}
	}

	public double evalNumber(VTDNavHuge vn){
		switch(op){
			case ADD: return left.evalNumber(vn) + right.evalNumber(vn);
			case SUB: return left.evalNumber(vn) - right.evalNumber(vn);
			case MULT:return left.evalNumber(vn) * right.evalNumber(vn);
			case DIV: return left.evalNumber(vn) / right.evalNumber(vn);
			case MOD: return left.evalNumber(vn) % right.evalNumber(vn);
			default	: if (evalBoolean(vn) == true)
					  return 1;
				  return 0;

		}
	}
		
	public int evalNodeSet(VTDNavHuge vn) throws XPathEvalExceptionHuge {
		throw new XPathEvalExceptionHuge("BinaryExpr can't eval to a node set!");
	}
	
    public String evalString(VTDNavHuge vn){
		if(isNumerical()){
		    
		    double d = evalNumber(vn);
		    if (d==(long)d){
		        return ""+(long)d;
		    }
		    else 
		        return ""+d;
		} else {
		    boolean b = evalBoolean(vn);
		    if (b)
		        return "true";
		    else
		        return "false";
		}
	}

	public void reset(VTDNavHuge vn){left.reset(vn); right.reset(vn);};

	public boolean  isNodeSet(){
		return false;
	}

	public boolean  isNumerical(){
		return isNumerical;
	}
	
	public boolean isString(){
	    return false;
	}
	
	public boolean isBoolean(){
	    return isBoolean;
	}
	// to support computation of context size 
	// needs to add 
	// public boolean needContextSize();
	// public boolean SetContextSize(int contextSize);
	//If both objects to be compared are node-sets, then 
	//the comparison will be true if and only if there is 
	//a node in the first node-set and a node in the second 
	//node-set such that the result of performing the comparison 
	//on the string-values of the two nodes is true. If one 
	//object to be compared is a node-set and the other is a 
	//number, then the comparison will be true if and only if 
	//there is a node in the node-set such that the result of 
	//performing the comparison on the number to be compared and on
	//the result of converting the string-value of that node to a 
	//number using the number function is true. If one object to be 
	//compared is a node-set and the other is a string, then the 
	//comparison will be true if and only if there is a node in 
	//the node-set such that the result of performing the comparison 
	//on the string-value of the node and the other string is true. 
	//If one object to be compared is a node-set and the other is a boolean, 
	//then the comparison will be true if and only if the result of 
	//performing the comparison on the boolean and on the result of 
	//converting the node-set to a boolean using the boolean function is true.

	//When neither object to be compared is a node-set and the operator 
	//is = or !=, then the objects are compared by converting them to a 
	//common type as follows and then comparing them. If at least one object 
	//to be compared is a boolean, then each object to be compared is 
	//converted to a boolean as if by applying the boolean function. 
	//Otherwise, if at least one object to be compared is a number, then 
	//each object to be compared is converted to a number as if by applying 
	//the number function. Otherwise, both objects to be compared are 
	//converted to strings as if by applying the string function. The = 
	//comparison will be true if and only if the objects are equal; the 
	//!= comparison will be true if and only if the objects are not equal. 
	//Numbers are compared for equality according to IEEE 754 [IEEE 754]. Two 
	//booleans are equal if either both are true or both are false. Two strings 
	//are equal if and only if they consist of the same sequence of UCS characters.


	private boolean computeComp(int op, VTDNavHuge vn){
	  //int i, t, i1 = 0, stackSize, s1, s2;
        String st1, st2;
        if (left.isNodeSet() && right.isNodeSet()) {
            return compNodeSetNodeSet(left, right, vn, op);
        } else {
            if (left.isNumerical() && right.isNodeSet()){
                return compNumericalNodeSet(left, right, vn, op);
            }
            if (left.isNodeSet() && right.isNumerical()) {
                return compNodeSetNumerical(left, right, vn, op);
            }
            if (left.isString() && right.isNodeSet()){
                return compStringNodeSet(left, right, vn, op);
            }
            if (left.isNodeSet() && right.isString()) {
                return compNodeSetString(left, right, vn, op);
            }
        }
        if (op==EQ || op==NE){
            if (left.isBoolean() || right.isBoolean()) {
                if (op == EQ)
                    return left.evalBoolean(vn) == right.evalBoolean(vn);
                else
                    return left.evalBoolean(vn) != right.evalBoolean(vn);
            }

            if (left.isNumerical() || right.isNumerical()) {
                if (op == EQ)
                    return left.evalNumber(vn) == right.evalNumber(vn);
                else
                    return left.evalNumber(vn) != right.evalNumber(vn);
            }

            st1 = left.evalString(vn);
            st2 = right.evalString(vn);
            /*if (st1 == null || st2 == null)
                if (op == EQ)
                    return false;
                else
                    return true;*/

            return (op == EQ) ? (st1.equals(st2)) : (!st1.equals(st2));
        }
        return compNumbers(left.evalNumber(vn),right.evalNumber(vn),op);
        
	}
	
	public boolean requireContextSize(){
	    return left.requireContextSize() || right.requireContextSize();
	}
	
	public void setContextSize(int size){
	    left.setContextSize(size);
	    right.setContextSize(size);
	}
	public void setPosition(int pos){
	    left.setPosition(pos);
	    right.setPosition(pos);
	}
	
	// this function computes the case where one expr is a node set, the other is a string
	
	private boolean compNodeSetString(Expr left, Expr right, VTDNavHuge vn,int op){
	     int i, i1 = 0, stackSize;
	     String s;	     
	     
       try {
           s = right.evalString(vn);
           vn.push2();
           stackSize = vn.contextStack2.size;
           while ((i = left.evalNodeSet(vn)) != -1) {
               i1 = getStringVal(vn,i); 
               // if (i1==-1 && s.length()==0)
               //return true;
               if (i1 != -1) {
                   boolean b = compareVString1(i1,vn,s,op);
                   if (b){
                	   left.reset(vn);
                	   vn.contextStack2.size = stackSize;
                	   vn.pop2();
                	   return b;
                   }
               }
           }           
           vn.contextStack2.size = stackSize;
           vn.pop2();
           left.reset(vn);            
           return false; //compareEmptyNodeSet(op, s); 
       } catch (Exception e) {
           throw new RuntimeException("Undefined behavior");
       }
	}
	
	private boolean compareEmptyNodeSet(int op, String s){
	    if (op == NE ){
	        if (s.length()==0) {
	            return false;
	        } else 
	            return true;	        
	    }else{
	        if (s.length()==0) {
	            return true;
	        } else 
	            return false;
	    }	        
	}
	private boolean compStringNodeSet(Expr left, Expr right, VTDNavHuge vn,int op){
	     int i, i1 = 0, stackSize;
	     String s;
        try {
            s = left.evalString(vn);
            vn.push2();
            stackSize = vn.contextStack2.size;
            while ((i = right.evalNodeSet(vn)) != -1) {
                i1 = getStringVal(vn,i); 
                if (i1 != -1){
                    boolean b = compareVString2(i1,vn,s,op);
                    if (b){
                    	right.reset(vn);
                    	vn.contextStack2.size = stackSize;
                    	vn.pop2();
                    	return b;
                    }
                }
            }    
            vn.contextStack2.size = stackSize;
            vn.pop2();
            right.reset(vn);            
            return false;//compareEmptyNodeSet(op, s); 
        } catch (Exception e) {
            throw new RuntimeException("Undefined behavior");
        }
	}
	
	private boolean compNumbers(double d1, double d2, int op) {
        switch (op) {
        case LE:
            return d1 <= d2;
        case GE:
            return d1 >= d2;
        case LT:
            return d1 < d2;
        case GT:
            return d1 > d2;
        }
        return false;
    }
	// this function computes the boolean when one expression is node set
	// the other is numerical
	private boolean compNumericalNodeSet(Expr left, Expr right, VTDNavHuge vn, int op ){
	     int i, i1 = 0, stackSize;
	     double d;
        try {
            d = left.evalNumber(vn);
            vn.push2();
            stackSize = vn.contextStack2.size;
            while ((i = right.evalNodeSet(vn)) != -1) {
                i1 = getStringVal(vn,i); 
                if (i1!=-1 && compareVNumber1(i1,vn,d,op)){
                    right.reset(vn);
                    vn.contextStack2.size = stackSize;
                    vn.pop2();
                    return true;
                }
            }    
            vn.contextStack2.size = stackSize;
            vn.pop2();
            right.reset(vn);            
            return false; 
        } catch (Exception e) {
            throw new RuntimeException("Undefined behavior");
        }
	}
	private boolean compNodeSetNumerical(Expr left, Expr right, VTDNavHuge vn, int op ){
	     int i,i1 = 0, stackSize;
	     double d;
       try {
           d = right.evalNumber(vn);
           vn.push2();
           stackSize = vn.contextStack2.size;
           while ((i = left.evalNodeSet(vn)) != -1) {
               i1 = getStringVal(vn,i); 
               if (i1!=-1 && compareVNumber2(i1,vn,d,op)){
                   left.reset(vn);
                   vn.contextStack2.size = stackSize;
                   vn.pop2();
                   return true;
               }
           }    
           vn.contextStack2.size = stackSize;
           vn.pop2();
           left.reset(vn);            
           return false; 
       } catch (Exception e) {
           throw new RuntimeException("Undefined behavior");
       }
	}
	
	private int getStringVal(VTDNavHuge vn,int i){
        int i1,t = vn.getTokenType(i);
        if (t == VTDNavHuge.TOKEN_STARTING_TAG){
            i1 = vn.getText();
            return i1;
        }
        else if (t == VTDNavHuge.TOKEN_ATTR_NAME
                || t == VTDNavHuge.TOKEN_ATTR_NS)
        	return i+1;
        else 
            return i;
	}
	
	private boolean compareVNumber1(int k, VTDNavHuge vn, double d, int op)
	throws NavExceptionHuge {
	    double d1 = vn.parseDouble(k);
	    switch (op){
	    	case EQ:
	    	    return d == d1;
	    	case NE:
	    		return d != d1;
	    	case GE:
	    	    return d >= d1;
	    	case LE:
	    	    return d <= d1;
	    	case GT:
	    	    return d > d1;
	    	default:
	    	    return d < d1;	    	
	    }
	}
	
	private boolean compareVString1(int k, VTDNavHuge vn, String s, int op)
	throws NavExceptionHuge {
	    int i = vn.compareTokenString(k, s);
        switch (i) {
        case -1:
            if (op == NE || op == LT || op == LE) {
                return true;
            }
            break;
        case 0:
            if (op == EQ || op == LE || op == GE) {
                return true;
            }
            break;
        case 1:
            if (op == NE || op == GE || op == GT) {
                return true;
            }       
        }
        return false;
	}
	private boolean compareVString2(int k, VTDNavHuge vn, String s, int op)
	throws NavExceptionHuge {
	    int i = vn.compareTokenString(k, s);
        switch(i){        	
        	case -1:
        	    if (op== NE || op == GT || op == GE){
        	        return true;
        	    }
        	    break;
        	case 0: 
        	    if (op==EQ || op == LE || op == GE ){
        	        return true;
        	    }
        	    break;        	    
        	case 1:
        	    if (op == NE || op==LE  || op == LT ){
        	        return true;
        	    }
        }
        return false;
	}
	
	private boolean compareVNumber2(int k, VTDNavHuge vn, double d, int op)
	throws NavExceptionHuge {
	    double d1 = vn.parseDouble(k);
	    switch (op){
	    	case EQ:
	    	    return d1 == d;
	    	case NE:
	    		return d1 != d;
	    	case GE:
	    	    return d1 >= d;
	    	case LE:
	    	    return d1 <= d;
	    	case GT:
	    	    return d1 > d;
	    	default:
	    	    return d1 < d;	    	
	    }
	}
	private boolean compareVV(int k,  VTDNavHuge vn, int j,int op) 
	throws NavExceptionHuge {
	    int i = vn.compareTokens(k, vn, j);
        switch(i){        	    
        	case 1:
        	    if (op == NE || op==GE  || op == GT ){
        	        return true;
        	    }
        	    break;
        	case 0: 
        	    if (op==EQ || op == LE || op == GE ){
        	        return true;
        	    }
        	    break;
        	case -1:
        	    if (op== NE || op == LT || op == LE){
        	        return true;
        	    }
        }
        return false;
	}
	
	// this method compare node set with another node set
	private boolean compNodeSetNodeSet(Expr left, Expr right, VTDNavHuge vn, int op){
	    int i,i1,stackSize,s1; 
	    try {
	          if (fib1 == null)
	              fib1 = new FastIntBuffer(BUF_SZ_EXP);
	          vn.push2();
	          stackSize = vn.contextStack2.size;
	          while ((i = left.evalNodeSet(vn)) != -1) {
	              i1 = getStringVal(vn,i);
	              if (i1 != -1)
	              fib1.append(i1);
	          }
	          left.reset(vn);
	          vn.contextStack2.size = stackSize; 
	          vn.pop2();
	          vn.push2();
	          stackSize = vn.contextStack2.size;
	          while ((i = right.evalNodeSet(vn)) != -1) {
	              i1 = getStringVal(vn,i);
	              if (i1 != -1){
	                  s1 = fib1.size();
	                  for (int k = 0; k < s1; k++) { 
		                  boolean b = compareVV(fib1.intAt(k),vn,i1,op);
		                  if (b){
		                      fib1.clear();
		                      vn.contextStack2.size = stackSize; 
		        	          vn.pop2();
		        	          right.reset(vn);
		                      return true;
		                  }
		              }
	              }
	          }
	          vn.contextStack2.size = stackSize; 
	          vn.pop2();
	          right.reset(vn);	         
	          fib1.clear();
	          return false;
	      } catch (Exception e) {
	          fib1.clear();
	          throw new RuntimeException("Undefined behavior");
	      }
	}
	public int adjust(int n){
	    int i = left.adjust(n);
	    int j = right.adjust(n);
	    if (i>j)return i; else return j;
	}
}
