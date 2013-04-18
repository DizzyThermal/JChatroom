import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GUI extends JFrame implements KeyListener
{
	private static final long serialVersionUID = 1L;
	
	JPanel leftPanel = new JPanel();
	JScrollPane chatText = new JScrollPane(new JTextArea());
	JTextField messageField = new JTextField();
	JButton addImageButton = new JButton("Add Image");
	
	
	JPanel rightPanel = new JPanel();
	JScrollPane users = new JScrollPane(new JTextArea()); 

	// Generate Traffic and Clear Buttons
	JButton generateTrafficButton = new JButton("Fire!");
	JButton resetButton = new JButton("Reset");
	
	GUI()
	{
		super("ECE 369 - JChatroom (" + Resource.VERSION_NUMBER + " - " + Resource.VERSION_CODENAME + ")");
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);
		setLayout(fl);
		
		createLeftPanel();
		createRightPanel();
		
		add(leftPanel);
		add(rightPanel);
	}
	
	public void createLeftPanel()
	{
		leftPanel.setPreferredSize(new Dimension(600, 600));
		
		chatText.setPreferredSize(new Dimension(600, 520));
		messageField.setPreferredSize(new Dimension(600, 25));
		
		leftPanel.add(chatText);
		leftPanel.add(messageField);
	}
	
	public void createRightPanel()
	{
		rightPanel.setPreferredSize(new Dimension(175, 600));
		
		users.setPreferredSize(new Dimension(175, 520));
		addImageButton.setPreferredSize(new Dimension(175, 25));
		
		rightPanel.add(users);
		rightPanel.add(addImageButton);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
}