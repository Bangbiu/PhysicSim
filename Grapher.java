import java.awt.*;
import java.util.List;

public class Grapher{
    public static void drawOval(Graphics2D g, Contour ctr) {
        g.setColor(Color.BLUE);
        g.fillOval(0,0,ctr.getWidth(), ctr.getHeight());
    }

    public static void drawBackHole(Graphics2D g, Contour ctr) {
        g.setStroke(new BasicStroke(4f));
        int ctX = ctr.getWidth() / 2, ctY = ctr.getHeight() / 2;
        for (int i = 0; i < Math.min(ctX, ctX); i += 2) {
            g.setColor(new Color(0,0,0, 255 - 255 * i / ctX));
            g.drawOval(ctX - i, ctY - i, i * 2, i * 2);
        }
    }

    public static void drawImage(Graphics2D g, Contour ctr, String img) {

        Image image=Toolkit.getDefaultToolkit().getImage(FileSystem.getImgFull(img));
        g.drawImage(image, 0, 0, ctr.getHeight(), ctr.getHeight(), null);
        //ctr.getClass().getResourceAsStream("");
    }

    public static void drawArrow(Graphics2D g,Contour ctr, Vector2D vector) {
        g.setColor(Color.white);
        g.setStroke(new BasicStroke(2f));

        if (!vector.isZero()) {
            Vector2D center = ctr.getCenter();
            Vector2D dest = center.getOffset(vector);
            g.drawLine(center.getX(), center.getY(), dest.getX(), dest.getY());
        }
    }
    public static void drawTrace(Graphics2D g, TraceSet trace) {
        //System.out.println(pts);
        g.setColor(Color.white);
        g.setStroke(new BasicStroke(3f));
        List<Point> pts = trace.getPointSet();
        for (int i = 0; i < pts.size() - 1; i++) {
            g.drawLine((int) pts.get(i).getX(), (int) pts.get(i).getY(),
                    (int) pts.get(i+1).getX(), (int) pts.get(i + 1).getY());
        }
    }

    public static void drawMark(Graphics2D g, Contour ctr) {
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3f));
        g.drawRect(0,0,ctr.getWidth(), ctr.getHeight());
        g.setColor(new Color(255, 0, 0, 100));
        g.fillRect(0, 0, ctr.getWidth(), ctr.getHeight());
    }

    public static void drawGravityRange(Graphics2D g, Contour ctr, int radius) {
        if (radius > 0) {
            g.setColor(Color.white);
            g.setStroke(new BasicStroke(3f));
            g.drawOval(ctr.getCenter().getX() - radius, ctr.getCenter().getY() - radius, radius * 2, radius * 2);
            g.setColor(new Color(255, 255, 255, 100));
            g.fillOval(ctr.getCenter().getX() - radius, ctr.getCenter().getY() - radius, radius * 2, radius * 2);
        }
    }
}
