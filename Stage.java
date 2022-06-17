import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.function.Function;

public class Stage extends JFrame implements PropertyMapped {
    private List<Entity> entities;
    private Timer ticker = new Timer();
    private Console console;
    private boolean tickerOn = false;
    private boolean backOn = false;

    public double FricScale;
    public Vector2D EnvGrav;// = new Vector2D(0,4.8);s/
    private int delta;
    private boolean bound;

    private String caption;

    public void build(Map<String, String> propMap) {
        FricScale = Double.valueOf(propMap.get("fric"));
        EnvGrav = new Vector2D(propMap.get("envg"));
        setDelta(Integer.valueOf(propMap.get("delta")));
        bound = Boolean.valueOf(propMap.get("bnd"));
        backOn = Boolean.valueOf(propMap.get("bak"));
        if (backOn) setBackground(new Color(0, 0, 0, 220));
        else setBackground(new Color(0, 0, 0, 0));
    }

    public Map<String, String> getPropMap() {
        Map<String, String> toRet = new HashMap<>();
        toRet.put("fric", String.valueOf(FricScale));
        toRet.put("envg", String.valueOf(EnvGrav));
        toRet.put("delta", String.valueOf(getDelta()));
        toRet.put("bnd", String.valueOf(bound));
        toRet.put("bak", String.valueOf(backOn));
        //toRet.put("tik", String.valueOf(tickerOn));
        return toRet;
    }


