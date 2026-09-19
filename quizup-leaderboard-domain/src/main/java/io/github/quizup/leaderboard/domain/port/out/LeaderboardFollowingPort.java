package io.github.quizup.leaderboard.domain.port.out;

import java.util.List;

/**
 * Port sortant inter-module : identifiants des joueurs **suivis** (quizup-social), pour la
 * portée « abonnements » du classement.
 */
public interface LeaderboardFollowingPort {

    List<String> getFollowingIds(String userId);
}
