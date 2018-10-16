package ru.generator.db.data.worker.data;
// 2018.07.27 

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Boris Zhguchev
 */
public interface NakedObjectRepository extends JpaRepository<NakedObject,Integer> {
}
