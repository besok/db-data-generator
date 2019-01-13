package ru.generator.db.data.converter;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import ru.generator.db.data.converter.file.FileConverter;
import ru.generator.db.data.converter.file.StringTransformer;
import ru.generator.db.data.worker.MetaDataList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Created by Boris Zhguchev on 12/01/2019
 */
@Component
public class DbDataConverter {
  private final MetaDataList metaDataList;
  private StringTransformer transformer;

  public DbDataConverter(MetaDataList metaDataList) {
	this.metaDataList = metaDataList;
  }

  public DbDataConverter withTransformer(StringTransformer transformer) {
	this.transformer = transformer;
	return this;
  }

  public <V> Path toFile(V data, Path file) throws IOException {
	return new FileConverter(metaDataList, file, transformer).to(unproxy(data));
  }

  public <V> List<V> fromFile(Path p, Class<V> vClass) throws IOException {
	return new FileConverter(metaDataList, p, transformer).from(vClass);
  }

  @SuppressWarnings("unchecked")
  private <P> P unproxy(P obj) {
	return (P) Hibernate.unproxy(obj);
  }

}
