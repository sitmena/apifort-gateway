package me.sitech.apifort.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import me.sitech.apifort.config.AppLifecycleBean;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.sitech.apifort.constant.ApiFort.API_TOKEN_CLAIM;


public class Util {

    private Util() {
        throw new APIFortGeneralException("Utility class");
    }

    public static RSAPublicKey readStringPublicCertificate(String publicCertificate)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        Base64.Decoder decodeBase64 = Base64.getDecoder();
        byte[] decode = decodeBase64.decode(publicCertificate);
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(decode);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return  (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
    }

    public static String regexEndpointUniqueCacheId(String apiKey, String context, String method, String endpointRegex){
        return  String.format("%s-%s-%s-%s",
                apiKey,context, method.toUpperCase(),endpointRegex);
    }

    public static String getContextPath(String path){
        final Matcher fullMatcher = Pattern.compile(ApiFort.EXTRACT_CONTEXT_REGEX).matcher(path);
        if(!fullMatcher.find()){
            throw new APIFortGeneralException("Path with no context path");
        }
        return fullMatcher.group(0).toLowerCase();
    }

    public static String getSHA1(String str){
        return  new DigestUtils("SHA-1").digestAsHex(str);
    }


    public static void verifyAllowedRestMethod(boolean isPublicEndpoint, String methodType){
        if (isPublicEndpoint) {
            if (AppLifecycleBean.getAllowedPublicMethods().stream().filter(item-> item.equalsIgnoreCase(methodType)).count() == 0) {
                throw new APIFortGeneralException(String.format("%s method is not allowed for public endpoints",methodType));
            }
        } else {
            if (AppLifecycleBean.getAllowedPrivateMethods().stream().filter(item-> item.equalsIgnoreCase(methodType)).count() == 0) {
                throw new APIFortGeneralException(String.format("%s method is not allowed for private endpoints",methodType));
            }
        }
    }

    public static void verifyEndpointPath(String path){
        if(!path.startsWith("/"))
            throw new APIFortGeneralException(String.format("you Path (%s) should start with /",path));
    }

    public static String generateApiFortPathRegex(boolean isPublicEndpoint, String contextPath, String endpointPath){
        endpointPath = endpointPath.replaceAll("\\{.*?}", "[^\\/]+");
        return String.format("^/%s/%s%s$",isPublicEndpoint?
                AppLifecycleBean.getPublicContext():AppLifecycleBean.getPrivateContext(),
                contextPath,endpointPath);
    }


    public static String downStreamServiceEndpoint(String servicePath,String path){
        final Matcher fullMatcher = Pattern.compile(String.format("(?<=%s).*",getContextPath(path))).matcher(path);
        if(!fullMatcher.find()){
            throw new APIFortGeneralException("Path with no context path");
        }
        return servicePath+fullMatcher.group(0).toLowerCase();
    }

    public static String generateApiFortPath(boolean isPublicEndpoint, String contextPath, String endpointPath){
        return String.format("/%s/%s%s",isPublicEndpoint?
                AppLifecycleBean.getPublicContext():AppLifecycleBean.getPrivateContext()
                ,contextPath,endpointPath);
    }


    public static List<String> extractClaims(String token, String certificate)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(Util.readStringPublicCertificate(certificate))
                .build()
                .parseClaimsJws(token.replaceAll(ApiFort.API_FORT_JWT_TOKEN_PREFIX, ApiFort.API_FORT_EMPTY_STRING));
        Map<String, List<String>> roles = claims.getBody().get(API_TOKEN_CLAIM, Map.class);
        return roles.get(ApiFort.API_TOKEN_ROLES);
    }

    public static boolean isEmpty(String str){
        return (str==null || str.isEmpty() || str.trim().isEmpty());
    }

    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

}
