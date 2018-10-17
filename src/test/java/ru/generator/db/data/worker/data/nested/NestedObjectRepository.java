package ru.generator.db.data.worker.data.nested;
// 2018.07.27 

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Boris Zhguchev
 */
public interface NestedObjectRepository extends JpaRepository<NestedObject,Integer> {
}
