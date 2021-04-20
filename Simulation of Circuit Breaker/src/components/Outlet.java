package components;

/**
 * the outlet component class
 * @author Feng Jiang
 */
public class Outlet extends Component {

    /**
     * constructor for an outlet and connect to its source
     * @param name  String  name of outlet
     * @param source    component   the source component of outlet
     */
    public Outlet(String name, Component source) {
        super(name, source);
        source.attach(this);
        this.draw = 0;
    }
}
