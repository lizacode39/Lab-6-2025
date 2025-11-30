package functions.Meta;

import functions.Function;

public class Power implements Function {
    private Function func;
    private double pow;

    public Power(Function func, double pow){
        this.func = func;
        this.pow = pow;
    }

    public double getLeftDomainBorder() {
        return func.getLeftDomainBorder();
    }

    public double getRightDomainBorder() {
        return func.getRightDomainBorder();
    }

    public double getFunctionValue(double x) {
        return Math.pow(func.getFunctionValue(x), pow);
    }
}
