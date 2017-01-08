package chat;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by Paweł Dąbrowski on 02.01.2017.
 */
public class RSA {

    public static final Integer LENGTH = 1024;

    private KeyDTO privateKey = new KeyDTO();
    private KeyDTO publicKey = new KeyDTO();

    public RSA() {

    }

    public KeyDTO getPublicKey() {
        return publicKey;
    }

    public void createKeys(BigInteger aP, BigInteger aQ) {
        BigInteger pEulerValue = (aP.subtract(BigInteger.ONE)).multiply(aQ.subtract(BigInteger.ONE));
        BigInteger pE =  BigInteger.probablePrime(LENGTH / 2, new Random());
        while (pEulerValue.gcd(pE).compareTo(BigInteger.ONE) == 1 && pE.compareTo(pEulerValue) == -1) {
            pE = pE.add(BigInteger.ONE);
        }
        BigInteger n = aP.multiply(aQ);
        publicKey.setValue(pE);
        publicKey.setN(n);
        privateKey.setValue(pE.modInverse(pEulerValue));
        privateKey.setN(n);
    }

    public String decrypt(String aMessage) {
        return new String((new BigInteger(aMessage)).modPow(privateKey.getValue(),  privateKey.getN()).toByteArray());
    }
}
