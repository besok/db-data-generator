package ru.generator.db.data.worker.data.joinprimarykey;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Boris Zhguchev on 19/12/2018
 */
@Entity
@Data
@Table(schema = "test",name = "onetoone_right")
public class OneToOneRight {
  @Id
  private UUID id;

  @OneToOne(optional = false)
  private OneToOneLeft ent;

}
