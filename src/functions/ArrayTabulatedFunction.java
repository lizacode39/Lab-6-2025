package functions;

import java.io.Serializable;

public class ArrayTabulatedFunction implements TabulatedFunction, Serializable {
    private static final double EPS = 1e-10;
    private FunctionPoint[] pointValue;
    private int sizeValue;

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

    public ArrayTabulatedFunction(FunctionPoint[] pointArray, int pointCount) throws InappropriateFunctionPointException {
        if (pointCount < 2) {
            throw new IllegalArgumentException("Point count must be at least 2");
        }
        sizeValue = 1;
        pointValue = new FunctionPoint[pointCount + (pointCount / 2)];
        pointValue[0] = pointArray[0];
        for (int i = 1; i < pointCount; i++) {
            if (!less(pointArray[i - 1].getX(), pointArray[i].getX())) {
                throw new IllegalArgumentException("X values must be strictly increasing");
            }
            addPoint(pointArray[i]);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, int pointCount) {
        if (!less(leftX, rightX) || pointCount < 2) {
            throw new IllegalArgumentException("Invalid domain or point count");
        }
        sizeValue = pointCount;
        pointValue = new FunctionPoint[2 * sizeValue];
        double step = (rightX - leftX) / (sizeValue - 1);
        for (int i = 0; i < sizeValue; i++) {
            pointValue[i] = new FunctionPoint(leftX + step * i, 0);
        }
    }

    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (!less(leftX, rightX) || values.length < 2) {
            throw new IllegalArgumentException("Invalid domain or values length");
        }
        sizeValue = values.length;
        pointValue = new FunctionPoint[2 * sizeValue];
        double step = (rightX - leftX) / (sizeValue - 1);
        for (int i = 0; i < sizeValue; i++) {
            pointValue[i] = new FunctionPoint(leftX + step * i, values[i]);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= sizeValue) {
            throw new FunctionPointIndexOutOfBoundsException("Index out of bounds: " + index);
        }
    }

    public double getLeftDomainBorder() {
        return pointValue[0].getX();
    }

    public double getRightDomainBorder() {
        return pointValue[sizeValue - 1].getX();
    }

    public double getFunctionValue(double x) {
        double left = getLeftDomainBorder();
        double right = getRightDomainBorder();

        if (less(x, left) || greater(x, right)) {
            return Double.NaN;
        }

        // Прижимаем x к границам, если он "почти" на краю
        if (lessOrEquals(x, left)) return pointValue[0].getY();
        if (greaterOrEquals(x, right)) return pointValue[sizeValue - 1].getY();

        // Линейный поиск интервала [x_i, x_{i+1}], содержащего x
        int i = 0;
        while (i < sizeValue - 1 && greater(x, pointValue[i + 1].getX())) {
            i++;
        }

        double x1 = pointValue[i].getX();
        double y1 = pointValue[i].getY();
        double x2 = pointValue[i + 1].getX();
        double y2 = pointValue[i + 1].getY();

        if (equals(x1, x2)) {
            return y1; // избегаем деления на ноль
        }

        return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
    }

    public int getPointCount() {
        return sizeValue;
    }

    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(pointValue[index]);
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);
        double newX = point.getX();

        if (index > 0 && index < sizeValue - 1) {
            double prevX = pointValue[index - 1].getX();
            double nextX = pointValue[index + 1].getX();
            if (greater(newX, prevX) && less(newX, nextX)) {
                pointValue[index] = new FunctionPoint(point);
            } else {
                throw new InappropriateFunctionPointException("X must be between neighbors");
            }
        } else if (index == 0) {
            double nextX = pointValue[1].getX();
            if (less(newX, nextX)) {
                pointValue[index] = new FunctionPoint(point);
            } else {
                throw new InappropriateFunctionPointException("X must be less than next point");
            }
        } else { // index == sizeValue - 1
            double prevX = pointValue[index - 1].getX();
            if (greater(newX, prevX)) {
                pointValue[index] = new FunctionPoint(point);
            } else {
                throw new InappropriateFunctionPointException("X must be greater than previous point");
            }
        }
    }

    public double getPointX(int index) {
        checkIndex(index);
        return pointValue[index].getX();
    }

    public double getPointY(int index) {
        checkIndex(index);
        return pointValue[index].getY();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);
        setPoint(index, new FunctionPoint(x, getPointY(index)));
    }

    public void setPointY(int index, double y) {
        checkIndex(index);
        pointValue[index].setY(y);
    }

    public void deletePoint(int index) {
        checkIndex(index);
        if (sizeValue < 3) {
            throw new IllegalStateException("Cannot delete point: less than 3 points");
        }
        for (int i = index; i < sizeValue - 1; i++) {
            pointValue[i] = pointValue[i + 1];
        }
        pointValue[sizeValue - 1] = null;
        sizeValue--;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        if (pointValue.length <= sizeValue) {
            FunctionPoint[] newPoints = new FunctionPoint[sizeValue + sizeValue / 2];
            System.arraycopy(pointValue, 0, newPoints, 0, sizeValue);
            pointValue = newPoints;
        }

        double x = point.getX();
        int i = 0;
        while (i < sizeValue && less(pointValue[i].getX(), x)) {
            i++;
        }

        // Проверка на дубликат по x
        if (i < sizeValue && equals(pointValue[i].getX(), x)) {
            throw new InappropriateFunctionPointException("Point with this X already exists");
        }

        // Сдвиг вправо
        System.arraycopy(pointValue, i, pointValue, i + 1, sizeValue - i);
        pointValue[i] = new FunctionPoint(point);
        sizeValue++;
    }

    public void printTabFun() {
        for (int i = 0; i < sizeValue; i++) {
            System.out.println("№" + (i + 1) + " x: " + pointValue[i].getX() + " y: " + pointValue[i].getY());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append(pointValue[0]);
        for (int i = 1; i < sizeValue; i++) {
            sb.append(", ").append(pointValue[i]);
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
            FunctionPoint p1 = pointValue[i];
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
        for (int i = 0; i < sizeValue; i++) {
            long xBits = Double.doubleToLongBits(pointValue[i].getX());
            long yBits = Double.doubleToLongBits(pointValue[i].getY());
            result = 31 * result + (int) (xBits ^ (xBits >>> 32));
            result = 31 * result + (int) (yBits ^ (yBits >>> 32));
        }
        return result;
    }

    @Override
    public Object clone() {
        FunctionPoint[] copy = new FunctionPoint[sizeValue];
        for (int i = 0; i < sizeValue; i++) {
            copy[i] = new FunctionPoint(pointValue[i]);
        }
        try {
            return new ArrayTabulatedFunction(copy, sizeValue);
        } catch (InappropriateFunctionPointException e) {
            throw new RuntimeException("Clone failed due to invalid points", e);
        }
    }
}