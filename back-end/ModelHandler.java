import java.util.regex.Pattern;

public class ModelHandler
{
	private DBQueryHandler dbHandler;

	/**
	 * Parses the string passed by the BookServerThreadHandler and uses the data to create a 
	 * new User object in the database.
	 * @param  userData a string containing the following information the this order;
	 *                  user email|user name|user phone #
	 *         
	 * @return A test object inorder to endure that the data was binded correctly.
	 */
	public User initUser(String userData)
	{
		String[] userInput = userData.split(Pattern.quote("|"));
		User user = new User();
		if(!dbHandler.containsUser(userInput[0]))
		{
			dbHandler.insertUser(userData);	
			user = new User(userInput[0], userInput[1], userInput[2], "");
		}
		else
			user = new User(userInput[0], userInput[1], userInput[2], dbHandler.getBookList(userInput[0]));

		return user;
	}

	/**
	 * Checks to see if the user is found in the USER table, adds the book to the BOOK table,
	 * updates/creates the USER booklist, checks to see if the SellerID is found in order to
	 * update the the booklist and sellers list. Finally, it inserts the passes the parameters
	 * to populate the PRICES table.
	 * @param  bookData  a string containing the following information the this order;
	 *                   isbn|title|author|user email|price
	 * @return A test object inorder to endure that the data was binded correctly.
	 */
	public Book initBook(String bookData)
	{
		String[] bookInput = bookData.split(Pattern.quote("|"));
		/*for(String s : bookInput)
			System.out.println(s);*/
		if(!hasUser(bookInput[3]))
			return null;
		if(!dbHandler.containsBook(bookInput[1]))
		{	
			dbHandler.insertBook(bookData);
			if(!dbHandler.containsBookList(bookInput[3]))
			{
				dbHandler.createBookList(bookInput[3], bookInput[1]);
			}
			else
				dbHandler.updateUserBookList(bookInput[3], bookInput[1]);		
		}
		else if(!dbHandler.containsSellerID(bookInput[3], bookInput[1]))
		{
			dbHandler.updateUserBookList(bookInput[3], bookInput[1]);
			dbHandler.updateBookSellers(bookInput[1], bookInput[3]);
		}

		dbHandler.insertPrice(bookInput[4], bookInput[1], bookInput[3]);
		return new Book(bookInput[0], bookInput[1], bookInput[2], dbHandler.getUserID(bookInput[3]));
	}

	/**
	 * Checks to see if the user is found in the USER table, checks if the book is found in the
	 * BOOK Table and removes the book from the database.
	 * @param  bookData  a string containing the following information the this order;
	 *                   isbn|title|author|user email|price
	 * @return False if the user is found or true if the book was succesfully removed.
	 */
	public boolean delBook(String bookData)
	{
		String[] bookInput = bookData.split(Pattern.quote("|"));
		/*for(String s : bookInput)
			System.out.println(s);*/
		if(!hasUser(bookInput[3]))
			return false;
		if(dbHandler.containsBook(bookInput[1]))
			dbHandler.removeBook(bookData);	
		return true;
	}

	/**
	 * Gets the sellers that are associated with the query.
	 * @param  a query containing the following information that can be;
	 *         isbn: isbn #
	 *         title: book title
	 *         author: book author
	 *                   
	 * @return a formatted version of the sellers list to be passed back to the client.
	 */
	public String getBookSellers(String query)
	{
		String users = "";
		String title = dbHandler.getBookTitle(query);
		if(title.equalsIgnoreCase(""))
			return "";
		System.out.println(title);
		String[] list = dbHandler.getUsersEmailFromBook(dbHandler.getSellersFromQuery(query));
		for(int i = 0; i < list.length; i++)
			users +=  title + "|" + dbHandler.getUser(list[i]).toString() + "|" + dbHandler.getPrice("user%"+list[i]) + "%";
		return users;
	}

	/**
	 * Gets the booklist the is associated with the user, and formats a new string to be passed
	 * to the BookServerThreadHandler.
	 * @param  email of the user
	 * @return a formatted version of the booklist.
	 */
	public String getUserBookList(String email)
	{
		String[] bookIDs = dbHandler.getBookIDs(email);
		String list = "";
		for(int i = 0; i < bookIDs.length; i++)
			list += dbHandler.getBook(Integer.parseInt(bookIDs[i].trim())).toString() + "|" +
			        dbHandler.getPrice("title%" + dbHandler.getBookTitle(Integer.parseInt(bookIDs[i].trim())) + "%" + email) + "%";
		return list;
	}

	/**
	 * Gets the toString value of the book object.
	 * @param  title of the book to be returned.
	 * @return a toString value of the book object.
	 */
	public String getBook(String title)
	{
		return dbHandler.getBook(Integer.parseInt(dbHandler.getBookID(title))).toString();
	}

	/**
	 * Returns if the user is found in the database.
	 * @param  email of the user.
	 * @return true/false
	 */
	public boolean hasUser(String email)
	{
		return dbHandler.containsUser(email);
	} 

	/**
	 * Opens the database connection and populates accordingly. 
	 * @return true if a connection was established or false if it could not connect.
	 */
	public boolean openDB()
	{
		dbHandler = new DBQueryHandler();
		if(dbHandler.connect())
		{
			dbHandler.initDB();
			dbHandler.createTables();
			return true;
		}
		return false;
	}

	/**
	 * Closes the database connection
	 */
	public void closeDB()
	{
		dbHandler.close();
	}

	/*public void printDB()
	{
		dbHandler.printDB();
	}*/
}