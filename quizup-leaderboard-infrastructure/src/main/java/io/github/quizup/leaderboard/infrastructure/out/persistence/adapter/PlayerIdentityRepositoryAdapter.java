package io.github.quizup.leaderboard.infrastructure.out.persistence.adapter;

import io.github.quizup.leaderboard.domain.model.PlayerIdentity;
import io.github.quizup.leaderboard.domain.port.out.PlayerIdentityRepositoryPort;
import io.github.quizup.leaderboard.infrastructure.out.persistence.entity.PlayerIdentityEntity;
import io.github.quizup.leaderboard.infrastructure.out.persistence.repository.PlayerIdentityJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
public class PlayerIdentityRepositoryAdapter implements PlayerIdentityRepositoryPort {

    private final PlayerIdentityJpaRepository repository;

    public PlayerIdentityRepositoryAdapter(PlayerIdentityJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerIdentity> findById(String userId) {
        return repository.findById(userId).map(PlayerIdentityRepositoryAdapter::toDomain);
    }

    @Override
    @Transactional
    public void save(PlayerIdentity identity, Instant updatedAt) {
        PlayerIdentityEntity entity = repository.findById(identity.userId())
                .orElseGet(PlayerIdentityEntity::new);
        entity.setUserId(identity.userId());
        entity.setPseudonym(identity.pseudonym());
        entity.setCountry(identity.country());
        entity.setAvatarOptions(identity.avatarOptions());
        entity.setUpdatedAt(updatedAt);
        repository.save(entity);
    }

    private static PlayerIdentity toDomain(PlayerIdentityEntity entity) {
        return new PlayerIdentity(
                entity.getUserId(),
                entity.getPseudonym(),
                entity.getCountry(),
                entity.getAvatarOptions());
    }
}
