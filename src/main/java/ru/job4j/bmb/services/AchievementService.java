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
        if (user != null) {
            Stream<MoodLog> moodLogStream = moodService
                    .getMoodLogRepository()
                    .findByUserIdOrderByCreatedAtDesc(user.getId());
            long count = moodLogStream
                    .takeWhile(item -> item.getMood()
                            .isGood())
                    .count();
            AwardRepository awardRepository = moodService.getAwardRepository();
            for (var award : awardRepository.findAll()) {
                if (award.getDays() <= count) {
                    Achievement achievement = new Achievement();
                    achievement.setAward(award);
                    achievement.setUser(user);
                    achievement.setCreateAt(System.currentTimeMillis());
                    moodService.getAchievementRepository().save(achievement);
                }
            }
            Optional<Content> content = moodService.awards(user.getChatId(), user.getClientId());
            if (content.isPresent()) {
                sentContent.sent(content.get());
            }
        }
    }
}
