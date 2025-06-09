package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.File;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
@Slf4j
public class FileRepository {

    private CrudRepository crudRepository;

    public Optional<File> save(File file) {
        try {
            crudRepository.run(session -> session.persist(file));
            return Optional.of(file);
        } catch (ConstraintViolationException e) {
            log.warn("Duplicate file path attempt: {}", file.getPath());
            return Optional.empty();
        }
    }

    public void update(File file) {
        crudRepository.run(session -> session.merge(file));
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from File where id =:id",
                Map.of("id", id)
        );
    }

    public List<File> findByPostId(int postId) {
        return crudRepository.query(
                "from File where post.id = :postId order by id asc",
                File.class,
                Map.of("postId", postId)
        );
    }
}
