package ru.job4j.bmb.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();

    @Query("""
    SELECT u FROM User u
    WHERE u.id NOT IN (
        SELECT m.user.id FROM MoodLog m
        WHERE m.createdAt BETWEEN ?1 AND ?2
    )
""")
    List<User> findUsersWhoDidNotVoteToday(long startOfDay, long endOfDay);

    public List<MoodLog> findByUserId(Long userId);

    public Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    public List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart);

    public List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart);
}
