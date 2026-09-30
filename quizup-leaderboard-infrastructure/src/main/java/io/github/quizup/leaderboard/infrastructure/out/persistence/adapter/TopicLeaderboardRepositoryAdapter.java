package io.github.quizup.leaderboard.infrastructure.out.persistence.adapter;

import io.github.quizup.leaderboard.domain.model.LeaderboardPage;
import io.github.quizup.leaderboard.domain.model.LeaderboardRank;
import io.github.quizup.leaderboard.domain.model.TopicLeaderboardEntry;
import io.github.quizup.leaderboard.domain.port.out.LeaderboardRepositoryPort;
import io.github.quizup.leaderboard.infrastructure.out.persistence.mapper.TopicLeaderboardEntryMapper;
import io.github.quizup.leaderboard.infrastructure.out.persistence.repository.TopicLeaderboardEntryJpaRepository;
import io.github.quizup.leaderboard.infrastructure.out.persistence.repository.TopicLeaderboardMonthlyEntryJpaRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class TopicLeaderboardRepositoryAdapter implements LeaderboardRepositoryPort {

    private final TopicLeaderboardEntryJpaRepository allTimeRepository;
    private final TopicLeaderboardMonthlyEntryJpaRepository monthlyRepository;

    public TopicLeaderboardRepositoryAdapter(TopicLeaderboardEntryJpaRepository allTimeRepository,
                                             TopicLeaderboardMonthlyEntryJpaRepository monthlyRepository) {
        this.allTimeRepository = allTimeRepository;
        this.monthlyRepository = monthlyRepository;
    }

    @Override
    public void saveAllTime(TopicLeaderboardEntry entry) {
        allTimeRepository.save(TopicLeaderboardEntryMapper.toEntity(entry));
    }

    @Override
    public void saveMonthly(TopicLeaderboardEntry entry) {
        monthlyRepository.save(TopicLeaderboardEntryMapper.toMonthlyEntity(entry));
    }

    @Override
    public Optional<TopicLeaderboardEntry> findByTopicAndUser(String topicId, String userId) {
        return allTimeRepository.findByTopicIdAndUserId(topicId, userId)
                .map(TopicLeaderboardEntryMapper::toDomain);
    }

    @Override
    public Optional<TopicLeaderboardEntry> findMonthlyByTopicAndUser(String topicId, String userId, String month) {
        return monthlyRepository.findByTopicIdAndUserIdAndMonth(topicId, userId, month)
                .map(TopicLeaderboardEntryMapper::toDomain);
    }

    @Override
    public void refreshPseudonym(String userId, String pseudonym) {
        allTimeRepository.refreshPseudonym(userId, pseudonym);
        monthlyRepository.refreshPseudonym(userId, pseudonym);
    }

    @Override
    public void refreshCountry(String userId, String country) {
        allTimeRepository.refreshCountry(userId, country);
        monthlyRepository.refreshCountry(userId, country);
    }

    @Override
    public void refreshAvatarOptions(String userId, String avatarOptions) {
        allTimeRepository.refreshAvatarOptions(userId, avatarOptions);
        monthlyRepository.refreshAvatarOptions(userId, avatarOptions);
    }

    @Override
    public LeaderboardPage findPage(String topicId,
                                    boolean monthly,
                                    String month,
                                    List<String> memberIds,
                                    String country,
                                    int page,
                                    int size) {
        if (monthly) {
            return findPage(monthlyRepository, TopicLeaderboardEntryMapper::toDomain,
                    true, topicId, month, memberIds, country, page, size);
        }
        return findPage(allTimeRepository, TopicLeaderboardEntryMapper::toDomain,
                false, topicId, month, memberIds, country, page, size);
    }

    @Override
    public Optional<LeaderboardRank> findRank(String topicId,
                                              String userId,
                                              boolean monthly,
                                              String month,
                                              List<String> memberIds,
                                              String country) {
        if (monthly) {
            return monthlyRepository.findByTopicIdAndUserIdAndMonth(topicId, userId, month)
                    .filter(entry -> inScope(entry.getUserId(), entry.getCountry(), entry.getMonth(),
                            true, month, memberIds, country))
                    .map(entry -> new LeaderboardRank(
                            TopicLeaderboardEntryMapper.toDomain(entry),
                            (int) monthlyRepository.count(rankedBefore(
                                    true, topicId, month, memberIds, country, userId, entry.getMonthlyXp())) + 1));
        }
        return allTimeRepository.findByTopicIdAndUserId(topicId, userId)
                .filter(entry -> inScope(entry.getUserId(), entry.getCountry(), null,
                        false, month, memberIds, country))
                .map(entry -> new LeaderboardRank(
                        TopicLeaderboardEntryMapper.toDomain(entry),
                        (int) allTimeRepository.count(rankedBefore(
                                false, topicId, month, memberIds, country, userId, entry.getTotalXp())) + 1));
    }

    private <T> LeaderboardPage findPage(JpaSpecificationExecutor<T> repository,
                                         Function<T, TopicLeaderboardEntry> mapper,
                                         boolean monthly,
                                         String topicId,
                                         String month,
                                         List<String> memberIds,
                                         String country,
                                         int page,
                                         int size) {
        Page<T> result = repository.findAll(
                scope(monthly, topicId, month, memberIds, country),
                PageRequest.of(page, size, sort(monthly)));
        return LeaderboardPage.builder()
                .entries(result.getContent().stream().map(mapper).toList())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    private <T> Specification<T> scope(boolean monthly,
                                       String topicId,
                                       String month,
                                       List<String> memberIds,
                                       String country) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("topicId"), topicId));
            if (monthly) {
                predicates.add(cb.equal(root.get("month"), month));
            }
            if (memberIds != null) {
                predicates.add(memberIds.isEmpty()
                        ? cb.disjunction()
                        : root.get("userId").in(memberIds));
            } else if (country != null) {
                predicates.add(cb.equal(root.get("country"), country));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    /**
     * Entrées classées avant le joueur : XP strictement supérieure, ou XP égale et {@code userId}
     * lexicographiquement inférieur — exactement l'ordre de {@link #sort(boolean)} (départage
     * déterministe des ex æquo, cohérent entre la page et le rang du joueur).
     */
    private <T> Specification<T> rankedBefore(boolean monthly,
                                              String topicId,
                                              String month,
                                              List<String> memberIds,
                                              String country,
                                              String userId,
                                              int xp) {
        String xpField = monthly ? "monthlyXp" : "totalXp";
        Specification<T> base = scope(monthly, topicId, month, memberIds, country);
        return base.and((root, query, cb) -> cb.or(
                cb.greaterThan(root.get(xpField), xp),
                cb.and(
                        cb.equal(root.get(xpField), xp),
                        cb.lessThan(root.get("userId"), userId))));
    }

    private boolean inScope(String entryUserId,
                            String entryCountry,
                            String entryMonth,
                            boolean monthly,
                            String month,
                            List<String> memberIds,
                            String country) {
        if (monthly && !month.equals(entryMonth)) {
            return false;
        }
        if (memberIds != null && !memberIds.contains(entryUserId)) {
            return false;
        }
        return country == null || country.equals(entryCountry);
    }

    private Sort sort(boolean monthly) {
        return Sort.by(
                Sort.Order.desc(monthly ? "monthlyXp" : "totalXp"),
                Sort.Order.asc("userId"));
    }
}
