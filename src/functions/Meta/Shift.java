package functions.Meta;

import functions.Function;

public class Shift implements Function {
    private Function fun;
    private double shiftX;
    private double shiftY;

    public Shift(Function fun, double shiftX, double shiftY){
        this.fun = fun;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    public double getLeftDomainBorder() {
        return fun.getLeftDomainBorder() + shiftX;
    }

    public double getRightDomainBorder() {
        return fun.getRightDomainBorder() + shiftX;
    }

    public double getFunctionValue(double x) {
        return fun.getFunctionValue(x + shiftX) + shiftY;
    }
}
