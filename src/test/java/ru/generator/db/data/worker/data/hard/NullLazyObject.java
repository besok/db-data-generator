package ru.generator.db.data.worker.data.hard;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 12/01/2019
 */
@Entity
@Table(schema = "test",name = "null_lazy_object")
@Data
public class NullLazyObject {
  @Id
  @GeneratedValue
  private long id;
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "main_object_id")
  private MainObject mainObject;

}
