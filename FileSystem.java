import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FileSystem {
    public static final String RES_PATH = "RES";
    public static final String IMG_PATH = RES_PATH + File.separator + "IMG" + File.separator;
    public static final String PRS_PATH = RES_PATH + File.separator + "PRS" + File.separator;

    public static final String PNG_SUFF = ".png";
    public static final String PX_SUFF = ".px";

    public static String getImgFull(String fileName) {
        return IMG_PATH + fileName + PNG_SUFF;
    }

    public static Image getImageByName(String ImageName) {
        Image toRet;
        return null;
    }

    public static List<String> getAllImgName() {
        File imgFolder = new File(IMG_PATH);
        List<String> toRet = new LinkedList<>();
        for(String imgName : imgFolder.list()) {
            toRet.add(imgName.replace(PNG_SUFF, ""));
        }
        return toRet;
    }

    public static List<String> getAllLines(String pxPath) {
        File file = new File((pxPath.indexOf(File.separator)==-1?PRS_PATH : "")+ pxPath);
        if (!file.exists() || pxPath.indexOf(PX_SUFF) == -1) {
            return null;
        }
        List<String> toRet = new LinkedList<>();
        try {
            FileReader reader = new FileReader(file.getAbsoluteFile());
            BufferedReader br = new BufferedReader(reader);
            String line;
            while((line = br.readLine()) != null){
                for (String part : line.split(Interpreter.CMD_SPLITER)) toRet.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return toRet;
    }

}