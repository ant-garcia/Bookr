public class User
{
	private String email;
	private String name;
	private String phone;
	private String bookList;

	public User(){}

	public User(String email, String name, String phone, String bookList)
	{
		this.email = email;
		this.name = name;
		this.phone = phone;
		this.bookList = bookList;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getPhone()
	{
		return this.phone;
	}

	public void setBookList(String bookList)
	{
		this.bookList = bookList;
	}

	public String getBookList()
	{
		return this.bookList;
	}

	public String toString()
	{
		return this.email + "|" + this.name + "|" + this.phone;
	}
}