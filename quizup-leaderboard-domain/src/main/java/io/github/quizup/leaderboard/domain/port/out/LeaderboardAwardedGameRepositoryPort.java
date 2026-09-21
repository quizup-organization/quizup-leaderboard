package io.github.quizup.leaderboard.domain.port.out;

/**
 * Port sortant - journal des XP de classement déjà enregistrés (clé
 * {@code topicId + userId + gameId}). Rend la projection idempotente au rejeu.
 */
public interface LeaderboardAwardedGameRepositoryPort {

    /**
     * @return {@code true} si c'est un nouvel enregistrement (à appliquer), {@code false} sinon.
     */
    boolean record(String topicId, String userId, String gameId);
}
