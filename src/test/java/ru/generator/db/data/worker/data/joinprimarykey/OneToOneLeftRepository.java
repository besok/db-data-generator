package ru.generator.db.data.worker.data.joinprimarykey;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Boris Zhguchev on 19/12/2018
 */
public interface OneToOneLeftRepository extends JpaRepository<OneToOneLeft,Integer> {
}
