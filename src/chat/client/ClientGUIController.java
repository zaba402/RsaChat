package chat.client;

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
public class ClientGUIController implements Initializable {

    @FXML
    public TextField input;

    @FXML
    public TextArea message;

    private Client client;

    public void setClient(Client aClient) {
        client = aClient;
        client.setCallback(data -> {
            Platform.runLater(() -> {
                message.appendText("Server(encrypted): ");
                message.appendText(data.toString());
                message.appendText("\n");
                message.appendText("Server(decrypted): ");
                message.appendText(client.getRsa().decrypt(data.toString()));
                message.appendText("\n");
            });
        });
    }

    public void sendMessage(ActionEvent aActionEvent) {
        StringBuilder pMessageBuilder = new StringBuilder("Client: ").append(input.getText()).append("\n");
        message.appendText(pMessageBuilder.toString());
        try {
            client.send(input.getText());
            input.clear();
        } catch (Exception aE) {
            message.appendText("Failed to send client message: " + aE.toString() + "\n");
        }
    }

    @Override public void initialize(URL location, ResourceBundle resources) {

    }
}
