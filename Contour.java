import javax.swing.*;
import java.awt.*;

public class Contour {
    private double cX, cY;
    private double cW, cH;

    public Contour() {
        cX = cY = cW = cH = 0;
    }

    public Contour(double cX, double cY, double cW, double cH) {
        this.cX = cX;
        this.cY = cY;
        this.cW = cW;
        this.cH = cH;
    }

    public Contour(JComponent comp) {
        cX = comp.getX();
        cY = comp.getY();
        cW = comp.getWidth();
        cH = comp.getHeight();
    }

    public Dimension getDimension() {
        return new Dimension((int) cW,(int) cH);
    }

    public Point getLocation() {
        return new Point((int) cX, (int) cY);
    }

    public Vector2D getCenter() {
        return new Vector2D(cX + cW/2, cY + cH/2);
    }

    public int getWidth() { return (int) cW;}

    public int getHeight() { return (int) cH;}

    public int getX() { return (int) cX;}

    public int getY() { return (int) cY;}

    public int right() {
        return (int) (cX + cW);
    }

    public int left() {
        return (int) cX;
    }

    public int bottom() {
        return (int) (cY + cH);
    }

    public int top() {
        return (int) cY;
    }

}
