package io.github.quizup.leaderboard.infrastructure.in.api;

import io.github.quizup.leaderboard.domain.model.LeaderboardScope;
import io.github.quizup.leaderboard.domain.port.in.GetLeaderboardUseCase;
import io.github.quizup.leaderboard.infrastructure.in.api.mapper.LeaderboardResponseMapper;
import io.github.quizup.leaderboard.infrastructure.in.api.response.TopicLeaderboardEntryResponse;
import io.github.quizup.microservice.security.SecurityHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * LeaderboardController — classement **par thème** :
 * all-time (général) et mensuel (reset au 1er du mois),
 * portée monde / amis / pays.
 */
@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    private static final int MAX_LIMIT = 100;

    private final GetLeaderboardUseCase getLeaderboardUseCase;

    public LeaderboardController(GetLeaderboardUseCase getLeaderboardUseCase) {
        this.getLeaderboardUseCase = getLeaderboardUseCase;
    }

    /**
     * Top du classement d'un thème. {@code period} = all-time (défaut) · monthly ;
     * {@code scope} = world (défaut) · friends · country.
     */
    @GetMapping("/topics/{topicId}")
    public CompletableFuture<ResponseEntity<List<TopicLeaderboardEntryResponse>>> topByTopic(
            @PathVariable String topicId,
            @RequestParam(defaultValue = "all-time") String period,
            @RequestParam(defaultValue = "world") String scope,
            @RequestParam(defaultValue = "50") int limit) {
        int bounded = Math.min(Math.max(limit, 1), MAX_LIMIT);
        String requesterId = SecurityHelper.getUserId();

        return getLeaderboardUseCase.topByTopic(
                        topicId,
                        "monthly".equalsIgnoreCase(period),
                        bounded,
                        parseScope(scope),
                        requesterId)
                .thenApply(LeaderboardResponseMapper::toResponse)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Rang du joueur courant dans un thème (204 si aucun duel joué dans ce thème).
     */
    @GetMapping("/topics/{topicId}/me")
    public CompletableFuture<ResponseEntity<TopicLeaderboardEntryResponse>> myRank(
            @PathVariable String topicId,
            @RequestParam(defaultValue = "all-time") String period,
            @RequestParam(defaultValue = "world") String scope) {
        String userId = SecurityHelper.getUserId();

        return getLeaderboardUseCase.rankOf(
                        topicId,
                        userId,
                        "monthly".equalsIgnoreCase(period),
                        parseScope(scope))
                .thenApply(optional -> optional
                        .map(ranked -> ResponseEntity.ok(LeaderboardResponseMapper.toResponse(ranked)))
                        .orElseGet(() -> ResponseEntity.<TopicLeaderboardEntryResponse>noContent().build()));
    }

    private LeaderboardScope parseScope(String scope) {
        String normalized = scope.toUpperCase();
        if ("FOLLOWING".equals(normalized)) {
            return LeaderboardScope.FRIENDS;
        }
        try {
            return LeaderboardScope.valueOf(normalized);
        } catch (IllegalArgumentException exception) {
            return LeaderboardScope.WORLD;
        }
    }
}
