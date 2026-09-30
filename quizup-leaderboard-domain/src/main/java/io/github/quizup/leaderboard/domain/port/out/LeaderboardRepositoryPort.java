package io.github.quizup.leaderboard.domain.port.out;

import io.github.quizup.leaderboard.domain.model.LeaderboardPage;
import io.github.quizup.leaderboard.domain.model.LeaderboardRank;
import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;

import java.util.List;
import java.util.Optional;

/**
 * Port sortant — lecture/écriture des projections de classement par thème
 * (entrée all-time et entrées mensuelles).
 */
public interface LeaderboardRepositoryPort {

    /** Enregistre l'entrée all-time (XP cumulée + identité/niveau). */
    void saveAllTime(TopicLeaderboardEntry entry);

    /** Enregistre l'entrée mensuelle ({@code month} renseigné) d'un joueur. */
    void saveMonthly(TopicLeaderboardEntry entry);

    Optional<TopicLeaderboardEntry> findByTopicAndUser(String topicId, String userId);

    Optional<TopicLeaderboardEntry> findMonthlyByTopicAndUser(String topicId, String userId, String month);

    /**
     * Rafraîchit le pseudonyme public d'un joueur sur toutes ses entrées (all-time et mensuelles),
     * quel que soit le thème.
     */
    void refreshPseudonym(String userId, String pseudonym);

    /** Rafraîchit le pays d'un joueur sur toutes ses entrées (all-time et mensuelles). */
    void refreshCountry(String userId, String country);

    /** Rafraîchit les options d'avatar d'un joueur sur toutes ses entrées (all-time et mensuelles). */
    void refreshAvatarOptions(String userId, String avatarOptions);

    /**
     * Page du classement d'un thème, filtrée par portée : {@code memberIds} (amis) et/ou
     * {@code country} (pays). {@code null} = pas de filtre (monde).
     */
    LeaderboardPage findPage(
            String topicId,
            boolean monthly,
            String month,
            List<String> memberIds,
            String country,
            int page,
            int size
    );

    /**
     * Rang (1-based) d'un joueur dans la portée demandée. Vide si le joueur n'a pas d'entrée
     * ou n'appartient pas à la portée (mois sans XP, hors abonnements, hors pays).
     */
    Optional<LeaderboardRank> findRank(
            String topicId,
            String userId,
            boolean monthly,
            String month,
            List<String> memberIds,
            String country
    );
}
