package ru.generator.db.data.worker.data.similar;

import lombok.Data;
import ru.generator.db.data.worker.data.similar.p1.SimilarObject;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 16/10/2018
 */

@Entity
@Data
@Table(schema = "test",name = "for_similar_object")
public class ForSimilarObject {

  @Id
  @GeneratedValue
  private int id;


  @ManyToOne @JoinColumn
  private SimilarObject obj;

}
