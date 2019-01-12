package ru.generator.db.data.converter;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import ru.generator.db.data.converter.file.FileConverter;
import ru.generator.db.data.worker.MetaDataList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Boris Zhguchev on 12/01/2019
 */
@Component
public class DbDataConverter {
  private final MetaDataList metaDataList;


  public DbDataConverter(MetaDataList metaDataList) {
	this.metaDataList = metaDataList;
  }

  public <V> Path toFile(V data,Path file) throws IOException {
    return new FileConverter(metaDataList,file).to(unproxy(data));
  }
  public <V> V fromFile(Path p){
    return null;
  }

  @SuppressWarnings("unchecked")
  private<P> P unproxy(P obj){
	return (P) Hibernate.unproxy(obj);
  }

}
