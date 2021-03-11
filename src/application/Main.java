package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application 
{
    public static void main(String[] args) {

        launch(args);
    }

    
    @Override
    public void start(Stage primaryStage) throws Exception
    {

        System.setProperty( "prism.lcdtext" , "false" );

        FXMLLoader      fxmlLoader  = new FXMLLoader( getClass().getResource( "/application/FMToneEditor.fxml" ) );

        Pane    root        = (Pane) fxmlLoader.load();

        Scene   scene       = new Scene( root , 650 , 800 );
        
        root.setMinSize(650, 800);
     
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN ,new EventHandler<WindowEvent>() {
    	   @Override
     	   public void handle(WindowEvent window) {
    		/* ウィンドウが開いた後に処理 */


     	   }	
        });	 
        primaryStage.setResizable(false);
        primaryStage.setTitle("FM Tone Editor");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
        	@Override
        	public void handle(WindowEvent t) {
        		  //System.out.println("window close");

      		   Alert alert = new Alert( AlertType.NONE,"",ButtonType.OK, ButtonType.CANCEL);
      		   alert.setTitle("終了");
      		   alert.getDialogPane().setContentText( "終了してもよろしいですか？" );     		   
      		   
      		   Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
      		   stage.setAlwaysOnTop(true);
      		   stage.toFront(); // not sure if necessary
      		   
      		   
 		   
      		   ButtonType              button  = alert.showAndWait().orElse( ButtonType.CANCEL );     
      		 if(button.getButtonData() != ButtonData.OK_DONE) {

      			 t.consume();  			 
      		 }else {

      			javafx.application.Platform.exit();
      		 }

        	
        	}
        });

        primaryStage.setScene( scene );
        primaryStage.show();
        

 
    }
    @Override
    public void stop() throws Exception{
    	/* 終了処理 */
	
    	//STM32FM.getInstance().close();
    	javafx.application.Platform.exit();
 
    	
    }

 
 
}