package io.github.quizup.leaderboard.domain.model;

/**
 * Identité publique d'un joueur, résolue auprès de quizup-profile pour
 * l'affichage du classement (le read model ne stocke que les XP).
 */
public record PlayerIdentity(
        String userId,
        String displayName,
        String country
) {
}
