package functions;

public interface TabulatedFunction extends Function, Cloneable {

    int getPointCount();

    FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException;

    void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException;

    double getPointX(int index) throws FunctionPointIndexOutOfBoundsException;

    void setPointX(int index, double x) throws InappropriateFunctionPointException;

    double getPointY(int index) throws FunctionPointIndexOutOfBoundsException;

    void setPointY(int index, double y);

    void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException;

    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;

}