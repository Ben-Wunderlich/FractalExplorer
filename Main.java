import javafx.application.Application; 
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.image.WritableImage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane; 
import javafx.event.ActionEvent; 
import javafx.event.EventHandler; 
import javafx.stage.Stage; 
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import java.io.File;
import java.io.IOException;
import java.awt.image.RenderedImage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import utilities.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.Thread;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;


public class Main extends Application {

   final int DARK = 0;
   final int XMIN = 1;
   final int XMAX = 2;
   final int YMIN = 3;
   final int YMAX = 4;
   final int EXPAN = 5;
   final int CVAL = 6;
   final int WIDTH = 7;
   final int XFORM = 8;
   final int YFORM = 9;

   TextField[] inpFields = new TextField[10];

   ComboBox<Integer> COLOUR;
   FadeTransition fracDone;
   Text loadingText;
   Text errorText;
   /**
    * 0 = darkness
    * 1 = xView
    * 2 = yView
    * 3 = expansion
    * 4 = cVal
    * 5 = width
    * 6 = xFormula
    * 7 = yFormula
    */

   int imgWidth = 400;
   julia currentJulia;
   ImageView lastImage = null;

   private void setImage(WritableImage image, Pane root, int xpos, int ypos){
      if(lastImage != null){root.getChildren().remove(lastImage);}
      ImageView viewer = new ImageView(image);
      viewer.relocate(xpos, ypos);
      root.getChildren().add(viewer);
      lastImage = viewer;
   }

   private void saveImage(Stage primaryStage){

      try {
         FileChooser fileChooser = new FileChooser();
         fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png"));
         String path = getStorageDirec();
         fileChooser.setInitialDirectory(new File(path));

         fileChooser.setInitialFileName(utils.randFileName(20));

         File newFile = fileChooser.showSaveDialog(primaryStage);
         if(newFile == null){
            return;}
         //utils.errorMsg(fileName);
      
         WritableImage image = currentJulia.getImage();
         File file = new File(newFile.getAbsolutePath());
         RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
         ImageIO.write(renderedImage,"png",file);
      } catch (IOException e) {
        utils.errorMsg("THINGS HAVE GONE WRONG");
      }
   }

   private String getStorageDirec(){
      Path currentRelativePath = Paths.get("");
      String s = currentRelativePath.toAbsolutePath().toString();
      return s + "\\images";
   }

   private void showError(){
      errorText.setOpacity(1);
   }

   private void makeFractal(Pane root){
      new Thread(() -> {
         Platform.runLater(()-> errorText.setOpacity(0));
         Platform.runLater(()-> loadingText.setOpacity(1));
         Platform.runLater(()-> setColour());
     
         boolean wasError = false;
        try{
         double[] fVals = getFields();
         currentJulia = new julia((int)fVals[WIDTH], fVals[CVAL], fVals[EXPAN],
         fVals[DARK],fVals[XMIN], fVals[XMAX], fVals[YMIN], fVals[YMAX],
         inpFields[XFORM].getText(), inpFields[YFORM].getText());
         }
         catch(Exception e){
            wasError = true;
         }

         Platform.runLater(()-> loadingText.setOpacity(0));
         if(!wasError){
            Platform.runLater(()-> setImage(currentJulia.getImage(), root, 400, 100));
            Platform.runLater(()-> fracDone.play());
         }
         else{
            Platform.runLater(()-> showError());
         }
     }).start();

     
   }

   private void setColour(){
      Integer val = COLOUR.getValue();
      if(val==null){julia.colour = 1;}
      else{julia.colour = val;}
   }

   private double[] getFields(){
      double[] doubleVals = new double[8];
      for(int i = 0; i < 8; i++){
         String jxl = inpFields[i].getText();
            doubleVals[i] = Double.parseDouble(jxl);
      }
      if(doubleVals[XMIN] >= doubleVals[XMAX] || doubleVals[YMIN] >= doubleVals[YMAX]){
         throw new NumberFormatException();
      }

      if(utils.isInt(inpFields[WIDTH].getText()))
         doubleVals[WIDTH] = Double.parseDouble(inpFields[WIDTH].getText());
      return doubleVals;
   }

