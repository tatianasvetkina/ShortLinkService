package shortlinkservice.model;

import java.util.UUID;

public class User {
    private String id;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }
}

