package functions;

import java.io.ObjectInput;
import java.io.Serializable;

public class FunctionPoint implements Serializable {
    private double x;
    private double y;

    public FunctionPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

    public FunctionPoint(FunctionPoint point){
        this.x = point.x;
        this.y = point.y;
    }

    public FunctionPoint(){
        x = 0;
        y = 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String toString(){
        return "(" + String.valueOf(x) + "; " + String.valueOf(y) + ")";
    }

    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionPoint point = (FunctionPoint) o;
        return ((getX() == point.getX()) && (getY() == point.getY()));
    }

    public int hashCode() {
        long hashX = Double.doubleToLongBits(getX());
        long hashY = Double.doubleToLongBits(getY());
        int hash = 31;
        hash = hash*31 + (int)(hashX ^ (hashX>>>32));
        hash = hash*31 + (int)(hashY ^ (hashY>>>32));
        return hash;
    }

    public Object clone() {
        return new FunctionPoint(this);
    }
}

