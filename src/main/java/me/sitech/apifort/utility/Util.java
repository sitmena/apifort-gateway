package me.sitech.apifort.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Util {

    public static String escape(String input) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            // let's not put any nulls in our strings
            assert ((int) ch != 0);

            if (ch == '\n') {
                output.append("\\n");
            } else if (ch == '\t') {
                output.append("\\t");
            } else if (ch == '\r') {
                output.append("\\r");
            } else if (ch == '\\') {
                output.append("\\\\");
            } else if (ch == '"') {
                output.append("\\\"");
            } else if (ch == '\b') {
                output.append("\\b");
            } else if (ch == '\f') {
                output.append("\\f");
            } else if ((int) ch > 127) {
                output.append(String.format("\\u%04x", (int) ch));
            } else {
                output.append(ch);
            }
        }

        return output.toString();
    }

    public static String unescape(String input) {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        while (i < input.length()) {
            char delimiter = input.charAt(i);
            i++; // consume letter or backslash
            if (delimiter == '\\' && i < input.length()) {
                char ch = input.charAt(i);
                i++;
                if (ch == '\\' || ch == '/' || ch == '"' || ch == '\'') {
                    builder.append(ch);
                } else if (ch == 'n') builder.append('\n');
                else if (ch == 'r') builder.append('\r');
                else if (ch == 't') builder.append('\t');
                else if (ch == 'b') builder.append('\b');
                else if (ch == 'f') builder.append('\f');
                else if (ch == 'u') {
                    StringBuilder hex = new StringBuilder();
                    if (i + 4 > input.length()) {
                        throw new RuntimeException("Not enough unicode digits! ");
                    }
                    for (char x : input.substring(i, i + 4).toCharArray()) {
                        if (!Character.isLetterOrDigit(x)) {
                            throw new RuntimeException("Bad character in unicode escape.");
                        }
                        hex.append(Character.toLowerCase(x));
                    }
                    i += 4; // consume those four digits.
                    int code = Integer.parseInt(hex.toString(), 16);
                    builder.append((char) code);
                } else {
                    throw new RuntimeException("Illegal escape sequence: \\" + ch);
                }
            } else { // it's not a backslash, or it's the last character.
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }


    public static PublicKey readPublicCertificateFromFile (String filePath)
            throws CertificateException, FileNotFoundException {

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(new FileInputStream(filePath));
        return cert.getPublicKey();
    }

    public static RSAPublicKey readStringPublicCertificate(String publicCertificate)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        Base64.Decoder decodeBase64 = Base64.getDecoder();
        byte[] decode = decodeBase64.decode(publicCertificate);
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(decode);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return  (RSAPublicKey) keyFactory.generatePublic(keySpecX509);

    }
}
