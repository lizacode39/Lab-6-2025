package functions.basic;

import functions.Function;

public class Log implements Function {

    private double base;

    public Log(double base) {
        if (base <= 0 || base == 1.0) {
            throw new IllegalArgumentException("Основание логарифма должно быть > 0 и ≠ 1");
        }
        this.base = base;
    }

    public double getLeftDomainBorder() {
        return 0.0;
    }

    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    public double getFunctionValue(double x) {
        if (x <= 0) {
            return Double.NaN;
        }
        return Math.log(x) / Math.log(base);
    }
}