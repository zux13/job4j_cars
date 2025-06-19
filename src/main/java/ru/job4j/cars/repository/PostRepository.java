package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.dto.PostFilterDto;
import ru.job4j.cars.model.Post;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class PostRepository {
    private CrudRepository crudRepository;

    public Post create(Post post) {
        crudRepository.run(session -> session.persist(post));
        return post;
    }

    public void update(Post post) {
        crudRepository.run(session -> session.merge(post));
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Post where id =:id",
                Map.of("id", id)
        );
    }

    public List<Post> findAll() {
        return crudRepository.query("from Post order by id asc", Post.class);
    }

    public Optional<Post> findById(int id) {
        return crudRepository.tx(session -> {
            var postOpt = session.createQuery("""
                select p from Post p
                where p.id = :id
                """, Post.class)
                    .setParameter("id", id)
                    .uniqueResultOptional();

            if (postOpt.isPresent()) {
                Post post = postOpt.get();

                Hibernate.initialize(post.getUser());
                Hibernate.initialize(post.getCar().getBrand());
                Hibernate.initialize(post.getCar().getOwners());
                Hibernate.initialize(post.getFiles());
                Hibernate.initialize(post.getPriceHistoryList());
            }

            return postOpt;
        });
    }

    public List<Post> findByFilter(PostFilterDto filter) {
        return crudRepository.tx(session -> {
            LocalDateTime createdAfter = getCreatedAfterFromId(filter.getPeriodId());

            String hql = buildHql(filter, createdAfter, false);
            Map<String, Object> params = buildParams(filter, createdAfter);

            var query = session.createQuery(hql, Post.class);
            params.forEach(query::setParameter);

            query.setFirstResult(filter.getOffset());
            query.setMaxResults(filter.getLimit());

            List<Post> posts = query.list();
            posts.forEach(p -> Hibernate.initialize(p.getPriceHistoryList()));
            return posts;
        });
    }

    public long countByFilter(PostFilterDto filter) {
        return crudRepository.tx(session -> {
            LocalDateTime createdAfter = getCreatedAfterFromId(filter.getPeriodId());

            String hql = buildHql(filter, createdAfter, true);
            Map<String, Object> params = buildParams(filter, createdAfter);

            var query = session.createQuery(hql, Long.class);
            params.forEach(query::setParameter);

            return query.getSingleResult();
        });
    }

    private LocalDateTime getCreatedAfterFromId(int periodId) {
        return switch (periodId) {
            case 1 -> LocalDateTime.now().minusDays(1);
            case 2 -> LocalDateTime.now().minusDays(3);
            case 3 -> LocalDateTime.now().minusWeeks(1);
            case 4 -> LocalDateTime.now().minusMonths(1);
            default -> null;
        };
    }

    private String buildHql(PostFilterDto filter, LocalDateTime createdAfter, boolean onlyCount) {
        StringBuilder hql = new StringBuilder();

        if (onlyCount) {
            hql.append("select count(distinct p) from Post p where 1=1");
        } else {
            hql.append("select distinct p from Post p left join fetch p.files where 1=1");
        }

        if (filter.getBrandIds() != null && !filter.getBrandIds().isEmpty()) {
            hql.append(" and p.car.brand.id in (:brandIds)");
        }

        if (filter.isOnlyActive()) {
            hql.append(" and not p.sold");
        }

        if (filter.isOnlyWithPhoto()) {
            hql.append(" and exists (from File f where f.post.id = p.id)");
        }

        if (filter.isOnlyMyPosts()) {
            hql.append(" and p.user.id = :userId");
        }

        if (createdAfter != null) {
            hql.append(" and p.created >= :createdAfter");
        }

        if (!onlyCount) {
            hql.append(" order by p.id desc");
        }

        return hql.toString();
    }

    private Map<String, Object> buildParams(PostFilterDto filter, LocalDateTime createdAfter) {
        Map<String, Object> params = new HashMap<>();

        if (filter.getBrandIds() != null && !filter.getBrandIds().isEmpty()) {
            params.put("brandIds", filter.getBrandIds());
        }

        if (filter.isOnlyMyPosts()) {
            params.put("userId", filter.getUserId());
        }

        if (createdAfter != null) {
            params.put("createdAfter", createdAfter);
        }

        return params;
    }

}
