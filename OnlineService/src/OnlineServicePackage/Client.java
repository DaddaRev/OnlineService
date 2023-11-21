package OnlineServicePackage;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//MANCA GESTIONE DELLE ECCEZIONI, chiusura del client e Javadoc

public class Client
{
	private static final int SPORT = 4444;
	private static final String SHOST = "localhost";
	public List<Product> purchasedProductsList = new ArrayList<>();
	
	public Client()
	{
	}

	public void requestProduct(DataOutputStream os,BufferedReader   is, Scanner s) throws IOException
	{
		System.out.println("\nDigit the name of the product you want to buy:\n");
		String product_name = s.nextLine();
		os.writeBytes(product_name + "\n");
		String server_answer = is.readLine();
		
		if(server_answer.equals("notfound"))
		{
			System.out.println("\nProduct not found!\n");
		}else
		{
			System.out.println("\nYou bought the product:\n"+server_answer+"\n");
			String[] parts = server_answer.split(" ");  //Adding the bought product to the local List.
            if (parts.length == 3) 
            {
                String productName = parts[0]; 
                double productPrice = Double.parseDouble(parts[1]); 
                int productID = Integer.parseInt(parts[2]);
                purchasedProductsList.add(new Product(productName, productPrice, productID));
            }
		}		
	}
	
	public void returnProduct(DataOutputStream os, BufferedReader is, Scanner s) throws IOException
	{
		System.out.println("\nList of purchased products:\n");
		for(Product p : this.purchasedProductsList)
		{
			System.out.println(p.getName()+" "+p.getPrice()+" "+p.getId());
		}
		System.out.println("\nDigit the name of the product you want to return:\n");
		
		String return_product = s.nextLine();
		for(Product p : this.purchasedProductsList)
		{
			if(return_product.equals(p.getName()))
			{
				os.writeBytes(p.getName()+" "+p.getPrice()+" "+p.getId()+"\n");
				System.out.println(return_product + " is successfully returned!\n");
				purchasedProductsList.remove(p);
				return;
			}
		}
		System.out.println("\nProduct not found!\n");
	}
	
	public void insertNewProduct(DataOutputStream os, BufferedReader is, Scanner s) throws IOException  
	{				
		try
		{
			System.out.println("\nDigit the name of the new product you want to add in the list:\n");
			String new_productName = s.nextLine();
			
			System.out.println("\nDigit the price of the new product you want to add: \n(Do not use the comma for decimal numbers)\n");
			double new_productPrice= Double.parseDouble(s.nextLine());
			
			os.writeBytes(new_productName+" "+new_productPrice+"\n");
			
			String rispostaString = is.readLine();
			
			if (rispostaString.equals("alreadyin"))
			{
				System.out.println("The product you are trying to add already exist!\nTry entering a new one..\n");
			}
			if(rispostaString.equals("ok"))
			{
				System.out.println("New product successfully added!\n");
			}
		}
		catch(NumberFormatException e)
		{
			System.out.println("\nInvalid price, do not use the comma for decimal prices\n");
		}
		
	}
	
	public void send()
	{
		Scanner s = new Scanner(System.in);
		
		try (Socket client = new Socket(SHOST, SPORT))
		{
			String user = "User\n";
			String password = "password\n";
			String data_in = "";
			String user_command = "";
			
			BufferedReader   is = new BufferedReader(new InputStreamReader(client.getInputStream()));
			DataOutputStream os = new DataOutputStream(client.getOutputStream());

			System.out.println(is.readLine()); //Reading "Insert User" from Server
			user = s.nextLine();

			os.writeBytes(user + "\n");

			System.out.println(is.readLine()); //Reading "Insert Password" from server
			password = s.nextLine();

			os.writeBytes(password + "\n");
			
			if(!is.readLine().equals("ok"))    //Reading the outcome of the authentication process
			{
				return;
			}else {
				System.out.println("Succesfully logged in\n");
			}
			
			while(true)
			{
				System.out.println("\nSupported Commands:\n\n\"s\" -showlist -> show the list of available products\n\"b\" -buy -> buy a product\n"
						+ "\"r\" -return -> return a purchased product\n\"a\" -add -> add a new product\n\"c\" -close -> close the client connection\n\"q\" -quitService -> close the server\n");
				System.out.println("Insert the operation:\n");
				user_command = s.nextLine();
				
				os.writeBytes(user_command + "\n"); 	//Sending the user command to the server to perform the request
				
				switch (user_command) {
					case "s": 	//Showlist of products
					{
						System.out.println("\nAvailable products: (Format: [ProductName Price ID])\n");
						data_in = is.readLine();    		//Reading from the server buffer stream
						while(!data_in.equals("end"))
						{
							System.out.println(data_in);
							data_in = is.readLine();
						}
						break;
					}
					case "b":	
					{			//Buy a product from the server	list
						requestProduct(os,is,s);
						break;
					}
					case "r":
					{			//Return one of the purchased products
						returnProduct(os, is, s);
						break;
					}
					case "a":
					{			//Add a new product to the server list
						insertNewProduct(os, is, s);
						break;
					}
					case "c":
					{			//Close the client
						System.out.println("\nAre you sure you want to quit? (y/n)\n");
						if(s.nextLine().equals("y"))
						{
							os.writeBytes("close\n");	//telling the server to shut down the client
							System.out.println("\nCONNECTION CLOSED");
							return;
						}
						break;
					}
					case "q":
					{			//Close the socket connection
						System.out.println("\nAre you sure you want to permanently stop running the service? (y/n)\n");
						if(s.nextLine().equals("y"))
						{
							os.writeBytes("quit\n");  	//telling the server to shut down the service
							System.out.println("\nCONNECTION CLOSED");
							return;
						}
						break;
					}
					default:
					{
						System.out.println("\nInvalid command, try entering a new one..\n");
						break;
					}
				}

			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally 
		{
			s.close();
		}
	}

	public static void main(final String[] args)
	{
		new Client().send();
	}
}
