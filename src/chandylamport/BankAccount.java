package chandylamport;

import chandylamport.message.Message;
import chandylamport.message.MessageType;
import chandylamport.node.Node;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * A single bank account that exposes functionality for transferring and receiving funds.
 *
 * Can be run as a thread to make random transfers or random amounts to other bank accounts.
 */
public class BankAccount extends Thread {

	private int balance;
	private Node node;
	
	private final Object lock = new Object();
	
    /**
     * Constructs a new bank account using the node that should be used to send transfer messages, and the initial balance.
     *
     * @param node  The node used to send messages to other banks.
     * @param balance  The starting balance.
     */
	public BankAccount(Node node, int balance) {
		this.node = node;
		this.balance = balance;
	}
	
    /**
     * Transfers a given amount to a specified bank account.
     *
     * @param port  The port number that identitifies the bank account (i.e. the node that it's running on).
     * @param amount  The amount to transfer.
     */
	private void transfer(int port, int amount) {
		try {
			synchronized (lock) {
				if ((balance - amount) < 0) {
					System.err.println("Insufficient funds");
				} else {
					Message message = new Message(MessageType.TRANSFER);
					message.setAmount(amount);
					this.balance -= amount;
					node.send(port, message);
				}
			}
		} catch (IOException e) {
			System.err.println("Unable to send money to: " + port);
		}
	}
	
    /**
     * Deposits the given amount in this bank account.
     *
     * @param amount  The amount to deposit.
     */
	public void receive(int amount) {
		synchronized (lock) {
			this.balance += amount;
		}
	}
	
    /**
     * Gets the current account balance.
     */
	public int getBalance() {
		synchronized (lock) {
			return this.balance;
		}
	}
	
    /**
     * Gets the node that this bank account uses to send messages to other bank accounts.
     */
	public Node getNode() {
		return this.node;
	}
	
    @Override
	public void run() {
		List<Integer> ports = node.getDestinationPorts();
		Random random = new Random();
		int minDelay = 1000;
		int maxDelay = 5000;
		
		while (true) {
			int amount = random.nextInt(100);
			int port = ports.get(random.nextInt(ports.size()));
			transfer(port, amount);
			
			System.out.println("Sent " + amount + " to port " + port);
			
			try {
				Thread.sleep(random.nextInt(maxDelay - minDelay) + minDelay);
			} catch (InterruptedException e) {
                // We don't care if a message gets sent earlier
            }
		}
	}

}
