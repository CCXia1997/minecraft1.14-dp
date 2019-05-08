package net.minecraft.network;

import org.apache.logging.log4j.LogManager;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.Logger;

public class NetworkEncryptionUtils
{
    private static final Logger LOGGER;
    
    @Environment(EnvType.CLIENT)
    public static SecretKey generateKey() {
        try {
            final KeyGenerator keyGenerator1 = KeyGenerator.getInstance("AES");
            keyGenerator1.init(128);
            return keyGenerator1.generateKey();
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException1) {
            throw new Error(noSuchAlgorithmException1);
        }
    }
    
    public static KeyPair generateServerKeyPair() {
        try {
            final KeyPairGenerator keyPairGenerator1 = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator1.initialize(1024);
            return keyPairGenerator1.generateKeyPair();
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException1) {
            noSuchAlgorithmException1.printStackTrace();
            NetworkEncryptionUtils.LOGGER.error("Key pair generation failed!");
            return null;
        }
    }
    
    public static byte[] generateServerId(final String baseServerId, final PublicKey publicKey, final SecretKey secretKey) {
        try {
            return hash("SHA-1", baseServerId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
        }
        catch (UnsupportedEncodingException unsupportedEncodingException4) {
            unsupportedEncodingException4.printStackTrace();
            return null;
        }
    }
    
    private static byte[] hash(final String algorithm, final byte[]... data) {
        try {
            final MessageDigest messageDigest3 = MessageDigest.getInstance(algorithm);
            for (final byte[] arr7 : data) {
                messageDigest3.update(arr7);
            }
            return messageDigest3.digest();
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException3) {
            noSuchAlgorithmException3.printStackTrace();
            return null;
        }
    }
    
    public static PublicKey readEncodedPublicKey(final byte[] arr) {
        try {
            final EncodedKeySpec encodedKeySpec2 = new X509EncodedKeySpec(arr);
            final KeyFactory keyFactory3 = KeyFactory.getInstance("RSA");
            return keyFactory3.generatePublic(encodedKeySpec2);
        }
        catch (NoSuchAlgorithmException ex) {}
        catch (InvalidKeySpecException ex2) {}
        NetworkEncryptionUtils.LOGGER.error("Public key reconstitute failed!");
        return null;
    }
    
    public static SecretKey decryptSecretKey(final PrivateKey privateKey, final byte[] encryptedSecretKey) {
        return new SecretKeySpec(decrypt(privateKey, encryptedSecretKey), "AES");
    }
    
    @Environment(EnvType.CLIENT)
    public static byte[] encrypt(final Key key, final byte[] data) {
        return crypt(1, key, data);
    }
    
    public static byte[] decrypt(final Key key, final byte[] data) {
        return crypt(2, key, data);
    }
    
    private static byte[] crypt(final int opMode, final Key key, final byte[] data) {
        try {
            return crypt(opMode, key.getAlgorithm(), key).doFinal(data);
        }
        catch (IllegalBlockSizeException illegalBlockSizeException4) {
            illegalBlockSizeException4.printStackTrace();
        }
        catch (BadPaddingException badPaddingException4) {
            badPaddingException4.printStackTrace();
        }
        NetworkEncryptionUtils.LOGGER.error("Cipher data failed!");
        return null;
    }
    
    private static Cipher crypt(final int opMode, final String algorithm, final Key key) {
        try {
            final Cipher cipher4 = Cipher.getInstance(algorithm);
            cipher4.init(opMode, key);
            return cipher4;
        }
        catch (InvalidKeyException invalidKeyException4) {
            invalidKeyException4.printStackTrace();
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException4) {
            noSuchAlgorithmException4.printStackTrace();
        }
        catch (NoSuchPaddingException noSuchPaddingException4) {
            noSuchPaddingException4.printStackTrace();
        }
        NetworkEncryptionUtils.LOGGER.error("Cipher creation failed!");
        return null;
    }
    
    public static Cipher cipherFromKey(final int opMode, final Key key) {
        try {
            final Cipher cipher3 = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher3.init(opMode, key, new IvParameterSpec(key.getEncoded()));
            return cipher3;
        }
        catch (GeneralSecurityException generalSecurityException3) {
            throw new RuntimeException(generalSecurityException3);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
