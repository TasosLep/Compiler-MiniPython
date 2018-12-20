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
	
	public void printError(String error)
	{
		m_error_counter++;
		System.out.println("[Error " + m_error_counter + "] " + error);
	}
	
	public int getErrorCount()
	{
		return m_error_counter;
	}
}
