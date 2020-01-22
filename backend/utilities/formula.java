package utilities;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


import utilities.formulanodes.*;

public class formula{
    private String[] postFix;
    private Node head=null;
    public Stack<Double> actionStack = new Stack<Double>();
    //public boolean noError = true;
    
    /**
     * make linked list with each node a type depending on operation
     * have stack for saved values and also pass along with it x and y
     * 
     */


    public formula(String str){
        //try{
            makePostfix(str);//note that this can throw some mad exceptions
            simulateEquation();
        //}
        //catch(Exception e){
         //   noError = false;
        //}
    }

    private boolean isDualOperator(String str){
        switch(str){
            case "+":
            case "-": 
            case "*": 
            case "/":
            case "^":
                return true;
            default:
                return false;
        }
    }

    private boolean isMonOperator(String str){
        return str.equals("sin") || str.equals("cos");
    }

    private boolean isOperand(String str){
        if(isNumber(str)){
            return true;
        }
        return str.equals("x") || str.equals("y");
    }

    private void newNode(String operator, String a, String b){
        double aStart=0;
        double bStart=0;
        int aVal=0;
        int bVal=0;
        Node nNode = new Node(null, null);

        //System.out.println("recieved "+a+" " + operator+" "+b);

        if(isNumber(a)){aStart=Double.parseDouble(a);}
        else if(a.equals("x")){aVal=1;}
        else if(a.equals("y")){aVal=2;}
        else if(a.equals("a")){aVal=3;}
        else{print("PANIC");}

        if(b==null){}
        else if(isNumber(b)){bStart=Double.parseDouble(b);}
        else if(b.equals("x")){bVal=1;}
        else if(b.equals("y")){bVal=2;}
        else if(b.equals("a")){bVal=3;}
        else{print("PANIC MOre");}

        switch (operator) {
            case "+":
                //System.out.println("adding "+aStart+" "+bStart + " " + aVal + " "+ bVal);
                nNode = new addNode(null, actionStack, aStart, bStart, aVal, bVal);
                break;
            case "-":
                nNode = new subNode(null, actionStack, aStart, bStart, aVal, bVal);
                break;
            case "*":
               // System.out.println("multing "+aStart+" "+bStart);
                nNode = new multNode(null, actionStack, aStart, bStart, aVal, bVal);
                break;
            case "/":
               // System.out.println("diving "+aStart+" "+bStart);
                nNode = new divNode(null, actionStack, aStart, bStart, aVal, bVal);
                break;
            case "^":
                nNode = new powNode(null, actionStack, aStart, bStart, aVal, bVal);
                break;
            case "sin":
                nNode = new sinNode(null, actionStack, aStart, aVal);
                break;
            case "cos":
                nNode = new cosNode(null, actionStack, aStart, aVal);
                break;
            default:
                print("HEYYYYY, FREAK OUT");
                break;
        }
        insertNode(nNode);
    }

    private void insertNode(Node newNode){
        if(head == null){
            head = newNode;   
        }
        else{
            Node currNode = head;
            while(currNode.next != null){
                currNode = currNode.next;
            }
            currNode.next = newNode;
        }
    }


    public double doEquation(double x, double y){
        head.doCalc(x, y);
        //System.out.println(x+" "+y+" "+actionStack.peek());
        return actionStack.pop();
    }


    public void simulateEquation(){
        Stack<String> stack = new Stack<String>();
        String a;
        String start = postFix[0];

        String currStr;
        for(int i=0;i<postFix.length;i++){
            //print(stack.toString());
            currStr = postFix[i];
            if(isDualOperator(currStr)){
                a = stack.pop();
                //print("dual op made");
                //System.out.println(currStr+ stack.peek()+ a);
                //result = eval(currStr, stack.pop(), a);
                newNode(currStr, stack.pop(), a);
                stack.push("a");//is a placeholder
            }
            else if(isMonOperator(currStr)){
                //result = eval(currStr, stack.pop());
                newNode(currStr, stack.pop(), null);
                //print("mono op made");
                stack.push("a");
            }
            else if(isOperand(currStr)){
                //stack.push(toNum(currStr, x,y));
                stack.push(currStr);
            }
        }
        if(stack.pop().equals(start)){
            //print("null stack made");
            newNode("+", start, "0");
        }//for if is just one char
    }

