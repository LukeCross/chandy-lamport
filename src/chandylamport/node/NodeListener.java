package chandylamport.node;

import chandylamport.BankAccount;
import chandylamport.message.Message;
import chandylamport.message.MessageType;
import chandylamport.snapshot.Snapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.json.JSONObject;

/**
 * Listens for and handles messages from other nodes in the network.
 */
public class NodeListener extends Thread {
	
	private BankAccount account;
	private int port;
	private Snapshot snapshot;
    private List<Integer> ports;

    private final Object lock = new Object();
	
    /**
     * Constructs a new NodeListener.
     *
     * @param account  The bank account to deposit funds into.
     * @param port  The port number to listen on (i.e. the port number of this node).
     * @param ports  The list of ports, required if this listener receives a "START" message.
     */
	public NodeListener(BankAccount account, int port, List<Integer> ports) {
		this.account = account;
		this.port = port;
        this.ports = ports;
	}
	
    /**
     * Informs this NodeListener of a snapshot in progress, allowing it to alter it's message handling to accommodate the snapshot.
     *
     * @param snapshot  The snapshot.
     */
	public void setSnapshot(Snapshot snapshot) {
        synchronized (lock) {
            this.snapshot = snapshot;
        }
	}
	
    /**
     * Listens to and handles messages from other nodes in the network.
     */
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(port)){
			
			while (true) {
				Socket connection = serverSocket.accept();
				SingleNodeListener thread = new SingleNodeListener(connection);
				thread.start();
			}
			
		} catch (IOException e) {
			System.err.println("Unable to listen on port: " + port);
            System.exit(0);
		}
	}

    /**
     * Listens for messages from a single other node in the network.
     */
    private class SingleNodeListener extends Thread {
        private Socket connection;

        /**
         * Constructs an instance of SingleNodeListener.
         *
         * @param connection  The socket connected to the other node in the network.
         */
        public SingleNodeListener(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while (true) {
                    // Messages are sent as JSON objects of the following form
                    // { type: "TRANSFER", sender: "5000", amount: "123" }
                    // { type: "MARKER", sender: "5000" }
                    // { type: "START", sender: "5000" }
                    String line = in.readLine();

                    // If readLine returns null then we've reached the end of the stream
                    // Indicating that another node in the network has exited, which should only happen when the user tells it to
                    // So we should also exit, to make the users life easier
                    if (line == null) {
                        System.out.println("Another node has exited, so I am exiting too.");
                        System.exit(0);
                    }

                    JSONObject jsonObject = new JSONObject(line);
                    MessageType type = MessageType.valueOf(jsonObject.getString("type"));
                    int sender = jsonObject.getInt("sender");
                    
                    if (type == MessageType.START) {
                        Node node = account.getNode();
                        if (!node.isSetup()) {
                            node.setup(ports);
                            account.start();
                        }
                    } else if (type == MessageType.TRANSFER) {
                        int amount = jsonObject.getInt("amount");
                        synchronized (lock) {
                            if (snapshot != null) {
                                Message message = new Message(type);
                                message.setAmount(amount);
                                snapshot.receiveMessage(sender, message);
                            }
                        }
                        account.receive(amount);
                        System.out.println("Received " + amount + " from port " + sender);
                        
                    } else if (type == MessageType.MARKER) {
                        System.out.println("Received marker from port " + sender);
                        synchronized (lock) {
                            if (snapshot == null) {
                                snapshot = new Snapshot(account, account.getNode());
                            }
                            boolean snapshotFinished = snapshot.receiveMarker(sender);
                            if (snapshotFinished) {
                                snapshot = null;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Unable to receive on port: " + connection.getPort());
            }
        }
    };

}
