import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class BookServer
{
	protected boolean isStopped = false;
	protected int port = 9072;
	protected ServerSocket server = null;
	protected Thread t = null;

	public static void main(String[] args)
	{
		try
		{
			ServerSocket server = new ServerSocket(9072);
			for(;;)
			{
				System.out.println("Waiting for device to connect...");
				new BookServerThreadHandler(server.accept()).start();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("Server Stopped!");
	}
}