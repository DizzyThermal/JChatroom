import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;

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
	
	public static ArrayList<User> userList = new ArrayList<User>();
	public static ArrayList<String> commands = new ArrayList<String>();
	public static int commandHistory = 0;
	
	public static Socket clientSocket;
	public static PrintWriter pWriter;
	public static BufferedReader bReader;
	
	public static int id;
	public static String username = "JC-User";
	
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
		
		try
		{
			clientSocket = new Socket("localhost", Integer.parseInt(Resource.PORT));
			pWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			bReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			pWriter.println("/connected " + username);
		}
		catch (Exception e) { e.printStackTrace(); }

		
		Thread t1 = (new Thread()
		{
			@Override
			public void run()
			{
				while(true)
				{
					String incomingMessage = null;
					try
					{
						incomingMessage = bReader.readLine();
					}
					catch(Exception e) { e.printStackTrace(); }
					
					if(!incomingMessage.equals(null))
					{
						System.out.println("Message: " + incomingMessage);
						if(incomingMessage.contains("/userlist"))
							buildUserList(incomingMessage);
						else if(incomingMessage.contains("/update"))
							updateUser(incomingMessage);
						else if(incomingMessage.contains("/remove"))
							removeUser(incomingMessage);
						else if(incomingMessage.contains("/msg"))
							addMessageToChat(incomingMessage);
					}
				}
			}
		});
		t1.start();

		this.addWindowListener(new WindowAdapter()
		{
		    public void windowOpened( WindowEvent e )
		    {
		        messageField.requestFocus();
		    }
		}); 
	}
	
	public void createLeftPanel()
	{
		leftPanel.setPreferredSize(new Dimension(600, 600));
		
		chatText.setPreferredSize(new Dimension(600, 520));
		((JTextArea)((JViewport)chatText.getComponent(0)).getView()).setEditable(false);
		messageField.setPreferredSize(new Dimension(600, 25));
		messageField.addKeyListener(this);
		
		leftPanel.add(chatText);
		leftPanel.add(messageField);
	}
	
	public void createRightPanel()
	{
		rightPanel.setPreferredSize(new Dimension(175, 600));
		
		users.setPreferredSize(new Dimension(175, 520));
		((JTextArea)((JViewport)users.getComponent(0)).getView()).setEditable(false);
		addImageButton.setPreferredSize(new Dimension(175, 25));
		
		rightPanel.add(users);
		rightPanel.add(addImageButton);
	}

	public void sendMessageToServer(String message)
	{
		if(message.contains("/name"))
		{
			username = message.substring(6);
			if(username.charAt(0) == '"')
				username = username.substring(1, username.length()-1);
			pWriter.println("/name " + id + "\\\"" + username + "\"");
		}
		else if(message.contains("/exit"))
			disconnect();
		else
			pWriter.println(username + ": " + message);
		
		addCommand(message);
		messageField.setText("");
	}
	
	public void resetUserList()
	{
		String userString = "";
		for(int i = 0; i < userList.size(); i++)
			userString = userString + userList.get(i).getName() + "\n"; 

		((JTextArea)((JViewport)users.getComponent(0)).getView()).setText(userString);
	}
	
	public void addMessageToChat(String message)
	{
		message = message.substring(5);
		((JTextArea)((JViewport)chatText.getComponent(0)).getView()).setText(((JTextArea)((JViewport)chatText.getComponent(0)).getView()).getText() + message + "\n");
	}
	
	public void buildUserList(String userString)
	{
		userString = userString.substring(10);
		String[] users = userString.split("\\\\");
		
		for(int i = 0; i < (users.length)/2; i++)
			userList.add(new User(Integer.parseInt(users[i*2]), users[i*2+1]));
		
		id = userList.get(userList.size()-1).getId();
		orderUsers();
	}
	
	public void orderUsers()
	{
		for(int i = 0; i < userList.size(); i++)
		{
			for(int j = 0; j < (userList.size() - 1 - i); j++)
			{
				if((userList.get(j).getName().compareTo(userList.get(j+1).getName())) > 0)
				{
					int tmpId = userList.get(j).getId();
					String tmpName = userList.get(j).getName();
					
					userList.get(j).setInfo(userList.get(j+1).getId(), userList.get(j+1).getName());
					userList.get(j+1).setInfo(tmpId, tmpName);
				}
			}
		}
		
		resetUserList();
	}
	
	public void updateUser(String incomingString)
	{
		String userString = incomingString.substring(8);
		int id = Integer.parseInt(userString.split("\\\\")[0]);
		String name = userString.split("\\\\")[1];
		
		for(int i = 0; i < userList.size(); i++)
		{
			if(userList.get(i).getId() == id)
			{
				userList.get(i).setName(name);
				break;
			}
		}
		
		orderUsers();
		resetUserList();
	}
	
	public void removeUser(String incomingString)
	{
		int id = Integer.parseInt(incomingString.substring(8));

		for(int i = 0; i < userList.size(); i++)
		{
			if(userList.get(i).getId() == id)
			{
				userList.remove(i);
				break;
			}
		}
		
		resetUserList();
	}
	
	public void addCommand(String command)
	{
		for(int i = 0; i < commands.size(); i++)
		{
			if(commands.get(i).equals(command))
			{
				commands.remove(i);
				break;
			}
		}
		
		commands.add(command);
	}
	
	public void disconnect()
	{
		pWriter.println("/disconnect " + id);
		
		try
		{
			bReader.close();
			pWriter.close();
			clientSocket.close();
		}
		catch(Exception e) { e.printStackTrace(); }
		
		setVisible(false);
		dispose();
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			sendMessageToServer(messageField.getText());
			commandHistory = commands.size();
		}
		if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			if(commands.size() > 0)
			{
				if(commandHistory < 1)
					commandHistory = commands.size();
				
				messageField.setText(commands.get(--commandHistory));
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			if(commands.size() > 0)
			{
				if(commandHistory > (commands.size()-2))
					commandHistory = -1;
				
				messageField.setText(commands.get(++commandHistory));
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
}