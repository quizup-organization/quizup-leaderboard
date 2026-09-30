package io.github.quizup.leaderboard.domain.model;

/**
 * Identité publique d'un joueur, résolue auprès de quizup-profile puis **dénormalisée** dans le
 * read model du classement (rafraîchie par les événements de champ du profil).
 */
public record PlayerIdentity(
        String userId,
        String pseudonym,
        String country,
        String avatarOptions
) {
}
