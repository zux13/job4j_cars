package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.OwnershipPeriod;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OwnershipPeriodRepositoryTest {

    @Autowired
    private OwnershipPeriodRepository ownershipPeriodRepository;

    @Autowired
    private SessionFactory sessionFactory;

    @AfterEach
    void clearTable() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM OwnershipPeriod").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private OwnershipPeriod createPeriod(LocalDateTime start, LocalDateTime end) {
        OwnershipPeriod period = new OwnershipPeriod();
        period.setStartAt(start);
        period.setEndAt(end);
        return ownershipPeriodRepository.create(period);
    }

    @Test
    void whenCreateThenFindInList() {
        LocalDateTime start = LocalDateTime.now().minusDays(10).withNano(0);
        LocalDateTime end = LocalDateTime.now().withNano(0);
        OwnershipPeriod period = createPeriod(start, end);

        int savedId = period.getId();

        var maybePeriod = ownershipPeriodRepository.findAll().stream()
                .filter(p -> p.getId() == savedId)
                .findFirst();

        assertThat(maybePeriod).isPresent();

        OwnershipPeriod found = maybePeriod.get();
        assertThat(found.getStartAt().withNano(0)).isEqualTo(start);
        assertThat(found.getEndAt().withNano(0)).isEqualTo(end);
    }

    @Test
    void whenUpdateThenGetUpdatedPeriod() {
        LocalDateTime start = LocalDateTime.now().minusDays(10).withNano(0);
        LocalDateTime end = LocalDateTime.now().withNano(0);
        OwnershipPeriod period = createPeriod(start, end);

        period.setEndAt(end.plusDays(5));
        ownershipPeriodRepository.update(period);

        int savedId = period.getId();

        var maybeUpdated = ownershipPeriodRepository.findAll().stream()
                .filter(p -> p.getId() == savedId)
                .findFirst();

        assertThat(maybeUpdated).isPresent();

        OwnershipPeriod updated = maybeUpdated.get();
        assertThat(updated.getStartAt().withNano(0)).isEqualTo(start);
        assertThat(updated.getEndAt().withNano(0)).isEqualTo(end.plusDays(5));
    }

    @Test
    void whenDeleteThenPeriodNotFound() {
        OwnershipPeriod period = createPeriod(LocalDateTime.now().minusDays(10), LocalDateTime.now());
        ownershipPeriodRepository.delete(period.getId());

        List<OwnershipPeriod> periods = ownershipPeriodRepository.findAll();
        assertThat(periods).doesNotContain(period);
    }

    @Test
    void whenFindAllOrderByIdThenCorrectOrder() {
        OwnershipPeriod p1 = createPeriod(LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(15));
        OwnershipPeriod p2 = createPeriod(LocalDateTime.now().minusDays(10), LocalDateTime.now());

        List<OwnershipPeriod> periods = ownershipPeriodRepository.findAll();

        assertThat(periods).isNotEmpty();
        assertThat(periods.get(0).getId()).isLessThan(periods.get(1).getId());
        assertThat(periods).extracting(OwnershipPeriod::getId).contains(p1.getId(), p2.getId());
    }
}
