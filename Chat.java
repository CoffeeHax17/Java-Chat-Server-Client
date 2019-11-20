/*********************************************
 * ©CoffeeHax17                              *
 * The whole program and all of the classes  *
 * used are included in just this file cause *
 * I was bored. Deal with it.                *
 *********************************************/

import java.net.*;
import java.io.*;
import java.util.Scanner;
import static java.lang.System.*;

class User
{
	protected Socket socket;
	protected int port;
	protected String userName;
	public User(int port, String userName) throws IOException
	{
		this.port = port;
		this.userName = userName;
	}
	public String getUserName()
	{
		return this.userName;
	}
	public Socket getSocket()
	{
		return this.socket;
	}
}

class Server extends User
{
	private ServerSocket serverSocket;
	public Server(int port, String userName) throws IOException
	{
		super(port, userName);
		this.serverSocket = new ServerSocket(this.port);
		this.socket = this.serverSocket.accept();
	}
}

class Client extends User
{
	private String host;
	public Client(String host, int port, String userName) throws IOException
	{
		super(port, userName);
		this.host = host;
		this.socket = new Socket(this.host, this.port);
	}
}

class MsgInput implements Runnable
{
	private User user;
	private DataInputStream is;
	public MsgInput(User user) throws IOException
	{
		this.user = user;
		Socket socket = this.user.getSocket();
		this.is = new DataInputStream(socket.getInputStream());
	}
	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				out.println(is.readUTF());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

class MsgOutput implements Runnable
{
	private User user;
	private DataOutputStream os;
	public MsgOutput(User user) throws IOException
	{
		this.user = user;
		Socket socket = this.user.getSocket();
		this.os = new DataOutputStream(socket.getOutputStream());
	}
	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				Scanner sc = new Scanner(System.in);
				String message = this.user.getUserName() + ": " + sc.nextLine();
				os.writeUTF(message);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

public class Chat
{
	private static User user;
	private static MsgInput msgin;
	private static MsgOutput msgout;
	public static void main(String[] args)
	{
		try
		{
			switch (args[0])
			{
				case "server":
					user = new Server(Integer.parseInt(args[1]), args[2]);
					break;
				case "client":
					String host = args[1];
					int port = Integer.parseInt(args[2]);
					String name = args[3];
					user = new Client(host, port, name);
					break;
				default:
					out.println("java server [port] [username]");
					out.println("java client [host] [port] [username]");
			}
			msgin = new MsgInput(user);
			msgout = new MsgOutput(user);
			Thread t1 = new Thread(msgin);
			Thread t2 = new Thread(msgout);
			t1.start();
			t2.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
