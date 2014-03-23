package android.remote.connection;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import remote.api.Packet;

public class ConnectionConfig {
    public final PublicKey publicKey;
    public final InetSocketAddress inetSocketAddress;
    public final byte[] user;
    public final byte[] password;

    public ConnectionConfig(String rsaModulus, String rsaExponent, String serverAddress,
                            int serverPort, String loginUser, String loginPassword) throws
            GeneralSecurityException, UnsupportedEncodingException {
        KeyFactory keyFactory = KeyFactory.getInstance(Packet.SECURE_ALGORITHM_NAME);
        publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(new BigInteger(rsaModulus),
                new BigInteger(rsaExponent)));
        inetSocketAddress = new InetSocketAddress(serverAddress, serverPort);
        user = loginUser.getBytes("UTF-8");
        password = loginPassword.getBytes("UTF-8");
    }
}
