package io.github.quizup.leaderboard.domain.port.out;

/**
 * Port sortant des KPI métier du classement.
 *
 * <p>Implémenté en infrastructure avec Micrometer. Types JDK uniquement (règle hexagonale).
 */
public interface LeaderboardMetricsPort {

    /**
     * Une entrée de classement a enregistré de l'XP.
     *
     * @param topicId thème concerné
     * @param xp      XP enregistrée
     */
    void xpRecorded(String topicId, int xp);
}
