package pl.com.karwowsm.musiqueue.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.com.karwowsm.musiqueue.persistence.model.FileEntity;

@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, String> {
}
