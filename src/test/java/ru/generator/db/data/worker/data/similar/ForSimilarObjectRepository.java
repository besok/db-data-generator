package ru.generator.db.data.worker.data.similar;
// 2018.07.27 

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gpb.utils.db.data.generator.worker.data.similar.p1.SimilarObject;

/**
 * @author Boris Zhguchev
 */
public interface ForSimilarObjectRepository extends JpaRepository<ForSimilarObject,Integer> {
}
