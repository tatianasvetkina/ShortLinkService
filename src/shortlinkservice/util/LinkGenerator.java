package shortlinkservice.util;

import java.util.Base64;

public class LinkGenerator {
    public static String generateShortUrl(String originalUrl, String userId) {
        String base = originalUrl + System.nanoTime() + userId;
        String encoded = Base64.getUrlEncoder().encodeToString(base.getBytes());
        return "http://short.ly/" + encoded.substring(0, 8);
    }
}
