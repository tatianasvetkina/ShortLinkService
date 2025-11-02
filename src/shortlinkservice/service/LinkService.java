package shortlinkservice.service;

import shortlinkservice.model.Link;
import shortlinkservice.storage.InMemoryStorage;
import shortlinkservice.util.LinkGenerator;

import java.awt.*;
import java.net.URI;
import java.time.LocalDateTime;

public class LinkService {

    // Время жизни ссылки — 24 часа
    private static final int LINK_LIFETIME_HOURS = 24;

    public Link createShortLink(String originalUrl, String userId, int maxClicks) {
        String shortUrl = LinkGenerator.generateShortUrl(originalUrl, userId);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(LINK_LIFETIME_HOURS);
        Link link = new Link(originalUrl, shortUrl, userId, maxClicks, expiresAt);
        InMemoryStorage.save(link);
        return link;
    }

    public void openLink(String shortUrl) throws Exception {
        Link link = InMemoryStorage.get(shortUrl);
        if (link == null) {
            System.out.println(" Ссылка не найдена.");
            return;
        }

        if (!link.canAccess()) {
            if (link.isExpired()) {
                System.out.println(" Срок действия ссылки истёк.");
            } else {
                System.out.println(" Лимит переходов исчерпан.");
            }
            InMemoryStorage.remove(shortUrl);
            return;
        }

        link.registerClick();
        System.out.println(" Переход по ссылке: " + link.getOriginalUrl());
        Desktop.getDesktop().browse(new URI(link.getOriginalUrl()));
    }

    public void cleanExpiredLinks() {
        InMemoryStorage.getAll().removeIf(Link::isExpired);
    }
}