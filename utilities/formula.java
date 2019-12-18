package utilities;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class formula{
    private String[] postFix;
    

    public formula(String str){
        makePostfix(str);//note that this can throw some mad exceptions
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

    private double toNum(String str, double x, double y){
        if(str.equals("x")){return x;}
        if(str.equals("y")){return y;}
        return Double.parseDouble(str);
    }

    private boolean isOperand(String str){
        if(isNumber(str)){
            return true;
        }
        return str.equals("x") || str.equals("y");
    }

    public double doEquation(double x, double y){
        Stack<Double> stack = new Stack<Double>();
        double result;
        double a;

        String currStr;
        for(int i=0;i<postFix.length;i++){
            //print(stack.toString());
            currStr = postFix[i];
            if(isDualOperator(currStr)){
                a = stack.pop();
                result = eval(currStr, stack.pop(), a);
                stack.push(result);
            }
            else if(isMonOperator(currStr)){
                result = eval(currStr, stack.pop());
                stack.push(result);
            }
            else if(isOperand(currStr)){
                stack.push(toNum(currStr, x,y));
            }
        }
        //System.out.println("final result is"+stack.peek());
        return stack.pop();
    }

    public double eval(String operator, double a, double b){
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
        print("SOMETHING HAS GONE HORRIBLY WRONG");
        return 1;
    }

    public double eval(String operator, double a){
        if(operator.equals("sin")){return Math.sin(a);}
        if(operator.equals("cos")){return Math.cos(a);}
        print("SOMETHING HAS GONE WRONG WITH SIN OR COS");
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
            if(isNumber(currStr)){
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
                //print("from sincos");
                currOper.add(currChar);
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