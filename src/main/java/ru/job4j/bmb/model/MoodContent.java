package ru.job4j.bmb.model;

import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "mb_mood_content")
public class MoodContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mood_id")
    private Mood mood;

    private String text;

    public MoodContent(Mood mood, String s) {
        this.mood = mood;
        this.text = s;
    }

    public MoodContent() {
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MoodContent that = (MoodContent) o;
        return Objects.equals(id, that.id) && Objects.equals(mood, that.mood) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mood, text);
    }
}
