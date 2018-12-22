import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class Visitor1 extends DepthFirstAdapter 
{
	private Hashtable symtable;	
	private Error error;

	Visitor1(Hashtable symtable) 
	{
		error = Error.getInstance();
		this.symtable = symtable;
	}
	
	/*public void inAIdExpExpression(AIdExpExpression node)
    {
        String vName = node.getId().toString();
		int line = ((TId) node.getId()).getLine();
		if (symtable.containsKey(vName))
		{
			error.printError("Line " + line + ": " +" Variable " + vName +" is already defined");
		}
		else
		{
			symtable.put(vName, node);
		}
    }*/
	
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
				
				// We have to check if all the ids are the same and if so, then we have to throw an error to the user
				
				// if the first id is different then we are ok
				if(!arg1.getId().getText().equals(arg2.getId().getText()))
				{
					symtable.put(fName, node);
					return;
				}
				// Now we have to find if the two nodes have default arguments in all of their coresponding nodes
				// 6 on the Symbol_Semantic.html
				// TODO def add(x, y = 0) and def add(x)
				// This variable is true when all common arguments are of the same "type"
				// e.g it's true when a pair of common args, both have a default value or both have not
				//boolean commonIdsDefault = true;
				
				// if one of the args has not a default value at its first id while the other has 
				// then we are sure that they are not the same
				//if ((arg1.getEqvalue().size() == 0 && arg2.getEqvalue().size() != 0) || (arg1.getEqvalue().size() != 0 && arg2.getEqvalue().size() == 0))
				//commonIdsDefault = false;
					
				LinkedList list1;
				LinkedList list2;
					
				list1 = arg1.getCommaid();	
				list2 = arg2.getCommaid();

				if(list1.size() == list2.size())
				{
					if (list1.size() == 0)
					{
						// there are no other arguments after the first one
						errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" is already defined", "test4");
						//return;
					}
					for(int i = 0; i < list1.size(); i++)
					{
						ACommaid commaid1 = (ACommaid) list1.get(i);
						ACommaid commaid2 = (ACommaid) list2.get(i);
						LinkedList eqval1 = commaid1.getEqvalue();
						LinkedList eqval2 = commaid2.getEqvalue();
						if(!(commaid1.getId().getText()).equals(commaid2.getId().getText()))
						{
							symtable.put(fName, node);
							return;
						}
						
						/*if (eqval1.size() != eqval2.size())
							commonIdsDefault = false;*/
					}
					// if we get here then because all the ids after the first are the same and
					// because we know that the argument lists have the same size we must throw an error
					//if (commonIdsDefault)
					//{
						errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" is already defined", "test3");
						//return;
					//}
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
					int i;
					for(i = 0; i < small.size(); i++)
					{

						ACommaid commaid1 = (ACommaid) small.get(i);
						ACommaid commaid2 = (ACommaid) big.get(i);
						LinkedList eqval1 = commaid1.getEqvalue();
						LinkedList eqval2 = commaid2.getEqvalue();
						if(!(commaid1.getId().getText()).equals(commaid2.getId().getText()))
						{
							symtable.put(fName, node);
							return;
						}
						
						/*if (eqval1.size() != eqval2.size())
							commonIdsDefault = false;*/
					}
					
					if (big.size() != small.size())
					{
						
						ACommaid commaid = (ACommaid) big.get(i);
						LinkedList eqval = commaid.getEqvalue();
						if (eqval.size() != 0)
						{
							//commonIdsDefault = false;
							errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" is already defined", "test7");
							//return;
						}
					}
				}

				/*if (commonIdsDefault)
				{
					errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" is already defined", "test6");
					/eturn;
					}*/
			}else if (args.size() != 0 && other_args.size() == 0)
			{
				// Here we must check if the two functions have the same name but one of them has 
				// default arguments(case 6 on the Symbol_Semantic.html)
				
				// def add(x = 0, y = 0) and def add() -> should throw an error
			
				// first we have to chech the case in which a node has args while the other doesn't
			
				AArgument argument = (AArgument)args.get(0);
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
				
		
				
			}else if (args.size() == 0 && other_args.size() != 0)
			{
				AArgument argument = (AArgument)other_args.get(0);
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
						errorOccurred = error.printError("Line " + line + ": " +" Function " + fName +" is already defined", "test2");
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
		// if a function produces at least one error, then it is not inserted into the symbol table (is this the correct behavior?)
		if (!errorOccurred)
			symtable.put(fName, node);
    }
    
    public int getErrorCount()
    {
    	return error.getErrorCount();
    }
}
