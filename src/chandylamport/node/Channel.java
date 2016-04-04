package chandylamport.node;

import chandylamport.message.Message;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A communication channel with another node.
 *
 * In order to demonstrate the Chandy Lamport snapshot algorithm, the channel stores messages without sending them straight away.
 * Instead, periodically sending the messages in the channels.
 */
public class Channel {
	
	private Queue<Message> queue;
	private NodeSender sender;
	
	private final Object lock = new Object();
	
    /**
     * Constructs a new channel with a specified destination node.
     *
     * @param port  The port number of the node that this channel is owned by.
     * @param destinationPort  The port number of the destination node.
     */
	public Channel(int port, int destinationPort) throws UnknownHostException, IOException {
		 this.queue = new LinkedList<Message>();
		 this.sender = new NodeSender(port, destinationPort);
		 
		 Thread thread = new Thread() {
			public void run() {
				while(true) {
					synchronized (lock) {
						while (!queue.isEmpty()) {
							try {
								sender.send(queue.remove());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
					try {
						Thread.sleep(3000);	// Wait a second to simulate message transport
					} catch (InterruptedException e) {
                        // We don't care if the messages get sent early
                    }
				}
			}
		 };
		 thread.start();
	}
	
    /**
     * Sends a new message by putting it in the channel.
     *
     * @param message  The message to send.
     */
	public void put(Message message) {
		synchronized (lock) {
			this.queue.add(message);
		}
	}
}
