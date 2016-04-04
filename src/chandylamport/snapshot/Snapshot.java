package chandylamport.snapshot;

import chandylamport.BankAccount;
import chandylamport.message.Message;
import chandylamport.message.MessageType;
import chandylamport.node.Node;

import java.io.IOException;
import java.util.HashMap;

/**
 * Provides the functionality required for the Chandy Lamport snapshot algorithm.
 */
public class Snapshot {
	
	private SnapshotState state;
	private Node node;
	private HashMap<Integer, Boolean> markersReceived;
	
    /**
     * Constructs a new snapshot.
     *
     * @param account  The bank account to gather state information from.
     * @param node  The node that this snapshot exists on.
     */
	public Snapshot(BankAccount account, Node node) throws IOException {
        System.out.println("Starting snapshot");

		this.state = new SnapshotState(node, account);
		this.node = node;
		this.markersReceived = new HashMap<Integer, Boolean>();
		for (int port : this.node.getDestinationPorts()) {
			this.markersReceived.put(port, false);
		}
		sendMarkers();
	}
	
    /**
     * Should be called whenever a marker is received from another node in the network.
     *
     * @param port  The port number of the node that sent the marker.
     */
	public boolean receiveMarker(int port) {
		this.markersReceived.put(port, true);
		
        // Check whether all markers have been received (i.e. snapshot is finished)
		for (boolean check : this.markersReceived.values()) {
			if (!check)
				return false;
		}
		
		System.out.println(state.toString());
		return true;
	}
	
    /**
     * Should be called whenever a transfer message is received from another node in the network.
     *
     * @param port  The port number of the node that sent the message.
     * @param message  The message that was received.
     */
	public void receiveMessage(int port, Message message) {
		if (!markersReceived.get(port))
			this.state.addReceivedMessage(port, message);
	}
	
    /**
     * Sends marker messages to all other nodes in the network.
     */
	private void sendMarkers() throws IOException {
		for (int port : this.node.getDestinationPorts()) {
			Message message = new Message(MessageType.MARKER);
			this.node.send(port, message);
		}
	}

}
