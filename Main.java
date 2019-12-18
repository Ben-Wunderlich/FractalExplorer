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
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import java.io.File;
import java.io.IOException;
import java.awt.image.RenderedImage;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import utilities.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.Thread;
import javafx.application.Platform;


public class Main extends Application {

   final int DARK = 0;
   final int XVIEW = 1;
   final int YVIEW = 2;
   final int EXPAN = 3;
   final int CVAL = 4;
   final int WIDTH = 5;
   final int XFORM = 6;
   final int YFORM = 7;

   TextField[] inpFields = new TextField[8];
   FadeTransition fracDone;
   Text loadingText;
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
      primaryStage.getIcons().add(new Image("file:icon.png"));
      primaryStage.setScene(scene); 
      primaryStage.show();

   }

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

   private void makeFractal(Pane root){
      new Thread(() -> {
         Platform.runLater(()-> loadingText.setOpacity(1));
     
         double[] fVals = getFields();

         currentJulia = new julia((int)fVals[WIDTH], fVals[CVAL], fVals[EXPAN], fVals[DARK],
         fVals[XVIEW], fVals[YVIEW], inpFields[XFORM].getText(), inpFields[YFORM].getText());

         Platform.runLater(()-> loadingText.setOpacity(0));
         Platform.runLater(()-> setImage(currentJulia.getImage(), root, 400, 100));
         Platform.runLater(()-> fracDone.play());
     }).start();
   }

   private double[] getFields(){
      double[] doubleVals = new double[6];
      for(int i = 0; i < 5; i++){
         String jxl = inpFields[i].getText();
         if(utils.isNumber(jxl)){
            doubleVals[i] = Double.parseDouble(jxl);
         }
      }
      if(utils.isInt(inpFields[WIDTH].getText()))
         doubleVals[WIDTH] = Double.parseDouble(inpFields[WIDTH].getText());
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

         makeTextBox(3, fromLeft+40, fromTop, root, "2", XVIEW);//xview

         fromTop += spacing;
         makeText("y", defaultTextSize, fromLeftInset, fromTop, root);
         makeTextBox(3, fromLeft+40, fromTop, root, "2", YVIEW);
      
      fromTop += spacing;
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
      //maybe add option for colours
      makeSaveButton("save", fromLeft+60, 400, root, pStage);

      loadingText = makeText("loading...", 35, 400, 20, root);
      loadingText.setOpacity(0);
      loadingText.setFill(Color.LIMEGREEN);


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