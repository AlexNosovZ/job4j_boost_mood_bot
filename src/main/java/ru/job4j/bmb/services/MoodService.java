package ru.job4j.bmb.services;

import org.springframework.stereotype.Service;
import ru.job4j.bmb.content.Content;
import ru.job4j.bmb.model.Award;
import ru.job4j.bmb.model.Mood;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;
import ru.job4j.bmb.repository.AchievementRepository;
import ru.job4j.bmb.repository.MoodLogRepository;
import ru.job4j.bmb.repository.MoodRepository;
import ru.job4j.bmb.repository.UserRepository;

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

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       MoodRepository moodRepository) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.moodRepository = moodRepository;
    }

    public void chooseMood(User user, Long moodId) {
        Optional<Mood> mood = moodRepository.findById(moodId);
        if (mood.isPresent()) {
            MoodLog moodLog = new MoodLog();
            moodLog.setUser(user);
            moodLog.setMood(mood.get());
            moodLog.setCreatedAt(System.currentTimeMillis());
            moodLogRepository.save(moodLog);
        }
    }

    public MoodLogRepository weekMoodLogCommand(long chatId, Long clientId) {
        MoodLogRepository localRep = null;
        Optional<User> user;
        long threshHold = System.currentTimeMillis() - 7 * 24 * 3600 * 1000;
        user = userRepository.findByClientId(clientId);
        if (user.isPresent()) {
            for (var item : moodLogRepository.findAll()) {
                if (item.getUser().equals(user) && item.getCreatedAt() >= threshHold) {
                    localRep.save(item);
                }
            }
        }
        return localRep;
    }

    public MoodLogRepository monthMoodLogCommand(long chatId, Long clientId) {
        MoodLogRepository localRep = null;
        Optional<User> user;
        long threshHold = System.currentTimeMillis() - 30 * 7 * 24 * 3600 * 1000;
        user = userRepository.findByClientId(clientId);
        if (user.isPresent()) {
            for (var item : moodLogRepository.findAll()) {
                if (item.getUser().equals(user) && item.getCreatedAt() >= threshHold) {
                    localRep.save(item);
                }
            }
        }
        return localRep;
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

    public List<Award> awards(long chatId, Long clientId) {
        List<Award> awards = null;
        User user = null;
        for (var tmpUser : userRepository.findAll()) {
            if (tmpUser != null && tmpUser.getChatId() == chatId) {
                user = tmpUser;
                break;
            }
        }
        if (user != null) {
            for (var item : achievementRepository.findAll()) {
                if (item.getUser().equals(user)) {
                    awards.add(item.getAward());
                }
            }
        }
        return awards;
    }
}
