package ru.job4j.bmb.content;

public interface ContentProvider {
    Content byMood(Long chatId, Long moodId);
}
