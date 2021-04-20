package components.testing;

import components.*;

import java.util.Map;
import java.util.TreeMap;

import static java.util.Map.entry;

/**
 * This test class does not use a standard configuration file.
 * All tests are set up directly in code, so it is no substitute
 * for the required Overload progream.
 *
 * @author RIT CS
 */
public class Tests {

    public static final int BAD_ARGS = 1;
    public static final int BAD_TEST_NUM = 2;

    static {
        Reporter.addError(
                BAD_ARGS, "Usage: java components.testing.Tests [<test#>]" );
        Reporter.addError( BAD_TEST_NUM, "Illegal test number" );
    }

    /**
     * This variable uses some more advanced Java features to
     * map test numbers to tests.
     */
    private static final Map< Integer, Runnable > tests =
            new TreeMap<>(
                Map.ofEntries(
                        entry( 1, Tests::oneAppl ),
                        entry( 2, Tests::twoApplWithCB ),
                        entry( 3, Tests::treeOf15 ),
                        entry( 4, Tests::multiCircuit )
                )
            );

    public static void main( String[] args ) {
        System.out.println( "Overload Project, CS2" );
        switch ( args.length ) {
            case 0: // Run all tests
                System.out.println(
                        "RUNNING ALL TESTS" + System.lineSeparator() );
                for ( int testNum : tests.keySet() ) {
                    System.out.println( "Test #" + testNum );
                    System.out.println(
                            "----------------" + System.lineSeparator() );
                    tests.get( testNum ).run();
                    System.out.println();
                }
                break;
            case 1: // Run test whose number is on the command line
                try {
                    int testNum = Integer.parseInt( args[ 0 ] );
                    if ( !tests.containsKey( testNum ) ) {
                        Reporter.usageError( BAD_TEST_NUM );
                    }
                    tests.get( testNum ).run();
                }
                catch( NumberFormatException nfe ) {
                    Reporter.usageError( BAD_ARGS );
                }
                break;
            default:
                Reporter.usageError( BAD_ARGS );
        }
    }

    /**
     * Plug an appliance directly into a power source.
     * This is abnormal and will not appear in any
     * configuration files.
     */
    public static void oneAppl() {
        PowerSource a = new PowerSource( "Root" );
        Appliance b = new Appliance( "Radio", a, 10 );
        a.display();
        a.engage();
        b.turnOn();
        b.turnOff();
    }

    /**
     * Plug two appliances through a circuit breaker
     * and watch the breaker blow.
     */
    public static void twoApplWithCB() {
        PowerSource root = new PowerSource( "Root" );
        CircuitBreaker breaker = new CircuitBreaker( "Breaker", root, 15 );
        Outlet outlet = new Outlet( "Outlet", breaker );
        Appliance a1 = new Appliance( "Heater", outlet, 10 );
        Appliance a2 = new Appliance( "Pump", outlet, 8 );
        root.display();
        root.engage();
        breaker.turnOn();
        a1.turnOn();
        a2.turnOn();
        a2.turnOff();
        breaker.turnOn();
    }

    /**
     * A larger more realistic example with all kinds of components.
     * Enough stuff is added so that a circuit breaker eventually
     * blows when things are turned on.
     */
    public static void treeOf15() {
        System.out.println( "\n===== Connection Phase ==========" );
        PowerSource ps = new PowerSource( "Root" );
        CircuitBreaker cb1 = new CircuitBreaker( "Breaker1", ps, 15 );
        CircuitBreaker cb2 = new CircuitBreaker( "Breaker2", ps, 25 );
        Outlet out1a = new Outlet( "Outlet1A", cb1 );
        Outlet out1b = new Outlet( "Outlet1B", cb1 );
        Outlet out2a = new Outlet( "Outlet2A", cb2 );
        Outlet out2b = new Outlet( "Outlet2B", cb2 );
        Appliance light1 = new Appliance( "Light1", out1a, 1 );
        Appliance light2 = new Appliance( "Light2", out1a, 1 );
        Appliance light3 = new Appliance( "Light3", out1b, 1 );
        Appliance heater = new Appliance( "Heater", out1b, 13 );
        Appliance ecar1 = new Appliance( "ECar1", out2a, 7 );
        Appliance ecar2 = new Appliance( "ECar2", out2a, 7 );
        Appliance ecar3 = new Appliance( "ECar3", out2b, 7 );
        Appliance ecar4 = new Appliance( "ECar4", out2b, 7 );

        ps.display();

        System.out.println( "\n===== Circuit 1 Test Phase ==========" );
        ps.engage();
        cb1.turnOn();
        light1.turnOn();
        light2.turnOn();
        light3.turnOn();
        heater.turnOn(); // Circuit breaker should blow here.

        System.out.println( "\n===== Circuit 2 Overload Phase ==========" );
        cb2.turnOn();
        ecar3.turnOn();
        ecar4.turnOn();
        ecar1.turnOn();
        ecar2.turnOn(); // Circuit breaker should blow here.

        System.out.println( "\n===== Circuit 2 Turning things off ==========" );
        light2.turnOff();
        ecar3.turnOff();

        System.out.println( "\n===== Switching circuits back on ==========" );
        cb2.turnOn();
        cb1.turnOn();

        ps.display();
    }

    /**
     * A test for multiple main circuits, i.e., PowerSources.
     */
    public static void multiCircuit() {
        PowerSource master1 = new PowerSource( "Master1" );
        PowerSource master2 = new PowerSource( "Master2" );
        CircuitBreaker breaker1 = new CircuitBreaker( "Breaker1", master1, 20 );
        CircuitBreaker breaker2 = new CircuitBreaker( "Breaker2", master2, 20 );
        Outlet outlet1 = new Outlet( "Outlet1", breaker1 );
        Outlet outlet2 = new Outlet( "Outlet2", breaker2 );
        Appliance radio = new Appliance( "Radio", outlet1, 10 );
        Appliance lights = new Appliance( "Lights", outlet2, 1 );
        Appliance fluxCapacitor =
                new Appliance( "Flux Capacitor", outlet2, 12000000 );
        master1.display();
        master2.display();
        master1.engage();
        master2.engage();
        breaker1.turnOn();
        radio.turnOn();
        lights.turnOn();
        master2.display();
        fluxCapacitor.turnOn();
        breaker2.turnOn();
        fluxCapacitor.turnOff();
        lights.turnOff();
        breaker2.turnOn();
    }

}
