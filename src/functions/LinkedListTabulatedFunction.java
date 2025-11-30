package functions;

import java.io.*;

public class LinkedListTabulatedFunction implements TabulatedFunction, Externalizable {
    private static final double EPS = 1e-10;

    // Вспомогательные методы сравнения с учётом EPS
    private static boolean equals(double a, double b) {
        return Math.abs(a - b) < EPS;
    }

    private static boolean less(double a, double b) {
        return a < b - EPS;
    }

    private static boolean lessOrEquals(double a, double b) {
        return a <= b + EPS;
    }

    private static boolean greater(double a, double b) {
        return a > b + EPS;
    }

    private static boolean greaterOrEquals(double a, double b) {
        return a >= b - EPS;
    }

    private static class FunctionNode implements Serializable {
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;

        FunctionNode() {}

        FunctionNode(FunctionNode prev, FunctionPoint point, FunctionNode next) {
            this.point = new FunctionPoint(point);
            this.prev = prev;
            this.next = next;
        }
    }

    private FunctionNode head;
    private int sizeValue;

    // Кэш для ускорения доступа по индексу
    private transient FunctionNode cacheNode;
    private transient int cacheIndex = 0;

    // Конструктор по умолчанию (для Externalizable)
    public LinkedListTabulatedFunction() {}

    // Приватный метод для инициализации пустого списка
    private void initEmpty() {
        head = new FunctionNode();
        head.next = head;
        head.prev = head;
        sizeValue = 0;
        cacheNode = head;
        cacheIndex = 0;
    }

    // Вспомогательный метод: добавить точку в конец (без проверок монотонности)
    private void addPointUnsafe(FunctionPoint point) {
        FunctionNode newNode = new FunctionNode(head.prev, point, head);
        head.prev.next = newNode;
        head.prev = newNode;
        sizeValue++;
        if (sizeValue == 1) {
            cacheNode = newNode;
            cacheIndex = 0;
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= sizeValue) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    private FunctionNode getNodeByIndex(int index) {
        checkIndex(index);

        int distFromCache = Math.abs(index - cacheIndex);
        int distFromStart = index;
        int distFromEnd = sizeValue - 1 - index;

        if (distFromCache <= distFromStart && distFromCache <= distFromEnd) {
            // Идти от кэша
            while (cacheIndex > index) {
                cacheNode = cacheNode.prev;
                cacheIndex--;
            }
            while (cacheIndex < index) {
                cacheNode = cacheNode.next;
                cacheIndex++;
            }
        } else if (distFromStart <= distFromEnd) {
            // Идти с начала
            cacheNode = head.next;
            cacheIndex = 0;
            for (int i = 0; i < index; i++) {
                cacheNode = cacheNode.next;
                cacheIndex++;
            }
        } else {
            // Идти с конца
            cacheNode = head.prev;
            cacheIndex = sizeValue - 1;
            for (int i = sizeValue - 1; i > index; i--) {
                cacheNode = cacheNode.prev;
                cacheIndex--;
            }
        }
        return cacheNode;
    }

    // --- PUBLIC METHODS ---

    public LinkedListTabulatedFunction(FunctionPoint[] points, int pointCount) {
        if (pointCount < 2) {
            throw new IllegalArgumentException("At least 2 points required");
        }
        initEmpty();
        addPointUnsafe(points[0]);
        for (int i = 1; i < pointCount; i++) {
            if (!less(points[i - 1].getX(), points[i].getX())) {
                throw new IllegalArgumentException("X must be strictly increasing");
            }
            addPointUnsafe(points[i]);
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, int pointCount) {
        if (!less(leftX, rightX) || pointCount < 2) {
            throw new IllegalArgumentException("Invalid domain or point count");
        }
        initEmpty();
        double step = (rightX - leftX) / (pointCount - 1);
        for (int i = 0; i < pointCount; i++) {
            addPointUnsafe(new FunctionPoint(leftX + step * i, 0));
        }
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (!less(leftX, rightX) || values.length < 2) {
            throw new IllegalArgumentException("Invalid domain or values length");
        }
        initEmpty();
        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            addPointUnsafe(new FunctionPoint(leftX + step * i, values[i]));
        }
    }

    @Override
    public double getLeftDomainBorder() {
        return head.next.point.getX();
    }

    @Override
    public double getRightDomainBorder() {
        return head.prev.point.getX();
    }

    @Override
    public double getFunctionValue(double x) {
        double left = getLeftDomainBorder();
        double right = getRightDomainBorder();

        if (less(x, left) || greater(x, right)) {
            return Double.NaN;
        }

        if (lessOrEquals(x, left)) return head.next.point.getY();
        if (greaterOrEquals(x, right)) return head.prev.point.getY();

        // Линейный поиск
        int i = 0;
        while (i < sizeValue - 1 && greater(x, getPointX(i + 1))) {
            i++;
        }

        double x1 = getPointX(i);
        double y1 = getPointY(i);
        double x2 = getPointX(i + 1);
        double y2 = getPointY(i + 1);

        if (equals(x1, x2)) return y1;

        return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
    }

    @Override
    public int getPointCount() {
        return sizeValue;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(getNodeByIndex(index).point);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);
        double newX = point.getX();

        if (index > 0 && index < sizeValue - 1) {
            double prevX = getPointX(index - 1);
            double nextX = getPointX(index + 1);
            if (greater(newX, prevX) && less(newX, nextX)) {
                getNodeByIndex(index).point = new FunctionPoint(point);
            } else {
                throw new InappropriateFunctionPointException("X must be between neighbors");
            }
        } else if (index == 0) {
            if (less(newX, getPointX(1))) {
                getNodeByIndex(index).point = new FunctionPoint(point);
            } else {
                throw new InappropriateFunctionPointException("X must be less than next point");
            }
        } else { // index == sizeValue - 1
            if (greater(newX, getPointX(sizeValue - 2))) {
                getNodeByIndex(index).point = new FunctionPoint(point);
            } else {
                throw new InappropriateFunctionPointException("X must be greater than previous point");
            }
        }
    }

