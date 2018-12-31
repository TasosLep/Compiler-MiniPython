import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class Visitor1 extends DepthFirstAdapter 
{
	private Hashtable symtable;
	private Hashtable symtable_var;
	private Error error;
	private Hashtable expressionType;

	Visitor1(Hashtable symtable, Hashtable symtable_var) 
	{
		error = Error.getInstance();
		this.symtable = symtable;
		this.symtable_var = symtable_var;
		expressionType = new Hashtable();
	}
	
	//Find out if this variable(identifier) has been initialized (exists)
	public void inAEqualsStatement(AEqualsStatement node)
    {
		boolean errorOccurred = false;
        String vName = node.getId().toString();
		int line = ((TId) node.getId()).getLine();
		symtable_var.put(vName, node);
    }
	
	public void outAEqualsStatement(AEqualsStatement node)
	{
		boolean errorOccurred = false;
        String vName = node.getId().toString();
		int line = ((TId) node.getId()).getLine();
		PValue v = getValue(node.getExpression());
		//System.out.println(vName + " = " + v);
		// If the assignment expression did not produce any errors
		if (v != null)
		{
			expressionType.put(vName, v);
		}
	}
	
	private PValue getValue(PExpression exp)
	{
		boolean errorOccurred = false;
		PValue v = null;
		
		if (exp instanceof AValueExpression)
		{
			v = ((AValueExpression)exp).getValue();
			//System.out.println("l is a value");
		}else if (exp instanceof AIdExpression)
		{
			String vName = ((AIdExpression)exp).getId().toString();
			v = (PValue)expressionType.get(((AIdExpression)exp).getId().toString());
			/*if (v instanceof AStringValue)
				System.out.println(vName + " = StringValue");
			else
				System.out.println(vName + " = NumberValue");*/
		}else
		{
			v = (PValue)expressionType.get(exp.toString());
		}
		
		return v;
	}
	
	private boolean throwError(PValue lv, PValue rv, String op)
	{
		boolean errorOccurred = false;
		
		if (lv instanceof ANumberValue && rv instanceof AStringValue && !op.equals("*"))
		{
			errorOccurred = error.printError("Line " /*+ line*/ + ": " +"  " + "unsupported operand type(s) for "+ op + ": 'number' and 'str'", "add1");
		}else if (lv instanceof AStringValue && rv instanceof ANumberValue && !op.equals("*"))
		{
			errorOccurred = error.printError("Line " /*+ line*/+ ": " +"  " + "unsupported operand type(s) for "+ op + ": 'str' and 'number'", "add2");
		}else if (lv instanceof AStringValue && rv instanceof AStringValue && !op.equals("+"))
		{
			errorOccurred = error.printError("Line " /*+ line*/+ ": " +"  " + "unsupported operand type(s) for "+ op + ": 'str' and 'str'", "add3");
		}
		return errorOccurred;
	}
	
	private boolean checkValues(PExpression mainExp, PExpression l, PExpression r, String op)
	{
		boolean errorOccurred = false;
		PValue lv = null, rv = null;
		
		if (l instanceof AValueExpression && r instanceof AValueExpression)
		{
			lv = ((AValueExpression)l).getValue();
			rv = ((AValueExpression)r).getValue();
			errorOccurred = throwError(lv,rv, op);
			if (!errorOccurred)
					expressionType.put(mainExp.toString(), new ANumberValue());
		}else
		{
			// find the type of each operant
			lv = getValue(l);
			
			rv = getValue(r);
				
			//System.out.println(rv + " " + lv);
			if (lv != null && rv != null)
			{
				errorOccurred = throwError(lv,rv, op);
				if (!errorOccurred)
					expressionType.put(mainExp.toString(), new ANumberValue());
			}
		}
		
		return errorOccurred;
	}
	
	public void inAForStatement(AForStatement node)
    {
		boolean errorOccurred = false;
		String vName = node.getRightId().toString();
		int line = ((TId) node.getRightId()).getLine();
		if (!node.getRightId().getText().equals(node.getLeftId().getText()) && !symtable_var.containsKey(vName))
		{
			errorOccurred = error.printError("Line " + line + ": " +" Variable " + vName + "is not defined in this scope", "aek88");
		}
    }
	
	public void outAAddExpression(AAddExpression node)
    {
        boolean errorOccurred = false;
		String vName = node.getL().toString();
		//int line = ((TId) node.getL()).getLine();
		PExpression l = null, r = null;
		l = node.getL();
		r = node.getR();
		checkValues(node,l,r,"+");	
    }
	
	public void outAMultExpression(AMultExpression node)
	{
		boolean errorOccurred = false;
		String vName = node.getL().toString();
		//int line = ((TId) node.getL()).getLine();
		PExpression l = null, r = null;
		l = node.getL();
		r = node.getR();
		checkValues(node,l,r,"*");
	}
	
	public void outADivExpression(ADivExpression node)
	{
		boolean errorOccurred = false;
		String vName = node.getL().toString();
		//int line = ((TId) node.getL()).getLine();
		PExpression l = null, r = null;
		l = node.getL();
		r = node.getR();
		checkValues(node,l,r,"/");
	}
	
	public void outAPowExpression(APowExpression node)
	{
		//System.out.println(node);
		boolean errorOccurred = false;
		String vName = node.getL().toString();
		//int line = ((TId) node.getL()).getLine();
		PExpression l = null, r = null;
		l = node.getL();
		r = node.getR();
		checkValues(node,l,r,"**");
	}
	
	public void outASubExpression(ASubExpression node)
	{
		boolean errorOccurred = false;
		String vName = node.getL().toString();
		//int line = ((TId) node.getL()).getLine();
		PExpression l = null, r = null;
		l = node.getL();
		r = node.getR();
		checkValues(node,l,r,"-");
	}
	
	public void outAParExpExpression(AParExpExpression node)
    {
		//System.out.println("start out parExp " + node);
        PExpression exp = node.getExpression();
		//System.out.println("pepepepe");
		PValue expVal = (PValue)expressionType.get(exp.toString());
		//System.out.println("poopopopopop");
		//System.out.println(expVal == null ? "NULL EXPVAL" : "NOT NULL EXPVAL");
		if (expVal != null)
			expressionType.put(node.toString(), expVal);
		//System.out.println("end of out parExp");
    }

	public void inAIdExpression(AIdExpression node)
    {
		boolean errorOccurred = false;
        String vName = node.getId().toString();
		int line = ((TId) node.getId()).getLine();
		
		//getting return statement parameters
		Node parent = node.parent();	

		while (parent != null && !(parent instanceof AReturnStatement) && !(parent instanceof AForStatement) && !(parent instanceof AFunction)){
			//System.out.println(vName + " " + parent.getClass());
			
			parent = parent.parent();
		}
			
		if(parent instanceof AReturnStatement || parent instanceof AFunction)
		{
			while (parent != null && (!(parent instanceof AFunction))){
				parent = parent.parent();
			}
			if(parent != null)
			{
				AFunction func = (AFunction) parent;
				LinkedList func_args = func.getArgument();
				if (func_args.size() != 0)
				{
					AArgument arg = (AArgument) func_args.get(0);
					// check if the current id is the first argument
					if (!node.getId().getText().equals(arg.getId().getText()))
					{
						LinkedList cids = arg.getCommaid();
						boolean found = false;
						for(int i = 0; i < cids.size(); ++i)
						{
							ACommaid cid = (ACommaid)cids.get(i);
							if (node.getId().getText().equals(cid.getId().getText()))
							{
								found = true;
								break;
							}
						}
						if (!found)
						{
							// we are sure that the id is defined inside the scope of the function
							if (!symtable_var.containsKey(vName))
							{
								String fName = func.getId().getText();
								errorOccurred = error.printError("Line " + line + ": " +" Variable " + vName + "is not defined in the scope of function " + fName, "aek66");
								return;
							}
						}
					}

				}else
				{
					// we are sure that the id is defined inside the scope of the function
					if (!symtable_var.containsKey(vName))
					{
						String fName = func.getId().getText();
						errorOccurred = error.printError("Line " + line + ": " +" Variable " + vName + "is not defined in the scope of function " + fName, "aek661");
						return;
					}
					//String fName = func.getId().getText();
					//errorOccurred = error.printError("Line " + line + ": " +" Variable " + vName + "is not defined in the scope of function " + fName, "aek666");
					//return;
				}
			}
			return;
			
		}else if(parent instanceof AForStatement)
		{
			//System.out.println("instanceof for");
			if(parent != null)
			{
				AForStatement fors = (AForStatement) parent;
				//System.out.println(fors.getLeftId().getText());
				if (!fors.getLeftId().getText().equals(node.getId().getText()))
				{
					if (!symtable_var.containsKey(vName))
					{
						errorOccurred = error.printError("Line " + line + ": " +" Variable " + vName + "is not defined in the scope of the for statement", "aek77");
						return;
					}
				}
				// we are sure that the id is defined inside the scope of the for statement
				return;
			}
			
		}
		//checking if a variable is included in symtable_var hashtable
		if (!(symtable_var.containsKey(vName)))
		{
			errorOccurred = error.printError("Line " + line + ": " +" Variable " + vName + "is not defined in this scope", "aek55");
			return;
		}
		
		
    }
	
	/*
		TODO def foo(a) and def foo(b) -> error must be thrown
		TODO def foo(a,a) -> error must be thrown
		TODO find out why the fuck does it print the wrong lines???
	*/
	public void inAFunction(AFunction node)
    {
		boolean errorOccurred = false;
        String fName = node.getId().toString();
		int line = ((TId) node.getId()).getLine();
		
		// Here we check if the function is already defined		
		if (symtable.containsKey(fName))
		{
			LinkedList args = node.getArgument();
			AFunction other = (AFunction)symtable.get(fName);
			LinkedList other_args = other.getArgument();
			
			if (other_args.size() == args.size())
			{
				if(args.size() == 0)
				{
					errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" is already defined", "test5");
					return;
				}
				AArgument arg1 = (AArgument) args.get(0); 
				AArgument arg2 = (AArgument) other_args.get(0);
					
				LinkedList list1;
				LinkedList list2;
					
				list1 = arg1.getCommaid();	
				list2 = arg2.getCommaid();

				if(list1.size() == list2.size())
				{
					// Same name and argument number
					errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" is already defined", "test3");
					//return;
				}else
				{

					// If the arguments have lists of different sizes
					// we find the smalest and the longet lsit
					LinkedList small = null, big = null;
					
					if (list1.size() > list2.size())
					{
						big = list1;
						small = list2;
					}else 
					{
						big = list2;
						small = list1;
					}
					int i = 0;
					
					if (small.size() != 0)
						i = small.size();
					
						
					ACommaid commaid = (ACommaid) big.get(i);
					LinkedList eqval = commaid.getEqvalue();
					if (eqval.size() != 0)
					{
						//commonIdsDefault = false;
						errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" is already defined", "test7");
						//return;
					}
				}
			}else
			{
				// Here we must check if the two functions have the same name but one of them has 
				// default arguments(case 6 on the Symbol_Semantic.html)
				// def add(x = 0, y = 0) and def add() -> should throw an error
				LinkedList hasArgs = null, hasNotArgs = null;
				if (args.size() != 0)
				{
					hasArgs = args;
					hasNotArgs = other_args;
				}else
				{
					hasArgs = other_args;
					hasNotArgs = args;
				}
			
				AArgument argument = (AArgument)hasArgs.get(0);
				AEqvalue eqval = null;
				LinkedList comids = null;
				// now we have to check if every id has a default value
				// if it does then an error must be thrown
				if (argument.getEqvalue().size() != 0)
				{
					boolean isAllDefault = true;
					comids = argument.getCommaid();
					for (int i = 0; i < comids.size(); ++i)
					{
						LinkedList vallist = ((ACommaid)comids.get(i)).getEqvalue();
						if (vallist.size() == 0)
						{
							isAllDefault = false;
							break;
						}
					}
					
					if (isAllDefault)
					{
						errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" is already defined", "test1");
						//return;
					}
				}	
			}				
		}
		
		// here we check if a non-default argument follows default argument e.g foo(x=2, y)
		if (node.getArgument().size() != 0)
		{
			LinkedList args = node.getArgument();
			AEqvalue eqval = null;
			LinkedList comids = null;
			AArgument argument = (AArgument)args.get(0);
			// First we check the value of the very first id
			if (argument.getEqvalue().size() != 0)
				eqval = (AEqvalue)argument.getEqvalue().get(0);
			comids = argument.getCommaid();
			// if there are more arguments and the first id has a default value
			if (eqval != null && comids.size() != 0 && ((ACommaid)comids.get(0)).getEqvalue().size() == 0)
			{
				errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" a non-default argument follows default argument");
				//return;
			}
			comids = argument.getCommaid();
			// Now check the rest of the id values
			for (int i = 0; i < comids.size(); ++i)
			{
				LinkedList vallist = ((ACommaid)comids.get(i)).getEqvalue();
				if (vallist.size() != 0 && i+1 < comids.size() && ((ACommaid)comids.get(i+1)).getEqvalue().size() == 0)
				{
					errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" a non-default argument follows default argument");
					//return;
				}
			}
			
		}

		// We check if the function definition contains an argument with a name that has already been used in a previous argument.
		// We must parse the arguments and throw an error if the previous happens.
		LinkedList args = node.getArgument();
		if (args.size() != 0)
		{
			HashSet<String> duplicatedIds = new HashSet<String>();
			AArgument arg = (AArgument)args.get(0);
			String currentIdName = arg.getId().getText(), duplicatedIdNames = "";
			LinkedList comids = arg.getCommaid();
			
			for (int i = 0; i < comids.size(); ++i)
			{
				ACommaid cid = (ACommaid)comids.get(i);
				if (cid.getId().getText().equals(currentIdName))
				{
					//System.out.println("hereee");
					duplicatedIds.add(currentIdName);
					//errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" duplicate argument " + currentIdName +" in function definition ", "test_same_args");
				}
			}
			if (comids.size() != 0)
			{				
				for (int i = 0; i < comids.size() - 1; ++i)
				{
					currentIdName = ((ACommaid)comids.get(i)).getId().getText();
					for (int j = i+1; j < comids.size(); ++j)
					{
						ACommaid cid = (ACommaid)comids.get(j);
						if (cid.getId().getText().equals(currentIdName))
						{
							duplicatedIds.add(currentIdName);
							//errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" duplicate argument " + currentIdName +" in function definition ", "test_same_args");
							break;
						}
					}
				}
			}

			if (duplicatedIds.size() != 0)
			{
				String msg = "";
				Iterator iter = duplicatedIds.iterator();
				while (iter.hasNext())
				{
					msg += " " + iter.next();
				}
				errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" duplicate argument(s)" + msg +" in function definition ", "test_same_args");
			}
		

		}
		
		// if a function produces at least one error, then it is not inserted into the symbol table (is this the correct behavior?)
		if (!errorOccurred)
		{
			//System.out.println("Function " + fName + " was added");
			symtable.put(fName, node);
		}
    }
    
    public int getErrorCount()
    {
    	return error.getErrorCount();
    }
}
