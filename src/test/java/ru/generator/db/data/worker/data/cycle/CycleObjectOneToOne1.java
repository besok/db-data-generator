package ru.generator.db.data.worker.data.cycle;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 16/10/2018
 */
@Entity
@Data
@Table(schema = "test",name = "cycle_object_121_1")
public class CycleObjectOneToOne1 {

  @Id
  @GeneratedValue
  private int id;


  @OneToOne()
  private CycleObjectOneToOne2 cycle;




}