   /**
    * what values
    * 0 = up
    * 1 = right
    * 2 = down
    * 3 = left
    */
   private void moveCamera(int direction){
      final double moveFactor = 0.3;

      double xMin;
      double xMax;
      double yMin;
      double yMax;

      try{
         double[] allVals = getFields();
         xMin = allVals[XMIN];
         xMax = allVals[XMAX];
         yMin = allVals[YMIN];
         yMax = allVals[YMAX];
      }
      catch(Exception e){
         showError();return;
      }
      double shiftDist;
      if(direction%2==0){
         shiftDist = (yMax-yMin)*moveFactor;
         if(direction == 0){//up
            inpFields[YMIN].setText(String.valueOf(yMin-shiftDist));
            inpFields[YMAX].setText(String.valueOf(yMax-shiftDist));
         }
         else{//down
            inpFields[YMIN].setText(String.valueOf(yMin+shiftDist));
            inpFields[YMAX].setText(String.valueOf(yMax+shiftDist));
         }
      }
      else{
         shiftDist = (xMax-xMin)*moveFactor;
         if(direction == 1){//right
            inpFields[XMIN].setText(String.valueOf(xMin+shiftDist));
            inpFields[XMAX].setText(String.valueOf(xMax+shiftDist));
         }
         else{//left
            inpFields[XMIN].setText(String.valueOf(xMin-shiftDist));
            inpFields[XMAX].setText(String.valueOf(xMax-shiftDist));
         }
      }
   }

   /**
    * 0 = zoom in
    * 1 = zoom out
    */
   private void zoom(int direction){
      final double zoomFactor = 0.3;
      double zoomAmount;

      double xMin;
      double xMax;
      double yMin;
      double yMax;

      try{
         double[] allVals = getFields();
         xMin = allVals[XMIN];
         xMax = allVals[XMAX];
         yMin = allVals[YMIN];
         yMax = allVals[YMAX];
      }
      catch(Exception e){
         showError();return;
      }

      zoomAmount = (((xMax-xMin) + (yMax-yMin))/2)*zoomFactor;

      if(direction == 0){//in
         inpFields[XMIN].setText(String.valueOf(xMin+zoomAmount));
         inpFields[XMAX].setText(String.valueOf(xMax-zoomAmount));
         inpFields[YMIN].setText(String.valueOf(yMin+zoomAmount));
         inpFields[YMAX].setText(String.valueOf(yMax-zoomAmount));
      }
      else{//out
         inpFields[XMIN].setText(String.valueOf(xMin-zoomAmount));
         inpFields[XMAX].setText(String.valueOf(xMax+zoomAmount));
         inpFields[YMIN].setText(String.valueOf(yMin-zoomAmount));
         inpFields[YMAX].setText(String.valueOf(yMax+zoomAmount));
      }
   }

   private void goHome(Pane root){
      inpFields[XMIN].setText("-2");
      inpFields[XMAX].setText("2");
      inpFields[YMIN].setText("-2");
      inpFields[YMAX].setText("2");
      makeFractal(root);
   }

   /**
    * what values
    * 0 = up
    * 1 = right
    * 2 = down
    * 3 = left
    * 4 = zoom in
    * 5 = zoom out
    */
   private void makeZoomButton(String text, int xpos, int ypos, Pane root, int what){
      Button newButt = new Button(text);
      newButt.relocate(xpos, ypos);
      double buttSize = 45;
      if(what <=3){
         newButt.setMinSize(buttSize, buttSize);
      }

      EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
         public void handle(ActionEvent e) 
         {
            if(what<=3){moveCamera(what);}
            else{zoom(what-4);}
            makeFractal(root);
         } 
      };
      newButt.addEventHandler(ActionEvent.ACTION,event);
      root.getChildren().add(newButt);
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

