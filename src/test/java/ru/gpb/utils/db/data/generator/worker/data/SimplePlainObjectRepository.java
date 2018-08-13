package ru.gpb.utils.db.data.generator.worker.data;
// 2018.07.27 

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * @author Boris Zhguchev
 */
public interface SimplePlainObjectRepository extends JpaRepository<SimplePlainObject,Integer> {
}
