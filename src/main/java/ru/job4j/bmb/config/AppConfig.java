package ru.job4j.bmb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${telegram.bot.name")
    private String botName;

    public void printConfig() {
        System.out.println("App Name: " + botName);
    }
}