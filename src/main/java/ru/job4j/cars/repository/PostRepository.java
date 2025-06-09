package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    public List<Post> findAllFromLastDay() {
        return crudRepository.query(
                "from Post p where p.created >= :yesterday order by p.created desc",
                Post.class,
                Map.of("yesterday", LocalDateTime.now().minusDays(1))
        );
    }

    public List<Post> findAllWithPhoto() {
        return crudRepository.query(
                "select distinct p from Post p join p.files f",
                Post.class
        );
    }

    public List<Post> findAllByCarName(String name) {
        return crudRepository.query(
                "from Post p where p.car.name = :name",
                Post.class,
                Map.of("name", name)
        );
    }
}
