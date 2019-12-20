package utilities.formulanodes;

import java.util.Stack;
//import java.lang.Math;

public class Node{

    public Node next;
    public Stack<Double> stack;

    public Node(Node next, Stack<Double> stack){
        this.next = next;
        this.stack = stack;
    }

    public void doCalc(double x, double y){}
}