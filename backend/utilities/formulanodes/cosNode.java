package utilities.formulanodes;

import java.util.Stack;
import java.lang.Math;

public class cosNode extends Node{

    private double a;

    /**
     * 0 - static
     * 1 - is x
     * 2 - is y
     * 3 - is pop result
     */
    private int aVal;


    public cosNode(Node next, Stack<Double> stack, double aStart,
     int aVal){
        super(next, stack);
        this.a = aStart;
        this.aVal = aVal;
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

        //System.out.println(a+" * "+b);
        stack.push(Math.cos(a));

        if(next != null)
            next.doCalc(x,y);
    }
}