package ru.generator.db.data.worker.data.joinprimarykey;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Boris Zhguchev on 19/12/2018
 */
@Entity
@Data
@ToString(exclude = "ent")
@Table(schema = "test",name = "onetoone_left")
public class OneToOneLeft {
  @Id
  private UUID id;

  @OneToOne
  private OneToOneRight ent;

}
