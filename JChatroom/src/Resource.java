public class Resource
{
	public static final String VERSION_NUMBER		= "v0.0.4";
	public static final String VERSION_CODENAME		= "Dugtrio";
	
	public static String IP							= "192.168.1.100";//"localhost";
	public static String PORT						= "8010";
	public static String UPORT						= "8015";
	public static String TRANSFER_TYPE				= "TCP";
	
	public static String FILE_SAVE_DIR 				= (System.getProperty("os.name").contains("Windows"))?"C:\\Users\\" + getUsername() + "\\Downloads" :"/home/" + getUsername() + "/Downloads";
	
	public static String USERNAME					= getUsername();
	
	public static String getUsername()
	{
		return System.getProperty("user.name");
	}
}