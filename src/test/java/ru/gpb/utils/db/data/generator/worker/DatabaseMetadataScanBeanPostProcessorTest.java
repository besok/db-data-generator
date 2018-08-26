package ru.gpb.utils.db.data.generator.worker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gpb.utils.db.data.generator.worker.data.ComplexObject;
import ru.gpb.utils.db.data.generator.worker.data.NakedObject;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

// 2018.08.05 

/**
 * @author Boris Zhguchev
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseMetadataScanBeanPostProcessorTest {

  @Autowired
  private MetaDataList mdl;


  @Test
  public void camelCaseToSnakeCaseIfColumnNameExists() {

    MetaData meta = mdl.byClass(NakedObject.class).get();
    Set<MetaData.Column> columns = meta.getPlainColumns();

    for (MetaData.Column column : columns) {
      String field = column.getField();
      if (field.equals("fieldWithCamel"))
        assertEquals("field_with_camel", column.getColumn());
      if (field.equals("FieldWithCamelCase"))
        assertEquals("field_with_camel_case", column.getColumn());
    }

  }

  @Test
  public void ifColumnAnnExistsTakeFromEntity() {
    MetaData meta = mdl.byClass(NakedObject.class).get();
    MetaData metaCO = mdl.byClass(ComplexObject.class).get();

    assertEquals("",meta.getHeader().getTable());
    assertEquals("",meta.getHeader().getSchema());
    assertEquals("",metaCO.getHeader().getTable());
    assertEquals("test",metaCO.getHeader().getSchema());
  }


}