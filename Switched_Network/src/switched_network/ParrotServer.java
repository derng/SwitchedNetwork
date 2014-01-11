/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */

package switched_network;

/**
 *
 * Parrot Server which listens to a specified port on the local
 * machine and essentially repeats to the screen anything it
 * hears on that port.
 *
 * @author K. Bryson
 */
public class ParrotServer extends Application {

    /**
     * Port which this application listens to.
     */
    private int listenPort;

    public ParrotServer(ComputerOS computerOS, int listenPort) {

        super("Parrot Server v1.0", computerOS);

        this.listenPort = listenPort;

    }


    @Override
    public void run() {

        // Print out application information using the default application.
        super.run();

        while (true) {

            try {
                sleep(100);
            } catch (InterruptedException except) {
                // Ignore.
            }

            // This checks whether there is a message to receive on this port.
            byte[] message = getComputerOS().recv(listenPort);

            // If messages exists then print it out.
            if (message != null) {

                String msgString = new String(message);
                System.out.println("Computer " + getComputerOS().getHostname() + " received message: " + msgString + "\n");
                
            }

        }

    }

}
