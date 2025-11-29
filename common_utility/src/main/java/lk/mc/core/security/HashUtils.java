package lk.mc.core.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * jBCrypt is a Java™ implementation of OpenBSD's Blowfish password hashing code,
 * as described in "A Future-Adaptable Password Scheme" by Niels Provos and David Mazières.
 * <p>
 * This system hashes passwords using a version of Bruce Schneier's Blowfish block cipher
 * with modifications designed to raise the cost of off-line password cracking and frustrate fast hardware implementation.
 * The computation cost of the algorithm is parametised, so it can be increased as computers get faster.
 * The intent is to make a compromise of a password database less likely to result in an attacker gaining knowledge of
 * the plaintext passwords (e.g. using John the Ripper).
 * <p>
 * There seems to be a lack of good password hashes for Java - the top two hits in Google (as of 2006/05/24)
 * for "Java password hash" and "Java password encryption" both offer terrible advice: one uses an unsalted hash
 * which allows reverse dictionary lookup of passwords and the other recommends reversible encryption,
 * which is rarely needed and should only be used as a last resort.
 * <p>
 * <p>
 * jBCrypt is licensed under a ISC/BSD licence (see the LICENSE file for details) and ships with a set of JUnit unit
 * tests to verify correct operation of the library and compatibility with the canonical C implementation
 * of the bcrypt algorithm.
 *
 * @author vihangawicks
 * @since 06/10/22
 * MC-lms
 */
public class HashUtils {

    /**
     * Gensalt's log_rounds parameter determines the complexity
     * the work factor is 2**log_rounds, and the default is 10
     *
     * @param pw password
     * @return hash value
     */
    public static String hash(String pw) {

        return BCrypt.hashpw(pw, BCrypt.gensalt(12));

    }

    /**
     * Check that an unencrypted password matches one that has
     * previously been hashed
     *
     * @param candidate password
     * @return true if matched
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean checkEncrypted(String candidate, String hash) {

        return BCrypt.checkpw(candidate, hash);

    }

//    public static void main(String[] args) {
//        String pw = "1234567890";
//
//        String hash = HashUtils.hash(pw);
//        System.out.println(hash);
//
//        if (HashUtils.checkEncrypted(pw, hash))
//            System.out.println("Hash matched");
//        else
//            System.out.println("HASH MISMATCH");
//    }
}
