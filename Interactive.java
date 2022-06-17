import java.util.Map;
import java.util.function.Function;

public class Interactive extends Entity {
    private Function<PhysicalProp[], Boolean> condition;
    private String cmd;
    private Entity target;
    private int coolDown;
    private int cdning = 0;
    public Interactive (String script,Function<PhysicalProp[], Boolean> condition) {
        super(script);
        this.condition = condition;
    }

    @Override
    public void build(Map<String, String> propMap) {
        super.build(propMap);
        this.cmd = propMap.get("cmd");
        this.target = Interpreter.interpretEntity(propMap.get("tgt"), Main.mainStage);
        this.coolDown = Integer.valueOf(propMap.get("cdn"));
    }

    @Override
    public Map<String, String> getPropMap() {
        Map<String, String> toRet = super.getPropMap();
        toRet.put("cmd", this.cmd);
        toRet.put("tgt", this.target.Tag);
        toRet.put("cdn", String.valueOf(this.coolDown));
        return toRet;
    }

    public boolean isTriggered() {
        return cdning <= 0 && condition.apply(new PhysicalProp[] { this.Properties(), target.Properties() } );
    }

    public boolean trigger() {
        if (cdning > 0) cdning--;
        if (isTriggered()) {
            System.out.println(cmd);
            Interpreter.execute(cmd, Main.mainStage);
            cdning = coolDown;
            return true;
        } else {
            return false;
        }
    }

    public boolean enforce() {
        cdning = coolDown;
        Interpreter.execute(cmd, Main.mainStage);
        return true;
    }

    @Override
    public void tick(Function<PhysicalProp, Boolean> outerEff) {
        super.tick(outerEff);
        trigger();
    }

    public static boolean overlapped(PhysicalProp p1, PhysicalProp p2) {
        Vector2D loc1 = p1.Location, loc2 = p2.Location;
        Vector2D dm1 = p1.Size, dm2 = p2.Size;
        if (loc1.getX() - dm2.getX() < loc2.getX() &&
                loc1.getX() + dm1.getX() > loc2.getX() &&
                loc1.getY() - dm2.getY() < loc2.getY() &&
                loc1.getY() + dm1.getY() > loc2.getY()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean upperDown(PhysicalProp p1, PhysicalProp p2) {
        Vector2D loc1 = p1.Location, loc2 = p2.Location;
        Vector2D dm1 = p1.Size, dm2 = p2.Size;
        if (loc1.getX() > loc2.getX() &&
            loc1.getX() < loc2.getX() + dm2.getX() - dm1.getX() &&
            loc1.getY() > loc2.getY() - dm1.getY() &&
            loc1.getY() < loc2.getY() &&
            p1.Velocity.getY() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
