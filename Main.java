import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static final Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
    public static final String USERNAME = System.getProperty("user.name");
    public static final String SYSTEMNAME = "System";
    public static final double DISPLAY_SCALE = 100;

    public static Map<String, String> DEF_SETTING = new HashMap<>();

    public static Stage mainStage;
    
    public static void main(String[] args) {
        initalizeDefault();
        mainStage = new Stage("stage");
        mainStage.startTick();

        //System.out.println(mainStage.getClass().getResourceAsStream("RES/IMG/GLOBE.png")==null);
    }

    public static void initalizeDefault() {
        //property
        DEF_SETTING.put("loc", "0");
        DEF_SETTING.put("siz", "(100,100)");
        DEF_SETTING.put("vel", "0");
        DEF_SETTING.put("acc", "0");
        DEF_SETTING.put("mas", "10");
        DEF_SETTING.put("gvig", "false");
        DEF_SETTING.put("mtg", "false");
        DEF_SETTING.put("spt", "false");
        DEF_SETTING.put("undrg", "false");
        DEF_SETTING.put("rcvg", "false");
        DEF_SETTING.put("rcvf", "false");
        DEF_SETTING.put("elc", "1");
        DEF_SETTING.put("rad", "-1");
        //entity
        DEF_SETTING.put("tag", Interpreter.DEF_TAG);
        DEF_SETTING.put("trc", "20");
        DEF_SETTING.put("mot", "0.5");
        DEF_SETTING.put("trcv", "false");
        DEF_SETTING.put("velv", "false");
        DEF_SETTING.put("acv", "false");
        DEF_SETTING.put("mkv", "false");
        DEF_SETTING.put("img", "glo");
        //interactive
        DEF_SETTING.put("tgt", null);
        DEF_SETTING.put("cmd", "");
        DEF_SETTING.put("cdn","30");
        // stage
        DEF_SETTING.put("fric", "-0.02");
        DEF_SETTING.put("envg", "(0,2.4)");
        DEF_SETTING.put("delta", "30");
        DEF_SETTING.put("bnd", "false");
        DEF_SETTING.put("bak", "false");
        //DEF_SETTING.put("tik", "true");
        //global
        DEF_SETTING.put("path", "");
    }
}