package CoreGUI;

import DataAnalysis.Database;
import DataAnalysis.Product;
import DataAnalysis.Results;
import ScraperApp.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class Main extends Application
{

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("newPage.fxml"));
		Scene scene = new Scene(root);

		Controller controller = new Controller();
		loader.setController(controller);

		scene.getStylesheets().add(getClass().getResource("page.css").toExternalForm());

		primaryStage.setTitle("Get The Giant Fair-Price!");
		primaryStage.setScene(scene);
		primaryStage.show();

	}


	public static void main(String[] args) throws IOException
	{
       launch(args);

	}
}

