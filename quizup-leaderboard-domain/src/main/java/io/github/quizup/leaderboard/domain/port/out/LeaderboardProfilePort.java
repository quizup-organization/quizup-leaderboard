package io.github.quizup.leaderboard.domain.port.out;

import io.github.quizup.leaderboard.domain.model.PlayerIdentity;

import java.util.List;

/**
 * Port sortant inter-module : résout les identités publiques (quizup-profile).
 */
public interface LeaderboardProfilePort {

    List<PlayerIdentity> findIdentities(List<String> userIds);
}
