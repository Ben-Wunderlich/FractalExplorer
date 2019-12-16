import javafx.application.Application; 
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*; 
import javafx.event.ActionEvent; 
import javafx.event.EventHandler; 
import javafx.stage.Stage; 
//import javafx.collections.*;
//import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
//import java.lang.Object;
import java.io.File;
import java.awt.image.RenderedImage;
//import javax.imageio.ImageIO;

import mutabletypes.*;
import utilities.julia;

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
   MutableDouble xView= new MutableDouble(1.2);
   MutableDouble yView= new MutableDouble(1.2);
   MutableDouble expansion = new MutableDouble(1);
   MutableDouble cVal = new MutableDouble(0.4);
   MutableString xFormula = new MutableString("x");
   MutableString yFormula = new MutableString("y");
   final int imgWidth = 400;
   julia currentJulia;
   //String formula = ""; //could end up doing other data type


   @Override     
   public void start(Stage primaryStage) throws Exception {            
      Pane root = new Pane();
      initWindow(root);

      //julia fractal = new julia(12);
      //setImage(fractal.getImage(), root, 400, 200);
      

      Scene scene = new Scene(root ,1000, 600);

      scene.setOnKeyPressed(e -> {
         if (e.getCode() == KeyCode.ENTER) {
            makeFractal(root);
         }
      });

      primaryStage.setTitle("Fractals"); 
      primaryStage.setScene(scene); 
      primaryStage.show();

   }

   private void setImage(WritableImage image, Pane root, int xpos, int ypos){
      ImageView viewer = new ImageView(image);
      viewer.relocate(xpos, ypos);
      root.getChildren().add(viewer);
   }

   private void saveImage(){

      try {
         WritableImage image = currentJulia.getImage();
         File file = new File("test.png");
         RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
         ImageIO.write(renderedImage,"png",file);
      } catch (Exception e) {
        errorMsg("THINGS HAVE GONE WRONG");
      }


   }

   private void makeFractal(Pane root){
      currentJulia = new julia(imgWidth, cVal.get(), expansion.get(), darkness.get(), xView.get(), yView.get());
      setImage(currentJulia.getImage(), root, 400, 50);
   }

   private Button makeFractalButton(String text, int xpos, int ypos, Pane root){
      Button newButt = new Button(text);
      newButt.relocate(xpos, ypos);
      EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
         public void handle(ActionEvent e) 
         { 
            makeFractal(root);
         } 
      };
      newButt.addEventHandler(ActionEvent.ACTION,event);
      return newButt;
   }

   private void makeSaveButton(String text, int xpos, int ypos, Pane root){
      Button newButt = new Button(text);
      newButt.relocate(xpos, ypos);
      EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
         public void handle(ActionEvent e) 
         {
            saveImage();
         }
      };
      newButt.addEventHandler(ActionEvent.ACTION,event);
      root.getChildren().add(newButt);
   }

   private void initWindow(Pane root){
      final int defaultTextSize = 20;
      final int spacing = 30;
      int fromTop = 50;
      final int fromLeft = 20;
      final int fromLeftInset = fromLeft+20;

      //getIntList(1, 5, 300, 200, root);

      Button submit = makeFractalButton("create", fromLeft, 400, root);
      makeText("c value", defaultTextSize, fromLeft, fromTop, root);
      submit.addEventHandler(ActionEvent.ACTION, makeTextBoxDouble(3, fromLeft+80, fromTop, root, cVal, "0.4"));

      fromTop += spacing;
      makeText("expansion", defaultTextSize, fromLeft, fromTop, root);
      submit.addEventHandler(ActionEvent.ACTION,makeTextBoxDouble(3, fromLeft+100, fromTop, root, expansion, "1"));
      
      fromTop += spacing;
      makeText("darkness", defaultTextSize, fromLeft, fromTop, root);
      submit.addEventHandler(ActionEvent.ACTION,makeTextBoxDouble(3, fromLeft+90, fromTop, root, darkness, "40"));

      fromTop += spacing;
      makeText("camera position", defaultTextSize, fromLeft, fromTop, root);
         fromTop += spacing;
         makeText("x", defaultTextSize, fromLeftInset, fromTop, root);
         submit.addEventHandler(ActionEvent.ACTION,makeTextBoxDouble(3, fromLeft+40, fromTop, root, xView, "1.2"));

         fromTop += spacing;
         makeText("y", defaultTextSize, fromLeftInset, fromTop, root);
         submit.addEventHandler(ActionEvent.ACTION,makeTextBoxDouble(3, fromLeft+40, fromTop, root, yView, "1.2"));
      
      fromTop += spacing + 20;
      makeText("custom modifications on x and y", defaultTextSize, fromLeft, fromTop, root);
         fromTop += spacing;
         makeText("x=", defaultTextSize, fromLeftInset, fromTop, root);
         //submit.addEventHandler(ActionEvent.ACTION,makeTextBoxDouble(3, fromLeft+40, fromTop, root, yView, "x"));//XXX

         fromTop += spacing;
         makeText("y=", defaultTextSize, fromLeftInset, fromTop, root);
      //maybe add option for colours
      makeSaveButton("save", fromLeft+60, 400, root);
       
      root.getChildren().add(submit);
   }

   private EventHandler<ActionEvent> makeTextBoxDouble(int length, int xpos, int ypos, Pane root, MutableDouble v, String init){
      TextField newField = new TextField(init);
      newField.relocate(xpos, ypos);
      newField.setPrefColumnCount(length);
      root.getChildren().add(newField);

      EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
         public void handle(ActionEvent e) 
         { 
            String textCont = newField.getText();
            if(isNumber(textCont)){
               v.set(Double.parseDouble(textCont));
               //errorMsg("number was"+textCont);
            }
            else{
               newField.setText("error");
               errorMsg("non number was"+textCont);
            }
         } 
     };
     //when enter pressed

     return event;
   }

   /*private EventHandler<ActionEvent> makeTextBoxString(int length, int xpos, int ypos, Pane root, MutableDouble v, String init){
      TextField newField = new TextField(init);
      newField.relocate(xpos, ypos);
      newField.setPrefColumnCount(length);
      root.getChildren().add(newField);

      EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
         public void handle(ActionEvent e) 
         { 
            String textCont = newField.getText();
            if(isNumber(textCont)){
               v.set(Double.parseDouble(textCont));
               //errorMsg("number was"+textCont);
            }
            else{
               newField.setText("error");
               errorMsg("non number was"+textCont);
            }
         } 
     };
     //when enter pressed

     return event;
   }*/

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
         Double.parseDouble(str);return true;}
      catch(NumberFormatException e){
         return false;}
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