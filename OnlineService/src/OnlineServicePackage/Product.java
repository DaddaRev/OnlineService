package OnlineServicePackage;

public class Product {
	
	private String name = null;
	private double price = 0;
	private int id = 0;
	private static int id_counter = 1000;
	
	public Product()
	{
		//Invalid Object
	}
	
	public Product(String n, double d) {
		this.name = n;
		this.price = d;
		this.id = id_counter;
		Product.id_counter ++; 		//automatic id setting on each new product
	}
	
	public Product(String n, double d, int id)
	{
		this.name = n;
		this.price = d;
		this.id = id;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public double getPrice()
	{
		return this.price;
	}
	
	public int getId()
	{
		return this.id;
	}
	
}
