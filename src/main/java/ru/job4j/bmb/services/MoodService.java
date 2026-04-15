package ru.job4j.bmb.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.event.UserEvent;
import ru.job4j.bmb.model.*;
import ru.job4j.bmb.repository.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());
    private final MoodRepository moodRepository;
    private final AwardRepository awardRepository;
    private final ApplicationEventPublisher publisher;

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       MoodRepository moodRepository,
                       ApplicationEventPublisher publisher,
                       AwardRepository awardRepository) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.moodRepository = moodRepository;
        this.publisher = publisher;
        this.awardRepository = awardRepository;
    }

    public Content chooseMood(User user, Long moodId) {
        Optional<Mood> mood = moodRepository.findById(moodId);
        if (mood.isPresent()) {
            MoodLog moodLog = new MoodLog();
            moodLog.setUser(user);
            moodLog.setMood(mood.get());
            moodLog.setCreatedAt(System.currentTimeMillis());
            moodLogRepository.save(moodLog);
            publisher.publishEvent(new UserEvent(this, user));
        }
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        Optional<User> user;
        long threshHold = System.currentTimeMillis() - 7 * 24 * 3600 * 1000;
        user = userRepository.findByClientId(clientId);
        String formatedLog = null;
        if (user.isPresent()) {
            formatedLog = formatMoodLogs(moodLogRepository.findAll().stream()
                    .filter(item -> item.getUser().equals(user.get()))
                    .filter(item -> item.getCreatedAt() >= threshHold)
                    .toList(), "week log");
        }
        Content content = new Content(chatId);
        content.setText(formatedLog);
        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        Optional<User> user;
        long threshHold = System.currentTimeMillis() - 30 * 24 * 3600 * 1000;
        user = userRepository.findByClientId(clientId);
        String formatedLog = null;
        if (user.isPresent()) {
            formatedLog = formatMoodLogs(moodLogRepository.findAll().stream()
                    .filter(item -> item.getUser().equals(user.get()))
                    .filter(item -> item.getCreatedAt() >= threshHold)
                    .toList(), "month log");
        }
        Content content = new Content(chatId);
        content.setText(formatedLog);
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    private String formatAwardLogs(List<Achievement> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo awards logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreateAt()));
            sb.append(formattedDate).append(": ").append(log.getAward().getTitle()).append("\n");
        });
        return sb.toString();
    }

    public Optional<Content> awards(long chatId, Long clientId) {
        Optional<User> user;
        String awardLog = null;
        user = userRepository.findByClientId(clientId);
        if (user.isPresent()) {
            awardLog = formatAwardLogs(achievementRepository.findAll().stream()
                    .filter(item -> item.getUser().equals(user.get()))
                    .toList(), "awards log");

        }
        Content content = new Content(chatId);
        content.setText(awardLog);
        return Optional.of(content);
    }

    public long getGoodDays(User user) {
        if (user != null) {
            return moodLogRepository
                    .findByUserIdOrderByCreatedAtDesc(user.getId())
                    .takeWhile(item -> item.getMood()
                            .isGood())
                    .count();
        }
        return -1;
    }

    public void getAchievements(User user, long count) {
        for (var award : awardRepository.findAll()) {
            if (award.getDays() <= count) {
                Achievement achievement = new Achievement();
                achievement.setAward(award);
                achievement.setUser(user);
                achievement.setCreateAt(System.currentTimeMillis());
                achievementRepository.save(achievement);
            }
        }
    }

}
