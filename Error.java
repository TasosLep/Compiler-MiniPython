public class Error
{
	private static Error m_instance = null;
	private static int m_error_counter = 0;
	
	private Error()
	{
		
	}
	
	public static Error getInstance()
	{
		if (m_instance == null)
			m_instance = new Error();
		return m_instance;
	}
	
	public static void deleteInstance()
	{
		m_instance = null;
	}
	
	public boolean printError(String error)
	{
		m_error_counter++;
		System.out.println("[Error " + m_error_counter + "] " + error);
		return true;
	}
	
	public boolean printError(String error, String debug)
	{
		m_error_counter++;
		System.out.println("[Error " + m_error_counter + "] " + error + " [" + debug + "]");
		return true;
	}
	
	public int getErrorCount()
	{
		return m_error_counter;
	}
}
