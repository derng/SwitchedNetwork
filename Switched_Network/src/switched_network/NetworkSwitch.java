/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */
package switched_network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 *
 * Defines a network switch with a number of LAN Ports.
 *
 * @author K. Bryson.
 */
public class NetworkSwitch extends Thread {

    private final SwitchPort[] ports;
    
    private HashMap<InetAddress, Integer> table;
    
    /*
     * Create a Network Switch the specified number of LAN Ports.
     */
    public NetworkSwitch(int numberPorts) {
    	
        ports = new SwitchPort[numberPorts];

        // Create each ports.
        for (int i = 0; i < numberPorts; i++) {
            ports[i] = new SwitchPort(i);
        }

        // Create Hash table linking ports to ip address
        this.table = new HashMap<InetAddress, Integer>();
        
    }


    public int getNumberPorts() {
    	
        return ports.length;
        
    }
    
    
    public SwitchPort getPort(int number) {
    	
    	return ports[number];
    	
    }
    
    /*
     * Power up the Network Switch so that it starts
     * processing/forwarding network packet traffic.
     */
    public void powerUp() throws UnknownHostException {
    	for (int i = 0; i < ports.length; i++) {
    		InetAddress ipAddress = ports[i].getIPAddress();
    		
    		if (ipAddress == null) {
    			continue;
    		}

    		table.put(ipAddress, i);
    	}
    	
    	start();
    }

    // This thread is responsible for delivering any current incoming packets.
    public void run() {
    	
        while (true) {
            try {
                sleep(100);
            } catch (InterruptedException except) { }
            
            for (SwitchPort port: this.ports) {
            	//Check incoming packets only on connected ports
            	if (port.getIPAddress() == null) continue;

	            //Get incoming bytes
		    	byte[] packet = port.getIncomingPacket();

		    	//If no packet received, ignore this loop
		    	if (packet == null) continue;
		
				try {
					//Extract the destination ip address of the packet
			    	byte[] dst_address = extractData(packet, 4, 4);
					InetAddress ipAddress = InetAddress.getByAddress(dst_address);
					
					//Get port number associated with the ip address
					Integer port_no = table.get(ipAddress);

					//Port number not found in table, try find port
					if (port_no == null) {
						manualScan(packet, ipAddress);
						return;
					}
					
					//Send packet through the specific port
					ports[port_no].sendToComputer(packet);
					
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
	        }
        }
    }
    
    /*ip address not associated with any port no*/
    private void manualScan(byte[] packet, InetAddress ipAddress) {
    	for (SwitchPort port: this.ports) {
    		if (port.getIPAddress() == null) continue;
    		
    		//ip address found after search
    		if (port.getIPAddress().equals(ipAddress)) {
    			port.sendToComputer(packet);
    		}
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

