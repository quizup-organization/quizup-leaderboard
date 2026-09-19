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
 * TopicLeaderboardEntryEntity — projection JPA du classement d'un joueur dans un
 * thème : XP all-time et XP du mois courant.
 */
@Setter
@Getter
@Entity
@Table(name = "topic_leaderboard_entry", indexes = {
        @Index(name = "idx_topic_lb_all_time", columnList = "topic_id,total_xp"),
        @Index(name = "idx_topic_lb_monthly", columnList = "topic_id,month,monthly_xp")
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

    @Column(name = "monthly_xp", nullable = false)
    private int monthlyXp;

    @Column(name = "month", length = 7)
    private String month;

    @Column(name = "country", length = 60)
    private String country;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
