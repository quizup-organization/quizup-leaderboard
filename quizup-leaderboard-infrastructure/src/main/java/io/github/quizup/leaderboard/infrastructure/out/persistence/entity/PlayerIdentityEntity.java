package io.github.quizup.leaderboard.infrastructure.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Identité publique locale d'un joueur (pseudonyme, pays, avatar), alimentée par les événements
 * profil — remplace la résolution cross-service bloquante dans les projections du classement.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "player_identity_entry")
public class PlayerIdentityEntity {

    @Id
    @Column(name = "user_id", length = 255, nullable = false)
    private String userId;

    @Column(name = "pseudonym", length = 255)
    private String pseudonym;

    @Column(name = "country", length = 60)
    private String country;

    @Column(name = "avatar_options")
    private String avatarOptions;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