    public Stage(String stageName) {
        super(stageName);
        this.caption = stageName;
        entities = new LinkedList<>();

        setSize(Main.SCREEN);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setLayout(null);

        setBackground(new Color(0,0,0, 100));
        setVisible(true);
        build(Main.DEF_SETTING);

        console = new Console();

        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_SLASH) {
                    showConsole();
                }
            }
        });

    }

    public void paint(Graphics g) {
        //g.clearRect(0, 0, Main.SCREEN.width, Main.SCREEN.height);
        super.paint(g);
        Graphics2D canv = (Graphics2D)g;
        for (Entity ent : entities) {
            if (ent.ShowRange) Grapher.drawGravityRange(canv, ent.getContour(), (int) ent.Properties().GravityRadius);
            if (ent.ShowVelocity) Grapher.drawArrow(canv, ent.getContour(), ent.Properties().Velocity);
            if (ent.ShowAccelaration) Grapher.drawArrow(canv, ent.getContour(), ent.Properties().Accelaration);
            if (ent.ShowTrace) Grapher.drawTrace(canv, ent.getTraceSet());
        }
        repaint();
    }

    public void showConsole() {
        console.clear();
        console.setVisible(true);
    }

    public void hideConsole() { console.setVisible(false); }

    public Entity DelEntity(Entity entity) {
        entities.remove(entity);
        getContentPane().remove(entity);
        repaint();
        return entity;
    }

    public Entity AddEntity(Entity entity) {
        entities.add(entity);
        getContentPane().add(entity);
        repaint();
        return entity;
    }

    public Entity AddEntity(String script) {
        return AddEntity(new Entity(script));
    }

    public Interactive AddInteractive(String script) {
        return (Interactive) AddEntity(new Interactive(script, (p)->(Interactive.upperDown(p[0], p[1]))));

    }

    public void boundBounce(PhysicalProp prop) {
        Contour ctr = new Contour(prop.Location.getPresX(), prop.Location.getPresY(),
                prop.Size.getPresX(), prop.Size.getPresY());
        if (ctr.right() > getWidth()){
            prop.Velocity.negateX();
            prop.locateX(getWidth() - ctr.getWidth());
        } else if (ctr.left() < 0) {
            prop.Velocity.absoluteX();
            prop.locateX(0);
        }
        if (ctr.bottom() > getHeight()) {
            prop.Velocity.negateY();
            prop.locateY(getHeight() - ctr.getHeight());
        } else if (ctr.top() < 0) {
            prop.Velocity.absoluteY();
            prop.locateY(0);
        }
    }

    public int getDelta() {
        return this.delta;
    }

    public void setDelta(int newDelta) {
        this.delta = newDelta;
        if (tickerOn) {
            stopTick();
            startTick();
        }
    }

    public void startTick() {
        if (tickerOn) return;
        this.ticker = new Timer();
        this.ticker.schedule(new TickTask(), 0 , delta);
        tickerOn = true;
    }

    public void stopTick() {
        if (!tickerOn) return;
        this.ticker.cancel();
        tickerOn = false;
    }

    public void executeCommand(String script) {

        String[] sublines = script.split(Interpreter.CMD_SPLITER);
        for (String line : sublines) {
            console.Speak(Interpreter.UNDERSCORE.apply(line),Main.USERNAME);
            List<String> feedBacks = Interpreter.execute(line, this);
            boolean loading = feedBacks.size() <= 1?false:feedBacks.get(0).equals(Interpreter.LOAD_MARK);
            for (String sysfb : feedBacks) {
                console.Speak(sysfb,Main.SYSTEMNAME, loading? Interpreter.SELECTIZE:null);
            }
        }
        console.addLine(Interpreter.COMP_MARK, Interpreter.WEAKEN);

    }

    public void executeAllEntities(Function<Entity, Boolean> func) {
        for (Entity ent : entities) func.apply(ent);
    }

    private class  TickTask extends TimerTask {
        public void run() { for (Entity ent : entities)
            if (!ent.isDraggedByUser()) ent.tick(new EnvApplier());
        }
    }

    private class EnvApplier implements Function<PhysicalProp, Boolean> {

        public Boolean apply(PhysicalProp prop) {
            prop.Accelaration.clear();
            for (Entity ent : entities) {
                if (ent.Properties() != prop) {
                    if (prop.Susceptible) {
                        prop.Accelaration.offset(ent.Properties().gravitationTo(prop).quotient(prop.Mass));
                    }
                }
            }
            if (bound) boundBounce(prop);
            if (prop.Receive_g)  prop.Accelaration.offset(EnvGrav);
            if (prop.Receive_f)  prop.Velocity.multiply(1 + FricScale);
            return true;
        }
    }

    private class Console extends JFrame {
        private Vector2D DEF_SIZE = new Vector2D(700, 300);
        private int INPUT_HEIGHT = 30;
        private int DISPLAY_MAX = 11;

        private LinkedList<String> lines;
        private LinkedList<String> inputed;
        private int preInd = 0;
        private JLabel msgr;
        private JTextField inputer;
        protected Console() {
            super(caption + ".Console");

            setLayout(null);
            setUndecorated(true);
            setBackground(new Color(0,0,0,100));
            setSize(DEF_SIZE.getDimension());
            setLocation(new Vector2D(Main.SCREEN).getOffset(DEF_SIZE.opposite()).getPoint());
            setAlwaysOnTop(true);
            setVisible(true);

            lines = new LinkedList<>();
            inputed = new LinkedList<>();

            inputer = new JTextField();
            inputer.setOpaque(false);
            inputer.setForeground(Color.WHITE);
            inputer.setFont(new Font("Tahoma", Font.PLAIN, 20));
            inputer.setLocation(0, DEF_SIZE.getY() - INPUT_HEIGHT);
            inputer.setSize(DEF_SIZE.getDimension().width, INPUT_HEIGHT);
            getContentPane().add(inputer);
            inputer.setVisible(true);
            inputer.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    int key = e.getKeyCode();
                    if (e.getKeyChar() == KeyEvent.VK_SLASH) {
                        hideConsole();
                        preInd = 0;
                    } else if (key == KeyEvent.VK_ENTER && !inputer.getText().equals("")) {
                        executeCommand(inputer.getText());
                        inputed.addFirst(inputer.getText());
                        preInd = 0;
                        inputer.setText("");
                    } else if (key == KeyEvent.VK_UP) {
                        showPreInput();
                        preInd++;
                    } else if (key == KeyEvent.VK_DOWN) {
                        showPreInput();
                        preInd--;
                    } else {
                        preInd = 0;
                    }
                }
            });
            inputer.requestFocusInWindow();

            msgr = new JLabel();
            msgr.setOpaque(false);
            msgr.setForeground(Color.WHITE);
            msgr.setFont(new Font("Tahoma", Font.PLAIN, 20));
            msgr.setVerticalAlignment(SwingConstants.BOTTOM);
            msgr.setLocation(0, 0);
            msgr.setSize(DEF_SIZE.getOffset(new Vector2D(0, -INPUT_HEIGHT)).getDimension());
            getContentPane().add(msgr);
            msgr.setVisible(true);
            Speak("OS Version : " + System.getProperty("os.version"),Main.SYSTEMNAME);
            Speak("Architecture : " + System.getProperty("os.arch"), Main.SYSTEMNAME);
            Speak("Java Edition : " + System.getProperty("java.version"), Main.SYSTEMNAME);
            Speak("jre Home : " + System.getProperty("java.home"), Main.SYSTEMNAME);
            Speak("User : " + Main.USERNAME, Main.SYSTEMNAME);
            addLine(Interpreter.COMP_MARK, Interpreter.WEAKEN);
        }

        public void showPreInput() {
            if (inputed.size() <= 0)return;
            if (preInd < 0) preInd +=inputed.size();
            preInd %= inputed.size();
            inputer.setText(inputed.get(preInd));
        }

        public void Speak(String str, String host) {Speak(str, host, null);}

        public void Speak(String str, String host, Function<String, String> style) {
            addLine("[" + host + "] " + str, style);
        }

        public void addLine(String str, Function<String, String> style) {
            if (lines.size() >= DISPLAY_MAX) {
                lines.removeFirst();
            }
            if (style != null) str = style.apply(str);

            lines.addLast(str);
            msgr.setText(Interpreter.interpretToHTML(lines));
        }
        public void clear() {
            inputer.setText("");
        }
    }

    public Entity searchEntityByTag(String tag) {
        for (Entity entity : entities) if (entity.Tag.equals(tag)
        ) return entity;
        return null;
    }
}