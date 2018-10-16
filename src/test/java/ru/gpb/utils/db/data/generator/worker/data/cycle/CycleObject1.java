package ru.gpb.utils.db.data.generator.worker.data.cycle;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 16/10/2018
 */
@Entity
@Data
@Table(schema = "test",name = "cycle_object_1")
public class CycleObject1 {

  @Id
  @GeneratedValue
  private int id;


  @ManyToOne
  @JoinColumn
  private CycleObject2 cycleField;

}
