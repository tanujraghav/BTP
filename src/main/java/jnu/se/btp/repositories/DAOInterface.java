package jnu.se.btp.repositories;

import jnu.se.btp.entities.ResourceFileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface DAOInterface extends PagingAndSortingRepository<ResourceFileEntity, Long> {

    @Query("SELECT e FROM ResourceFileEntity e WHERE " +
            "WORD_SIMILARITY(?1, e.title) > 0.33 OR " +
            "WORD_SIMILARITY(?1, e.author) > 0.33 OR " +
            "WORD_SIMILARITY(?1, e.semester) > 0.33 OR " +
            "WORD_SIMILARITY(?1, e.field) > 0.33 OR " +
            "WORD_SIMILARITY(?1, e.description) > 0.33 OR " +
            "LOWER(e.title) LIKE %?1% OR " +
            "LOWER(e.author) LIKE %?1% OR " +
            "LOWER(e.semester) LIKE %?1% OR " +
            "LOWER(e.field) LIKE %?1% OR " +
            "LOWER(e.description) LIKE %?1%")
    Page<ResourceFileEntity> findAll(String keyword, PageRequest pageRequest);

    @Query("SELECT e FROM ResourceFileEntity e WHERE e.hash = ?1")
    ResourceFileEntity findResourceFileEntitiesByHash(String hash);

}
