package shortlinkservice.storage;

import shortlinkservice.model.Link;
import java.util.*;

public class InMemoryStorage {
    private static final Map<String, Link> links = new HashMap<>();

    public static void save(Link link) {
        links.put(link.getShortUrl(), link);
    }

    public static Link get(String shortUrl) {
        return links.get(shortUrl);
    }

    public static void remove(String shortUrl) {
        links.remove(shortUrl);
    }

    public static Collection<Link> getAll() {
        return links.values();
    }
}
