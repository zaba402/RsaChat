package chat;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Created by Paweł Dąbrowski on 28.12.2016.
 */
public abstract class Connection {

    protected Consumer<Serializable> callback;

    public void setCallback(Consumer<Serializable> aCallback) {
        callback = aCallback;
    }

    protected abstract void startConnection();

    protected abstract void send(Serializable data);

    protected abstract void closeConnection();
}
