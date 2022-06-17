import java.awt.*;
import java.util.LinkedList;

public class TraceSet {
    private int count;
    private LinkedList<Point> pts;

    public TraceSet(int count) {
        this.count = count;
        pts = new LinkedList<>();
    }

    public void trace(Point p) {
        if (pts.size() >= count && pts.size() > 1) {
            pts.removeFirst();
        }
        pts.addLast(p);
    }

    public LinkedList<Point> getPointSet() {
        return pts;
    }
    private class TracePoint extends Point {
        public TracePoint next;
        public TracePoint(Point p) {
            super(p);
        }
    }
}
