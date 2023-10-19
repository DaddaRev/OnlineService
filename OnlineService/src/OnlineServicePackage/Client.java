package OnlineServicePackage;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;


//Da chiedere: I prodotti da restituire devono per forza essere prodotti che sono stati acquistati in precedenza?
//			   La richiesta di mettere a disposizione nuovi prodotti è solo una richiesta o il server deve anche farlo decidendo autonomamente il prezzo?
//			   I prodotti con lo stesso nome devono essere sommati oppure il sistema tollera dei duplicati?
//			   Quando restituisco un prodotto devo specifiare anche il prezzo e l'id? (Controintuitivo?)

public class Client
{
	private static final int SPORT = 4444;
	private static final String SHOST = "localhost";
	
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
		}		
	}
	
	public void send()
	{
		Scanner s = new Scanner(System.in);
		
		try
		{
			Socket client = new Socket(SHOST, SPORT);
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
				System.out.println("\nSupported Commands:\n\n\"s\" -showlist -> show the list of available products\n\"r\" -request -> request a product\n"
						+ "\"c\" -close -> close the client connection\n\"q\" -quitService -> close the server\n");
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
					case "r":	//request a product to the server	
					{
						requestProduct(os,is,s);
						break;
					}
					case "c":
					{
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
					{
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
