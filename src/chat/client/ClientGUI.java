package chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Paweł Dąbrowski on 28.12.2016.
 */
public class ClientGUI extends Application {

    private Client client = new Client("127.0.0.1", 5555);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader =  new FXMLLoader(getClass().getResource("client.fxml"));
        Parent root = loader.load();
        ClientGUIController pClientGUIController = loader.getController();
        pClientGUIController.setClient(client);
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override public void init() throws Exception {
        client.startConnection();
    }

    @Override public void stop() throws Exception {
        client.closeConnection();
    }
}
