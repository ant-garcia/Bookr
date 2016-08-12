import java.util.ArrayList;
import java.util.regex.Pattern;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DBQueryHandler
{

	static final String DB_URL = "jdbc:mysql://localhost:3306/";

	static final String USER = "bookr_db";
	static final String PASS = "csc380";

	static Connection conn;

	/**
	 * Tries to establish a connection with MYSQL.
	 * @return whether or not the connection was established.
	 */
	public boolean connect()
	{
		boolean isConnected = true;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			isConnected = false;
		}
		catch(ClassNotFoundException f)
		{
			f.printStackTrace();
			isConnected = false;
		}
		return isConnected;
	}

	/**
	 * Closes the connection.
	 */
	public void close()
	{
		try
		{
			conn.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Checks to see if the BOOK table contains this title.
	 * @param  query: the title of the book 
	 * @return true/false 
	 */
	public boolean containsBook(String query)
	{
		boolean result = false;
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT title from BOOK WHERE " +
						 "title  = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, query);
			ResultSet rs = stmt.executeQuery();

			while(rs.next())
				if(rs.getString("title") != null)
					result = true;
			
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Checks if the USER table contains this email.
	 * @param  query: the email of this user
	 * @return true/false
	 */
	public boolean containsUser(String query)
	{
		boolean result = false;
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT email from USER WHERE " +
						 "email  = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, query);
			ResultSet rs = stmt.executeQuery();

			while(rs.next())
				if(rs.getString("email") != null)
					result = true;

			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Checks if this email has a booklist tied to it.
	 * @param  query: the email of this user
	 * @return true/false
	 */
	public boolean containsBookList(String query)
	{
		boolean result = false;
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT book_list from USER WHERE " +
						 "email  = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, query);
			ResultSet rs = stmt.executeQuery();

			while(rs.next())
				if(rs.getString("book_list") != null)
					result = true;
			
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Checks if the email has a booklist and checks the BOOK table to see if the title is present.
	 * @param  email of the user
	 * @param  title of the book to be checked in the user's booklist
	 * @return true/false
	 */
	public boolean containsSellerID(String email, String title)
	{
		if(getBookList(email).equalsIgnoreCase(""))
			return false;
		String[] bookIDs = getBookList(email).split(",");
		for(int i = 0; i < bookIDs.length; i++)
			if(bookIDs[i].equalsIgnoreCase(getBookID(title)))
				return true;
		return false;
	}

	/**
	 * Initializes the database.
	 */
	public void initDB()
	{
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS s15_bookr_db");
			stmt.executeUpdate("USE s15_bookr_db");
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates the databases tables.
	 */
	public void createTables()
	{
		String sql;
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			sql = "create table if not exists USER (id INTEGER AUTO_INCREMENT, " +
				  "email VARCHAR(255), name VARCHAR(255), " +
				  "phone VARCHAR(255), book_list VARCHAR(255), " +
				  "primary key(id))";
			stmt.executeUpdate(sql);
			sql = "create table if not exists BOOK (id INTEGER AUTO_INCREMENT, " +
				  "isbn VARCHAR(255), title VARCHAR(255), author VARCHAR(255), sellers VARCHAR(255), " +
				  "PRIMARY KEY(id))";
		  	stmt.executeUpdate(sql);
		  	sql = "create table if not exists PRICES (id INTEGER AUTO_INCREMENT, " +
				  "price VARCHAR(255), title VARCHAR(255), user VARCHAR(255), " +
				  "PRIMARY KEY(id))";
		  	stmt.executeUpdate(sql);
	  		stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Inserts the parameters into the PRICES table.
	 * @param price of the book
	 * @param title of the book
	 * @param user that is selling the book
	 */
	public void insertPrice(String price, String title, String user)
	{
		PreparedStatement stmt = null;
		try
		{
			String sql = "INSERT INTO PRICES " +
						 "(price, title, user) " + 
						 "VALUES (?, ?, ?)";

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, price);
			stmt.setString(2, title);
			stmt.setString(3, user);
			stmt.execute();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}	
	}

	/**
	 * Gets the price of the book
	 * @param  query that is to be parsed
	 * @return the price of the book listed
	 */
	public String getPrice(String query)
	{
		System.out.println(query);
		String price = "";
		String column;
		String[] inputs = query.split("%");
		PreparedStatement stmt = null;
		try
		{
			if(inputs[0].equalsIgnoreCase("title"))
				column = "title";
			else
				column = "user";
			String sql = "";
			if(inputs.length > 2)
			{
				sql = "SELECT price from PRICES where user = ? " + 
			   		  "AND title = ?";
	   		  	stmt = conn.prepareStatement(sql);
	   		  	stmt.setString(1, inputs[2]);
	   		  	stmt.setString(2, inputs[1]);
			}
			else
			{
				sql = "SELECT price from PRICES WHERE " + 
				      column + " = ?";
	      		stmt = conn.prepareStatement(sql);
		      	stmt.setString(1, inputs[1]);
			}
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				price = rs.getString("price");
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return price;
	}

	/**
	 * Inserts this User into the USER table
	 * @param user string formatted as; email|name|phone #
	 */
	public void insertUser(String user)
	{
		String[] input = user.split(Pattern.quote("|"));
		PreparedStatement stmt = null;
		try
		{
			String sql = "INSERT INTO USER " +
						 "(email, name, phone) " + 
						 "VALUES (?, ?, ?)"; 
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, input[0]);
			stmt.setString(2, input[1]);
			stmt.setString(3, input[2]);
			stmt.execute();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Creates the user's booklist that is tied to this email
	 * @param email of the user
	 * @param book id to be added to the booklist
	 */
	public void createBookList(String email, String title)
	{
		
		PreparedStatement stmt = null;
		try
		{
			String sql = "UPDATE USER " +
						 "SET book_list = ? " + 
						 "WHERE email = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, getBookID(title)); 
			stmt.setString(2, email); 
			stmt.execute();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Updates the user's booklist that is tied to this email
	 * @param email of the user
	 * @param book id to be added to the booklist
	 */
	public void updateUserBookList(String email, String book)
	{
		PreparedStatement stmt = null;
		try
		{
			String sql = "UPDATE USER " +
						 "SET book_list = ? " + 
						 "WHERE email = ?";
			stmt = conn.prepareStatement(sql);
		 	stmt.setString(1, getBookList(email).concat("," + getBookID(book)));
		 	stmt.setString(2, email);
			stmt.execute();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Returns a User object that is tied to the email
	 * @param  email of the user
	 * @return an User object
	 */
	public User getUser(String email)
	{
		User u = null;
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT * from USER WHERE " + 
						 "email = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
			{
				u = new User(rs.getString("email"), rs.getString("name"), 
							 rs.getString("phone"), rs.getString("book_list"));
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return u;
	}

	/**
	 * Gets the user id that is tied to this email
	 * @param  email of the user
	 * @return the user's id
	 */
	public String getUserID(String email)
	{
		String id = "";
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT id from USER WHERE " + 
						 "email = ?";
			stmt = conn.prepareStatement(sql);
		 	stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				id = new String(rs.getString("id"));
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return id;
	}

	/**
	 * Gets the email of this user
	 * @param  id of the user
	 * @return the user's email
	 */
	public String getUserEmail(int id)
	{
		System.out.println(id);
		String email = "";
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT email from USER WHERE " + 
						 "id = ?";
			stmt = conn.prepareStatement(sql);
		 	stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				email = new String(rs.getString("email"));
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return email;
	}

	/**
	 * Gets the emails that are tied to the books sellers 
	 * @param  sellers that are tied to this book
	 * @return a list of users emails
	 */
	public String[] getUsersEmailFromBook(String sellers)
	{
		String emails[] = sellers.split(",");
		String list[] = new String[emails.length];
		for(int i = 0; i < emails.length; i++)
			list[i] = getUserEmail(Integer.parseInt(emails[i].trim()));
		return list;
	}

	/**
	 * Gets the booklist tied to this users email
	 * @param  email of the user
	 * @return the user's booklist
	 */
	public String getBookList(String email)
	{
		String bookList = "";
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT book_list from USER WHERE " + 
						 "email = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				if((bookList = rs.getString("book_list")) != null)
					return bookList;			
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Creates a new row in the BOOK table
	 * @param book data the contains the values to create a new BOOK row
	 */
	public void insertBook(String book)
	{
		String[] input = book.split(Pattern.quote("|"));
		PreparedStatement stmt = null;
		try
		{
			String sql = "INSERT INTO BOOK " +
						 "(isbn, title, author, sellers) VALUES " +
						 "(?, ?, ?, ?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, input[0]);
			stmt.setString(2, input[1]);
			stmt.setString(3, input[2]);
			stmt.setString(4, getUserID(input[3]));
			stmt.execute();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Removes the book from the database.
	 * @param book that is to be removed
	 */
	public void removeBook(String book)
	{
		String[] input = book.split(Pattern.quote("|"));
		PreparedStatement stmt = null;
		try
		{
			removeFromBookList(getBookID(input[1]), getEmailFromPriceList(input[1]));

			String sql = "DELETE FROM BOOK " +
						 "WHERE title = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(2, input[1]);
			stmt.execute();
			stmt.close();

			sql = "DELETE FROM PRICES " +
						 "WHERE title = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(2, input[1]);
			stmt.execute();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Gets the email that is tied to the book title in the PRICES table
	 * @param  title of the book
	 * @return email of the user that is selling this book
	 */
	public String getEmailFromPriceList(String title)
	{
		String email = "";
		String bookId = "";
		PreparedStatement stmt = null;
		try
		{
			bookId = getBookID(title);
			String sql = "SELECT email FROM PRICES WHERE title = ? AND id = ?";
			stmt = conn.prepareStatement(sql);
		 	stmt.setString(1, title);
		 	stmt.setString(2, bookId);
		 	ResultSet rs = stmt.executeQuery();
			while(rs.next())
				email = new String(rs.getString("email"));
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return email;
	}

	/**
	 * Removes the id from the booklist and sets it to the email found in the USER table 
	 * @param id    to be removed
	 * @param email of the user to retrieve its booklist
	 */
	public void removeFromBookList(String id, String email)
	{
		String oldBookList = getBookList(email);
		String newBookList = delFromList(id, oldBookList);
		PreparedStatement stmt = null;
		try
		{
			String sql = "UPDATE USER SET book_list = ? WHERE email = ?";
			stmt = conn.prepareStatement(sql);
		 	stmt.setString(1, newBookList);
		 	stmt.setString(2, email);
		 	stmt.execute();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Deletes the id from the list
	 * @param  id   to be removed
	 * @param  list to be changed
	 * @return returns the new list
	 */
	private String delFromList(String id, String list)
	{
		return list.replace(id+",", "");
	}

	/**
	 * Updates the sellers of the book
	 * @param title of the book
	 * @param email of the seller to be added to the list
	 */
	public void updateBookSellers(String title, String email)
	{
		PreparedStatement stmt = null;
		try
		{
			String sql = "UPDATE BOOK SET sellers = ? WHERE title = ?";
			stmt = conn.prepareStatement(sql);
		 	stmt.setString(1, getSellers(title).concat("," + getUserID(email)));
		 	stmt.setString(2, title);
			stmt.execute();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Gets a Book object from the BOOK table
	 * @param  id of the book
	 * @return a Book object
	 */
	public Book getBook(int id)
	{
		Book b = null;
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT * from BOOK WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				b = new Book(rs.getString("isbn"), rs.getString("title"),
							 rs.getString("author"), rs.getString("sellers"));
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * Gets the title of the book using it's id.
	 * @param  id of the book
	 * @return title of the book
	 */
	public String getBookTitle(int id)
	{
		String title = "";
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT title from BOOK WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				title = rs.getString("title");
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return title;
	}

	/**
	 * Gets the title of the book associated with the query.
	 * @param  query to be parsed
	 * @return the title of the book
	 */
	public String getBookTitle(String query)
	{
		String column = "";
		String[] inputs = query.split("%");
		String title = "";
		PreparedStatement stmt = null;

		if(inputs[0].equalsIgnoreCase("isbn"))
			column = "isbn";
		else if(inputs[0].equalsIgnoreCase("title"))
			column = "title";
		else if(inputs[0].equalsIgnoreCase("author"))
			column = "author";
		try
		{
			String sql = "SELECT title from BOOK WHERE " +
						  column + " = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, inputs[1]);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				title = rs.getString("title");
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return title;
	}

	/**
	 * Gets the id of the book from the BOOK table using the title
	 * @param  title of the book
	 * @return the id of the book
	 */
	public String getBookID(String title)
	{
		String bookID = "";
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT id from BOOK WHERE title = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, title);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				bookID = Integer.toString(rs.getInt("id"));
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return bookID;
	}

	/**
	 * Checks if BOOK table has a user id associated with this book title
	 * @param  email of the user
	 * @param  title of the book
	 * @return true/false
	 */
	public boolean containsUserID(String email, String title)
	{
		String[] sellerIDs = getSellers(title).split(",");
		for(int i = 0; i < sellerIDs.length; i++)
			if(sellerIDs[i].equalsIgnoreCase(getUserID(email)))
				return true;
		return false;
	}

	/**
	 * Gets the sellers that are associated with the book title.
	 * @param  title of the book
	 * @return the list of sellers tied to the book title
	 */
	public String getSellers(String title)
	{
		String sellers = "";
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT sellers from BOOK WHERE title = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, title);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				sellers = new String(rs.getString("sellers"));
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return sellers;
	}

	/**
	 * Gets the sellers that are associated with the query.
	 * @param  query that needs to be parsed
	 * @return the list of sellers tied to the query
	 */
	public String getSellersFromQuery(String query)
	{
		String column = "";
		String[] inputs = query.split("%");
		String list = "";
		PreparedStatement stmt = null;

		if(inputs[0].equalsIgnoreCase("isbn"))
			column = "isbn";
		else if(inputs[0].equalsIgnoreCase("title"))
			column = "title";
		else if(inputs[0].equalsIgnoreCase("author"))
			column = "author";
		try
		{
			String sql = "SELECT sellers from BOOK WHERE " +
						  column + " = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, inputs[1]);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				list = rs.getString("sellers") + "\n";
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Gets the ids of the books that are tied to this email
	 * @param  email of the user
	 * @return an array of book ids
	 */
	public String[] getBookIDs(String email)
	{
		return getBookList(email).split(",");
	}

	/**
	 * Gets the user name tied to the email
	 * @param  email of the user
	 * @return the user name
	 */
	public String getUserName(String email)
	{
		String name = "";
		PreparedStatement stmt = null;
		try
		{
			String sql = "SELECT name from USER WHERE email = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			while(rs.next())
				name = rs.getString("email");
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return email;
	}

	/*public void printDB()
	{
		Statement stmt = null;
		try
		{
			stmt = conn.createStatement();
			String sql = "SELECT id, title, sellers from BOOK";
			System.out.println("Query: " + sql);
			ResultSet rs = stmt.executeQuery(sql);
			System.out.println("Selected records are:");
			int rowCount = 0;

			while(rs.next())
			{
				int id = rs.getInt("id");
				String name = rs.getString("title");
				System.out.println(id + " " + name);
				String[] sellers = getUsersEmailFromBook(rs.getString("sellers"));
				for(String s : sellers)
					System.out.println(s);
				++rowCount;
			}
			System.out.println("Total Records: " + rowCount);
			System.out.println();

			sql = "SELECT email, book_list from USER";
			System.out.println("Query: " + sql);
			rs = stmt.executeQuery(sql);
			System.out.println("Selected records are:");
			rowCount = 0;

			while(rs.next())
			{
				String email = rs.getString("email");
				String bookID = rs.getString("book_list");
				System.out.println(email + " " + bookID);
				++rowCount;
			}
			System.out.println("Total Records: " + rowCount);			
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}	
	}*/
}