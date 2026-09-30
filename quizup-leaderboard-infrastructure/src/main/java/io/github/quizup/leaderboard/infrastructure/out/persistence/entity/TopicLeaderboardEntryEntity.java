package io.github.quizup.leaderboard.infrastructure.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * TopicLeaderboardEntryEntity — projection JPA du classement **all-time** d'un joueur
 * dans un thème : XP cumulée + identité/niveau dénormalisés.
 */
@Setter
@Getter
@Entity
@Table(name = "topic_leaderboard_entry", indexes = {
        @Index(name = "idx_topic_lb_all_time", columnList = "topic_id,total_xp"),
        @Index(name = "idx_topic_lb_country", columnList = "topic_id,country,total_xp")
})
public class TopicLeaderboardEntryEntity {

    @Id
    @Column(name = "entry_id", length = 520, nullable = false)
    private String entryId;

    @Column(name = "topic_id", length = 255, nullable = false)
    private String topicId;

    @Column(name = "user_id", length = 255, nullable = false)
    private String userId;

    @Column(name = "total_xp", nullable = false)
    private int totalXp;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "pseudonym", length = 255)
    private String pseudonym;

    @Column(name = "avatar_options", columnDefinition = "text")
    private String avatarOptions;

    @Column(name = "country", length = 60)
    private String country;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
