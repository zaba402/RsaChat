package chat.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Paweł Dąbrowski on 28.12.2016.
 */
public class ServerGUI extends Application {

    private Server server = new Server(5555);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader =  new FXMLLoader(getClass().getResource("server.fxml"));
        Parent root = loader.load();
        ServerGUIController pServerGUIController = loader.getController();
        pServerGUIController.setServer(server);
        primaryStage.setTitle("Server");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override public void init() throws Exception {
        server.startConnection();
    }

    @Override public void stop() throws Exception {
        server.closeConnection();
    }


}
