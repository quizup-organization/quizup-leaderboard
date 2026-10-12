package io.github.quizup.leaderboard.application.projection;

import io.github.quizup.leaderboard.domain.model.PlayerIdentity;
import io.github.quizup.leaderboard.domain.port.out.PlayerIdentityRepositoryPort;
import io.github.quizup.profile.domain.event.ProfileEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test du read model local d'identité : alimenté par les événements profil, sans aucune query
 * cross-service bloquante (l'ancien {@code findIdentities(...).join()} est interdit — voir
 * {@code best-practices/.backend/anti-patterns.md}).
 */
class PlayerIdentityProjectionTest {

    private final PlayerIdentityRepositoryPort repository = mock(PlayerIdentityRepositoryPort.class);
    private final PlayerIdentityProjection projection = new PlayerIdentityProjection(repository);

    @Test
    void profileCreated_storesPseudonym() {
        when(repository.findById("user-1")).thenReturn(Optional.empty());

        Instant createdAt = Instant.parse("2026-09-01T10:00:00Z");
        projection.on(new ProfileEvent.ProfileCreatedEvent("user-1", "user-1@quizup.test", "Alicia", createdAt));

        ArgumentCaptor<PlayerIdentity> identity = ArgumentCaptor.forClass(PlayerIdentity.class);
        verify(repository).save(identity.capture(), eq(createdAt));
        assertThat(identity.getValue().pseudonym()).isEqualTo("Alicia");
        assertThat(identity.getValue().country()).isNull();
        assertThat(identity.getValue().avatarOptions()).isNull();
    }

    @Test
    void pseudonymUpdated_keepsOtherFields() {
        when(repository.findById("user-1"))
                .thenReturn(Optional.of(new PlayerIdentity("user-1", "Ancien", "FR", "{\"hair\":\"full\"}")));

        Instant updatedAt = Instant.parse("2026-09-20T10:00:00Z");
        projection.on(new ProfileEvent.ProfilePseudonymUpdatedEvent("user-1", "user-1", "Alicia", updatedAt));

        ArgumentCaptor<PlayerIdentity> identity = ArgumentCaptor.forClass(PlayerIdentity.class);
        verify(repository).save(identity.capture(), eq(updatedAt));
        assertThat(identity.getValue().pseudonym()).isEqualTo("Alicia");
        assertThat(identity.getValue().country()).isEqualTo("FR");
        assertThat(identity.getValue().avatarOptions()).isEqualTo("{\"hair\":\"full\"}");
    }

    @Test
    void countryUpdated_keepsOtherFields() {
        when(repository.findById("user-1"))
                .thenReturn(Optional.of(new PlayerIdentity("user-1", "Alicia", "BE", null)));

        Instant updatedAt = Instant.parse("2026-09-20T10:00:00Z");
        projection.on(new ProfileEvent.ProfileCountryUpdatedEvent("user-1", "user-1", "FR", updatedAt));

        ArgumentCaptor<PlayerIdentity> identity = ArgumentCaptor.forClass(PlayerIdentity.class);
        verify(repository).save(identity.capture(), eq(updatedAt));
        assertThat(identity.getValue().country()).isEqualTo("FR");
        assertThat(identity.getValue().pseudonym()).isEqualTo("Alicia");
    }

    @Test
    void avatarUpdated_createsMissingRow() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        Instant updatedAt = Instant.parse("2026-09-20T10:00:00Z");
        projection.on(new ProfileEvent.ProfileAvatarUpdatedEvent(
                "user-1", "user-1", "{\"hair\":\"full\"}", updatedAt));

        ArgumentCaptor<PlayerIdentity> identity = ArgumentCaptor.forClass(PlayerIdentity.class);
        verify(repository).save(identity.capture(), eq(updatedAt));
        assertThat(identity.getValue().avatarOptions()).isEqualTo("{\"hair\":\"full\"}");
    }
}
