import javax.swing.JFrame;

public class Main
{
	public static JFrame go;
	public static boolean connectionGUIStatus = false;
	
	public static void main(String[] args)
	{
		ConnectionGUI cGUI = new ConnectionGUI();
		
		cGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cGUI.setSize(300, 350);
		cGUI.setResizable(false);
		cGUI.setVisible(true);

		while(!connectionGUIStatus)
		{
			try { Thread.sleep(1);}
			catch (InterruptedException e1) { e1.printStackTrace(); }
		}
		if (Resource.TRANSFER_TYPE.equals("UDP"))
			go = new UGUI();
		else
			go = new GUI();

		go.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		go.setSize(800, 600);
		go.setResizable(false);
		go.setVisible(true);
	}
}