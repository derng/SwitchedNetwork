/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */

package switched_network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This simulates a computer network consisting of two computers
 * with server application running on one machine and a
 * client application running on the other machine.
 *
 * The code should work with this setup ... although it should
 * also work with different network structures ...
 * (so 10 computers each running 10 applications!)
 *
 * @author K. Bryson.
 */
public class Main {


    public static void main(String[] args) {

        try {

            // Create 2 computers named 'A' and 'B' with specific IP addresses.
            Computer computerA = new Computer("A", InetAddress.getByName("1.2.3.4"));
            Computer computerB = new Computer("B", InetAddress.getByName("1.2.3.7"));

            // Create a network switch with 4 ports.
            NetworkSwitch networkSwitch = new NetworkSwitch(4);

            // This connects network card of ComputerA
            // to Port 0 on the Network Switch.
            SwitchPort port0 = networkSwitch.getPort(0);
            port0.connectNetworkCard((NetworkCard) computerA);
            computerA.connectPort(port0);
            
            // This connects network card of ComputerB
            // to Port 1 on the Network Switch.
            SwitchPort port1 = networkSwitch.getPort(1);
            port1.connectNetworkCard((NetworkCard) computerB);
            computerA.connectPort(port1);

            // Start the switch operating.
            // Essentially the switch starts forwarding network packets.
            networkSwitch.powerUp();

            // Create a 'ParrotServer' running on Computer B listening to port number 9999.
            Application parrotServer = new ParrotServer((ComputerOS) computerB, 9999);
            parrotServer.start();

            // Create a 'HelloWorldClient' running on Computer A, sending out message to Computer B, port 9999.
            Application helloWorldClient = new HelloWorldClient((ComputerOS) computerA, InetAddress.getByName("1.2.3.7"), 9999);
            helloWorldClient.start();

        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
