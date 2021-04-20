package components;

/**
 * The appliance component class that is extended from the abstract class component.
 * @author Feng Jiang
 */
public class Appliance extends Component implements Switcher{

    /**
     * fields for Appliance component
     */
    private boolean power;
    private final int rating;

    /**
     * the appliance constructor, makes a new appliance component and attach to its source
     * @param name  String  the name of the appliance
     * @param source    component   the source component for this appliance
     * @param rating    int     the rating for the amount of drawchanges that this appliance will draw
     */
    public Appliance(String name, Component source, int rating) {
        super(name, source);
        source.attach(this);
        this.power = false;
        this.rating = rating;
    }

    /**
     * gets the rating for this appliance
     * @return  int rating
     */
    public int getRating() {
        return this.rating;
    }

    /**
     * turns the appliance on and checks if its engaged, if it is change the draw of the source component
     */
    @Override
    public void turnOn() {
        this.power = true;
        Reporter.report(this, Reporter.Msg.SWITCHING_ON);
        if (this.engaged()) {
            source.changeDraw(rating);
        }
    }

    /**
     * turns the appliance off and checks if its engaged, if it is change the draw of the source component
     */
    @Override
    public void turnOff() {
        this.power = false;
        Reporter.report(this, Reporter.Msg.SWITCHING_OFF);
        if (this.engaged()) {
            source.changeDraw(-1 * rating);
        }
    }

    /**
     * engage the appliance checks to see if its switched on, if it is change the draw of the source component by its
     * rating
     */
    @Override
    public void engage() {
        super.engage();
        if(this.engaged() && isSwitchOn()) {
            Reporter.report(this, Reporter.Msg.DRAW_CHANGE, rating);
            source.changeDraw(rating);
        }
    }

    /**
     * disengage the appliance, and change the draw of the source component by its rating.
     */
    @Override
    public void disengage() {
        super.disengage();
        Reporter.report(this, Reporter.Msg.DRAW_CHANGE, -rating);
        source.changeDraw(-rating);
    }

    /**
     * check to see if the power is on for this appliance.
     * @return  boolean     true if power is on and false if power is not on
     */
    @Override
    public boolean isSwitchOn() {
        return this.power;
    }
}
