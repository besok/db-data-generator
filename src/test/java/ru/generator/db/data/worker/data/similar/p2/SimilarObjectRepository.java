package ru.generator.db.data.worker.data.similar.p2;
// 2018.07.27 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Boris Zhguchev
 */
//@Repository("similarObject1")
public interface SimilarObjectRepository extends JpaRepository<SimilarObject,Integer> {
}
