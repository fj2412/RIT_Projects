package components;

import java.util.ArrayList;

/**
 * The abstract class for components, which can take many instances and subclass can inherit from.
 * @author Feng Jiang
 */
public abstract class Component{

    /**
     * the fields that could be used for all type of components
     */
    protected final String name;
    protected Component source;
    protected int draw;
    protected ArrayList<Component> connectionLoads;
    protected boolean engaged;
    protected boolean overloaded;

    /**
     * the constructor for constructing a component, can take many forms for which the subclass will inherit
     * @param name  String  the name of the component
     * @param source    (Object)Component   the component source
     */
    protected Component(String name, Component source) {
        this.name = name;
        this.source = source;
        this.engaged = false;
        this.overloaded = false;
        this.connectionLoads = new ArrayList<>();
        Reporter.report(this, Reporter.Msg.CREATING);
    }

    /**
     * Add a new load (something that draws current) to this Component. If this component is engaged, the new load
     * becomes engaged.
     * @param load      Component   the load being added and determined if it should be engaged
     */
    protected void attach(Component load) {
        Reporter.report(this, load, Reporter.Msg.ATTACHING);
        this.addLoad(load);
        if (this.engaged()) {
            load.engage();
        }
    }

    /**
     * Change the amount of current passing through this Component. This method is called by one of this Component's
     * loads when something changes. If the result is a change in current draw in this component, that information is
     * relayed up to the source Component via a call to its changeDraw method.
     * @param delta     int     the amount being changed
     */
    protected void changeDraw(int delta) {
        draw = draw + delta;
        Reporter.report(this, Reporter.Msg.DRAW_CHANGE, delta);
        source.changeDraw(delta);
    }

    /**
     * set the draw of this component to a given amount
     * @param draw  int     the draw to change
     */
    protected void setDraw(int draw) {
        this.draw = draw;
    }

    /**
     * gets the draw of this component
     * @return  int     this draw
     */
    protected int getDraw() {
        return draw;
    }

    /**
     * Check to see if the system is overloaded.
     */
    protected boolean getOverloaded() {
        return this.overloaded;
    }

    /**
     * gets the source of this component
     * @return  (Object)Component   the source component for this component
     */
    protected Component getSource() {
        return source;
    }

    /**
     * gets the list of loads connected to this component
     * @return  (Collection)Arraylist   list of components connected to this component
     */
    protected ArrayList<Component> getLoads() {
        return connectionLoads;
    }

    /**
     * check to see if this component is engaged
     * @return  boolean true or false based on if this component is engaged
     */
    protected boolean engaged() {
        return engaged;
    }

    /**
     * add a new component to this component's collection of component
     * @param newLoad   Component   the component to be added to the collection
     */
    protected void addLoad(Component newLoad) {
        connectionLoads.add(newLoad);
    }

    /**
     * engage all loads in this component collection
     */
    protected void engageLoads() {
        for (Component load : connectionLoads) {
            load.engage();
        }
    }

    /**
     * disengage all loads in this component collection
     */
    protected void disengageLoads() {
        for (Component load : connectionLoads) {
            load.disengage();
        }
    }

    /**
     * Get component's name
     * @return  String name
     */

    public String getName() {
        return name;
    }

    /**
     * This Component tells its loads that they can no longer draw current from it. If this is not a switchable
     * Component that has been switched off, this Component then passes on the information to its loads that they are
     * now disengaged.
     */

    public void disengage() {
        Reporter.report(this, Reporter.Msg.DISENGAGING);
        engaged = false;
        /*Reporter.report(this, Reporter.Msg.DRAW_CHANGE, 0);
        source.setDraw(0);*/
        disengageLoads();
    }

    /**
     * The source for this component is now being powered. For those Components that have sources, the source Component
     * is the one that calls this method. If this is not a switchable Component that has been switched off, this
     * Component then passes on the information to its loads that they are now engaged. The expected followup is
     * that each of the loads will inform this Component of how much current they need to draw.
     */
    public void engage() {
        Reporter.report(this, Reporter.Msg.ENGAGING);
        engaged = true;
        engageLoads();
    }

    /**
     * method for which allows recursion depth for correct display
     * @param indent    String  a tab for indentation on every recursive call
     */
    public void recurDisplay(String indent) {
        System.out.print(indent + "+ " + this.toString() + "\n");
        for (Component load : connectionLoads) {
            load.recurDisplay(indent + "    ");
        }
    }

    /**
     * display the components connection system into an easy to read format
     */
    public void display() {
        System.out.println();
        String indent = "";
        recurDisplay(indent);
    }

    /**
     * calls the reporter to report the String of this component
     * @return  Reporter.identify(this)
     */
    @Override
    public String toString() {
        return Reporter.identify(this);
    }
}

