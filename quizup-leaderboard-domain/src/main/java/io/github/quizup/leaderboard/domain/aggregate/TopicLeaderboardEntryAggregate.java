package io.github.quizup.leaderboard.domain.aggregate;

import io.github.quizup.leaderboard.domain.command.LeaderboardCommand;
import io.github.quizup.leaderboard.domain.event.LeaderboardEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.HashSet;
import java.util.Set;

/**
 * TopicLeaderboardEntryAggregate — classement d'un joueur **dans un thème** :
 * XP all-time et XP du mois courant (réinitialisée au changement de mois).
 *
 * <p>Identifiant {@code topicId::userId} (namespacé pour qu'un joueur ait une
 * entrée par thème). Créé à la volée au premier {@code RecordXpCommand}
 * ({@link AggregateCreationPolicy#CREATE_IF_MISSING}), idempotent par {@code gameId}.</p>
 */
@Aggregate
public class TopicLeaderboardEntryAggregate {

    @AggregateIdentifier
    private String entryId;

    private String topicId;
    private String userId;

    private final Set<String> awardedGameIds = new HashSet<>();

    private int totalXp;
    private int monthlyXp;
    private String month;
    private String country;

    protected TopicLeaderboardEntryAggregate() {
    }

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void handle(LeaderboardCommand.RecordXpCommand command) {
        if (this.entryId == null) {
            this.entryId = command.entryId();
        }
        if (this.topicId == null) {
            this.topicId = command.topicId();
        }
        if (this.userId == null) {
            this.userId = command.userId();
        }

        if (awardedGameIds.contains(command.gameId())) {
            return;
        }

        AggregateLifecycle.apply(new LeaderboardEvent.XpRecordedEvent(
                command.entryId(),
                command.topicId(),
                command.userId(),
                command.gameId(),
                command.xp(),
                command.month(),
                command.country(),
                command.occurredAt()
        ));
    }

    @EventSourcingHandler
    public void on(LeaderboardEvent.XpRecordedEvent event) {
        this.entryId = event.entryId();
        this.topicId = event.topicId();
        this.userId = event.userId();

        if (!event.month().equals(this.month)) {
            this.month = event.month();
            this.monthlyXp = 0;
        }

        this.totalXp += event.xp();
        this.monthlyXp += event.xp();
        this.awardedGameIds.add(event.gameId());
        this.country = event.country();
    }
}
