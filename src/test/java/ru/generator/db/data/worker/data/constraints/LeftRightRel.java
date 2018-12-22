package ru.generator.db.data.worker.data.constraints;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 22/12/2018
 */
@Data
@Table(schema = "test",name = "constraint_relation",
  uniqueConstraints = @UniqueConstraint(columnNames = {"left_id","right_id"}))
@Entity
public class LeftRightRel {

  @Id
  @GeneratedValue
  private int id;

  @ManyToOne
  @JoinColumn(name = "left_id")
  private EntityLeft left;

  @ManyToOne
  @JoinColumn(name="right_id")
  private EntityRight right;

}
