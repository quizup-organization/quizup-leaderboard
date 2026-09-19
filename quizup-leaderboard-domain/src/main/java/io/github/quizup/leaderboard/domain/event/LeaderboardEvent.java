package io.github.quizup.leaderboard.domain.event;

import java.time.Instant;

public interface LeaderboardEvent {

    /** Identifiant de l'entrée (thème + joueur). */
    String entryId();

    /**
     * XP enregistrée pour un duel dans un thème (un événement par joueur,
     * thème et duel).
     */
    record XpRecordedEvent(
            String entryId,
            String topicId,
            String userId,
            String gameId,
            int xp,
            String month,
            String country,
            Instant recordedAt
    ) implements LeaderboardEvent {
    }
}
