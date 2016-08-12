import java.util.regex.Pattern;

public class DBTester
{
	public static void main(String[] args) 
	{
		String userInput = "bob@example.com|Bob Burgers|8773934448";
		String userInput2 = "dan@anotheremail.com|Dan Dannington|3478935567";
		String bookInput = "1234567890|Book|TestAuthor|bob@example.com|47.99";
		String bookInput2 = "0987654321|AnotherBook|TestAuthor2|dan@anotheremail.com|25.00";
		String bookInput3 = "0000000000|AnotherBook2|TestAuthor3|dan@anotheremail.com|100";
		String bookInput4 = "0987654321|AnotherBook|TestAuthor2|bob@example.com|20.00";
		String bookInput5 = "0000000000|AnotherBook2|TestAuthor3|bob@example.com|95.00";
		String bookInput6 = "0073220345|Object-Oriented Software Engineering|Timothy Lethbridge; Robert Laganiere|bob@example.com|120";
		String query = "isbn:0987654321";
		String query2 = "title:AnotherBook2";
		String query3 = "author:TestAuthor2";
		String query4 = "bob@example.com";
		ModelHandler mh = new ModelHandler();
		mh.openDB();
		mh.initUser(userInput);
		mh.initUser(userInput2);
		mh.initBook(bookInput);
		mh.initBook(bookInput2);
		mh.initBook(bookInput3);
		mh.initBook(bookInput4);
		mh.initBook(bookInput5);
		//mh.printDB();
		System.out.println(mh.getBookSellers(query));
		System.out.println(mh.getBookSellers(query2));
		System.out.println(mh.getBookSellers(query3));
		//System.out.println(mh.getUserBookList(query4));
		String[] temp = mh.getUserBookList(query4).replace("|", "\r\n").split(":");
		for(int i = 0; i < temp.length; i++)
			System.out.println(temp[i]);
		mh.closeDB();
	}
}