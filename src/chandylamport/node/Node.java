package chandylamport.node;

import chandylamport.message.Message;
import chandylamport.message.MessageType;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A single node in a network of nodes.
 */
public class Node {

	private HashMap<Integer, Channel> channels;
	private int port;
    private boolean setup = false;
	
    /**
     * Constructs a new node.
     *
     * @param port  The port number of this node.
     */
	public Node(int port) {
		this.port = port;
	}
	
    /**
     * Initialises the communication channels with the other nodes in the network.
     *
     * This must only be called once the other nodes have started listening for connections.
     *
     * @param ports  A list of the port numbers of the other nodes in the network.
     */
	public void setup(List<Integer> ports) throws UnknownHostException, IOException {
		this.channels = new HashMap<Integer, Channel>(ports.size());

		for (int destinationPort : ports) {
			this.channels.put(destinationPort, new Channel(this.port, destinationPort));
		}

        this.setup = true;
	}

    /**
     * Returns true if the node has already been setup, false otherwise.
     */
    public boolean isSetup() {
        return this.setup;
    }
	
    /**
     * Sends a message to another node in the network.
     *
     * @param port  The port number of the node to send the messsage to.
     * @param message  The message to send.
     */
	public void send(int port, Message message) throws IOException {
		channels.get(port).put(message);
	}
	
    /**
     * Gets the list of port numbers of the other nodes in the network.
     */
	public List<Integer> getDestinationPorts() {
		return new ArrayList<Integer>(channels.keySet());
	}
	
    /**
     * Gets the port number of this node.
     */
	public int getPort() {
		return this.port;
	}

    public void startAllNodes() {
        Message startMessage = new Message(MessageType.START);
        for (Channel channel : this.channels.values()) {
            channel.put(startMessage);
        }
    }
	
}
