package io.github.quizup.leaderboard.application.handler.event;

import io.github.quizup.identity.domain.event.UserEvent.UserAccountDeletedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Retire un compte supprimé de tous les classements et de l'identité locale. */
@Component
@ProcessingGroup("account-deletion-leaderboard")
public class AccountDeletionHandler {

    private final JdbcTemplate jdbcTemplate;

    public AccountDeletionHandler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventHandler
    @Transactional
    public void on(UserAccountDeletedEvent event) {
        String userId = event.userId();
        jdbcTemplate.update("DELETE FROM topic_leaderboard_entry WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM topic_leaderboard_monthly_entry WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM topic_leaderboard_awarded_game WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM player_identity_entry WHERE user_id = ?", userId);
    }
}
