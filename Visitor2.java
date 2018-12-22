import minipython.analysis.*;
import minipython.node.*;
import java.util.*;

public class Visitor2 extends DepthFirstAdapter 
{
	private Hashtable symtable;	
	private Hashtable funcVariables;
	private Error error;

	Visitor2(Hashtable symtable) 
	{
		error = Error.getInstance();
		this.symtable = symtable;
		funcVariables = new Hashtable();
	}

	//Find out if this function we are calling exists
	public void inAFunctioncall (AFunctioncall node)
	{
		boolean errorOccurred = false;
        String fName = node.getId().toString();
		int line = ((TId) node.getId()).getLine();
		
		if(symtable.containsKey(fName)){
			LinkedList args = node.getArglist();
			AFunction other = (AFunction)symtable.get(fName);
			LinkedList other_args = other.getArgument();
			
			//dinoume ligotera orismata apo oti prepei
	//		if((other_args.size() == 0 && args.size() != 0) || (other_args.size() != 0 && args.size() == 0) )// p() ή p(200)
//			{
		//		errorOccurred = error.printError("Line " + line + ": " +" FunctionCall " + fName +" the number of parametes doesn't much the number of arguments","aek1");
//				return;
	//		}
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
					errorOccurred = error.printError("Line " + line + ": " +" FunctionCall " + fName + " the number of parametes doesn't much the number of arguments","aek2");
					return;
				}
				
				//gia na broume ta default parameters stis sinartiseis pou exoume orisei
				int index_of_first_defaultParam_on_other_args = 0;
				
				if (arg1.getEqvalue().size() == 0){
					index_of_first_defaultParam_on_other_args = 1;
					LinkedList comma_ids = arg1.getCommaid();
					for(int i = 0; i < comma_ids.size(); i++){
						LinkedList eqval = ((ACommaid)comma_ids.get(i)).getEqvalue();
						if(eqval.size() == 1){
							index_of_first_defaultParam_on_other_args++;
							break;
						}
					}
				}
				if(!(args_length <= other_args_length && args_length >= index_of_first_defaultParam_on_other_args))
					errorOccurred = error.printError("Line " + line + ": " +" FunctionCall " + fName +" the number of parametes doesn't much the number of arguments","aek3");
			
			}
			
			
			
			
			
			
			
				
				// We have to check if all the ids are the same and if so, we are ok
				
			//	AArgument arg1 = (AArgument) args.; 
		//		AArgument arg2 = (AArgument) other_args.get(0);
				
				// if the first id is different then we are ok
			//	if(!arg1.getId().getText().equals(arg2.getId().getText()))
				//{
					//symtable.put(fName, node);
					//return;
				//}
				
				//if()
				
				//if(arg1.get())
				//System.out.println(arg1.toString());
			
			//int args = 		
			
	//		HashMap checks = new HashMap<String,Boolean>();
		//	checks = doChecks(node);
			
			
		//	if (!((boolean) checks.get("functionNameExists") && (boolean) checks.get("functionCallCovered"))) {
			//if (!(boolean) checks.get("functionNameExists")) {
				//errorOccurred = error.printError("Line " + line + ": " + "Function " + fName + " is not defined!");
			//} else if (!(boolean) checks.get("functionCallCovered")) {
				//System.out.println("Line " + line + ": " + "No appropriate overloaded version of function " + fName + " was found!");
	//		}
			
	//		return;
//}

			
				//if()
			//	if(arg1.size() != arg2.size())
			//		return;
				
			//}
			
		}
	}
	
	public HashMap<String,Boolean> doChecks(AFunctioncall node) {
		boolean functionNameExists = false;
		boolean functionCallCovered = false;
		
		String funcName = node.getId().toString().trim();
		
		Set<String> keySet = symtable.keySet();
		Iterator<String> iterator = keySet.iterator();
		String key;
		int numberOfArguments = 0;
		int minimumRequiredArguments = 0;
		int defaultArguments = 0;
		
		LinkedList nodeArguments = node.getArglist();
		
		if (nodeArguments.size() == 1) {
			AArglist argList = (AArglist) nodeArguments.get(0);
			numberOfArguments = ((LinkedList) argList.getCommaexp()).size() + 1;
		}
		
		while (iterator.hasNext()) { 
			key = iterator.next();
			
			if (key.contains(funcName)) {
				if (!functionNameExists) {
					functionNameExists = true;
				}
				
				minimumRequiredArguments = Integer.valueOf(key.substring(key.indexOf("-") + 1, key.indexOf("_")));
				defaultArguments = Integer.valueOf(key.substring(key.indexOf("_") + 1));
				
				if (numberOfArguments >= minimumRequiredArguments && numberOfArguments <= minimumRequiredArguments + defaultArguments) {
					functionCallCovered = true;
					break;
				}
			}
		}
		
		HashMap checks = new HashMap<String,Boolean>();
		checks.put("functionNameExists", functionNameExists);
		checks.put("functionCallCovered", functionCallCovered);
		
		return checks;
	}
	
	
	public int getErrorCount()
    {
    	return error.getErrorCount();
    }
	
	
}