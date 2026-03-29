package ru.job4j.bmb.model;

import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "mb_mood")
public class Mood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private boolean good;

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

    public boolean isGood() {
        return good;
    }

    public void setGood(boolean good) {
        this.good = good;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Mood mood = (Mood) o;
        return good == mood.good && Objects.equals(id, mood.id) && Objects.equals(text, mood.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, good);
    }
}