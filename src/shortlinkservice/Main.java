package shortlinkservice;

import shortlinkservice.model.Link;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Link> links = new HashMap<>(); // shortUrl -> Link
    private static final Map<String, String> userMap = new HashMap<>(); // userId -> описание
    private static final int LINK_LIFETIME_HOURS = 24; // время жизни ссылки

    public static void main(String[] args) {

        System.out.println("=== ShortLinkService ===");
        String userId = UUID.randomUUID().toString();
        System.out.println("Ваш пользовательский UUID: " + userId);

        while (true) {
            showMenu();
            int choice = readInt();

            switch (choice) {
                case 1 -> createShortLink(userId);
                case 2 -> openShortLink(userId);
                case 3 -> cleanExpiredLinks();
                case 0 -> {
                    System.out.println("Выход...");
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n=== МЕНЮ ===");
        System.out.println("1. Создать короткую ссылку");
        System.out.println("2. Перейти по короткой ссылке");
        System.out.println("3. Очистить истёкшие ссылки");
        System.out.println("0. Выход");
        System.out.print(" Ваш выбор: ");
    }

    private static int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("Введите число: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }

    private static void createShortLink(String userId) {
        System.out.print("Введите оригинальный URL (с http/https): ");
        String originalUrl = scanner.nextLine();

        System.out.print("Введите максимальное количество переходов: ");
        int maxClicks = readInt();

        String shortUrl = generateShortUrl(originalUrl, userId);
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(LINK_LIFETIME_HOURS);

        Link link = new Link(originalUrl, shortUrl, userId, maxClicks, expiresAt);
        links.put(shortUrl, link);

        System.out.println("Короткая ссылка создана: " + shortUrl);
        System.out.println("Ссылка будет недоступна после: " + expiresAt);
    }

    private static String generateShortUrl(String originalUrl, String userId) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String shortUrl;
        Random random = new Random();
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            shortUrl = sb.toString();
        } while (links.containsKey(shortUrl));
        return shortUrl;
    }

    private static void openShortLink(String userId) {
        System.out.print("Введите короткую ссылку: ");
        String shortUrl = scanner.nextLine();

        Link link = links.get(shortUrl);
        if (link == null) {
            System.out.println("Ссылка не найдена.");
            return;
        }

        if (!link.getUserId().equals(userId)) {
            System.out.println("Доступ запрещён. Эта ссылка принадлежит другому пользователю.");
            return;
        }

        if (!link.canAccess()) {
            System.out.println("Ссылка недоступна: истёк срок действия или превышен лимит переходов.");
            return;
        }

        try {
            link.registerClick();
            Desktop.getDesktop().browse(new URI(link.getOriginalUrl()));
            System.out.println("Переход выполнен на: " + link.getOriginalUrl());
            System.out.println("Осталось переходов: " + (link.getMaxClicks() - link.getClickCount()));
        } catch (Exception e) {
            System.out.println("Ошибка при открытии ссылки: " + e.getMessage());
        }
    }

    private static void cleanExpiredLinks() {
        int removed = 0;
        Iterator<Map.Entry<String, Link>> iterator = links.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Link> entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
                removed++;
            }
        }
        System.out.println("Удалено истёкших ссылок: " + removed);
    }
}


