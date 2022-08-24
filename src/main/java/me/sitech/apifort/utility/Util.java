package me.sitech.apifort.utility;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {


    public static RSAPublicKey readStringPublicCertificate(String publicCertificate)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        Base64.Decoder decodeBase64 = Base64.getDecoder();
        byte[] decode = decodeBase64.decode(publicCertificate);
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(decode);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return  (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
    }

    public static String redisEndpointGroupCacheId(String apiKey, String context,String method){
        return  String.format("%s-%s-%s",
                apiKey,context, method.toUpperCase());
    }

    public static String regexEndpointUniqueCacheId(String apiKey, String context, String method, String endpointRegex){
        return  String.format("%s-%s-%s-%s",
                apiKey,context, method.toUpperCase(),endpointRegex);
    }

    public static String getContextPath(String path){
        final Matcher fullMatcher = Pattern.compile("(?s)(?<=/api/|/guest/).*?(?=/)").matcher(path);
        if(!fullMatcher.find()){
            throw new RuntimeException("Path with no context path");
        }
        return fullMatcher.group(0).toLowerCase();
    }

    public static String getRegex(String str){
        return str.replaceAll("\\{.*}", "[^\\/]+");
    }
}
