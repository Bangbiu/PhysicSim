import java.util.HashMap;
import java.util.Map;

public class PhysicalProp implements PropertyMapped {

    public enum PhysShape {
        Cube, Ball
    }

    public static double GRAVITYCONST =0.667 ;
    //6.67×10-11 N·m2 /kg2

    public Vector2D Location;
    public Vector2D Size;
    public Vector2D Velocity;
    public Vector2D Accelaration;
    public double Mass;
    public double GravityRadius;
    public PhysShape PhysicalShape;

    public boolean GravityIgnore;
    public boolean MutualGravity;
    public boolean Susceptible;
    public boolean Receive_g;
    public boolean Receive_f;
    public boolean Undraggable;
    public double Elasticity;

    public void construct(Vector2D loc, Vector2D siz, Vector2D vel, Vector2D acc, double mas,
                          boolean GvIg, boolean mtg, boolean spt, boolean undrg,
                          boolean rcvg, boolean rcvf, double elc, double rad) {
        this.Location = loc;
        this.Size = siz;
        this.Velocity = vel;
        this.Accelaration = acc;
        this.Mass = mas;
        this.GravityIgnore = GvIg;
        this.MutualGravity = mtg;
        this.Susceptible = spt;
        this.Undraggable = undrg;
        this.Receive_g = rcvg;
        this.Receive_f = rcvf;
        this.Elasticity = elc;
        this.GravityRadius = rad;
    }

    public void build(Map<String, String> propMap) {
        construct(new Vector2D(propMap.get("loc")), new Vector2D(propMap.get("siz")),
                new Vector2D(propMap.get("vel")), new Vector2D(propMap.get("acc")),
                Double.valueOf(propMap.get("mas")), Boolean.valueOf(propMap.get("gvig")),
                Boolean.valueOf(propMap.get("mtg")), Boolean.valueOf(propMap.get("spt")),
                Boolean.valueOf(propMap.get("undrg")),Boolean.valueOf(propMap.get("rcvg")),
                Boolean.valueOf(propMap.get("rcvf")), Double.valueOf(propMap.get("elc")),
                Double.valueOf(propMap.get("rad")));
    }

    public Map<String, String> getPropMap() {
        Map<String, String> toRet = new HashMap<>();
        toRet.put("loc", Location.toString());
        toRet.put("siz", Size.toString());
        toRet.put("vel", Velocity.toString());
        toRet.put("acc", Accelaration.toString());
        toRet.put("mas", String.valueOf(Mass));
        toRet.put("gvig", String.valueOf(GravityIgnore));
        toRet.put("mtg", String.valueOf(MutualGravity));
        toRet.put("spt", String.valueOf(Susceptible));
        toRet.put("undrg", String.valueOf(Undraggable));
        toRet.put("rcvg", String.valueOf(Receive_g));
        toRet.put("rcvf", String.valueOf(Receive_f));
        toRet.put("elc", String.valueOf(Elasticity));
        toRet.put("rad", String.valueOf(GravityRadius));
        return toRet;
    }

    public PhysicalProp() { build(Main.DEF_SETTING); }

    public PhysicalProp(Vector2D loc) {
        build(Main.DEF_SETTING);
        this.Location = loc;
    }

    public PhysicalProp(Vector2D loc, Vector2D siz) {
        build(Main.DEF_SETTING);
       this.Location = loc;
       this.Size = siz;
    }

    public PhysicalProp(String script) {
        build(Interpreter.interpretToMap(new HashMap<>(Main.DEF_SETTING), script));
    }

    public void applyAcc() {
        Velocity.offset(Accelaration);
    }

    public void locateX(double newX) { Location.setX(newX); }

    public void locateY(double newY) { Location.setY(newY);}

    public Vector2D getCenter() {return Location.getOffset(Size.quotient(2));}

    public Vector2D gravitationTo(PhysicalProp target) {
        if (GravityIgnore || target.Location.equals(this.Location))
            return new Vector2D(0, 0);

        Vector2D direc = target.getCenter().to(this.getCenter());
        Vector2D unit = direc.unit();
        double dis = direc.length();
        if (GravityRadius > 0 && dis > GravityRadius) return new Vector2D(0, 0);
        unit.multiply(GRAVITYCONST * this.Mass);
        if (MutualGravity) {
            unit.multiply( target.Mass );
            if (dis * 2 < Size.getPresX() || dis * 2 < Size.getPresY()) {
                unit.divide(Size.getPresX() * Size.getPresY());
            } else {
                dis /= Main.DISPLAY_SCALE;
                unit.divide(dis * dis);
            }
        }
        return unit;
    }

    @Override
    public String toString() {
        return "v = " + Velocity + " a = " + Accelaration;
    }

}
