/*
 *  (c) K.Bryson, Dept. of Computer Science, UCL (2013)
 */

package switched_network;

/**
 * Represents a 'Default Application' that only prints out information about itself.
 * All real applications should extend this 'Default Application'.
 * 
 * @author K. Bryson.
 */
public class Application extends Thread {

    private String programName;
    private ComputerOS computerOS;


    public Application(String programName, ComputerOS computerOS) {

        this.programName = programName;
        this.computerOS = computerOS;

    }

    public String getProgramName() {
        return programName;
    }

    public ComputerOS getComputerOS() {
        return computerOS;
    }

    
    /*
     * A "Default Application" that just prints some information.
     */

    @Override
    public void run() {

        System.out.println(programName + " Application. Running on Computer " + computerOS.getHostname() + "\n");

    }

}
