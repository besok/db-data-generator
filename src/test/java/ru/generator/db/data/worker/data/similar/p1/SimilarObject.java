package ru.generator.db.data.worker.data.similar.p1;
// 2018.08.13 

import lombok.Data;
import ru.generator.db.data.worker.data.SimplePlainObject2;

import javax.persistence.*;

/**
 * @author Boris Zhguchev
 */
@Entity
@Data
@Table(schema = "test", name = "similar_object_2")
public class SimilarObject {

  @Id
  @GeneratedValue
  private int id;

  private String field;
}
