package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.content.ContentProvider;

import java.util.List;
import java.util.Random;

@Service
public class RecommendationEngine {
    private final List<ContentProvider> contents;
    private static final Random RND = new Random(System.currentTimeMillis());

    public RecommendationEngine(List<ContentProvider> contents) {
        this.contents = contents;
    }

    public Content recommendFor(Long chatId, Long moodId) {
        var index = RND.nextInt(0, contents.size());
        return contents.get(index).byMood(chatId, moodId);
    }
}
