package io.github.quizup.leaderboard.domain.port.out;

import io.github.quizup.leaderboard.domain.model.PlayerIdentity;

import java.time.Instant;
import java.util.Optional;

/**
 * Port sortant local du read model d'identité publique. Alimenté par les événements du profil
 * (création + mises à jour de champ) : les projections et les queries du classement lisent
 * l'identité <b>en local</b>, sans query cross-service bloquante sur un thread d'event processor
 * (voir {@code best-practices/.backend/anti-patterns.md}).
 */
public interface PlayerIdentityRepositoryPort {

    Optional<PlayerIdentity> findById(String userId);

    void save(PlayerIdentity identity, Instant updatedAt);
}
