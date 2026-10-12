package io.github.quizup.leaderboard.infrastructure.out.persistence.repository;

import io.github.quizup.leaderboard.infrastructure.out.persistence.entity.PlayerIdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerIdentityJpaRepository extends JpaRepository<PlayerIdentityEntity, String> {
}
