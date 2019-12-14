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

/*inputs to make
   c value (only have julia set)
   xview
   yview (have under view header)
   darkness
   expansion

   button to create fractal
*/

public class Main extends Application { 
   @Override     
   public void start(Stage primaryStage) throws Exception {            

      Pane root = new Pane();
      


      makeText("test", 45, 10, 100, root); 
      makeText("new", 45, 100, 100, root);
       
      //Creating a Scene by passing the group object, height and width   
      Scene scene = new Scene(root ,1200, 600); 
      
      //setting color to the scene 
      //scene.setFill(Color.BROWN);  
      
      //Setting the title to Stage. 
      primaryStage.setTitle("Fractals"); 
   
      //Adding the scene to Stage 
      primaryStage.setScene(scene); 
       
      //Displaying the contents of the stage 
      primaryStage.show(); 
   }

   private Text makeText(String text, int size, int xpos, int ypos, Pane root){
      Text newText = new Text();
      newText.setFont(new Font(size)); 
      newText.setX(xpos); 
      newText.setY(ypos);
      newText.setText(text);
      root.getChildren().add(newText);
      return newText;
   }

   private ObservableList getIntList(int min, int max){
      ObservableList<Integer> options=FXCollections.observableArrayList();
      for(i=min; i <= max; i++){
         options.add(i);
      }
      return options;
   }

   public static void main(String args[]){          
      launch(args);     
   }         
} 