package io.github.quizup.leaderboard.infrastructure.out.persistence.adapter;

import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardRepositoryPort;
import io.github.quizup.leaderboard.infrastructure.out.persistence.entity.TopicLeaderboardEntryEntity;
import io.github.quizup.leaderboard.infrastructure.out.persistence.mapper.TopicLeaderboardEntryMapper;
import io.github.quizup.leaderboard.infrastructure.out.persistence.repository.TopicLeaderboardEntryJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TopicLeaderboardRepositoryAdapter implements LeaderboardRepositoryPort {

    private final TopicLeaderboardEntryJpaRepository repository;

    public TopicLeaderboardRepositoryAdapter(TopicLeaderboardEntryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(TopicLeaderboardEntry entry) {
        repository.save(TopicLeaderboardEntryMapper.toEntity(entry));
    }

    @Override
    public Optional<TopicLeaderboardEntry> findByTopicAndUser(String topicId, String userId) {
        return repository.findByTopicIdAndUserId(topicId, userId)
                .map(TopicLeaderboardEntryMapper::toDomain);
    }

    @Override
    public List<TopicLeaderboardEntry> findTop(String topicId,
                                               boolean monthly,
                                               String month,
                                               List<String> memberIds,
                                               String country,
                                               int limit) {
        PageRequest page = PageRequest.of(0, limit);

        List<TopicLeaderboardEntryEntity> entities;

        if (memberIds != null) {
            entities = monthly
                    ? repository.findTopByTopicMonthlyForUsers(topicId, month, memberIds, page)
                    : repository.findTopByTopicAllTimeForUsers(topicId, memberIds, page);
        } else if (country != null) {
            entities = monthly
                    ? repository.findTopByTopicMonthlyForCountry(topicId, month, country, page)
                    : repository.findTopByTopicAllTimeForCountry(topicId, country, page);
        } else {
            entities = monthly
                    ? repository.findTopByTopicMonthly(topicId, month, page)
                    : repository.findTopByTopicAllTime(topicId, page);
        }

        return entities.stream()
                .map(TopicLeaderboardEntryMapper::toDomain)
                .toList();
    }
}
