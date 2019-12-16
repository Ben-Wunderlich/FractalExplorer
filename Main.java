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
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.paint.Color;

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

   final int DARK = 0;
   final int XVIEW = 1;
   final int YVIEW = 2;
   final int EXPAN = 3;
   final int CVAL = 4;
   final int XFORM = 5;
   final int YFORM = 6;

   TextField[] inpFields = new TextField[7];
   FadeTransition fracDone;
   /**
    * 0 = darkness
    * 1 = xView
    * 2 = yView
    * 3 = expansion
    * 4 = cVal
    * 5 = xFormula
    * 6 = yFormula
    */

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

      /**
    * 0 = darkness
    * 1 = xView
    * 2 = yView
    * 3 = expansion
    * 4 = cVal
    * 5 = xFormula
    * 6 = yFormula
    */
   private void makeFractal(Pane root){
      double[] fVals = getFields();
      currentJulia = new julia(imgWidth, fVals[CVAL], fVals[EXPAN], fVals[DARK], fVals[XVIEW], fVals[YVIEW]);
      setImage(currentJulia.getImage(), root, 400, 100);
      fracDone.play();
   }

   private double[] getFields(){
      double[] doubleVals = new double[7];
      for(int i = 0; i < 5; i++){
         String jxl = inpFields[i].getText();
         if(isNumber(jxl)){
            doubleVals[i] = Double.parseDouble(jxl);
         }
      }
      return doubleVals;
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

      Button submit = makeFractalButton("create", fromLeft, 400, root);
      makeText("c value", defaultTextSize, fromLeft, fromTop, root);
      makeTextBox(3, fromLeft+80, fromTop, root, "0.4", CVAL);

      fromTop += spacing;
      makeText("expansion", defaultTextSize, fromLeft, fromTop, root);
      makeTextBox(3, fromLeft+100, fromTop, root, "1", EXPAN);
      
      fromTop += spacing;
      makeText("darkness", defaultTextSize, fromLeft, fromTop, root);
      makeTextBox(3, fromLeft+90, fromTop, root, "40", DARK);

      fromTop += spacing;
      makeText("camera position", defaultTextSize, fromLeft, fromTop, root);
         fromTop += spacing;
         makeText("x", defaultTextSize, fromLeftInset, fromTop, root);

         makeTextBox(3, fromLeft+40, fromTop, root, "1.2", XVIEW);//xview

         fromTop += spacing;
         makeText("y", defaultTextSize, fromLeftInset, fromTop, root);
         makeTextBox(3, fromLeft+40, fromTop, root, "1.2", YVIEW);
      
      fromTop += spacing + 20;
      makeText("custom modifications on x and y", defaultTextSize, fromLeft, fromTop, root);
         fromTop += spacing;
         makeText("x=", defaultTextSize, fromLeftInset, fromTop, root);
         makeTextBox(3, fromLeft+40, fromTop, root, "x", XFORM);

         fromTop += spacing;
         makeText("y=", defaultTextSize, fromLeftInset, fromTop, root);
         makeTextBox(3, fromLeft+40, fromTop, root, "y", YFORM);
      //maybe add option for colours
      makeSaveButton("save", fromLeft+60, 400, root);

      Text finishedNotifier = makeText("fractal complete", 35, 400, 20, root);
      finishedNotifier.setOpacity(0);
      finishedNotifier.setFill(Color.RED); 
      fracDone = new FadeTransition(Duration.millis(1000), finishedNotifier);
      fracDone.setFromValue(1.0);
      fracDone.setToValue(0);
       
      root.getChildren().add(submit);
   }

   private void makeTextBox(int length, int xpos, int ypos, Pane root, String init, int index){
      TextField newField = new TextField(init);
      newField.relocate(xpos, ypos);
      newField.setPrefColumnCount(length);
      root.getChildren().add(newField);
      inpFields[index] = newField;
   }

   public Text makeText(String text, int size, int xpos, int ypos, Pane root){
      Text newText = new Text(text);
      newText.setFont(new Font(size));
      newText.relocate(xpos, ypos);
      root.getChildren().add(newText);
      return newText;
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