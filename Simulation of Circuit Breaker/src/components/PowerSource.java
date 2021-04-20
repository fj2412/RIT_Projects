package components;

/**
 * the Power Source component class
 * @author Feng Jiang
 */
public class PowerSource extends Component {
    /**
     * the constructor for the power source component
     * @param name  String  the name of the power source
     */
    public PowerSource(String name) {
        super(name,null);
        this.draw = 0;
    }

    /**
     * the change draw method of power source, since its the main source of power, it should not call change draw to its
     * source that shouldn't exist
     * @param delta     int     the amount being changed
     */
    @Override
    protected void changeDraw(int delta) {
        draw = draw + delta;
        Reporter.report(this, Reporter.Msg.DRAW_CHANGE, delta);
    }

    /**
     * the method to engage this component
     */
    @Override
    public void engage() {
        super.engage();
    }

    /**
     * the method to disengage this component
     */
    @Override
    public void disengage() {
        super.disengage();
    }
}
