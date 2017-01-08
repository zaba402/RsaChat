package chat;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by Paweł Dąbrowski on 02.01.2017.
 */
public class KeyDTO implements Serializable {

    private BigInteger value;

    private BigInteger n;

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger aValue) {
        value = aValue;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger aN) {
        n = aN;
    }
}
