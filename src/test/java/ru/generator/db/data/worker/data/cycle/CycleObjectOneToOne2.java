package ru.generator.db.data.worker.data.cycle;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 16/10/2018
 */
@Entity
@Data
@ToString(exclude = "cycle")
@Table(schema = "test",name = "cycle_object_121_2")
public class CycleObjectOneToOne2 {

  @Id
  @GeneratedValue
  private int id;



  @OneToOne(optional = false)
  private CycleObjectOneToOne1 cycle;





}
