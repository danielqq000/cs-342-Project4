import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.control.ListView;


public class Server {
	// Privatized variables
	private int count = 1;
	private ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	private TheServer server;
	private Consumer<Serializable> listcallback;
	private Consumer<Serializable> messagecallback;

	public Server(Consumer<Serializable> listcall, Consumer<Serializable> messagecall) {
		listcallback = listcall;
		messagecallback = messagecall;
		server = new TheServer();
		server.start();
	}

	// Method to add a client to the clients ArrayList
	private synchronized void addClient(ClientThread client) {
		clients.add(client);
	}

	// Method to remove a client from the clients ArrayList
	private synchronized void removeClient(ClientThread client) {
		clients.remove(client);
	}

	// Method to change client into a Integer List
	protected synchronized ArrayList<Integer> getClientNum() {
		ArrayList<Integer> clientNumbers = new ArrayList<Integer>();
		for(ClientThread client : clients) {
			clientNumbers.add(client.count);
		}
		return clientNumbers;
	}

	// Method to send the client List
	private synchronized void updateClientList() {
		ArrayList<Integer> clientNumbers = new ArrayList<Integer>();
		clientNumbers = getClientNum();

		listcallback.accept(clientNumbers);

		for (ClientThread client : clients) {
			client.sendClientList(clientNumbers);
		}
	}

	// Method to broadcast a message to all connected clients
	private synchronized void broadcast(String message) {
		for (ClientThread client : clients) {
			client.sendMessage(message);
		}
	}

	// Method to show message between client messages
	private synchronized void messageSent(String message, int sender, int[] receivers) {
		StringBuilder receiverList = new StringBuilder();
		for (int i = 0; i < receivers.length; i++) {
			int receiverId = receivers[i];
			for (ClientThread client : clients) {
				if (client.count == receiverId) {
					client.sendMessage(message);
					if (i > 0) {
						receiverList.append(", ");
					}
					receiverList.append("#").append(receiverId);
					break;
				}
			}
		}
		messagecallback.accept("Client #" + sender + " sent to " + receiverList.toString() +
				": " + message);
	}

	public class TheServer extends Thread {

		public void run() {

			try (ServerSocket mysocket = new ServerSocket(5555);) {
				//System.out.println("Server is waiting for a client!");
				messagecallback.accept("Server is waiting for a client!");

				while (true) {

					ClientThread c = new ClientThread(mysocket.accept(), count);
					messagecallback.accept("client has connected to server: " + "client #" + count);
					updateClientList();
					System.out.println("client #" + count + " has connected");
					addClient(c); // Call to addClient method to add the client to the clients ArrayList
					c.start();

					count++; // Incrementing count in a thread-safe manner

				}
			} catch (Exception e) {
				messagecallback.accept("Server socket did not launch");
			}
		}
	}



	class ClientThread extends Thread {
		// Privatized variables
		private Socket connection;
		private int count;
		private ObjectInputStream in;
		private ObjectOutputStream out;

		ClientThread(Socket s, int count) {
			this.connection = s;
			this.count = count;
		}

		// Method to send a message to the client
		public void sendMessage(String message) {
			try {
				out.writeObject(message);
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Method to send the updated client list to the client
		public void sendClientList(ArrayList<Integer> clientNumbers) {
			try {
				out.writeObject(clientNumbers);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {

			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);

				updateClientList();
				broadcast("new client on server: client #" + count);

				while (true) {
					try {
						String data = in.readObject().toString();
						messagecallback.accept("client #: " + count + " sent: " + data);

						// seperate data into three parts, sender, receiver, message
						String[] parts = data.split(" > ");
						int sender = Integer.parseInt(parts[0]);
						String[] receiversStr = parts[1].split(":")[0].split(" ");
						int[] receivers = new int[receiversStr.length];

						for (int i = 0; i < receiversStr.length; i++) {
							receivers[i] = Integer.parseInt(receiversStr[i]);
						}
						String message = parts[1].split(":")[1].trim();

						messageSent(message, sender, receivers);

					} catch (Exception e) {
						messagecallback.accept(
								"Something wrong with the socket from client: " + count + "....closing down!");
						broadcast("Client #" + count + " has left the server!");
						removeClient(this); // Call to removeClient method to remove the client from the clients ArrayList
						updateClientList();
						break;
					}
				}
			} catch (Exception e) {
				System.out.println("Streams not open");
			} finally {
				// Close the streams and the socket in a finally block to ensure they are closed
				try {
					if (in != null) {
						in.close();
					}
					if (out != null) {
						out.close();
					}
					if (connection != null && !connection.isClosed()) {
						connection.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
