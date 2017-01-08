package chat.server;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Paweł Dąbrowski on 28.12.2016.
 */
public class ServerGUIController implements Initializable {

    @FXML
    public TextField input;

    @FXML
    public TextArea message;

    private Server server;

    public void setServer(Server aServer) {
        server = aServer;
        server.setCallback(data -> {
            Platform.runLater(() -> {
                message.appendText("Client(encrypted): ");
                message.appendText(data.toString());
                message.appendText("\n");
                message.appendText("Client(decrypted): ");
                message.appendText(server.getRsa().decrypt(data.toString()));
                message.appendText("\n");
            });
        });
    }

    public void sendMessage(ActionEvent aActionEvent) {
        StringBuilder pMessageBuilder = new StringBuilder("Server: ").append(input.getText()).append("\n");
        message.appendText(pMessageBuilder.toString());
        try {
            server.send(input.getText());
            input.clear();
        } catch (Exception aE) {
            message.appendText("Failed to send server message: " + aE.toString() + "\n");
        }
    }

    @Override public void initialize(URL location, ResourceBundle resources) {

    }
}
