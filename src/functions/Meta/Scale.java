package functions.Meta;

import functions.Function;

public class Scale implements Function {
    private Function fun;
    private double scaleX;
    private double scaleY;

    public Scale(Function fun, double scaleX, double scaleY){
        this.fun = fun;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public double getLeftDomainBorder() {
        return fun.getLeftDomainBorder()*scaleX;
    }

    public double getRightDomainBorder() {
        return fun.getRightDomainBorder()*scaleX;
    }

    public double getFunctionValue(double x) {
        return fun.getFunctionValue(x * scaleX) * scaleY;
    }
}
