package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws IOException {
       
		Parent root = FXMLLoader.load(getClass().getResource("logIn.fxml")); // creeam radacina utilizand SceneBuilder
		Scene scene = new Scene(root);		// creeam scena utilizand radacina
		stage.setScene(scene);  // selectam scene
		stage.show();			// afisam interfata grafica
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {  // implementam metoda abstracta din EventHandler 
				
				Platform.exit();		// inchiderea aplicatiei din butonul "X"
				System.exit(0);	
			}		
		});
	}	

	public static void main(String[] args) {
		launch(args);			// prornim intervata grafica
	}
}
