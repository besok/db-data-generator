package ru.generator.db.data.worker.data.similar.p1;
// 2018.08.13 

import lombok.Data;
import ru.generator.db.data.worker.data.SimplePlainObject2;
import ru.gpb.utils.db.data.generator.worker.data.SimplePlainObject2;

import javax.persistence.*;

/**
 * @author Boris Zhguchev
 */
@Entity
@Data
@Table(schema = "test",name = "similar_object_2")
public class SimilarObject {

  @Id
  @GeneratedValue
  private int id;

  private String name;

  @ManyToOne(optional = false)
  @JoinColumn(name = "plain_id")
  private SimplePlainObject2 plain;
}
