package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main launcher for the designer.
 * 
 * @author cdberkstresser
 *
 */
public class Main extends Application {
	@Override
	public final void start(final Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/QuantumCanvas.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Quantum Circuit Designer");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/view/Quantum.png")));
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main launcher.
	 * 
	 * @param args command line arguments.
	 */
	public static void main(final String[] args) {
		launch(args);
	}
}
