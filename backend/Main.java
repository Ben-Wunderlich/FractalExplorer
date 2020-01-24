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
import javafx.scene.control.Hyperlink;
import java.awt.Desktop;
import java.net.URISyntaxException;
import java.net.URI;
import javafx.scene.Node;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;
import java.io.PrintWriter;

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
   final int HEIGHT = 8;
   final int XFORM = 9;//these 2 always need to be last
   final int YFORM = 10;

   final String saveFilePath = "storage\\save.pathstorage";
   final String saveInfoPath = "storage\\imageInfo.txt";
   TextField[] inpFields = new TextField[YFORM+1];

   ComboBox<Integer> COLOUR;
   FadeTransition fracDone;
   Text loadingText;
   Text errorText;

   boolean busyLoading = false;

   julia currentJulia;
   ImageView lastImage = null;

   private void setImage(WritableImage image, Pane root, int xpos, int ypos){
      if(lastImage != null){root.getChildren().remove(lastImage);}
      ImageView viewer = new ImageView(image);
      viewer.relocate(xpos, ypos);
      root.getChildren().add(viewer);
      lastImage = viewer;
   }

   private boolean ensureFileExists(String filePath){
      File f = new File(filePath);
      if(!f.exists()){
         try{
            f.createNewFile();
         }
         catch(IOException e){
            return false;
         }
      }
      return true;
   }

   private void savePath(File fullFile){
      String fullPath = fullFile.getParentFile().getAbsolutePath();
      if(!ensureFileExists(saveFilePath)){
         showError("the exe file must be in the original folder");
      }
      try{
         PrintWriter pw = new PrintWriter(saveFilePath);//clears the file
         pw.close();
         Files.write(Paths.get(saveFilePath), fullPath.getBytes(), StandardOpenOption.WRITE);
      }
      catch(IOException e){
         //System.out.println("path not saved"+e);
         return;
      }
      //System.out.println(fullPath);
   }

   private String makeFileInfo(String fileName){
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");  
      LocalDateTime now = LocalDateTime.now(); 
      final String nuTab = "    ";
      String giantString = "\n\nFilename: "+fileName+"\n"+
      nuTab+"made on: "+dtf.format(now)+"\n"+
      nuTab+"c value: "+inpFields[CVAL].getText()+"\n"+
      nuTab+"expansion: "+inpFields[EXPAN].getText()+"\n"+
      nuTab+"darkness: "+inpFields[DARK].getText()+"\n"+
      nuTab+"x from: "+inpFields[XMIN].getText()+" to "+inpFields[XMAX].getText()+"\n"+
      nuTab+"y from: "+inpFields[YMIN].getText()+" to "+inpFields[YMAX].getText()+"\n"+
      nuTab+"x formula: "+inpFields[XFORM].getText()+"\n"+
      nuTab+"y formula: "+inpFields[YFORM].getText()+"\n";
      return giantString;
   }

   private void saveImageData(File fullFile){
      if(!ensureFileExists(saveInfoPath)){
         showError("the exe file must be in the original folder");
         utils.errorMsg("hey");
      }
      try {
         String appendThis = makeFileInfo(fullFile.getName());
         Files.write(Paths.get(saveInfoPath), appendThis.getBytes(), StandardOpenOption.APPEND);
     }catch (IOException e) {
         showError("the exe file must be in the original folder");
     }

   }

   private boolean imageToSave(){
      return lastImage != null;
   }

   private void saveImage(Stage primaryStage){

      try {
         if(!imageToSave()){
            showError("Image must be made before saved");
            return;
         }
         FileChooser fileChooser = new FileChooser();
         fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png"));
         String path = getStorageDirec();
         fileChooser.setInitialDirectory(new File(path));

         fileChooser.setInitialFileName(utils.randFileName(10));

         File newFile = fileChooser.showSaveDialog(primaryStage);
         if(newFile == null){
            return;}
         //utils.errorMsg(fileName);
         //if gotten to here there is a path selected
         savePath(newFile);
         saveImageData(newFile);
      
         WritableImage image = currentJulia.getImage();
         File file = new File(newFile.getAbsolutePath());
         RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
         ImageIO.write(renderedImage,"png",file);
      } catch (IOException e) {
         showError("Something went wrong when trying to make the file, try restarting");
      }
   }


   private String getSavedPath(){
      //look at saveFilePath
      String path;
      try {
         File myObj = new File(saveFilePath);
         Scanner myReader = new Scanner(myObj);
         path = myReader.nextLine();         
         myReader.close();
         if(new File(path).isDirectory()){
            return path;
         }
         else{
            return null;
         }
       } catch (Exception e) {
         return null;
       }
   }

   private String getStorageDirec(){
      String savedPath = getSavedPath();
      if(savedPath == null){
         Path currentRelativePath = Paths.get("");
         String s = currentRelativePath.toAbsolutePath().getParent().toString()+ "\\images";
         return s;
      }
      else{
         return savedPath;
      }
   }

   private void showError(String errorMsg){
      errorText.setOpacity(1);
      errorText.setText(errorMsg);
   }

   private void clearError(){
      errorText.setOpacity(0);
   }

   private void makeFractal(Pane root){
      if(busyLoading){
         return;
      }

      new Thread(() -> {
         Platform.runLater(()-> clearError());
         Platform.runLater(()-> loadingText.setOpacity(1));
         Platform.runLater(()-> setColour());
         busyLoading = true;
        try{
         double[] fVals = getFields();

         int imgWidth = (int)fVals[WIDTH];
         int imgHeight = (int)fVals[HEIGHT];
         double ratio;
         double addend;
         double middle;
         if(imgWidth > imgHeight){//hotdog
            ratio = fVals[WIDTH] / fVals[HEIGHT];
            addend = (fVals[YMAX] - fVals[YMIN])*ratio/2;
            middle = (fVals[XMIN] + fVals[XMAX])/2;

            fVals[XMIN] = middle - addend;
            fVals[XMAX] = middle + addend;
         }
         else{//hamburger
            ratio = fVals[HEIGHT]/fVals[WIDTH];
            addend = (fVals[XMAX] - fVals[XMIN])*ratio/2;
            middle = (fVals[YMIN] + fVals[YMAX])/2;

            fVals[YMIN] = middle - addend;
            fVals[YMAX] = middle + addend;
         }

         currentJulia = new julia(imgWidth, imgHeight, fVals[CVAL], fVals[EXPAN],
         fVals[DARK],fVals[XMIN], fVals[XMAX], fVals[YMIN], fVals[YMAX],
         inpFields[XFORM].getText(), inpFields[YFORM].getText());
         }
         catch(Exception e){
            String message = e.getMessage();
            //utils.errorMsg("the message was "+message);
            Platform.runLater(()-> showError(message));
            Platform.runLater(()-> loadingText.setOpacity(0));
            busyLoading = false;
            return;
         }

         Platform.runLater(()-> loadingText.setOpacity(0));
         Platform.runLater(()-> setImage(currentJulia.getImage(), root, 400, 100));
         Platform.runLater(()-> fracDone.play());
         
         busyLoading = false;
     }).start();
     
   }

   private void setColour(){
      Integer val = COLOUR.getValue();
      if(val==null){julia.colour = 1;}
      else{julia.colour = val;}
   }

   private double[] getFields(){
      double[] doubleVals = new double[XFORM];
      String jxl="";
      try{
      for(int i = 0; i < XFORM; i++){
         jxl = inpFields[i].getText();
            doubleVals[i] = Double.parseDouble(jxl);
      }
      }
      catch(Exception e){
         throw new NumberFormatException("'"+jxl+"' is not a number");
      }

      if(doubleVals[XMIN] >= doubleVals[XMAX] || doubleVals[YMIN] >= doubleVals[YMAX]){
         throw new NumberFormatException("min values must be smaller than max values");
      }
      return doubleVals;
   }

   /**
    * what values
    * 0 = up
    * 1 = right
    * 2 = down
    * 3 = left
    */
   private void moveCamera(int direction, Pane root){
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
         //System.out.println(e);
         showError("a field you entered was invalid");return;
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
      makeFractal(root);
   }

   /**
    * 0 = zoom in
    * 1 = zoom out
    */
   private void zoom(int direction, Pane root){
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
         showError("a field you entered was invalid");return;
      }

      zoomAmount = (((xMax-xMin) + (yMax-yMin))/2)*zoomFactor;

      if(direction == 1){//in
         inpFields[XMIN].setText(String.valueOf(xMin-zoomAmount));
         inpFields[XMAX].setText(String.valueOf(xMax+zoomAmount));
         inpFields[YMIN].setText(String.valueOf(yMin-zoomAmount));
         inpFields[YMAX].setText(String.valueOf(yMax+zoomAmount));
      }
      else{//out
         inpFields[XMIN].setText(String.valueOf(xMin+zoomAmount));
         inpFields[XMAX].setText(String.valueOf(xMax-zoomAmount));
         inpFields[YMIN].setText(String.valueOf(yMin+zoomAmount));
         inpFields[YMAX].setText(String.valueOf(yMax-zoomAmount));
      }
      makeFractal(root);
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
   private void makeCameraPositionButton(String text, int xpos, int ypos, Pane root, int dir){
      Button newButt = new Button(text);
      newButt.relocate(xpos, ypos);
      double buttSize = 45;
      if(dir <=3){
         newButt.setMinSize(buttSize, buttSize);
      }

      EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() { 
         public void handle(ActionEvent e) 
         {
            if(dir<=3){moveCamera(dir, root);}
            else{zoom(dir-4, root);}
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
      ObservableList<Node> rootChildren = root.getChildren();

      Button submit = makeFractalButton("create", 260, 250, root);
      submit.setTooltip(new Tooltip("or press enter"));

      makeText("c value", defaultTextSize, fromLeft, fromTop, rootChildren);
      makeTextBox(3, fromLeft+80, fromTop, root, "0.4", CVAL);

      fromTop += spacing;
      makeText("expansion", defaultTextSize, fromLeft, fromTop, rootChildren);
      makeTextBox(3, fromLeft+100, fromTop, root, "1", EXPAN);
      
      fromTop += spacing;
      makeText("darkness", defaultTextSize, fromLeft, fromTop, rootChildren);

      makeTextBox(3, fromLeft+90, fromTop, root, "20", DARK);

      fromTop += spacing;
      makeText("colour preset", defaultTextSize, fromLeft, fromTop, rootChildren);
      COLOUR = makeIntList(1, 8, fromLeft+130, fromTop, root);

      fromTop += spacing;
      makeText("camera position", defaultTextSize, fromLeft, fromTop, rootChildren);
         fromTop += spacing;
         makeText("x", defaultTextSize-1, fromLeftInset, fromTop, rootChildren);

            fromTop += spacing;
            makeText("from", defaultTextSize-2, fromLeftInset+10, fromTop, rootChildren);
            makeTextBox(3, fromLeftInset+60, fromTop, root, "-2", XMIN);
            makeText("to", defaultTextSize-2, fromLeftInset+115, fromTop, rootChildren);
            makeTextBox(3, fromLeftInset+145, fromTop, root, "2", XMAX);
      

         fromTop += spacing;
         makeText("y", defaultTextSize-1, fromLeftInset, fromTop, rootChildren);

            fromTop += spacing;
            makeText("from", defaultTextSize-2, fromLeftInset+10, fromTop, rootChildren);
            makeTextBox(3, fromLeftInset+60, fromTop, root, "-2", YMIN);
            makeText("to", defaultTextSize-2, fromLeftInset+115, fromTop, rootChildren);
            makeTextBox(3, fromLeftInset+145, fromTop, root, "2", YMAX);
      
      fromTop += spacing+10;
      makeText("image dimensions", defaultTextSize, fromLeft, fromTop, rootChildren);
         fromTop += spacing;
         makeText("width", defaultTextSize-1, fromLeftInset, fromTop, rootChildren);
         makeTextBox(3, fromLeftInset+60, fromTop, root, "400", WIDTH);

         fromTop += spacing;
         makeText("height", defaultTextSize-1, fromLeftInset, fromTop, rootChildren);
         makeTextBox(3, fromLeftInset+60, fromTop, root, "400", HEIGHT);

      fromTop += spacing + 20;
      makeText("custom modifications on x and y", defaultTextSize, fromLeft, fromTop, rootChildren);
         fromTop += spacing;
         makeText("x =", defaultTextSize, fromLeftInset, fromTop, rootChildren);
         makeTextBox(15, fromLeft+60, fromTop, root, "x", XFORM);

         fromTop += spacing;
         makeText("y =", defaultTextSize, fromLeftInset, fromTop, rootChildren);
         makeTextBox(15, fromLeft+60, fromTop, root, "y", YFORM);

      
      makeHyperlink("new here? click me!", 20, 5, 20, "help.html", root);

      fromTop += spacing+20;
      String txt = "Valid symbols in custom functions are:\nnumbers, x, y, (, ), +, -, *, /, ^, cos(), sin()";
      makeText(txt, defaultTextSize-4, fromLeft, fromTop, rootChildren);

      makeSaveButton("save", 320, 250, root, pStage);

      //field of view buttons
      makeCameraPositionButton("up", 295, 25, root, 0);
      makeCameraPositionButton("right", 340, 70, root, 1);
      makeCameraPositionButton("down", 295, 115, root, 2);
      makeCameraPositionButton("left", 250, 70, root, 3);
      //zooms
      makeCameraPositionButton("zoom in ", 250, 170, root, 4);
      makeCameraPositionButton("zoom out", 320, 170, root, 5);

      goHomeButton("reset", 290, 200, root);

      loadingText = makeText("loading...", 35, 400, 20, rootChildren);
      loadingText.setOpacity(0);
      loadingText.setFill(Color.LIMEGREEN);

      errorText = makeText("default", 35, 400, 20, rootChildren);
      clearError();
      errorText.setFill(Color.RED);


      Text finishedNotifier = makeText("fractal complete", 35, 400, 20, rootChildren);
      finishedNotifier.setOpacity(0);
      finishedNotifier.setFill(Color.BLUE); 
      fracDone = new FadeTransition(Duration.millis(1000), finishedNotifier);
      fracDone.setFromValue(1.0);
      fracDone.setToValue(0);
       
      root.getChildren().add(submit);
   }

   private void makeHyperlink(String text, int xpos, int ypos, int size, String linkedFile, Pane root){
      Hyperlink myHyperlink = new Hyperlink();
      myHyperlink.setText(text);
      myHyperlink.relocate(xpos, ypos);
      myHyperlink.setFont(new Font(size));
      myHyperlink.setOnAction(e -> {
            if(Desktop.isDesktopSupported())
            {
               try {
                  Desktop.getDesktop().browse(new URI(linkedFile));
               } catch (IOException e1) {

                  e1.printStackTrace();
               } catch (URISyntaxException e1) {
                  e1.printStackTrace();
               }
            }
      });
      root.getChildren().add(myHyperlink);
   }

   private void makeTextBox(int length, int xpos, int ypos, Pane root, String init, int index){
      TextField newField = new TextField(init);
      newField.relocate(xpos, ypos);
      newField.setPrefColumnCount(length);
      root.getChildren().add(newField);
      inpFields[index] = newField;
   }

   public Text makeText(String text, int size, int xpos, int ypos, ObservableList<Node> rootChildren){
      Text newText = new Text(text);
      newText.setFont(new Font(size));
      newText.relocate(xpos, ypos);
      rootChildren.add(newText);
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
         KeyCode input = e.getCode();
         //utils.errorMsg(input.toString());
         if (input == KeyCode.ENTER) {
            makeFractal(root);
         }
         else if(input == KeyCode.EQUALS || input == KeyCode.NUMPAD5){
            zoom(0, root);
         }
         else if(input == KeyCode.MINUS){
            zoom(1, root);
         }
         else if(input == KeyCode.NUMPAD4){
            moveCamera(3, root);
         }
         else if(input == KeyCode.NUMPAD8){
            moveCamera(0, root);
         }
         else if(input == KeyCode.NUMPAD6){
            moveCamera(1, root);
         }
         else if(input == KeyCode.NUMPAD2){
            moveCamera(2, root);
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