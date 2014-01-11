/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */

package switched_network;

import java.net.InetAddress;

/**
 *
 * Interface defining network card services.
 *
 * @author K.  Bryson.
 */
public interface NetworkCard {

    /*
     * Get the IP Address of the network card.
     */
    public InetAddress getIPAddress();

    
    /*
     * This simulates the connection of an Ethernet port
     * of an Ethernet Switch to be attached by an Ethernet
     * cable to this computer network card.
     */
    public void connectPort(SwitchPort lanPort);

    
    /*
     * This method is USED BY THE NETWORK SWITCH (or SwitchPort)
     * to send a packet of data from the network to this computer.
     */
    public void sendToComputer(byte[] packet);

}
