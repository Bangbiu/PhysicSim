
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
public class Interpreter {

    public static final int SHORTEN_LEN = 3;

    public static final String CMD_SPLITER = ";";
    public static final String PARAM_SPLITER = ",";
    public static final String NMVAL_SPLITER = "=";
    public static final String NAME_SPLITER = "_";
    public static final String CP_SPLITPER = " ";
    public static final String PERCENTAGE = "%";

    public static final String HTML_START = "<html>";
    public static final String HTML_END = "</html>";
    public static final String HTML_NEWLINE = "<br/>";

    public static final String OP_ADD = "+";
    public static final String OP_SUB = "-";

    public static final String DEF_STR = "def";
    public static final String DEF_TAG = "ent";

    public static final String COMP_MARK = "===============================";
    public static final String STAGE_MARK = "stg";
    public static final String SET_MARK = "set";
    public static final String SAVE_MARK = "save";
    public static final String PROP_MARK = "prop";
    public static final String START_MARK = "start";
    public static final String PAUSE_MARK = "pause";
    public static final String END_MARK = "end";
    public static final String POSS_MARK = ".";
    public static final String ALL_MARK = "all";
    public static final String ADD_MARK = "add";
    public static final String ADDCMDENT_MARK = "adde";
    public static final String DEL_MARK = "del";
    public static final String PRS_MARK = "preset";
    public static final String GET_MARK = "get";
    public static final String RND_MARK = "rnd";

    public static final String INV_MARK = "Nope";
    public static final String NF_MARK = "Not Found";
    public static final String LOAD_MARK = "Loading...";

    public static final Function<String ,String> WEAKEN = (text)->"<font size=3>" + text + "</font>";
    public static final Function<String ,String> UNDERSCORE = (text)->"<b>" + text + "</b>";
    public static final Function<String ,String> SELECTIZE = (text)->"<font color=7FFF00>" + text + "</font>";

    public static List<String> execute(String script, Stage stage) {
        List<String> toRet = new LinkedList<>();

        String[] splited = separate(script, CP_SPLITPER);
        String cmdStr = splited[0];

        //No Parameter
        if (splited.length <=1) {
            //Public Function
            if (cmdStr.indexOf(POSS_MARK) == -1) {
                switch (cmdStr) {
                    case END_MARK : System.exit(1);
                    case START_MARK : stage.startTick(); toRet.add("Start Ticking");break;
                    case PAUSE_MARK : stage.stopTick(); toRet.add("Pause Ticking");break;
                    case PROP_MARK : {
                        String propSet = new String();
                        int newLiner = 0;
                        for (String key : Main.DEF_SETTING.keySet()) {
                            propSet += key + PARAM_SPLITER;
                            newLiner++;
                            if (newLiner > 10) { propSet += HTML_NEWLINE; newLiner = 0;}
                        }
                        toRet.add(propSet);break;
                    }
                    default:toRet.add(INV_MARK);
                }
                return toRet;
            }
            //Private Function
            String tag = cmdStr.substring(0, cmdStr.indexOf(POSS_MARK));

            if (find(cmdStr, DEL_MARK)) {//del statement
                List<Entity> toDel = new LinkedList<>();
                stage.executeAllEntities((ent) -> {
                    if (ent.Tag.equals(tag) || tag.equals(ALL_MARK)) return toDel.add(ent);
                    return true;
                });
                for (Entity ent : toDel) {
                    stage.DelEntity(ent);
                    toRet.add(ent.Tag + " has been deleted");
                }
            } else toRet.add(INV_MARK);
            return toRet;
        }

        //Parameters Available
        //Public Function
        final String params = splited[1];
        if (cmdStr.indexOf(POSS_MARK) == -1) {
            switch (cmdStr) {
                //add statement
                case ADD_MARK:toRet.add(UNDERSCORE.apply(stage.AddEntity(params).Tag) + " has been added");break;
                //add Counter
                case ADDCMDENT_MARK: {
                    toRet.add(UNDERSCORE.apply(stage.AddInteractive(params).Tag) + " has been added");break;
                }
                //preset statement
                case PRS_MARK: {
                    List<String> cmdList = FileSystem.getAllLines(params);
                    if (cmdList != null) {
                        toRet.add(LOAD_MARK);
                        for (String line : cmdList) {
                            execute(line, stage);
                            toRet.add(line);
                        }
                    } else toRet.add(NF_MARK);
                    break;
                }

                default:toRet.add(INV_MARK);
            }
            return toRet;
        }

        //Private Function
        String tag = cmdStr.substring(0, cmdStr.indexOf(POSS_MARK));

        if (find(cmdStr, SET_MARK)) {//set statement
            switch (tag) {
                case STAGE_MARK: stage.build(interpretToMap(stage.getPropMap(), params));break;
                case DEF_STR: Main.DEF_SETTING = interpretToMap(Main.DEF_SETTING, params);break;
                default: {//Particular Entity Tag
                    stage.executeAllEntities((ent) -> {
                        if (ent.Tag.equals(tag) || tag.equals(ALL_MARK)) {
                            ent.build(interpretToMap(ent.getPropMap(), params));
                            PhysicalProp p = ent.Properties();
                            p.build(interpretToMap(p.getPropMap(), params));
                            toRet.add(ent.Tag + " has been set");
                        }return true;
                    });
                }
            }

        }
        else if (find(cmdStr, GET_MARK)) { //get statement
            //Properties Loop Start
            for (String prop : params.split(PARAM_SPLITER)) {
                switch (tag) {
                    case STAGE_MARK: toRet.add(prop + NMVAL_SPLITER + stage.getPropMap().get(prop));break;
                    case DEF_STR: toRet.add(prop + NMVAL_SPLITER + Main.DEF_SETTING.get(prop));break;
                    default: {
                        //System.out.println(tag);
                        stage.executeAllEntities((ent) -> {
                            if (ent.Tag.equals(tag) || tag.equals(ALL_MARK)) {
                                toRet.add(ent.Tag + POSS_MARK + prop + NMVAL_SPLITER +
                                        validate(ent.getPropMap().get(prop),
                                                ent.Properties().getPropMap().get(prop)));
                            }return true;
                        });
                    }
                }
            }
            //Properties Loop End
        }
        else toRet.add(INV_MARK);
        return toRet;
    }

