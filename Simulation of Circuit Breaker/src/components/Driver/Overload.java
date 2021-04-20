package components.testing;

import components.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * The main program for the Overload project. It will read configuration file from the terminal and the program is
 * executed, then it will prompt user for commands that that they want to manipulate in this system of components.
 *
 * @author Feng Jiang
 */
public class Overload {

    public static final int BAD_ARGS = 1;
    public static final int FILE_NOT_FOUND = 2;
    public static final int BAD_FILE_FORMAT = 3;
    public static final int UNKNOWN_COMPONENT = 4;
    public static final int REPEAT_NAME = 5;
    public static final int UNKNOWN_COMPONENT_TYPE = 6;
    public static final int UNKNOWN_USER_COMMAND = 7;
    public static final int UNSWITCHABLE_COMPONENT = 8;

    private static final String WHITESPACE_REGEX = "\\s+";
    private static final String[] NO_STRINGS = new String[ 0 ];

    private static final String PROMPT = "? -> ";

    private static HashMap<String,Component> link = new HashMap<>();
    private static ArrayList<Component> componentList = new ArrayList<>();
    private static PowerSource mainPower;

    static {
        Reporter.addError(
                BAD_ARGS, "Usage: java components.Overload <configFile>" );
        Reporter.addError( FILE_NOT_FOUND, "Config file not found" );
        Reporter.addError( BAD_FILE_FORMAT, "Error in config file" );
        Reporter.addError(
                UNKNOWN_COMPONENT,
                "Reference to unknown component in config file"
        );
        Reporter.addError(
                REPEAT_NAME,
                "Component name repeated in config file"
        );
        Reporter.addError(
                UNKNOWN_COMPONENT_TYPE,
                "Reference to unknown type of component in config file"
        );
        Reporter.addError(
                UNKNOWN_USER_COMMAND,
                "Unknown user command"
        );
        Reporter.addError(UNSWITCHABLE_COMPONENT,
                "Unswitchable component being asked to toggle"
        );
    }

    /**
     * error check to see if there is a component with the same name that already exist in the Component system.
     * @param name  String  the name being checked
     * @param component Component   the component for which is being added
     */
    private static void nameCheck(String name, Component component) {
        if (!link.containsKey(name)) {
            link.put(name, component);
            componentList.add(component);
        } else {
            Reporter.usageError(REPEAT_NAME);
        }
    }

    /**
     * error check to see if there is reference or connection to an unknown component that was never made
     * @param name  String  the name of the component for which is the source of connection
     */
    private static void compCheck(String name) {
        if (!link.containsKey(name)) {
            Reporter.usageError(UNKNOWN_COMPONENT);
        }
    }

    /**
     * the method that allows components to be constructed and attached into a system and checks to see if there was
     * an unknown component type trying to be constructed or added
     * @param data  the array that contains the information to construct a component
     * @param errorType int     the integer for different type of error message that is sourced from
     */
    private static void connectComponent( String[] data, int errorType) {
        if (data.length == 2 && data[0].equalsIgnoreCase("PowerSource")) {
            PowerSource ps = new PowerSource(data[1]);
            nameCheck(data[1], ps);
        } else if (data.length == 3 && data[0].equalsIgnoreCase("Outlet")) {
            compCheck(data[2]);
            Outlet ol = new Outlet(data[1], link.get(data[2]));
            nameCheck(data[1], ol);
        } else if (data.length == 4 && data[0].equalsIgnoreCase("CircuitBreaker")) {
            compCheck(data[2]);
            CircuitBreaker cb = new CircuitBreaker(data[1], link.get(data[2]), Integer.parseInt(data[3]));
            nameCheck(data[1], cb);
        } else if (data.length == 4 && data[0].equalsIgnoreCase("Appliance")) {
            compCheck(data[2]);
            Appliance ap = new Appliance(data[1], link.get(data[2]), Integer.parseInt(data[3]));
            nameCheck(data[1], ap);
        } else {
            Reporter.usageError(errorType);
        }
    }

    /**
     * the method that reads the configuration file from the terminal argument when the program executes and constructs
     * the system of components in the file if it is correctly formatted.
     * @param configFileName    String  the name of the file that have information of configuration
     */
    private static void readConfiguration (String configFileName) {
        try (Scanner configFile = new Scanner (new File(configFileName))) {
            try {
            while (configFile.hasNextLine()) {
                String line = configFile.nextLine();
                String[] data = line.split(WHITESPACE_REGEX);
                connectComponent(data, BAD_FILE_FORMAT);
                }
                System.out.println(componentList.size() + " components created.");
            } catch (Exception e) {
                Reporter.usageError(BAD_FILE_FORMAT);
            } finally {
                configFile.close();
            }
        } catch (FileNotFoundException fnf) {
            Reporter.usageError(FILE_NOT_FOUND);
        }
    }

    /**
     * power up the main PowerSource when the system is constructed from the config file
     */
    private static void MainPowerUp() {
        System.out.println("Starting up the main circuit(s).");
        for (Component component : componentList) {
            if (component instanceof PowerSource) {
                Reporter.report(component, Reporter.Msg.POWERING_UP);
                component.engage();
                mainPower = (PowerSource)component;
            }
        }
    }

    /**
     * prompt user for manipulation of the system of loads after the configuration.
     */
    private static void PromptUser() {
        Scanner input = new Scanner(System.in);
        String[] command;
        String action;
        while (true) {
            System.out.print(PROMPT);
            action = input.nextLine();
            command = action.split(WHITESPACE_REGEX);
            if (command[0].equalsIgnoreCase("display")) {
                mainPower.display();
            } else if (command[0].equalsIgnoreCase("quit")) {
                break;
            } else if (command[0].equalsIgnoreCase("toggle")) {
                Component component = link.get(command[1]);
                if (component instanceof CircuitBreaker || component instanceof Appliance) {
                    if (((Switcher) component).isSwitchOn()) {
                        ((Switcher) component).turnOff();
                    } else {
                        ((Switcher) component).turnOn();
                    }
                } else {
                    Reporter.usageError(UNSWITCHABLE_COMPONENT);
                }
            } else if (command[0].equalsIgnoreCase("connect")) {
                try {
                    String[] data = {command[1], command[2], command[3], command[4]};
                    connectComponent(data, UNKNOWN_COMPONENT_TYPE);
                } catch (ArrayIndexOutOfBoundsException aiob2) {
                    Reporter.usageError(UNKNOWN_USER_COMMAND);
                }
            } else {
                Reporter.usageError(UNKNOWN_USER_COMMAND);
            }

        }
    }

    /**
     * the main method which executes the program
     * @param args  String[]    the name of the config file should exist here
     */
    public static void main( String[] args ) {
        System.out.println( "Overload Project, CS2" );
        try {
            readConfiguration(args[0]);
            MainPowerUp();
            PromptUser();
        } catch (ArrayIndexOutOfBoundsException aiob){
            Reporter.usageError(BAD_ARGS);
        }

    }

}
