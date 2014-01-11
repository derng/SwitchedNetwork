/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */

package switched_network;

import java.net.InetAddress;

/**
 *
 * Interface defining operating system services.
 *
 * @author K. Bryson
 */
public interface ComputerOS {

    /*
     * Get the host name of the computer.
     */
    public String getHostname();

    /*
     * Ask the operating system to send this message
     * (byte array as the payload) to the computer with
     * the given IP Address.
     *
     * The message goes from a 'port' on this machine to
     * the specified port on the other machine.
     */
    public void send(byte[] payload, InetAddress ip_address_to, int port_from, int port_to);

    /*
     * This asks the operating system to check whether any incoming messages
     * have been received on the given port on this machine.
     *
     * If a message is pending then the 'payload' is returned as a byte array.
     * (i.e. without any network header information)
     */
    public byte[] recv(int port);

}
