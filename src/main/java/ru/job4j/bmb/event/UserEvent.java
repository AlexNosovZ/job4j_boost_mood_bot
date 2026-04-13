package ru.job4j.bmb.event;

import org.springframework.context.ApplicationEvent;
import ru.job4j.bmb.model.User;

public class UserEvent extends ApplicationEvent {
    private final User user;

    public UserEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
