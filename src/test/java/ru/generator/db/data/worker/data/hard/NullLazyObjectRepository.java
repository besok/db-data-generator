package ru.generator.db.data.worker.data.hard;
// 2018.07.27 

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Boris Zhguchev
 */
public interface NullLazyObjectRepository extends JpaRepository<NullLazyObject,Long> {
}
