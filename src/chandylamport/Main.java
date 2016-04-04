package chandylamport;

import chandylamport.node.Node;
import chandylamport.node.NodeListener;
import chandylamport.snapshot.Snapshot;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A program that uses bank accounts to demonstrate the Chandy Lamport distributed snapshot algorithm.
 */
public class Main {

	public static void main(String[] args) throws UnknownHostException, IOException {
		if (args.length < 2)
			System.exit(0);
		
        // Parse the arguments
        int port = 0;
        List<Integer> ports = null;
		try {
			port = Integer.parseInt(args[0]);

			ports = new ArrayList<Integer>();
			for (int i = 1; i < args.length; i++) {
				ports.add(Integer.parseInt(args[i]));
			}
		} catch (NumberFormatException e) {
			System.out.println("Usage: Node myPort portToConnect1 ...");
			System.exit(0);
		}

        // Initialise the objects
        Node node = new Node(port);
        BankAccount account = new BankAccount(node, 500);
        NodeListener listener = new NodeListener(account, port, ports);
        listener.start();
        System.out.println("Node listening on port: " + port);
        System.out.println("Press enter on any node to start the bank transfers.\nThen press enter again to take a snapshot.\n");

        // Wait for the user to send commands
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String read = scanner.nextLine();
            if (read.equals("exit"))
                break;

            if (node.isSetup()) {
                // Take a snapshot
                Snapshot snapshot = new Snapshot(account, node);
                listener.setSnapshot(snapshot);
            } else {
                // Start transferring money to the other nodes
                node.setup(ports);
                account.start();

                // Inform the other nodes that they need to start too
                node.startAllNodes();
            }
        }
        
        scanner.close();
        System.exit(0);
	}

}
