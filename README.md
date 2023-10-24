# OnlineService
Online service for the sale of tech products.

The application is based on two nodes: client and server. Their interaction is based on the use of sockets.
A user can interact via the client with the server, after authenticating with his username and file password. 
The server is created with an initial set of products given by an input file (products.txt) and usernames and password.
A product is described by: product name, price and identifier.
Products are deleted from the server when they are transferred to the customer (client).
