package ru.job4j.bmb.content;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Component
public class ContentProviderVideo implements ContentProvider {
    @Override
    public Content byMood(Long chatId, Long moodId) {
        var content = new Content(chatId);
        content.setVideo(new InputFile(new File("./video/video.avi")));
        return content;
    }
}

