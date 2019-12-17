package utilities;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class formula{
    private String[] postFix;
    

    public formula(String str){
        if(!makePostfix(str))
            return;
        
        
    }

    public double doEquation(double x, double y){
        double finalNum=0;
        return finalNum;
    }

    public boolean makePostfix(String str){
        
        List<String> tokenized = tokenize(str);
        Stack<String> stack = new Stack<String>();
        List<String> postBuilder = new ArrayList<String>();

        String currEl;
        while(!tokenized.isEmpty()){
            currEl = tokenized.remove(0);
            if(isNumber(currEl) || currEl=="x"||currEl=="y"){//if is operand
                postBuilder.add(currEl);
                //errorMsg("cmon man");
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
                    System.out.println("is the stack empy? "+stack.isEmpty());
                    while(!stack.isEmpty() && stack.peek() != "(" && prec(currEl) <= prec(stack.peek())){
                        postBuilder.add(stack.pop());
                    }
                    stack.push(currEl);break;
                case ")":
                    while(stack.peek()!="("){
                        postBuilder.add(stack.pop());
                    }
                    stack.pop();break;

            }
            while(!stack.isEmpty()){
                postBuilder.add(stack.pop());
            }

            }
        }
        System.out.println("postbuilder is "+postBuilder.toString());
        return true;//if can be turned into equation, else false
    }

    private int prec(String a){
        switch (a) {
            case "+":
            case "-": 
                return 1; 
            case "*": 
            case "/":
            case "sin"://idk about these
            case "cos":
                return 2; 
            case "^": 
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
                }
                else if(!currOper.isEmpty() && validWave(concat(currOper))){
                    tokenized.add(concat(currOper));
                }
                tokenized.add(currStr);
            }
            else if(fromSinCos(currChar)){
                currOper.add(currChar);
            }
        }
        if(!currNum.isEmpty()){
            tokenized.add(concat(currNum));
        }
        if(!currOper.isEmpty() && validWave(concat(currOper))){
            tokenized.add(concat(currOper));
        }
        System.out.println("tokenized is"+tokenized.toString());
        return tokenized;
    }

    private boolean validWave(String str){
        return str=="cos" || str=="sin";
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

    public static void errorMsg(String er){
        System.out.println(er);
     }

    public static boolean isNumber(String str){
        try{
           Double.parseDouble(str);return true;}
        catch(NumberFormatException e){
           return false;}
     }

}