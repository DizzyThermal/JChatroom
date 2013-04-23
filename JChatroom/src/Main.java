import javax.swing.JFrame;

public class Main
{
	public static void main(String[] args)
	{
		ConnectionGUI cGUI = new ConnectionGUI();
		
		cGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cGUI.setSize(300, 250);
		cGUI.setResizable(false);
		cGUI.setVisible(true);
		
		GUI go = new GUI();

		go.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		go.setSize(800, 600);
		go.setResizable(false);
		go.setVisible(true);
	}
}