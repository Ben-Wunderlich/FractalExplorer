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

     public static boolean isInt(String str){
      try{
         Integer.parseInt(str);return true;}
      catch(NumberFormatException e){
         return false;}
   }

      
   private static final String ALPHA_NUMERIC_STRING = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
   
   public static String randFileName(int count) {
      StringBuilder builder = new StringBuilder();
      while (count-- != 0) {
         int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
         builder.append(ALPHA_NUMERIC_STRING.charAt(character));
      }
      return builder.toString()+".png";
   }

}