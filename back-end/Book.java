public class Book
{
	private String isbn;
	private String title;
	private String author;
	private String sellers;

	public Book(){}

	public Book(String isbn, String title, String author, String sellers)
	{
		this.isbn = isbn;
		this.title = title;
		this.author = author;
		this.sellers = sellers;
	}

	public void setISBN(String isbn)
	{
		this.isbn = isbn;
	}

	public String getISBN()
	{
		return this.isbn;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return this.title;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getAuthor()
	{
		return this.author;
	}

	public void setSellers(String sellers)
	{
		this.sellers = sellers;
	}

	public String getSellers()
	{
		return this.sellers;
	}

	public String toString()
	{
		return this.isbn + "|" + this.title + "|" + this.author;
	}
}
