package functions;

import functions.Meta.*;

public class Functions {
    private Functions() {}

    public static Function shift(Function f, double shiftX, double shiftY){
        return new Shift(f,shiftX,shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY){
        return new Scale(f,scaleX,scaleY);
    }

    public static Function power(Function f, double power){
        return new Power(f,power);
    }

    public static Function sum(Function f1, Function f2){
        return new Sum(f1,f2);
    }

    public static Function mult(Function f1, Function f2){
        return new Mult(f1,f2);
    }

    public static Function composition(Function f1, Function f2){
        return new Composition(f1, f2);
    }

    public static double integral(Function function, double leftX, double rightX, double dx) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException();
        }

        int step = (int) Math.ceil((rightX - leftX) / dx);
        double square = 0;
        double x = leftX;
        for (int i = 0; i < step - 1; ++i, x += dx) {
            square  += (function.getFunctionValue(x) + function.getFunctionValue(x + dx)) * dx/ 2;
        }
        square += (function.getFunctionValue(x) + function.getFunctionValue(rightX)) * dx/ 2;
        return square;
    }

}
