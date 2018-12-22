package ru.generator.db.data.worker.data.withoutrepo;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Boris Zhguchev on 22/12/2018
 */
@Data
@Entity
@Table(schema = "test",name = "with_repo")
public class WithRepo {
  @Id
  @GeneratedValue
  private int id;

  @ManyToOne
  @JoinColumn
  private WithoutRepo withoutRepo;
}
