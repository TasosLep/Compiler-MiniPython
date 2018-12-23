import java.io.*;
import minipython.lexer.Lexer;
import minipython.parser.Parser;
import minipython.node.*;
import java.util.*;

public class ParserTest
{
  public static void main(String[] args)
  {
    try
    {
      Parser parser =
        new Parser(
        new Lexer(
        new PushbackReader(
        new FileReader(args[0].toString()), 1024)));

     Hashtable symtable =  new Hashtable();
	 Hashtable symtable_var = new Hashtable();
     Start ast = parser.parse();
     Visitor1 vstr1 = new Visitor1(symtable,symtable_var);
     Visitor2 vstr2 = new Visitor2(symtable,symtable_var);
     ast.apply(vstr1);
	 ast.apply(vstr2);
	 if (vstr1.getErrorCount() == 0 && vstr2.getErrorCount() == 0)
	 {
		 System.out.println("Compilation was successful");
	 }else
	 {
		 // Error is a singleton instance, so taking the count of one of the visitors is enough
		 System.out.println("Compilation failed!\nTotal number of errors: " + vstr1.getErrorCount());
	 }
     /*if (vstr1.getErrorCount() != 0)
      System.out.println("Compilation of Visitor1 failed!\nTotal errors: " + vstr1.getErrorCount());
     else{
     	System.out.println("Compilation of Visitor1 was successful!");
		 ast.apply(vstr2);
		 if (vstr2.getErrorCount() != 0)
			System.out.println("Compilation of Visitor2 failed!\nTotal errors: " + vstr2.getErrorCount());
		 else
			System.out.println("Compilation of Visitor2 was successful!");
	 }*/
	}
    catch (Exception e)
    {
      System.err.println(e);
    }
  }
}

