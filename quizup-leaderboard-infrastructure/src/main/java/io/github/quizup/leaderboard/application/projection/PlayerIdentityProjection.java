package io.github.quizup.leaderboard.application.projection;

import io.github.quizup.leaderboard.domain.model.PlayerIdentity;
import io.github.quizup.leaderboard.domain.port.out.PlayerIdentityRepositoryPort;
import io.github.quizup.profile.domain.event.ProfileEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.function.UnaryOperator;

/**
 * Read model local d'identité publique : le classement dénormalise pseudonyme, pays et avatar
 * depuis les événements du profil, sans jamais interroger {@code quizup-profile} en bloquant
 * (voir {@code best-practices/.backend/anti-patterns.md}). La fusion champ par champ préserve les
 * valeurs déjà matérialisées lors d'un rejeu.
 */
@Component
@ProcessingGroup("player-identity-projection")
public class PlayerIdentityProjection {

    private static final Logger logger = LoggerFactory.getLogger(PlayerIdentityProjection.class);

    private final PlayerIdentityRepositoryPort playerIdentityRepositoryPort;

    public PlayerIdentityProjection(PlayerIdentityRepositoryPort playerIdentityRepositoryPort) {
        this.playerIdentityRepositoryPort = playerIdentityRepositoryPort;
    }

    @EventHandler
    @Transactional
    public void on(ProfileEvent.ProfileCreatedEvent event) {
        upsert(event.userId(), event.createdAt(), identity -> new PlayerIdentity(
                identity.userId(), event.pseudonym(), identity.country(), identity.avatarOptions()));
        logger.debug("Identité locale créée: userId={}", event.userId());
    }

    @EventHandler
    @Transactional
    public void on(ProfileEvent.ProfilePseudonymUpdatedEvent event) {
        upsert(event.userId(), event.updatedAt(), identity -> new PlayerIdentity(
                identity.userId(), event.pseudonym(), identity.country(), identity.avatarOptions()));
    }

    @EventHandler
    @Transactional
    public void on(ProfileEvent.ProfileCountryUpdatedEvent event) {
        upsert(event.userId(), event.updatedAt(), identity -> new PlayerIdentity(
                identity.userId(), identity.pseudonym(), event.country(), identity.avatarOptions()));
    }

    @EventHandler
    @Transactional
    public void on(ProfileEvent.ProfileAvatarUpdatedEvent event) {
        upsert(event.userId(), event.updatedAt(), identity -> new PlayerIdentity(
                identity.userId(), identity.pseudonym(), identity.country(), event.avatarOptions()));
    }

    private void upsert(String userId, Instant updatedAt, UnaryOperator<PlayerIdentity> merge) {
        PlayerIdentity current = playerIdentityRepositoryPort.findById(userId)
                .orElseGet(() -> new PlayerIdentity(userId, null, null, null));
        playerIdentityRepositoryPort.save(merge.apply(current), updatedAt);
    }
}
