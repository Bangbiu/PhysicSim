import java.awt.*;

public class Vector2D {
    private double vX;
    private double vY;

    public static final String ORIGIN = "0";
    public static final String COR_SPLITER = ",";
    public static final char ST_BOUND = '(';
    public static final char ED_BOUND = ')';

    public Vector2D() {
        vX = 0;
        vY = 0;
    }

    public Vector2D(double initX, double initY) {
        vX = initX;
        vY = initY;
    }

    public Vector2D(Vector2D other) {
        assign(other);
    }

    public Vector2D(Dimension d) {
        vX = d.getWidth();
        vY = d.getHeight();
    }

    public Vector2D(Point p) {
        vX = p.getX();
        vY = p.getY();
    }

    public Vector2D(String script) {
        assign(Interpreter.interpretVector(script));
    }

    public void clear() {
        vX = 0;
        vY = 0;
    }

    public double getPresX() { return this.vX; }

    public double getPresY() { return this.vY; }

    public int getX() { return (int) this.vX; }

    public int getY() { return (int) this.vY; }

    public Point getPoint() { return new Point(getX(), getY()); }

    public Dimension getDimension() { return new Dimension(getX(), getY());}

    public void setX(double newX) { this.vX = newX; }

    public void setY(double newY) { this.vY = newY; }

    public void assign(double newX, double newY) {
        setX(newX);
        setY(newY);
    }

    public void assign(Vector2D other) {
        setX(other.getPresX());
        setY(other.getPresY());
    }

    public boolean isZero() {
        return vX == 0 && vY == 0;
    }


    public void offset(double offX, double offY) {
        offsetX(offX);
        offsetY(offY);
    }

    public Vector2D getOffset(Vector2D vector) {
        return new Vector2D(vX + vector.getPresX(), vY + vector.getPresY());
    }

    public void offset(Vector2D vector) { offset(vector.getPresX(), vector.getPresY()); }

    public void offsetX(double offX) { vX += offX; }

    public void offsetY(double offY) { vY += offY; }

    public void reverseX() { vX = -vX; }

    public void reverseY() { vY = -vY; }

    public void absoluteX() {vX = Math.abs(vX);}

    public  void absoluteY() {vY = Math.abs(vY);}

    public void negateX() { vX = - Math.abs(vX);}

    public  void negateY() {vY = - Math.abs(vY);}

    public Vector2D opposite() {
        return new Vector2D(-vX, -vY);
    }

    public  Vector2D quotient(double divider) { return new Vector2D(vX / divider, vY / divider); }

    public void divide(double divider) {
        vX /= divider;
        vY /= divider;
    }

    public Vector2D product(double multiplier) { return new Vector2D(
            vX * multiplier, vY * multiplier); }

    public void  multiply(double multiplier) {
        vX *= multiplier;
        vY *= multiplier;
    }

    public Vector2D to(Vector2D target) {
        return new Vector2D(target.getPresX() - vX, target.getPresY() - vY);
    }

    public Vector2D unit() {
        double len = length();
        if (len != 0) {
            return new Vector2D(vX / length(), vY / length());
        } else {
            return new Vector2D(0,0);
        }
    }

    public double length() { return Math.sqrt(vX * vX + vY * vY); }

    public boolean equals(Object other) {
        if (other instanceof Vector2D) {
            Vector2D vector = (Vector2D)other;
            return (vector.getPresX() == vX && vector.getPresY() == vY);
        } else if (other instanceof Dimension){
            Dimension dimen = (Dimension)other;
            return (dimen.width == (int) vX && dimen.height == (int) vY);
        } else if (other instanceof Point) {
            Point point = (Point)other;
            return (point.getX() == (int) vX && point.getY() == (int) vY);
        } else {
            return false;
        }
    }
    @Override
    public String toString() { return ST_BOUND + "" + vX + COR_SPLITER + vY + ED_BOUND;}
    public void printAll() { System.out.println(this); }

}
