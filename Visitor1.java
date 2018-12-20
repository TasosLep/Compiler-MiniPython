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
	
	public void inAIdExpExpression(AIdExpExpression node)
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
    }

	public void inAFunction(AFunction node)
    {
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
					error.printError("Line " + line + ": " +" Function " + fName +" is already defined");
					return;
				}
				AArgument arg1 = (AArgument) args.get(0); 
				AArgument arg2 = (AArgument) other_args.get(0);
				
				// We have to check if all the ids are the same and if so, then we have to create an error for the user
				if(!arg1.getId().getText().equals(arg2.getId().getText()))
				{
					symtable.put(fName, node);
					return;
				}
				
				LinkedList list1 = arg1.getEqvalue();
				LinkedList list2 = arg2.getEqvalue();
					
				list1 = arg1.getCommaid();	
				list2 = arg2.getCommaid();
				if(list1.size() == list2.size())
				{
					if (list1.size() == 0)
					{
						// there are no other arguments after the first one
						error.printError("Line " + line + ": " +" Function " + fName +" is already defined");
						return;
					}
					for(int i = 0; i < list1.size(); i++)
					{
						ACommaid commaid1 = (ACommaid) list1.get(i);
						ACommaid commaid2 = (ACommaid) list2.get(i);
						
						if(!(commaid1.getId().getText()).equals(commaid2.getId().getText()))
						{
							symtable.put(fName, node);
							return;
						}
					}
					// if we get here then all the ids after the first are the same
					error.printError("Line " + line + ": " +" Function " + fName +" is already defined");
					
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
				error.printError("Line " + line + ": " +" Function " + fName +" a non-default argument follows default argument");
				return;
			}
			comids = argument.getCommaid();
			// Now check the rest of the id values
			for (int i = 0; i < comids.size(); ++i)
			{
				LinkedList vallist = ((ACommaid)comids.get(i)).getEqvalue();
				if (vallist.size() != 0 && i+1 < comids.size() && ((ACommaid)comids.get(i+1)).getEqvalue().size() == 0)
				{
					error.printError("Line " + line + ": " +" Function " + fName +" a non-default argument follows default argument");
					return;
				}
			}
			
		}
			
		// if we managed to come here then everything is ok
		symtable.put(fName, node);
    }
    
    public int getErrorCount()
    {
    	return error.getErrorCount();
    }
}
