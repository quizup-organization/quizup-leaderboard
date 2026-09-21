package io.github.quizup.leaderboard.infrastructure.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Journal des XP de classement déjà pris en compte (idempotence au rejeu).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "topic_leaderboard_awarded_game")
@IdClass(LeaderboardAwardedGameEntity.AwardedGameId.class)
public class LeaderboardAwardedGameEntity {

    @Id
    @Column(name = "topic_id", length = 255, nullable = false)
    private String topicId;

    @Id
    @Column(name = "user_id", length = 255, nullable = false)
    private String userId;

    @Id
    @Column(name = "game_id", length = 255, nullable = false)
    private String gameId;

    public LeaderboardAwardedGameEntity(String topicId, String userId, String gameId) {
        this.topicId = topicId;
        this.userId = userId;
        this.gameId = gameId;
    }

    /** Clé composite {@code (topic_id, user_id, game_id)}. */
    @Getter
    @Setter
    public static class AwardedGameId implements Serializable {

        private String topicId;
        private String userId;
        private String gameId;

        public AwardedGameId() {
        }

        public AwardedGameId(String topicId, String userId, String gameId) {
            this.topicId = topicId;
            this.userId = userId;
            this.gameId = gameId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof AwardedGameId that)) {
                return false;
            }
            return Objects.equals(topicId, that.topicId)
                    && Objects.equals(userId, that.userId)
                    && Objects.equals(gameId, that.gameId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(topicId, userId, gameId);
        }
    }
}
