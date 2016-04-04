package chandylamport.node;

import chandylamport.message.Message;
import chandylamport.message.MessageType;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.JSONObject;

/**
 * Provides the ability to send messages to a single other node in the network.
 */
public class NodeSender {

	private Socket connection;
	private int port;

    /**
     * Constructs a new NodeSender.
     *
     * @param port  The port number of this node.
     * @param destinationPort  The port number of the node to send messages to.
     */
	public NodeSender(int port, int destinationPort) throws UnknownHostException, IOException {
		this.port = port;
		connection = new Socket("127.0.0.1", destinationPort); // Connecting to localhost
	}

    /**
     * Sends a message to the node.
     *
     * @param message  The message to send.
     */
	public void send(Message message) throws IOException {
        // Messages are sent as JSON objects of the following form
        // { type: "TRANSFER", sender: "5000", amount: "123" }
        // { type: "MARKER", sender: "5000" }
		PrintWriter out = new PrintWriter(connection.getOutputStream());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", message.getType().toString());
		jsonObject.put("sender", this.port);
		
		if (message.getType() == MessageType.TRANSFER) {
			jsonObject.put("amount", message.getAmount());
		}
		out.println(jsonObject.toString());
		out.flush();
		
		if (message.getType() == MessageType.MARKER) {
			System.out.println("Sent marker to port " + connection.getPort());
		}
	}

}
