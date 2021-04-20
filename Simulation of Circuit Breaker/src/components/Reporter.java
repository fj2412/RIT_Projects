package components;

import java.util.HashMap;
import java.util.Map;

/**
 * Components report their activity to this class.
 * String messages are pre-defined so that uniformity of
 * output from students' work is assured.
 *
 * @author RIT CS
 */
public class Reporter {

    /**
     * ( error-number, error-message ) pairs are added
     * to this map when the program first starts up.
     */
    private static final Map< Integer, String > usageErrors = new HashMap<>();

    /**
     * Add a new error condition and its code.
     * @param code the "key" for the message and
     *             the error code with which the program exits
     * @param message the string message associated with the code
     */
    public static void addError( int code, String message ) {
        usageErrors.put( code, message );
    }

    /**
     * When a fatal error is discovered, this method is called.
     * Information will be printed and the program will halt.
     *
     * @param errNum the integer assigned to this type of error.
     *     Upon exit, the operating system will be told that the error
     *     code was this number.
     * @param line (optional) used when the problem was with reading
     *     information from a file or the console, to pass other
     *     values of interest -- arguments -- that should be
     *     printed. They can be passed as an array or as separate
     *     arguments.
     *
     * @see #addError
     */
    public static void usageError( int errNum, String... line ) {
        System.err.println( usageErrors.get( errNum ) );
        if ( line.length > 0 ) {
            System.err.print( "\tArguments read were (" );
            for ( String arg: line ) System.err.print( ' ' + arg );
            System.err.println( " )" );
        }
        System.exit( errNum );
    }

    /**
     * All normal status messages for the simulation
     */
    public enum Msg {
        CREATING( "creating" ),
        ENGAGING( "engaging" ),
        DISENGAGING( "disengaging" ),
        SWITCHING_ON( "switching on" ),
        SWITCHING_OFF( "switching off" ),
        BLOWN( "has blown; current would be" ),
        DRAW_CHANGE( "draw change"),
        ATTACHING( "attaching-->"),
        POWERING_UP( "powering up" );

        private final String message;

        Msg( String message ) {
            this.message = message;
        }
    }

    /**
     * Build a string that identifies a Component in a
     * way that is appropriate for its type.
     * Use this whenever you want to display information about
     * a Component.
     * <br><br>
     * Warning! Sometimes this method is invoked when Components'
     * constructors are running. Because of the order of execution
     * of constructors in inheritance situations, attributes of
     * brand new objects may not have been set yet, so values will
     * show up as 0, null, etc.
     * This is not an error.
     *
     * @param comp the Component to identify
     * @return a string containing Component's class,
     *         name, and another piece of information
     *         depending on its class
     *         ( CircuitBreaker: limit and draw,
     *           Appliance: rating
     *           Component: draw
     *         )
     */
    public static String identify( Component comp ) {
        String identity =
                comp.getClass().getSimpleName() + " " + comp.getName();
        if ( comp instanceof Appliance ) {
            final Appliance appl = (Appliance)comp;
            identity += "(" + (appl.isSwitchOn()?"on":"off");
            identity += "; rating " + appl.getRating() + ")";
        }
        else if ( comp instanceof CircuitBreaker ) {
            final CircuitBreaker cb = (CircuitBreaker)comp;
            identity += "(" + (cb.isSwitchOn()?"on":"off");
            identity += "; draw " + comp.getDraw() +
                        "; limit " + cb.getLimit() + ")";
        }
        else {
            identity += "(draw " + comp.getDraw() + ")";
        }
        return identity;
    }

    /**
     * Print a message about an event involving one Component.
     * @param comp the Component involved
     * @param msg the event key
     */
    public static void report( Component comp, Msg msg ) {
        String eventMsg = identify( comp );
        eventMsg += ": " + msg.message;
        System.out.println( eventMsg );
    }

    /**
     * Print a message about an event involving one Component
     * and a numerical value.
     * @param comp the Component involved
     * @param msg the event key
     * @param param the numerical quantity of interest
     */
    public static void report( Component comp, Msg msg, int param ) {
        String eventMsg = identify( comp );
        eventMsg += ": " + msg.message;
        eventMsg += " " + param;
        System.out.println( eventMsg );
    }

    /**
     * Print a message about an event that involves two Components.
     * @param comp1 the first Component involved
     * @param comp2 the second Component involved
     * @param msg the event key
     */
    public static void report( Component comp1, Component comp2, Msg msg ) {
        String eventMsg = identify( comp1 );
        eventMsg += ' ' + msg.message + ' ';
        eventMsg += identify( comp2 );
        System.out.println( eventMsg );
    }
}
