package io.github.quizup.leaderboard.domain.model;

/**
 * Identité publique d'un joueur, résolue auprès de quizup-profile puis **dénormalisée** dans le
 * read model du classement (rafraîchie sur {@code ProfileUpdatedEvent}).
 */
public record PlayerIdentity(
        String userId,
        String displayName,
        String country,
        String avatarOptions
) {
}
