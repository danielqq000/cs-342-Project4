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
/*
 * Thread safety issues:
 * 1. Access to shared resources:
 *    - The 'clients' ArrayList is accessed by multiple threads (TheServer and ClientThread threads)
 *      without synchronization, leading to potential concurrent modifications and data inconsistencies.
 *      (Lines: 16, 30, 46, 57, 61, 68)
 * 2. ArrayList usage:
 *    - The 'clients' ArrayList is not thread safe, and concurrent modifications can occur from multiple threads.
 *      (Lines: 16, 30, 46, 57, 61, 68)
 * 3. Non-atomic operations:
 *    - The 'count' variable is accessed and modified by multiple threads without proper synchronization,
 *      leading to potential race conditions and lost updates. (Lines: 11, 28, 39, 58)
 * 4. Resource cleanup:
 *    - The 'ClientThread' does not properly close the ObjectInputStream, ObjectOutputStream, and Socket resources,
 *      leading to potential resource leaks. (Lines: 73, 75, 77)
 *
 * Potential issues if not fixed:
 * - Race conditions, data inconsistencies, and lost updates due to concurrent modifications of shared resources.
 * - Concurrent modification exceptions or other unexpected behavior due to unsafe ArrayList usage.
 * - Incorrect count tracking and potential inconsistencies in client count.
 * - Resource leaks and potential overuse of system resources.
 *
 * It is necessary to properly synchronize access to shared resources, use thread-safe data structures, ensure atomicity
 * of operations, and properly clean up resources to make the code thread safe.
 */

public class Server {
	// Privatized variables
	private int count = 1;
	private ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	private TheServer server;
	private Consumer<Serializable> callback;

	Server(Consumer<Serializable> call) {
		callback = call;
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

	// Method to broadcast a message to all connected clients
	private synchronized void broadcast(String message) {
		for (ClientThread client : clients) {
			client.sendMessage(message);
		}
	}

	public class TheServer extends Thread {

		public void run() {

			try (ServerSocket mysocket = new ServerSocket(5555);) {
				//System.out.println("Server is waiting for a client!");
				callback.accept("Server is waiting for a client!");

				while (true) {

					ClientThread c = new ClientThread(mysocket.accept(), count);
					callback.accept("client has connected to server: " + "client #" + count);
					addClient(c); // Call to addClient method to add the client to the clients ArrayList
					c.start();

					count++; // Incrementing count in a thread-safe manner

				}
			} catch (Exception e) {
				callback.accept("Server socket did not launch");
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

		public void run() {

			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);

				broadcast("new client on server: client #" + count);

				while (true) {
					try {
						String data = in.readObject().toString();
						callback.accept("client: " + count + " sent: " + data);
						broadcast("client #" + count + " said: " + data);

					} catch (Exception e) {
						callback.accept(
								"Something wrong with the socket from client: " + count + "....closing down!");
						broadcast("Client #" + count + " has left the server!");
						removeClient(this); // Call to removeClient method to remove the client from the clients ArrayList
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


	
	

	
