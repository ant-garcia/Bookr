import java.util.regex.Pattern;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class BookServerThreadHandler extends Thread
{
	Socket sock;
	ModelHandler mh = new ModelHandler();

	BookServerThreadHandler(Socket s)
	{
		this.sock = s;
	}

	public void run()
	{
		System.out.println("Creating Thread...");
		processRequest(sock);
		System.out.println("Deleting Thread...");
	}

	/**
	 * parses the string that was received from the client, passes parameters to the modelhandler
	 * to be used by the database. Any information that is returned from the modelhandler is sent
	 * back to the client to be displayed.
	 * 
	 * ISBN/Title/Author: returns the sellers from the modelhandler that are associated with
	 * the search parameter.
	 *  
	 * GET: passes parameters to the modelhandler to check if the user is found in the database
	 * and returns the list of books, if the user is absent, a new user is created then.
	 *
	 * ADD: passes parameters to the modelhandler to create a new book object and add it to the
	 * database.
	 *
	 * DEL:  passes parameters to the modelhandler to remove the book object from the database.
	 * @param client socket that was caught by the book server to be parsed.
	 */
	private void processRequest(Socket client)
	{
		PrintWriter out = null;
		BufferedReader in = null;
	
		try
		{
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			System.out.println("Device connected");	
			String fromDevice = in.readLine();
			String toDevice = "";
			System.out.println("Recieved: " + fromDevice);
			mh.openDB();
			if(fromDevice.startsWith("ISBN") || fromDevice.startsWith("Title") || fromDevice.startsWith("Author"))
			{
				toDevice = mh.getBookSellers(fromDevice);
				if(toDevice.equalsIgnoreCase(""))
				{
					toDevice = "This book is not being sold at the time being!";
					System.out.println("ERROR: this book is not listed");
				}
				else
					toDevice = mh.getBook((toDevice.split(Pattern.quote("|"))[0])) + "$" + toDevice;
				
			}
			else if(fromDevice.startsWith("GET"))
			{
				System.out.println(fromDevice.substring(fromDevice.indexOf("%") + 1, fromDevice.indexOf("|")));
				if(mh.hasUser(fromDevice.substring(fromDevice.indexOf("%") + 1, fromDevice.indexOf("|"))))
				{
					System.out.println("Fetching Book List");
					toDevice = mh.getUserBookList(fromDevice.substring(fromDevice.indexOf("%") + 1, fromDevice.indexOf("|")));
					if(toDevice.equalsIgnoreCase(""))
					{
						toDevice = "You currently do not have any books for sale!";
						System.out.println("ERROR: this user has an empty book list");		
					}
				}
				else
				{
					System.out.println(fromDevice.substring(fromDevice.indexOf("%") + 1));
					if(fromDevice.substring(fromDevice.indexOf("%") + 1).split(Pattern.quote("|")).length < 3)
					{
						System.out.println("ERROR: Not enough parameters to create a user");
						toDevice = "This user has not been created yet! Please go to My Personal Information to create a profile";
					}
					else
					{
						mh.initUser(fromDevice.substring(fromDevice.indexOf("%") + 1));	
						System.out.println("Created User");
					}
				}
			}
			else if(fromDevice.startsWith("ADD"))
			{
				System.out.println(fromDevice.substring(fromDevice.indexOf("%") + 1));
				if(mh.initBook(fromDevice.substring(fromDevice.indexOf("%") + 1)) == null)
					System.out.println("ERROR: tried to add a book with no user profile!");	
				System.out.println("Updated database");
			}
			else if(fromDevice.startsWith("DEL"))
			{
				System.out.println(fromDevice.substring(fromDevice.indexOf("%") + 1));
				if(!mh.delBook(fromDevice.substring(fromDevice.indexOf("%") + 1)))
					System.out.println("ERROR: tried to delete a book with no user profile!");	
				System.out.println("Updated database");
			}
			System.out.println("Sending Back: " + toDevice);
			out.println(toDevice);
            in.close();
			out.close();
			System.out.println("Finished Request");
			mh.closeDB();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}