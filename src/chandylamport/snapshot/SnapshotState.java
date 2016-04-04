package chandylamport.snapshot;

import chandylamport.BankAccount;
import chandylamport.message.Message;
import chandylamport.node.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Stores state information for a Chandy Lamport snapshot.
 */
public class SnapshotState {
	
	private int balance;
	private int port;
	private HashMap<Integer, Queue<Message>> receiveChannels;
	
    /**
     * Constructs a new instance of SnapshotState.
     *
     * @param node  The node that this snapshot is stored on.
     * @param account  The bank account to gather state information from.
     */
	public SnapshotState(Node node, BankAccount account) {
		this.receiveChannels = new HashMap<Integer, Queue<Message>>(node.getDestinationPorts().size());
		
		for (int p : node.getDestinationPorts()) {
			receiveChannels.put(p, new LinkedList<Message>());
		}
		
		this.port = node.getPort();
		this.balance = account.getBalance();
	}
	
    /**
     * Adds a message received from another node to the state.
     *
     * @param port  The port number of the node that sent the message.
     * @param message  The message that was sent.
     */
	public void addReceivedMessage(int port, Message message) {
		this.receiveChannels.get(port).add(message);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\nLocalState: " + this.balance);
		builder.append("\nChannels:");
		for (int port : receiveChannels.keySet()) {
			builder.append("\n\t" + port + " -> " + this.port + ": " + receiveChannels.get(port).toString());
		}
		builder.append("\n");
		return builder.toString();
	}
	
}
