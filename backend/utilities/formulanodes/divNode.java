package utilities.formulanodes;

import java.util.Stack;

public class divNode extends Node{

    private double a;
    private double b;

    /**
     * 0 - static
     * 1 - is x
     * 2 - is y
     * 3 - is pop result
     */
    private int aVal;
    private int bVal;


    public divNode(Node next, Stack<Double> stack, double aStart, double bStart,
     int aVal, int bVal){
        super(next, stack);
        this.a = aStart;
        this.b = bStart;
        this.aVal = aVal;
        this.bVal = bVal;
    }

    @Override
    public void doCalc(double x, double y){

        switch(aVal){
            case 0:
                break;
            case 1:
                a=x;break;
            case 2:
                a=y;break;
            default:
                a=stack.pop();
        }

        switch(bVal){
            case 0:
                break;
            case 1:
                b=x;break;
            case 2:
                b=y;break;
            default:
                b=stack.pop();
        }

        //System.out.println(a+" / "+b);
        if(b != 0){
            stack.push(a/b);}
        else{
            stack.push(a);
        }

        if(next != null)
            next.doCalc(x,y);
    }
}