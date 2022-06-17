import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Entity extends JPanel implements PropertyMapped{
    public double MotionScale;
    public int TraceCount;
    public boolean ShowTrace;
    public boolean ShowVelocity;
    public boolean ShowAccelaration;
    public boolean ShowMark;
    public boolean ShowRange;
    public String Tag;
    public String Image;

    private PhysicalProp prop;
    private TraceSet tracer;
    private boolean dragged;
    private volatile int draggedAtX, draggedAtY;

    public void build(Map<String, String> propMap) {
        //System.out.println(propMap);
        Tag = propMap.get("tag");
        if (Tag.equals(Interpreter.DEF_TAG)) {
            Tag = Interpreter.DEF_TAG + "@" + String.valueOf(this.hashCode()).substring(0,4);
        }
        TraceCount = Integer.valueOf(propMap.get("trc"));
        tracer = new TraceSet(TraceCount);
        MotionScale = Double.valueOf(propMap.get("mot"));
        ShowTrace = Boolean.valueOf(propMap.get("trcv"));
        ShowVelocity = Boolean.valueOf(propMap.get("velv"));
        ShowAccelaration = Boolean.valueOf(propMap.get("acv"));
        ShowMark = Boolean.valueOf(propMap.get("mkv"));
        ShowRange = Boolean.valueOf(propMap.get("grg"));
        String temp = Interpreter.interpretImgName(propMap.get("img"));
        if (temp != null) Image = temp;
    }

    public Map<String, String> getPropMap() {
        Map<String, String> toRet = new HashMap<>();
        toRet.put("tag", Tag);
        toRet.put("trc", String.valueOf(TraceCount));
        toRet.put("mot", String.valueOf(MotionScale));
        toRet.put("trcv", String.valueOf(ShowTrace));
        toRet.put("velv", String.valueOf(ShowVelocity));
        toRet.put("acv", String.valueOf(ShowAccelaration));
        toRet.put("mkv", String.valueOf(ShowMark));
        toRet.put("grg", String.valueOf(ShowRange));
        toRet.put("img", Image);
        return toRet;
    }

    public Entity() {
        super();
        build(Main.DEF_SETTING);
        prop = new PhysicalProp();
        setup();
    }

    public Entity(String tag,PhysicalProp p) {
        super();
        build(Main.DEF_SETTING);
        this.Tag = tag;
        prop = p;
        setup();
    }

    public Entity(String script) {
        super();
        build(Interpreter.interpretToMap(new HashMap<>(Main.DEF_SETTING), script));
        prop = new PhysicalProp(script);
        setup();
    }

    private void setup() {
        dragged = false;
        tracer = new TraceSet(TraceCount);
        tracer.trace(prop.Location.getPoint());
        //System.out.println(prop.Velocity.getPresX());
        SyncLoc();
        SyncSize();

        setBackground(new Color(0,0,0,0));
        setDoubleBuffered(true);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 2) {
                    System.exit(1);
                }
            }
            public void mousePressed(MouseEvent e) {
                draggedAtX = e.getX();
                draggedAtY = e.getY();
                dragged = true;
            }
            public void mouseReleased(MouseEvent e) {
                dragged = false;
                prop.Velocity.printAll();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (prop.Undraggable) return;
                prop.Velocity.assign(e.getX() - draggedAtX,
                        e.getY() - draggedAtY);
                prop.Location.offset(prop.Velocity);
                SyncLoc();
            }
        });
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D canv = (Graphics2D)g;
        //System.out.println(Image);
        Contour ctr = new Contour(this);
        if (ShowMark) Grapher.drawMark(canv,ctr);
        Grapher.drawImage(canv, ctr, Image);
    }

    public void tick(Function<PhysicalProp, Boolean> outerEff) {
        //Apply OuterEffect
        outerEff.apply(prop);
        //Apply Velocity to Location
        prop.applyAcc();
        Offset(prop.Velocity.product(MotionScale));
    }

    public boolean isDraggedByUser() {
        return dragged;
    }

    public void Offset(Vector2D vector) {
        prop.Location.offset(vector);
        SyncLoc();
        if (!prop.Size.equals(getSize()))SyncSize();
        tracer.trace(prop.getCenter().getPoint());
    }

    public void SyncLoc() {
        setLocation(prop.Location.getPoint());
    }

    public void SyncSize() {
        setSize(prop.Size.getDimension());
    }

    public TraceSet getTraceSet() {
        return tracer;
    }

    public Contour getContour() {
        return new Contour(this);
    }

    public PhysicalProp Properties() {
        return prop;
    }

}