    @Override
    public double getPointX(int index) {
        checkIndex(index);
        return getNodeByIndex(index).point.getX();
    }

    @Override
    public double getPointY(int index) {
        checkIndex(index);
        return getNodeByIndex(index).point.getY();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);
        setPoint(index, new FunctionPoint(x, getPointY(index)));
    }

    @Override
    public void setPointY(int index, double y) {
        checkIndex(index);
        getNodeByIndex(index).point.setY(y);
    }

    @Override
    public void deletePoint(int index) {
        checkIndex(index);
        if (sizeValue < 3) {
            throw new IllegalStateException("Cannot delete: less than 3 points");
        }
        FunctionNode node = getNodeByIndex(index);
        node.prev.next = node.next;
        node.next.prev = node.prev;
        sizeValue--;
        cacheNode = head.next;
        cacheIndex = 0;
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int index = 0;
        while (index < sizeValue && less(getPointX(index), point.getX())) {
            index++;
        }

        if (index < sizeValue && equals(getPointX(index), point.getX())) {
            throw new InappropriateFunctionPointException("Point with this X already exists");
        }

        if (index == sizeValue) {
            addPointUnsafe(point);
        } else {
            FunctionNode nextNode = getNodeByIndex(index);
            FunctionNode newNode = new FunctionNode(nextNode.prev, point, nextNode);
            nextNode.prev.next = newNode;
            nextNode.prev = newNode;
            sizeValue++;
            cacheNode = newNode;
            cacheIndex = index;
        }
    }

    // --- SERIALIZATION ---

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(sizeValue);
        FunctionNode current = head.next;
        for (int i = 0; i < sizeValue; i++) {
            out.writeDouble(current.point.getX());
            out.writeDouble(current.point.getY());
            current = current.next;
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        initEmpty();
        int pointCount = in.readInt();
        for (int i = 0; i < pointCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            addPointUnsafe(new FunctionPoint(x, y));
        }
    }

    // --- OBJECT METHODS ---

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append(getPoint(0));
        for (int i = 1; i < sizeValue; i++) {
            sb.append(", ").append(getPoint(i));
        }
        return sb.append("}").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TabulatedFunction)) return false;
        TabulatedFunction other = (TabulatedFunction) obj;
        if (sizeValue != other.getPointCount()) return false;
        for (int i = 0; i < sizeValue; i++) {
            FunctionPoint p1 = getPoint(i);
            FunctionPoint p2 = other.getPoint(i);
            if (!equals(p1.getX(), p2.getX()) || !equals(p1.getY(), p2.getY())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + sizeValue;
        FunctionNode current = head.next;
        for (int i = 0; i < sizeValue; i++) {
            long xBits = Double.doubleToLongBits(current.point.getX());
            long yBits = Double.doubleToLongBits(current.point.getY());
            result = 31 * result + (int) (xBits ^ (xBits >>> 32));
            result = 31 * result + (int) (yBits ^ (yBits >>> 32));
            current = current.next;
        }
        return result;
    }

    @Override
    public Object clone() {
        FunctionPoint[] points = new FunctionPoint[sizeValue];
        FunctionNode current = head.next;
        for (int i = 0; i < sizeValue; i++) {
            points[i] = new FunctionPoint(current.point);
            current = current.next;
        }
        return new LinkedListTabulatedFunction(points, sizeValue);
    }
}