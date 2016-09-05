import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Server extends JFrame {
	
	//variables
	//GUI variables
	private JMenuBar menubar;
	private JMenu help;
	private JMenuItem about;
	private JTextField userText;
	private JTextArea chatWindow;
	private Font font = new Font("Verdana" , Font.PLAIN , 14);
	private JLabel label;
	//.net variable
	private ServerSocket server;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connect;
	private JScrollPane s;
	//constructor and create GUI
	public Server(){
		//create Window
		super("ServerSide");
		
		setLayout(null);
		setVisible(true);
		setFont(font);
		setResizable(false);

		//add menu
		menubar = new JMenuBar();
		setJMenuBar(menubar);
		help = new JMenu("Help");
		menubar.add(help);
		about = new JMenuItem("About");
		help.add(about);
		about.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){JOptionPane.showMessageDialog(null, "Developer - Prince Gupta");}});
		
		//set user text
		label = new JLabel("Enter Message:");
		label.setBounds(10,10,120,30);
		label.setFont(font);
		add(label);
		add(label);
		userText = new JTextField();
		userText.setFont(font);
		userText.setEditable(false);
		userText.setBounds(140, 10, 450, 30);
		userText.addActionListener(
		new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendMessage(e.getActionCommand());
				userText.setText("");
			};
		}	
		);
		add(userText);
		
		//chat window
		chatWindow = new JTextArea();
		chatWindow.setLineWrap(true);
		chatWindow.setWrapStyleWord(true);
		chatWindow.setFont(font);
		chatWindow.setBackground(Color.GRAY);
		chatWindow.setBorder(BorderFactory.createRaisedBevelBorder());
		//chatWindow.setBounds(30, 45, 550, 350);
		s = new JScrollPane(chatWindow);
		s.setBounds(30, 55, 550, 350);
		chatWindow.setText("Chat History-");
		chatWindow.setEditable(false);
		add(s);
		//extra house keeping
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640,480);
		getContentPane().setBackground(Color.WHITE);
	}
	
	//.net stuff
	public void startRun(){
		try{
			server = new ServerSocket(776,100);
			//chat
			while(true){
			try{
				waitForConnection();
				setStreams();
				chat();
			}catch(EOFException ee){
				showMessage("Server ended the connection!");
			}finally{
				closeStuff();
				}
			break;
			}
			
			
		}catch(IOException e){e.printStackTrace();}
	}
	//wait to connect
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect");
		connect = server.accept();
		showMessage("Connected to "+connect.getInetAddress().getHostName());
	}
	//set streams for data exchange
	private void setStreams() throws IOException{
		output = new ObjectOutputStream(connect.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connect.getInputStream());
		showMessage("Streams are good to go!");
	}
	//chat now
	private void chat() throws IOException{
		String message="You are now Connected";
		userText.setEditable(true);
		sendMessage(message);
		do{
			try{
				message = (String)input.readObject();
				showMessage(message);
		}catch(ClassNotFoundException e){
			showMessage("I can't read what the user sent!");
		}
			
		}while(!message.equals("CLIENT - END"));
		
	}
	
	//close everything
	private void closeStuff(){
		try{
			showMessage("Closing the Streams and Sockets");

			output.close();
			input.close();
			connect.close();
			userText.setEditable(false);
		}catch(IOException ee){ee.printStackTrace();}
	}
	//send message
	private void sendMessage(String msg){
		try{
			output.writeObject("SERVER - " +msg);
			output.flush();
			showMessage("SEVRER - "+msg);
		}catch(IOException e){showMessage("\nI can't send this message!");}
	}
	private void showMessage(final String ms){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				chatWindow.append("\n" +ms);
			}
		});
	}
	
}
