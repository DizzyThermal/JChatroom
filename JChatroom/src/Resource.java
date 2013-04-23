public class Resource
{
	public static final String VERSION_NUMBER		= "v0.0.3";
	public static final String VERSION_CODENAME		= "Charizard";
	
	public static String IP							= "192.168.1.100";//"localhost";
	public static String PORT						= "8010";
	
	public static String FILE_SAVE_DIR 				= (System.getProperty("os.name") == "Windows")?"C:\\Users\\" + getUsername() + "\\Downloads" :"/home/" + getUsername() + "/Downloads";
	
	public static String USERNAME					= "JC-User";
	
	public static String HASH						=	"1011001111001111101100011110011111111100111110011" +
														"1100111111110010110011011001010100001110001110010" +
														"1110010110011000110110011000100011100111111110001" +
														"1100111111110010110011011001010100001110001110010" +
														"1110010110011000110110011000100011100111111110001" +
														"1100111111110010110011011001010100001110001110010" +
														"1011001111001111101100011110011111111100111110011" +
														"1011001111001111101100011110011111111100111110011" +
														"0011100110100110100101111001011010011011010010100" +
														"1011001111001111101100011110011111111100111110011" +
														"1001101001010010101000101101010100111001110011111";
	
	public static String getUsername()
	{
		return System.getProperty("user.name");
	}
}