import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class Visitor1 extends DepthFirstAdapter 
{
	private Hashtable symtable;	

	Visitor1(Hashtable symtable) 
	{
		this.symtable = symtable;
	}
	
	public void inAIdExpExpression(AIdExpExpression node)
    {
        String vName = node.getId().toString();
		int line = ((TId) node.getId()).getLine();
		if (symtable.containsKey(vName))
		{
			System.out.println("Line " + line + ": " +" Variable " + vName +" is already defined");
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
		if (symtable.containsKey(fName))
		{
			LinkedList args = node.getArgument();
			AFunction other = (AFunction)symtable.get(fName);
			LinkedList other_args = other.getArgument();
			
			
		//	System.out.println(other_args.get(0));
		//	System.out.println(args.get(0));
		//	System.out.println(other_args.get(1));
		//	System.out.println(args.get(1));
		//	System.out.println(other_args.size());
		//	System.out.println(args.size());
		//	System.out.println(args.equals(other_args));
		//	System.out.println(fName);
		//	System.out.println(other.getId().toString());
			if (other_args.size() == args.size())
			{
				if(args.size() == 0)
				{
					System.out.println("Line " + line + ": " +" Function " + fName +" is already defined");
					return;
				}
				AArgument arg1 = (AArgument) args.get(0); 
				AArgument arg2 = (AArgument) other_args.get(0);
				
				// We have to check if all the ids are the same and if so, then we have to create an error for the user
				if(!arg1.getId().getText().equals(arg2.getId().getText()))
				{ //elegxei an to onoma tou identifier tou enos einai idio me tou allou
					symtable.put(fName, node);
					return;
				}
				
				//boolean same_value = false;
				LinkedList list1 = arg1.getEqvalue();
				LinkedList list2 = arg2.getEqvalue();
				/*
				if (list1.size() == list2.size())
				{ // an einai idio to mege8os ton liston
					if (list1.size() == 1)
					{
						same_value = (((AEqvalue)list1.get(0)).getValue()).equals(((AEqvalue)list2.get(0)).getValue());
					}
					else
						same_value = true;					
				}*/
					
				list1 = arg1.getCommaid();	
				list2 = arg2.getCommaid();
				if(list1.size() == list2.size())
				{
					if (list1.size() == 0)
					{
						// there are no other arguments after the first one
						System.out.println("Line " + line + ": " +" Function " + fName +" is already defined");
						return;
					}
					//boolean diff = false;
					for(int i = 0; i < list1.size(); i++)
					{
						ACommaid commaid1 = (ACommaid) list1.get(i);
						ACommaid commaid2 = (ACommaid) list2.get(i);
						
						if(!(commaid1.getId().getText()).equals(commaid2.getId().getText()))
						{
							symtable.put(fName, node);
							return;
						}
						/*else
						{
							LinkedList newlist1 = commaid1.getEqvalue();
							LinkedList newlist2 = commaid2.getEqvalue();
							if(newlist1.size() == newlist2.size())
							{
								if(newlist1.size() == 0)
								{
									if(!((AEqvalue)newlist1.get(0)).equals(((AEqvalue)newlist1.get(0))))
									diff = true;
								}
								
							}
							if(!(list1.get(i)).equals(list2.get(i)))
								diff = true;
						}
						if(diff)
							symtable.put(fName, node);
						else
							System.out.println("Line " + line + ": " +" Function " + fName +" is already defined");
						*/
					}
					// if we get here then all the ids after the first are the same
					
					System.out.println("Line " + line + ": " +" Function " + fName +" is already defined");
					
				}
				else
				{
					/*if(same_value)
						System.out.println("Line " + line + ": " +" Function " + fName +" is already defined");	
					else
						symtable.put(fName, node);
					*/
				}				
			}
		}
		
		symtable.put(fName, node);
    }
}
