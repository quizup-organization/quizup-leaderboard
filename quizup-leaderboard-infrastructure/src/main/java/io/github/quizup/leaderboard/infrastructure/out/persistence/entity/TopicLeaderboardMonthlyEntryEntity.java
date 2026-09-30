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
 * TopicLeaderboardMonthlyEntryEntity — projection JPA du classement **mensuel** d'un joueur
 * dans un thème pour un mois donné ({@code YYYY-MM}) : l'historique des mois est conservé.
 */
@Setter
@Getter
@Entity
@Table(name = "topic_leaderboard_monthly_entry", indexes = {
        @Index(name = "idx_topic_lb_monthly_xp", columnList = "topic_id,month,monthly_xp"),
        @Index(name = "idx_topic_lb_monthly_country", columnList = "topic_id,month,country,monthly_xp")
})
public class TopicLeaderboardMonthlyEntryEntity {

    @Id
    @Column(name = "entry_id", length = 550, nullable = false)
    private String entryId;

    @Column(name = "topic_id", length = 255, nullable = false)
    private String topicId;

    @Column(name = "user_id", length = 255, nullable = false)
    private String userId;

    @Column(name = "month", length = 7, nullable = false)
    private String month;

    @Column(name = "monthly_xp", nullable = false)
    private int monthlyXp;

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
