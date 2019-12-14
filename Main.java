import javafx.application.Application; 
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.control.*; 
import javafx.scene.layout.*; 
import javafx.event.ActionEvent; 
import javafx.event.EventHandler; 
import javafx.scene.control.Label; 
import javafx.stage.Stage; 
import javafx.collections.*;

import mutabletypes.*;

/*inputs to make
   c value (only have julia set)
   xview
   yview (have under view header)
   darkness
   expansion

   //box to get formula

   button to create fractal
*/

public class Main extends Application {

   MutableDouble darkness = new MutableDouble(40);
   MutableDouble xView= new MutableDouble(2);
   MutableDouble yView= new MutableDouble(2);
   MutableDouble expansion = new MutableDouble(1);
   MutableDouble cVal = new MutableDouble(0.5);
   String formula = ""; //could end up doing other data type


   @Override     
   public void start(Stage primaryStage) throws Exception {            
      Pane root = new Pane();
      initWindow(root);

      Scene scene = new Scene(root ,1200, 600);
      primaryStage.setTitle("Fractals"); 
      primaryStage.setScene(scene); 
      primaryStage.show(); 
   }

   private Button makeButton(String text, int xpos, int ypos, Pane root){
      Button newButt = new Button(text);
      newButt.relocate(xpos, ypos);
      //root.getChildren().add(newButt);
      return newButt;
   }

   private void initWindow(Pane root){
      final int defaultTextSize = 20;
      final int spacing = 30;
      int fromTop = 50;
      final int fromLeft = 20;

      //getIntList(1, 5, 300, 200, root);

      Button submit = makeButton("button", 300, 50, root);
      makeText("c value", defaultTextSize, fromLeft, fromTop, root);
      submit.addEventHandler(ActionEvent.ACTION, makeTextBoxDouble(3, fromLeft+80, fromTop, root, cVal));

      fromTop += spacing;
      makeText("expansion", defaultTextSize, fromLeft, fromTop, root);
      submit.addEventHandler(ActionEvent.ACTION,makeTextBoxDouble(3, fromLeft+100, fromTop, root, expansion));
      
      fromTop += spacing;
      makeText("darkness", defaultTextSize, fromLeft, fromTop, root);
      submit.addEventHandler(ActionEvent.ACTION,makeTextBoxDouble(3, fromLeft+90, fromTop, root, darkness));

      fromTop += spacing;
      makeText("camera position", defaultTextSize, fromLeft, fromTop, root);
         fromTop += spacing;
         makeText("x", defaultTextSize, fromLeft+20, fromTop, root);
         submit.addEventHandler(ActionEvent.ACTION,makeTextBoxDouble(3, fromLeft+40, fromTop, root, xView));

         fromTop += spacing;
         makeText("y", defaultTextSize, fromLeft+20, fromTop, root);
         submit.addEventHandler(ActionEvent.ACTION,makeTextBoxDouble(3, fromLeft+40, fromTop, root, yView));
      
       
      //Creating a Scene by passing the group object, height and width
      root.getChildren().add(submit);
   }

   private EventHandler<ActionEvent> makeTextBoxDouble(int length, int xpos, int ypos, Pane root, MutableDouble v){
      TextField newField = new TextField();
      newField.relocate(xpos, ypos);
      newField.setPrefColumnCount(length);
      root.getChildren().add(newField);

      EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
         public void handle(ActionEvent e) 
         { 
            String alx = newField.getText();
            if(isNumber(alx))
               v.set(Double.parseDouble(alx));
            else
               newField.setText("error");
            errorMsg(alx);
         } 
     };
     return event;
   }
   public void makeText(String text, int size, int xpos, int ypos, Pane root){
      Text newText = new Text(text);
      newText.setFont(new Font(size));
      newText.relocate(xpos, ypos);
      root.getChildren().add(newText);
   }

   private void errorMsg(String er){
      System.out.println(er);
   }

   private boolean isNumber(String str){
      try{
         Double.parseDouble(str);}
      catch(NullPointerException e){
         return false;}
      catch(NumberFormatException e){
         return false;}
      return true;
   }
   /*
   private ObservableList getIntList(int min, int max,int xpos, int ypos, Pane root){
      ObservableList<Integer> options=FXCollections.observableArrayList();
      int i;
      for(i=min; i <= max; i++){
         options.add(i);
      }
      ComboBox dropDown = new ComboBox(options);

      //TilePane tile_pane = new TilePane(combo_box); 
      dropDown.relocate(xpos, ypos);
      root.getChildren().add(dropDown);
      return options;
   }*/


   public static void main(String args[]){          
      launch(args);     
   }         
} 