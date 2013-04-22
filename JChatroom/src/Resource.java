public class Resource
{
	public static final String VERSION_NUMBER		= "v0.0.2";
	public static final String VERSION_CODENAME		= "Blastoise";
	
	public static String IP							= "localhost";
	public static String PORT						= "8010";
	
	public static String FILE_SAVE_DIR 				= "C:\\Users\\" + getUsername() + "\\Downloads";
	
	public static String getUsername()
	{
		return System.getProperty("user.name");
	}
}