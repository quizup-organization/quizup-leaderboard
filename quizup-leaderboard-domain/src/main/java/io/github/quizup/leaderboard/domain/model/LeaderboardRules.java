package io.github.quizup.leaderboard.domain.model;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;

/**
 * Règles pures du classement : clé de mois (reset mensuel) et niveau dérivé
 * de l'XP totale (même barème que la progression `quizup-profile`).
 */
public final class LeaderboardRules {

    private static final int XP_PER_LEVEL_UNIT = 100;

    private LeaderboardRules() {
    }

    /** Clé de mois au format {@code YYYY-MM} (base du classement mensuel). */
    public static String monthKey(YearMonth yearMonth) {
        return yearMonth.toString();
    }

    /** Clé de mois d'un instant (UTC). */
    public static String monthKey(Instant instant) {
        return monthKey(YearMonth.from(instant.atZone(ZoneOffset.UTC)));
    }

    /** Mois courant {@code YYYY-MM}. */
    public static String currentMonth() {
        return monthKey(YearMonth.now());
    }

    /**
     * Identifiant d'agrégat d'une entrée de classement (thème + joueur).
     * Namespacé : un même joueur a une entrée par thème.
     */
    public static String entryId(String topicId, String userId) {
        return topicId + "::" + userId;
    }

    /** Niveau atteint pour une XP totale cumulée (démarre à 1). */
    public static int levelFor(int xpTotal) {
        if (xpTotal <= 0) {
            return 1;
        }

        return 1 + (int) Math.floor(Math.sqrt(xpTotal / (double) XP_PER_LEVEL_UNIT));
    }
}
