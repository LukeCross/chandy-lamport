package chandylamport.message;

/**
 * A message to be sent from a node.
 *
 * The message can either be a fund transfer or a snapshot marker.
 */
public class Message {

	private MessageType type;
	private int amount;
	
    /**
     * Constructs a new message of the given type.
     *
     * If constructing a transfer message, setAmount should also be called.
     *
     * @param type  The type of the message.
     */
	public Message(MessageType type) {
		this.type = type;
	}
	
    /**
     * Gets the transfer amount of the message.
     *
     * Should only be used for transfer messages.
     */
	public int getAmount() {
		return amount;
	}

    /**
     * Sets the transfer amount of the message.
     *
     * Should only be used for transfer messages.
     */
	public void setAmount(int amount) {
		this.amount = amount;
	}

    /**
     * Gets the type of the message.
     */
	public MessageType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.amount);
	}
}
