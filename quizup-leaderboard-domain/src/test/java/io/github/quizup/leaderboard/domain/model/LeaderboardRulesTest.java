package io.github.quizup.leaderboard.domain.model;

import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaderboardRulesTest {

    @Test
    void monthKey_isYearMonth() {
        assertEquals("2026-09", LeaderboardRules.monthKey(YearMonth.of(2026, 9)));
        assertEquals("2026-01", LeaderboardRules.monthKey(YearMonth.of(2026, 1)));
    }

    @Test
    void levelFor_startsAtOneAndGrowsByHundreds() {
        assertEquals(1, LeaderboardRules.levelFor(0));
        assertEquals(1, LeaderboardRules.levelFor(99));
        assertEquals(2, LeaderboardRules.levelFor(100));
        assertEquals(3, LeaderboardRules.levelFor(400));
    }
}
