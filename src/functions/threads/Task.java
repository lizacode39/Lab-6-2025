package functions.threads;

import functions.Function;
import functions.Functions;

public class Task {
    private Function function;
    private double leftX;
    private double rightX;
    private double dx;
    private int taskCount;

    public Task(){}

    public Task(int taskCount){this.taskCount = taskCount;}
    public int getTaskCount() {return taskCount;}

    public Function getFunction() {return function;}
    public double getLeftX() {return leftX;}
    public double getRightX() {return rightX;}
    public double getDx() {return dx;}

    public void setFunction(Function function) {this.function = function;}
    public void setLeftX(double leftX) {this.leftX = leftX;}
    public void setRightX(double rightX) {this.rightX = rightX;}
    public void setDx(double dx) {this.dx = dx;}
}
