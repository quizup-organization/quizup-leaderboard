package io.github.quizup.leaderboard.domain.exception;

import io.github.quizup.microservice.core.domain.exception.BaseProblem;
import io.github.quizup.microservice.core.domain.exception.ProblemCategory;

import java.util.Map;

public interface LeaderboardProblems {

    class LeaderboardEntryNotFoundProblem extends BaseProblem {
        public LeaderboardEntryNotFoundProblem(String userId) {
            super("urn:quizup:leaderboard:notFound",
                    ProblemCategory.BUSINESS_RESOURCE_MISSING,
                    "Leaderboard entry not found",
                    "No leaderboard entry was found for user " + userId,
                    Map.of("userId", userId));
        }
    }
}
