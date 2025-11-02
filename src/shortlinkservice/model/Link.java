package shortlinkservice.model;

import java.time.LocalDateTime;

public class Link {
    private final String originalUrl;
    private final String shortUrl;
    private final String userId;
    private final int maxClicks;
    private int clickCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    public Link(String originalUrl, String shortUrl, String userId, int maxClicks, LocalDateTime expiresAt) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.userId = userId;
        this.maxClicks = maxClicks;
        this.clickCount = 0;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    public String getOriginalUrl() { return originalUrl; }
    public String getShortUrl() { return shortUrl; }
    public String getUserId() { return userId; }
    public int getClickCount() { return clickCount; }
    public int getMaxClicks() { return maxClicks; }

    public boolean isExpired() { return LocalDateTime.now().isAfter(expiresAt); }

    public boolean canAccess() { return clickCount < maxClicks && !isExpired(); }

    public void registerClick() { clickCount++; }
}
