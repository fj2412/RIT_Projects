package components;

/**
 * the CircuitBreaker component class
 * @author Feng Jiang
 */
public class CircuitBreaker extends Component implements Switcher{
    /**
     * the fields of circuit breaker component
     */
    private boolean power;
    private final int limit;
    private int overDraw;

    /**
     * the CircuitBreaker constructor and connect it to the source of power after its being created
     * @param name  String  the name of the circuit breaker
     * @param source    component   the source component for this circuit breaker
     * @param limit     int     the limit that this circuit breaker can draw power from before its overloaded
     */
    public CircuitBreaker(String name, Component source, int limit) {
        super(name, source);
        source.attach(this);
        this.power = false;
        this.draw = 0;
        this.limit = limit;
    }

    /**
     * gets the limit of this circuit breaker
     * @return  int     the limit of this circuit breaker
     */
    public int getLimit() {
        return this.limit;
    }

    /**
     * the method that gets called when the draw > limit, meaning that the circuit breaker is now overloaded, so
     * it will disengage all components and turn itself off
     */
    public void overload() {
        Reporter.report(this, Reporter.Msg.BLOWN, draw);
        overloaded = true;
        turnOff();
    }

    /**
     * the changeDraw method for circuit breaker, it will check to see if it is overloaded after it draw power from its
     * loads, if it doesn't it will perform the regular operations.
     * @param delta     int     the amount being changed
     */
    @Override
    protected void changeDraw(int delta) {
        overDraw = draw;
        draw = draw + delta;
        if (draw > limit && !overloaded) {
            overload();
        } else if (!overloaded){
            Reporter.report(this, Reporter.Msg.DRAW_CHANGE, delta);
            source.changeDraw(delta);
        }
    }

    /**
     * the engage method for circuit breaker, it will be engaged only from the power source, it will not engage all its loads
     * until its turned on
     */
    @Override
    public void engage() {
        Reporter.report(this, Reporter.Msg.ENGAGING);
        engaged = true;
    }

    /**
     * turns the circuit breaker on and engage all its loads
     */
    @Override
    public void turnOn() {
        this.power = true;
        this.overloaded = false;
        Reporter.report(this, Reporter.Msg.SWITCHING_ON);
        engageLoads();
    }

    /**
     * turn the circuit breaker off and disengage all its loads
     */
    @Override
    public void turnOff() {
        this.power = false;
        Reporter.report(this, Reporter.Msg.SWITCHING_OFF);
        int SourcePrevDraw = source.getDraw();
        source.setDraw(SourcePrevDraw - overDraw);
        Reporter.report(source, Reporter.Msg.DRAW_CHANGE, -overDraw);
        disengageLoads();
    }

    /**
     * checks to see if the circuit breaker is turned powered on
     * @return  boolean     if this power is on then true otherwise false
     */
    @Override
    public boolean isSwitchOn() {
        return this.power;
    }
}
