package ru.generator.db.data.worker.data.hard;
// 2018.07.27 

import org.springframework.data.jpa.repository.JpaRepository;
import ru.generator.db.data.worker.data.ComplexObject;

/**
 * @author Boris Zhguchev
 */
public interface MainObjectRepository extends JpaRepository<MainObject,Integer> {
}
