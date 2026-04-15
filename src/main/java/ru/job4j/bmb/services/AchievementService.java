package ru.job4j.bmb.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.event.UserEvent;
import ru.job4j.bmb.model.Achievement;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.repository.AwardRepository;
import ru.job4j.bmb.repository.MoodLogRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class AchievementService implements ApplicationListener<UserEvent> {
    @Autowired
    private SentContent sentContent;

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        var user = event.getUser();
        MoodService moodService = (MoodService) event.getSource();
        long count = moodService.getGoodDays(user);
        if (count != -1) {
            moodService.getAchievements(user, count);
            Optional<Content> content = moodService.awards(user.getChatId(), user.getClientId());
            if (content.isPresent()) {
                sentContent.sent(content.get());
            }
        }
    }
}