    public static Map<String, String> interpretToMap(Map<String, String> baseMap, String script) {
        Map<String, String> toRet = new HashMap(baseMap);
        String[] splited = script.split(PARAM_SPLITER);
        for (int i = 0; i < splited.length; i++) {
            String sub = splited[i];
            //SPLITER inside Vector
            if (sub.indexOf(Vector2D.ST_BOUND) != -1) {
                i++;
                sub += Vector2D.COR_SPLITER + splited[i];
            }
            if (sub.indexOf(NMVAL_SPLITER) == -1) continue;
            String[] grp = separate(sub, NMVAL_SPLITER);
            String defVal = Main.DEF_SETTING.get(grp[0]);
            switch (grp[1]) {
                case DEF_STR : grp[1] = defVal;break;
                case RND_MARK : grp[1] = defVal;break;
                case "t" : grp[1] = "true";break;
                case "f" : grp[1] = "false";break;
                default: {
                    if (grp[0].endsWith(OP_ADD)) {
                        grp[0] = grp[0].substring(0, grp[0].length()-1);
                        if (baseMap.get(grp[0]) != null) {
                            grp[1] = interpretOffset(baseMap.get(grp[0]), grp[1]);
                        }
                    }
                }
            }
            toRet.put(grp[0].trim(), grp[1]);
        }
        return toRet;
    }

    public static Entity interpretEntity(String script, Stage stage) {
        return stage.searchEntityByTag(script);
    }

    public static Vector2D interpretVector(String script) {
        if (script.indexOf(Vector2D.COR_SPLITER) == -1) {
            if (script.equals(Vector2D.ORIGIN)) {
                return new Vector2D();
            }
            return null;
        }

        Vector2D toRet = new Vector2D();
        String[] raw = script.split(Vector2D.COR_SPLITER);
        String tX = raw[0].replace(Vector2D.ST_BOUND, ' ').trim();
        String tY = raw[1].replace(Vector2D.ED_BOUND, ' ').trim();
        if (tX.indexOf(PERCENTAGE) != -1) {
            tX = tX.substring(0, tX.length() - 1);
            tY = tY.substring(0, tY.length() - 1);
            toRet.assign(Main.SCREEN.width / 100 * Integer.valueOf(tX),
                    Main.SCREEN.height / 100 * Integer.valueOf(tY));
        } else {
            System.out.println(tY);
            toRet.assign(Double.valueOf(tX), Double.valueOf(tY));
        }
        return toRet;
    }

    public static String interpretImgName(String script) {
        script = script.toLowerCase();
        for (String imgName : FileSystem.getAllImgName()) {
            if (script.equals(imgName.toLowerCase())) {
                return imgName;
            }

            if (imgName.indexOf(NAME_SPLITER) == -1) {
                if (shorten(script).equals(shorten(imgName))) return imgName;
            } else {
                String acr = new String();
                for (String part : imgName.split(NAME_SPLITER)) acr += part.toLowerCase().charAt(0);
                if (script.substring(0, acr.length()).equals(acr)) {
                    return imgName;
                }
            }
        }
        return null;
    }

    public static String interpretOffset(String base, String off) {
        if (isVector(off)) {
            return new Vector2D(base).getOffset(new Vector2D(off)).toString();
        } else if (isNumeric(base) && isNumeric(off)) {
            if (base.indexOf(".") == -1 && off.indexOf(".") == -1)
                return String.valueOf(Integer.valueOf(base) + Integer.valueOf(off));
            else
                return String.valueOf(Double.valueOf(base) + Double.valueOf(off));
        } else {
            return base;
        }
    }

    public static String interpretToHTML(List<String> lines) {
        String toRet = HTML_START;
        for (int i = 0; i < lines.size() - 1; i++) {
            toRet += lines.get(i) + HTML_NEWLINE;
        }
        toRet += lines.get(lines.size() - 1) + HTML_END;
        return toRet;
    }

    public static boolean isVector(String str) {
        return Pattern.compile("\\(-?\\d+\\.?\\d*,-?\\d+\\.?\\d*\\)").matcher(str).matches();
    }

    public static boolean isNumeric(String str) {
        return Pattern.compile("\\d+|\\d+\\.\\d+").matcher(str).matches();
    }
    private static String validate(String str1, String str2) {
        if (str1 == null) {
            return str2;
        } else {
            return str1;
        }
    }
    private static String shorten(String str) {
        if (str.length() < SHORTEN_LEN) {
            return str;
        } else {
            return str.toLowerCase().substring(0, SHORTEN_LEN);
        }
    }

    private static String[] separate(String string, String spliter) {

        int index = string.indexOf(spliter);
        if (index <= 1) {
            return new String[] {string};
        } else {
            return new String[] {string.substring(0, index), string.substring(index + 1)};
        }
    }

    private static boolean find(String src, String tofind) {
        return (src.indexOf(tofind) != -1);
    }
}
