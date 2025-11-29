package lk.mc.core.security;

import lk.mc.core.exceptions.TsActiveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

/**
 * Password util will encrypt/decrypt the text based on a key value.
 * Encryption will use AES algorithm
 *
 * @author vihangawicks
 * @since 11/12/21
 * MC-lms
 */
public class EncryptUtils {

    private static final String ALGORITHM = "AES";
    /**
     * THIS IS NOT A SECURE PRACTICE! Encryption key should not be exposed; need to move this to a config-server
     */
    private static final byte[] KEY_VALUE
            = new byte[]{'V', 'Y', 'w', 'T', 'S', 's', 'i', 'n', 'a', 'g', 'E', 'v', 'S', 'I', 'X', 'z'};
    private static Logger logger = LogManager.getLogger(EncryptUtils.class);

    /**
     * AES encryption for passwords based on ${KEY_VALUE}.
     *
     * @param valueToEnc text value
     * @return encrypted string
     * @throws TsActiveException on a algorithm failure
     */
    public static String encrypt(String valueToEnc) throws TsActiveException {
        try {
            Key key = generateKey();
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(c.doFinal(valueToEnc.getBytes()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new TsActiveException(e);
        }
    }

    /**
     * Decrypt a text value based on AES algorithm, using ${KEY_VALUE}.
     * This will throw an exception if the encryptedValue is invalid or use a different key.
     *
     * @param encryptedValue encrypted text value
     * @return decrypted text
     * @throws Exception on a key failure or encryptedValue failure
     */
    public static String decrypt(String encryptedValue) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        return new String(c.doFinal(Base64.getDecoder().decode(encryptedValue)));
    }

//    public static void main(String[] args) throws Exception {
//        System.out.println(decrypt("XNqP541tcngvxMns2+dPpA=="));
//        ;
//    }
    /**
     * Generates the key for encryption and decryption with the key.
     * Note:- key value should not be exposed.
     *
     * @return serialized representation of Key
     */
    private static Key generateKey() {
        return new SecretKeySpec(KEY_VALUE, ALGORITHM);
    }

    /* For testing only*/
//    public static void main(String[] args) {
//        try {
//            System.out.println(encrypt("rcrcfodhpanfgolb"));  //byVy4th7/LFtzHPUK3Az6g==
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
