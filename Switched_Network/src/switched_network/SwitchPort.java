/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */

package switched_network;

import java.net.InetAddress;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * This represents a 'Switch Port' on the front of a Network Switch.
 *
 * For instance, a Network Switch may have 4 ports which are
 * Ethernet sockets at the front of the Network Switch.
 * Each Ethernet socket is physically connected to the
 * network card of a computer using an Ethernet cable.
 *
 * @author K. Bryson.
 */
public class SwitchPort {

    private final int portNumber;
    private NetworkCard connectedNetworkCard = null;
    private InetAddress ipAddress = null;
    
    private byte[] packet;
    private Lock lock = new ReentrantLock();
    private Condition inTransit = lock.newCondition();

    public SwitchPort(int number) {
        portNumber = number;
        this.packet = null;
    }
    
    public int getNumber() {
    	return portNumber;
    }
    
    public InetAddress getIPAddress() {
    	return ipAddress;
    }

    /*
     * This method is USED BY THE COMPUTER to send a packet of
     * data to this Port on the Switch.
     *
     * The packet of data should follow the simplified
     * header format as specified in the coursework descriptions.
     */
    public void sendToNetwork(byte[] packet) {
    	lock.lock();
    	try {    		
    		//wait for the previous packet to be picked up
    		while (this.packet != null) inTransit.await();
        	this.packet = packet;

		} catch (InterruptedException e) {
			e.printStackTrace();
			
		} finally {
    		lock.unlock();
    	}
    	
    }

    public byte[] getIncomingPacket() {
    	lock.lock();
    	
    	byte[] packet = null;
    	try {    
        	//Clone bytes to send
    		if (this.packet != null)  
    			packet = this.packet.clone();
        	
        	//Clear stored packet before sending
        	this.packet = null;
        	
        	//Signal one thread that the packet is sent
        	inTransit.signal();
        	//System.out.println("here2");
		} finally {
			lock.unlock();
		}
		return packet;
    }

    
    public void sendToComputer(byte[] packet) {
    	
   		connectedNetworkCard.sendToComputer(packet);
    	
    }


    public void connectNetworkCard(NetworkCard networkCard) {

        connectedNetworkCard = networkCard;
        ipAddress = networkCard.getIPAddress();

    }

}
