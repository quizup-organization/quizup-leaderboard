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
    void monthlyEntryId_isNamespacedByMonth() {
        assertEquals("topic-1::user-1::2026-09",
                LeaderboardRules.monthlyEntryId("topic-1", "user-1", "2026-09"));
        assertEquals("topic-1::user-1",
                LeaderboardRules.entryId("topic-1", "user-1"));
    }
}
