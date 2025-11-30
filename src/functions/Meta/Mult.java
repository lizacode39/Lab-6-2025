package functions.Meta;

import functions.Function;

public class Mult implements Function {
    private Function funOne;
    private Function funTwo;

    public Mult(Function funOne, Function funTwo){
        this.funOne = funOne;
        this.funTwo = funTwo;
    }


    public double getLeftDomainBorder() {
        return Math.min(funOne.getLeftDomainBorder(), funTwo.getLeftDomainBorder());
    }

    public double getRightDomainBorder() {
        return Math.min(funOne.getRightDomainBorder(), funTwo.getRightDomainBorder());
    }

    public double getFunctionValue(double x) {
        return funOne.getFunctionValue(x) * funTwo.getFunctionValue(x);
    }
}
