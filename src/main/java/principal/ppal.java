package principal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ppal extends Application {

	 @Override
	    public void start(Stage primaryStage) throws Exception {
	        // Cargar el archivo FXML
	        Parent root = FXMLLoader.load(getClass().getResource("/form.fxml"));
	        
	        // Crear la ventana principal
	        primaryStage.setTitle("Gestión de Notebooks");
	        primaryStage.setScene(new Scene(root, 1000, 600));  // width - height
	        primaryStage.show();

	    }

	    public static void main(String[] args) {
	        launch(args);  // Iniciar la aplicación JavaFX
	    }
}
