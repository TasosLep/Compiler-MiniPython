import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class Visitor2 extends DepthFirstAdapter 
{
	private Hashtable symtable;	
	private Hashtable symtable_var;
	private Error error;

	Visitor2(Hashtable symtable, Hashtable symtable_var) 
	{
		error = Error.getInstance();
		this.symtable = symtable;
		this.symtable_var = symtable_var;
	}
	
	
	//Find out if this variable(identifier) has been initialized (exists)
	public void inAIdExpression(AIdExpression node)
    {
		boolean errorOccurred = false;
        String vName = node.getId().toString();
		int line = ((TId) node.getId()).getLine();
		
		//getting return statement parameters
		Node parent = node.parent();

		//checking if a variable is included in symtable_var hashtable
		/*if (!(symtable_var.containsKey(vName)))
		{
			errorOccurred = error.printError("Line " + line + ": " +" Variable " + vName + "is not defined in this scope", "aek55");
			return;
		}*/
		
    }
	
	

	//Find out if this function we are calling exists
	public void inAFunctioncall (AFunctioncall node)
	{
		boolean errorOccurred = false;
        String fName = node.getId().toString();
		int line = ((TId) node.getId()).getLine();
		
		// checking if the function call has ever been declared
		if(symtable.containsKey(fName)){
			LinkedList args = node.getArglist();
			AFunction other = (AFunction)symtable.get(fName);
			LinkedList other_args = other.getArgument();
			System.out.println("here");

			//otan perimenei orismata kai emeis dinoume ligotera i perissotera
			if(other_args.size() != 0 && args.size() != 0){
				//prepei na metrisoume posa arguments 8eloume
				int other_args_length = 1; // 3eroume oti arxizei apo 1
				AArgument arg1 = (AArgument) other_args.get(0);
				other_args_length += arg1.getCommaid().size();
				//System.out.println(other_args_length);
				
				
				//prepei na metrisoume posa arguments dinoume
				int args_length = 1; // panta arxizei apo ena (afou periexei ena expression panta)
				args_length  += ((AArglist) args.get(0)).getCommaexp().size(); 
			//	System.out.println(args_length);

				if(args_length > other_args_length){			
					errorOccurred = error.printError("Line " + line + ": " +" FunctionCall " + fName + "the number of parametes doesn't much the number of arguments","aek2");
					return;
				}
				
				//gia na broume ta default parameters stis sinartiseis pou exoume orisei
				int index_of_first_defaultParam_on_other_args = 0;
				
				if (arg1.getEqvalue().size() == 0){
					index_of_first_defaultParam_on_other_args = 1;
					LinkedList comma_ids = arg1.getCommaid();
					for(int i = 0; i < comma_ids.size(); i++){
						LinkedList eqval = ((ACommaid)comma_ids.get(i)).getEqvalue();
						if(eqval.size() == 0){
							index_of_first_defaultParam_on_other_args++;
							break;
						}
					}
				}
				System.out.println(index_of_first_defaultParam_on_other_args);
				if(!(args_length <= other_args_length && args_length >= index_of_first_defaultParam_on_other_args)) // checking how many parameters can have as an input without the default parameters
				{
					errorOccurred = error.printError("Line " + line + ": " +" FunctionCall " + fName +"the number of parametes doesn't much the number of arguments","aek3");		
					return;
				}
			}
			
		}else
		{
			errorOccurred = error.printError("Line " + line + ": " +" FunctionCall " + fName + "is not defined ", "aek4");			
		}
	}	
	
	public int getErrorCount()
    {
    	return error.getErrorCount();
    }	
}