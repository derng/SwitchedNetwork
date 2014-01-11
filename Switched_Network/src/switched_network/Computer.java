/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */

package switched_network;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Models a computer system which can have a number of
 * applications running on it and provides these applications
 * with operating system services including networking.
 *
 * The Computer provides the operating system services by
 * implementing the ComputerOS interface.
 *
 * The Computer also handles network traffic to/from switch ports
 * by implementing a NetworkCard interface.
 *
 * @author K. Bryson.
 */
public class Computer implements ComputerOS, NetworkCard {

    private final String hostname;
    private final InetAddress ipAddress;

    // This is the switch port which the computer is attached to.
    private SwitchPort port = null;

    private final static int MAX_PORTS = 65536;
    
    private ConcurrentHashMap<Integer, byte[]> table;
    private Lock lock = new ReentrantLock();
    private Condition isMemoryClear = lock.newCondition();
    

    public Computer(String hostname, InetAddress ipAddress) {

        this.hostname = hostname;
        this.ipAddress = ipAddress;
        this.table = new ConcurrentHashMap<Integer, byte[]>();

    }
    
    
    /**********************************************************************************
     * The following methods provide the 'Operating System Services'
     * of the computer which are directly used by applications.
     **********************************************************************************/

    /*
     * Get the host name of the computer.
     */
    public String getHostname() {
        return hostname;
    }

    /*
     * Ask the operating system to send this message
     * (byte array as the payload) to the computer with
     * the given IP Address.
     *
     * The message goes from a 'port' on this machine to
     * the specified port on the other machine.
     */
    public void send(byte[] payload, InetAddress ip_address_to, int port_from, int port_to) {
    	//don't allow sending packets
    	if (port_to > MAX_PORTS || port_to < 0) return;
    	
    	lock.lock();
    	
    	byte[] packet = null;
    	try {
	    	byte[] src_address = ipAddress.getAddress();
	        byte[] dst_address = ip_address_to.getAddress();
	        byte[] src_port = getPortBytes(port_from);
	        byte[] dst_port = getPortBytes(port_to);
	  
	        packet = getPacket(src_address, dst_address, src_port, dst_port, payload);
    	} finally {
    		lock.unlock();
    	}
    	
    	this.port.sendToNetwork(packet);	
    }
    
    /*Convert decimal to 16 bit*/
    private byte[] getPortBytes(int port_number) {
    	byte[] data = new byte[2];
        data[0] = (byte) (port_number & 0xFF);
        data[1] = (byte) ((port_number >> 8) & 0xFF);
        return data;
    }
    
    /*Combine bytes of data into a byte array packet*/
    private byte[] getPacket(byte[] src_address, byte[] dst_address, byte[] src_port, 
    						byte[] dst_port, 	byte[] payload) {
    	byte[] packet = new byte[12 + payload.length];
    	
    	int offset = 0;
    	System.arraycopy(src_address, 0, packet, offset, src_address.length);
    	offset += src_address.length;
    	System.arraycopy(dst_address, 0, packet, offset, dst_address.length);
    	offset += dst_address.length;
    	System.arraycopy(src_port, 0, packet, offset, src_port.length);
    	offset += src_port.length;
    	System.arraycopy(dst_port, 0, packet, offset, dst_port.length);
    	offset += dst_port.length;
    	System.arraycopy(payload, 0, packet, offset, payload.length);
    	return packet;
    	
    }

    
    /*
     * This asks the operating system to check whether any incoming messages
     * have been received on the given port on this machine.
     *
     * If a message is pending then the 'payload' is returned as a byte array.
     * (i.e. without any UDP/IP header information)
     */
    public byte[] recv(int port) {
    	lock.lock();
    	byte[] payload = null;
    	try {
    		if (table.containsKey(port)) {
	    		payload = table.get(port);
	    		table.remove(port);
	    		
	    		//Signal one thread that memory is clear
	    		isMemoryClear.signal();
    		}
    		
		} finally {
    		lock.unlock();
    	}
    	
    	return payload;
    }


    /**********************************************************************************
     * The following methods implement the Network Card interface for this computer.
     *
     * They are used by the 'operating system' to send and recv packets.
     ***********************************************************************************/


    /*
     * Get the IP Address of the network card.
     */
    public InetAddress getIPAddress() {
        return ipAddress;
    }

    
    /*
     * This allows a port of a network switch
     * to be attached to this computers network card.
     */
    public void connectPort(SwitchPort port) {
        this.port = port;
    }
    

    /*
     * This method is used by the Network Switch (SwitchPort)
     * to send a packet of data from the network to this computer.
     */
    public void sendToComputer(byte[] packet) {
    	lock.lock();
    	
    	try {
		    //Extract payload
		    int payload_size = packet.length - 12;
		    byte[] payload = extractData(packet, 12, payload_size);
		    	
		    //Extract send port number
		    byte[] send_port = extractData(packet, 10, 2);
		    int port_no = (send_port[1] & 0xFF) << 8 | send_port[0]& 0xFF;
		
		    //Wait until memory at port is used
    		while (table.containsKey(port_no)) isMemoryClear.await(); 
    		
		    table.put(port_no, payload);
		    
    	} catch (InterruptedException e) {
			e.printStackTrace();
			
		} finally {
    		lock.unlock();
    	}
    }
    
    /*Used to extract data from packet using offset and size*/
    private byte[] extractData(byte[] packet, int offset, int size) {
    	byte[] data = new byte[size];
    	for (int i = 0; i < size; i++) {
    		data[i] = packet[i+offset];
    	}
    	return data;
    }

}
