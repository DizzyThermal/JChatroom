import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;

public class UGUI extends JFrame implements KeyListener
{
	private static final long serialVersionUID = 1L;	
	JPanel leftPanel = new JPanel();
	JScrollPane chatText = new JScrollPane(new JTextArea());
	JTextField messageField = new JTextField();
	JPanel rightPanel = new JPanel();
	JScrollPane users = new JScrollPane(new JTextArea()); 
	
	public static ArrayList<User> userList = new ArrayList<User>();
	public static ArrayList<String> commands = new ArrayList<String>();
	public static int commandHistory = 0;
	
	public static DatagramSocket clientSocket;
	public static byte[] pWriter = new byte[1024];
	public static byte[] bReader = new byte[1024];
	InetAddress IPAddress;
	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	
	public Thread t1;
	
	public static int id = -1;
	
	public static boolean connectionGUIStatus = false;
	
	UGUI()
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
			clientSocket = new DatagramSocket(Integer.parseInt(Resource.UPORT));
			IPAddress = InetAddress.getByName(Resource.IP);
			String temp = "/connected " + Resource.USERNAME;
			pWriter = temp.getBytes();
			sendPacket = new DatagramPacket(pWriter, pWriter.length, IPAddress, Integer.parseInt(Resource.UPORT));
			clientSocket.send(sendPacket);
		}
		catch (Exception e) { e.printStackTrace(); }

		
		t1 = (new Thread()
		{
			@Override
			public void run()
			{
				String incomingMessage = "";
				while(this.isAlive())
				{
					bReader = new byte[1024];
					receivePacket = new DatagramPacket(bReader, bReader.length);
					try { clientSocket.receive(receivePacket); }
					catch (IOException e) { e.printStackTrace(); }
					incomingMessage = new String(receivePacket.getData()).trim();
					System.out.println(incomingMessage);
					if(!incomingMessage.equals(""))
					{
						if(incomingMessage.contains("/userlist"))
							buildUserList(incomingMessage);
						else if(incomingMessage.contains("/id"))
							setID(incomingMessage);
						else if(incomingMessage.contains("/update"))
							updateUser(incomingMessage);
						else if(incomingMessage.contains("/remove"))
							removeUser(incomingMessage);
						else if(incomingMessage.contains("/msg"))
							addMessageToChat(incomingMessage, true);
						else if(incomingMessage.contains("/console"))
							addMessageToChat(incomingMessage, false);
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
		    public void windowClosing( WindowEvent e )
		    {
		    	disconnect();
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
		
		rightPanel.add(users);
	}

	public void sendMessageToServer(String message)
	{
		try{
		if(message.contains("/name"))
		{
			Resource.USERNAME = message.substring(6);
			if(Resource.USERNAME.charAt(0) == '"')
				Resource.USERNAME = Resource.USERNAME.substring(1, Resource.USERNAME.length()-1);
			sendPacket = new DatagramPacket(("/name " + id + "\\\"" + Resource.USERNAME + "\"").getBytes(),("/name " + id + "\\\"" + Resource.USERNAME + "\"").getBytes().length,IPAddress,Integer.parseInt(Resource.UPORT));
			clientSocket.send(sendPacket);
		}
		else if(message.contains("/exit"))
			disconnect();
		else
		{
			sendPacket = new DatagramPacket((Resource.USERNAME + ": " + message).getBytes(),(Resource.USERNAME + ": " + message).getBytes().length,IPAddress,Integer.parseInt(Resource.UPORT));
			clientSocket.send(sendPacket);
		}
		} catch (Exception e){}
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
	
	public void addMessageToChat(String message, boolean console)
	{
		if(console)
		{
			message = message.substring(5);
			String name = message.split(":")[0];
			message = message.split(":")[1];
			
			message = name + ":" + message;
		}
		else
			message = message.substring(9);

		((JTextArea)((JViewport)chatText.getComponent(0)).getView()).setText(((JTextArea)((JViewport)chatText.getComponent(0)).getView()).getText() + message + "\n");
	}
	
	public void setID(String userString)
	{
		userString = userString.substring(4);
		id = Integer.parseInt(userString);
	}
	
	public void buildUserList(String userString)
	{
		userList.clear();

		userString = userString.substring(10);
		String[] users = userString.split("\\\\");
		
		for(int i = 0; i < (users.length)/2; i++)
			userList.add(new User(Integer.parseInt(users[i*2]), users[i*2+1]));
		
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
		
	public String getUserNameFromId(int id)
	{
		for(int i = 0; i < userList.size(); i++)
		{
			if(userList.get(i).getId() == id)
				return userList.get(i).getName();
		}
		
		return null;
	}

	public void disconnect()
	{
		sendPacket = new DatagramPacket(("/disconnect " + id).getBytes(),("/disconnect " + id).getBytes().length,IPAddress,Integer.parseInt(Resource.UPORT));
		try 
		{
			clientSocket.send(sendPacket);
		} catch (IOException e1) { e1.printStackTrace(); }
		t1.stop();
		try
		{
			clientSocket.close();
		}
		catch(Exception e) { e.printStackTrace(); }
		
		setVisible(false);
		dispose();
		System.exit(0);
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