   private void goHomeButton(String text, int xpos, int ypos, Pane root){
      Button newButt = new Button(text);
      newButt.relocate(xpos, ypos);
      EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
         public void handle(ActionEvent e) 
         {
            goHome(root);
         } 
      };
      newButt.addEventHandler(ActionEvent.ACTION,event);
      root.getChildren().add(newButt);
   }

   private void makeSaveButton(String text, int xpos, int ypos, Pane root, Stage pStage){
      Button newButt = new Button(text);
      newButt.relocate(xpos, ypos);
      EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
         public void handle(ActionEvent e) 
         {
            saveImage(pStage);
         }
      };
      newButt.addEventHandler(ActionEvent.ACTION,event);
      root.getChildren().add(newButt);
   }

   private void initWindow(Pane root, Stage pStage){
      final int defaultTextSize = 20;
      final int spacing = 30;
      int fromTop = 50;
      final int fromLeft = 20;
      final int fromLeftInset = fromLeft+20;

      Button submit = makeFractalButton("create", 260, 250, root);
      submit.setTooltip(new Tooltip("or press enter"));

      makeText("c value", defaultTextSize, fromLeft, fromTop, root);
      makeTextBox(3, fromLeft+80, fromTop, root, "0.4", CVAL);

      fromTop += spacing;
      makeText("expansion", defaultTextSize, fromLeft, fromTop, root);
      makeTextBox(3, fromLeft+100, fromTop, root, "1", EXPAN);
      
      fromTop += spacing;
      makeText("darkness", defaultTextSize, fromLeft, fromTop, root);
      makeTextBox(3, fromLeft+90, fromTop, root, "40", DARK);

      fromTop += spacing;
      makeText("colour preset", defaultTextSize, fromLeft, fromTop, root);
      COLOUR = makeIntList(1, 8, fromLeft+130, fromTop, root);

      fromTop += spacing;
      makeText("camera position", defaultTextSize, fromLeft, fromTop, root);
         fromTop += spacing;
         makeText("x", defaultTextSize-1, fromLeftInset, fromTop, root);

            fromTop += spacing;
            makeText("from", defaultTextSize-2, fromLeftInset+10, fromTop, root);
            makeTextBox(3, fromLeftInset+60, fromTop, root, "-2", XMIN);
            makeText("to", defaultTextSize-2, fromLeftInset+115, fromTop, root);
            makeTextBox(3, fromLeftInset+145, fromTop, root, "2", XMAX);
      

         fromTop += spacing;
         makeText("y", defaultTextSize-1, fromLeftInset, fromTop, root);

            fromTop += spacing;
            makeText("from", defaultTextSize-2, fromLeftInset+10, fromTop, root);
            makeTextBox(3, fromLeftInset+60, fromTop, root, "-2", YMIN);
            makeText("to", defaultTextSize-2, fromLeftInset+115, fromTop, root);
            makeTextBox(3, fromLeftInset+145, fromTop, root, "2", YMAX);
      
      fromTop += spacing+10;
      makeText("image width", defaultTextSize, fromLeft, fromTop, root);
      makeTextBox(3, fromLeft+120, fromTop, root, "400", WIDTH);


      fromTop += spacing + 20;
      makeText("custom modifications on x and y", defaultTextSize, fromLeft, fromTop, root);
         fromTop += spacing;
         makeText("x=", defaultTextSize, fromLeftInset, fromTop, root);
         makeTextBox(3, fromLeft+40, fromTop, root, "x", XFORM);

         fromTop += spacing;
         makeText("y=", defaultTextSize, fromLeftInset, fromTop, root);
         makeTextBox(3, fromLeft+40, fromTop, root, "y", YFORM);

      fromTop += spacing+20;
      String txt = "Valid symbols in custom functions are:\nx, y, (, ), +, -, *, /, ^, cos, sin";
      makeText(txt, defaultTextSize-4, fromLeft, fromTop, root);

      makeSaveButton("save", 320, 250, root, pStage);

      //field of view buttons
      makeZoomButton("up", 295, 25, root, 0);
      makeZoomButton("right", 340, 70, root, 1);
      makeZoomButton("down", 295, 115, root, 2);
      makeZoomButton("left", 250, 70, root, 3);

      makeZoomButton("zoom in ", 250, 170, root, 4);
      makeZoomButton("zoom out", 320, 170, root, 5);

      goHomeButton("reset", 290, 200, root);

      loadingText = makeText("loading...", 35, 400, 20, root);
      loadingText.setOpacity(0);
      loadingText.setFill(Color.LIMEGREEN);

      errorText = makeText("oops, there was an error", 35, 400, 20, root);
      errorText.setOpacity(0);
      errorText.setFill(Color.RED);


      Text finishedNotifier = makeText("fractal complete", 35, 400, 20, root);
      finishedNotifier.setOpacity(0);
      finishedNotifier.setFill(Color.BLUE); 
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

   
   private ComboBox<Integer> makeIntList(int min, int max,int xpos, int ypos, Pane root){
      ObservableList<Integer> options=FXCollections.observableArrayList();
      int i;
      for(i=min; i <= max; i++){
         options.add(i);
      }
      ComboBox<Integer> dropDown = new ComboBox<Integer>(options);

      //TilePane tile_pane = new TilePane(combo_box); 
      dropDown.relocate(xpos, ypos);
      root.getChildren().add(dropDown);
      return dropDown;
   }

   @Override     
   public void start(Stage primaryStage) throws Exception {            
      Pane root = new Pane();
      initWindow(root, primaryStage);

      ScrollPane sp = new ScrollPane();
      sp.setContent(root);
      Scene scene = new Scene(sp ,1000, 600);

      scene.setOnKeyPressed(e -> {
         if (e.getCode() == KeyCode.ENTER) {
            makeFractal(root);
         }
      });

      primaryStage.setTitle("Fractals");
      primaryStage.getIcons().add(new Image("file:utilities\\icon.png"));
      primaryStage.setScene(scene); 
      primaryStage.show();

   }


   public static void main(String args[]){          
      launch(args);     
   }         
} 