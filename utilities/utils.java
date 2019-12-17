package utilities;

public class utils{
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