    public double eval(String operator, double a, double b){
        //System.out.println(a+operator+b);
        switch (operator) {
            case "+":
                return a+b;
            case "-": 
                return a-b;
            case "*": 
                return a*b;
            case "/":
                if(b!=0){return a/b;}return a;
            case "^":
                try{return Math.pow(a,b);}
                catch (Exception e) {return a;}
        };
        //print("SOMETHING HAS GONE HORRIBLY WRONG");
        return 1;
    }

    public void makePostfix(String str){
        
        List<String> tokenized = tokenize(str);
        //print("tokenzid is"+tokenized.toString());
        Stack<String> stack = new Stack<String>();
        List<String> postBuilder = new ArrayList<String>();

        String currEl;
        while(!tokenized.isEmpty()){
            currEl = tokenized.remove(0);
            if(isNumber(currEl) || currEl.equals("x")||currEl.equals("y")){//if is operand
                postBuilder.add(currEl);
            }
            else{
            switch(currEl){
                case "(":
                    stack.push(currEl);break;
                case "+":
                case "-":
                case "*":
                case "/":
                case "^":
                case "sin":
                case "cos":
                    //System.out.println("is the stack empy? "+stack.isEmpty());
                    while(!stack.isEmpty() && !stack.peek().equals("(") && prec(currEl) <= prec(stack.peek())){
                        postBuilder.add(stack.pop());
                    }
                    stack.push(currEl);break;
                case ")":
                    //System.out.println(stack.isEmpty());
                    while(!stack.peek().equals("(")){
                        postBuilder.add(stack.pop());
                    }
                    stack.pop();break;
            }
            }
        }
        while(!stack.isEmpty()){
            postBuilder.add(stack.pop());
        }
        //System.out.println("postbuilder is "+postBuilder.toString());
        postFix = postBuilder.toArray(new String[postBuilder.size()]);
    }

    private int prec(String a){
        switch (a) {
            case "+":
            case "-": 
                return 1; 
            case "*": 
            case "/":
                return 2; 
            case "^": 
            case "sin"://idk about these
            case "cos":
                return 3; 
            case "(": 
                return 4; 
            default:
                return -1;
        }
    }

    private List<String> tokenize(String str){
        List<String> tokenized=new ArrayList<String>();
        List<Character> currNum=new ArrayList<Character>();
        List<Character> currOper=new ArrayList<Character>();

        char currChar;
        for(int i=0;i<str.length();i++){
            currChar = str.charAt(i);
            String currStr = String.valueOf(currChar);
            if(isNumber(currStr) || currStr.equals(".")){
                currNum.add(currChar);}
            else if(isLoneSymbol(currChar)){
                if(!currNum.isEmpty()){
                    tokenized.add(concat(currNum));
                    currNum.clear();
                }
                else if(!currOper.isEmpty() && validWave(concat(currOper))){
                    tokenized.add(concat(currOper));
                    currOper.clear();
                }
                tokenized.add(currStr);
            }
            else if(fromSinCos(currChar)){
                ////print("from sincos");
                currOper.add(currChar);
            }
            else{
                throw new NumberFormatException("Invalid symbol");
            }
        }
        if(!currNum.isEmpty()){
            tokenized.add(concat(currNum));
        }
        if(!currOper.isEmpty() && validWave(concat(currOper))){
            tokenized.add(concat(currOper));
        }
        return tokenized;
    }

    private boolean validWave(String str){
        return str.equals("cos") || str.equals("sin");
    }

    private boolean fromSinCos(char e){
        switch(e){
            case 's':
            case 'i':
            case 'n':
            case 'c':
            case 'o':
                return true;
        default:
            return false;
        }
    }

    private String concat(List<Character> jeff){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<jeff.size();i++){
            sb.append(jeff.get(i));
        }
        return sb.toString();
    }

    private boolean isLoneSymbol(char e){
        switch(e){
            case '(':
            case ')':
            case '+':
            case '-':
            case '*':
            case '/':
            case '^':
            case 'x':
            case 'y':
                return true;
            default:
                return false;

        }
    }

    public boolean isNumber(String str){
        try{
           Double.parseDouble(str);return true;}
        catch(NumberFormatException e){
           return false;}
     }

    private void print(String e){
        System.out.println(e);
    }

}