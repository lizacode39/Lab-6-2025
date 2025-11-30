package functions;

import java.io.*;

public class TabulatedFunctions {
    private TabulatedFunctions() {}

    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount)
            throws InappropriateFunctionPointException {
        if (pointsCount < 2 || leftX >= rightX) {
            throw new IllegalArgumentException("Invalid point count or domain");
        }
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Tabulation domain outside function domain");
        }

        FunctionPoint[] pointArray = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + step * i;
            pointArray[i] = new FunctionPoint(x, function.getFunctionValue(x));
        }
        return new ArrayTabulatedFunction(pointArray, pointsCount);
    }

    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            int n = function.getPointCount();
            dos.writeInt(n);
            for (int i = 0; i < n; i++) {
                dos.writeDouble(function.getPointX(i));
                dos.writeDouble(function.getPointY(i));
            }
        }
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        try (DataInputStream dis = new DataInputStream(in)) {
            int n = dis.readInt();
            FunctionPoint[] points = new FunctionPoint[n];
            for (int i = 0; i < n; i++) {
                points[i] = new FunctionPoint(dis.readDouble(), dis.readDouble());
            }
            try {
                return new ArrayTabulatedFunction(points, n);
            } catch (InappropriateFunctionPointException e) {
                throw new IOException("Invalid points in stream", e);
            }
        }
    }

    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(out)) {
            int n = function.getPointCount();
            bw.write(Integer.toString(n));
            for (int i = 0; i < n; i++) {
                bw.write(' ');
                bw.write(Double.toString(function.getPointX(i)));
                bw.write(' ');
                bw.write(Double.toString(function.getPointY(i)));
            }
        }
    }

    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer st = new StreamTokenizer(in);
        if (st.nextToken() != StreamTokenizer.TT_NUMBER) {
            throw new IOException("Expected number of points");
        }
        int n = (int) st.nval;
        FunctionPoint[] points = new FunctionPoint[n];
        for (int i = 0; i < n; i++) {
            if (st.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Expected x");
            double x = st.nval;
            if (st.nextToken() != StreamTokenizer.TT_NUMBER) throw new IOException("Expected y");
            double y = st.nval;
            points[i] = new FunctionPoint(x, y);
        }
        try {
            return new ArrayTabulatedFunction(points, n);
        } catch (InappropriateFunctionPointException e) {
            throw new IOException("Invalid points in stream", e);
        }
    }
}