package functions.Meta;

import functions.Function;

public class Composition implements Function {
    private Function funOuter;
    private Function funInner;

    public Composition(Function funOuter, Function funInner){
        this.funOuter = funOuter;
        this.funInner = funInner;
    }

    public double getLeftDomainBorder() {
        return funOuter.getLeftDomainBorder();
    }

    public double getRightDomainBorder() {
        return funOuter.getRightDomainBorder();
    }

    public double getFunctionValue(double x) {
        return funOuter.getFunctionValue(funInner.getFunctionValue(x));
    }
}
