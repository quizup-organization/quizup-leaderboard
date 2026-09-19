package io.github.quizup.leaderboard.domain.query;

import java.util.List;

public interface LeaderboardQuery {

    /**
     * Top d'un thème. {@code monthly} = classement du mois courant, sinon all-time.
     * {@code memberIds} et {@code country} filtrent la portée (amis / pays) —
     * {@code null} = pas de filtre (monde).
     */
    record TopByTopicQuery(
            String topicId,
            boolean monthly,
            String month,
            int limit,
            List<String> memberIds,
            String country
    ) implements LeaderboardQuery {
    }

    /**
     * Rang d'un joueur dans un thème (all-time ou mois courant), dans une portée donnée.
     */
    record GetTopicRankQuery(
            String topicId,
            String userId,
            boolean monthly,
            String month,
            List<String> memberIds,
            String country
    ) implements LeaderboardQuery {
    }
}
