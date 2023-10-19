
package OnlineServicePackage;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Server
{
	private static final int SPORT = 4444;
	private static final String FILENAME = "./src/OnlineServicePackage/Products.txt";
	
    public final Map<String, String> userMap = Map.of("User", "password", "root", "password");
    public List<Product> productsList = new ArrayList<>();

	public Server()  //Costructor reads a file to get products in the List
	{ 
		try {
            BufferedReader br = new BufferedReader(new FileReader(FILENAME));
            String line;

            while (!(line = br.readLine()).equals("-1")) 
            {
                String[] parts = line.split(" "); 
                if (parts.length == 2) 
                {
                    String productName = parts[0]; 
                    double productPrice = Double.parseDouble(parts[1]); 
                    productsList.add(new Product(productName, productPrice));
                }
            }

            br.close(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void products_showToClient(DataOutputStream os) throws IOException  //Function to send the productList in the Client buffer
	{
		for (Product product : this.productsList) 
		{
			os.writeBytes(product.getName()+" "+product.getPrice()+" "+product.getId()+"\n");
		}
		os.writeBytes("end\n");

	}
	
	public void product_sendToClient(DataOutputStream os, BufferedReader is) throws IOException  //Function to send a SINGLE product to the client
	{
		String product_name = is.readLine(); //Server is waiting for the name of the product
		boolean found = false;
		for(Product p : this.productsList)
		{
			if(p.getName().equals(product_name))
			{
				os.writeBytes(p.getName()+" "+p.getPrice()+" "+p.getId()+"\n"); //Sending the found product to the client
				productsList.remove(p);
				found = true;
				break;
			}
		}
		if(!found)
		{
			os.writeBytes("notfound\n");
		}
	}

	public void service()
	{
		try
		{
			ServerSocket server = new ServerSocket(SPORT);

			boolean authentication = false;
			String client_user = "";
			String password_client = "";
			String command = "";
			
			while(true)  
			{
				System.out.println("Waiting for a connection\n");
				Socket client = server.accept();
				System.out.println("Connected");
				authentication = false;

				BufferedReader   is = new BufferedReader(new InputStreamReader(client.getInputStream()));
				DataOutputStream os = new DataOutputStream(client.getOutputStream());
				
				os.writeBytes("Insert User:\n");
				client_user = is.readLine();
				
				//Checking if user has entered the right credentials
				for (java.util.Map.Entry<String, String> e: userMap.entrySet())
				{
					if(client_user.equals(e.getKey()))
					{
						os.writeBytes("Insert Password:\n");
						password_client = is.readLine();
						
						if(password_client.equals(e.getValue())) {
							os.writeBytes("ok\n");
							authentication = true;
							break;
						}
					}
				}
				if(authentication) //if authentication is propertly done, service is ready to be used
				{
					boolean closed_client = false;
					while(!closed_client)
					{
						System.out.println("Waiting for input command\n");
						command = is.readLine();
						
						switch (command) {
						case "s": this.products_showToClient(os); break;		//Show the list of available products
						case "r": this.product_sendToClient(os,is); break;		//Send a requested produt to the client
						case "close":
						{
							//Close the client connection
							closed_client = true;
							client.close();
							break;
						}
						case "quit":
						{
							//Close the service (and the connected client)
							client.close();
							server.close();
							System.out.println("\nSERVICE CLOSED");
							return;
						}
						default:
							//Invalid command is handled by the client
							break;
						}
						
					}
					
				}else {
					System.out.println("Invalid user or password, closing the client..");
					os.writeBytes("Invalid user or password, closing the connection..");
					client.close();
					continue;
				}
			}			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(final String[] args)
	{
		Server server = new Server();
		server.service();
	}
}
