/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */

package switched_network;

import java.net.InetAddress;

/**
 *
 * The 'Hello World Client' application which essentially
 * connects to a specified port on a specified computer
 * and sends a 'Hello World' message to this server.
 *
 * @author K. Bryson
 */
public class HelloWorldClient extends Application {

    /*
     * Specifies the IP address and Port number to
     * which this client should send a Hello World message.
     */
    private InetAddress toServerIPAddress;
    private int toPort;


    // Dedicated port number for this application.
    private int fromPort = 20000;

    // Dedicated message sent by this application.
    private String message = "Hello World!";


    public HelloWorldClient(ComputerOS computerOS, InetAddress toServerIPAddress, int toPort) {

        super("HelloWorldServer v1.0", computerOS);

        this.toServerIPAddress = toServerIPAddress;
        this.toPort = toPort;

    }


    public void run() {

        // Print out application information using default application.
        super.run();
     
        for (int i = 0; i < 100; i++) {

            try {
                sleep(5000);
            } catch (InterruptedException except) {
                // Ignore.
            }

            System.out.println("HelloWorldClient sending message to IP " + toServerIPAddress + " Port " + toPort + ": " + message);

            getComputerOS().send(message.getBytes(), toServerIPAddress, fromPort, toPort);

        }
    }
}
