package ru.generator.db.data.converter.file;

import ru.generator.db.data.worker.MetaData;
import ru.generator.db.data.worker.MetaDataList;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Boris Zhguchev on 10/01/2019
 */
public class FileConverter<V> implements Converter<V, Path> {

  private final MetaDataList mdStore;
  private String name = "";
  private String del = "@@@";

  public FileConverter(MetaDataList mdStore) {
	this.mdStore = mdStore;
  }

  public FileConverter<V> withName(String name) {
	this.name = name;
	return this;
  }


  private List<String> makeData(Object data, MetaData md) {
    List<String> res = new ArrayList<>();
	res.add(header(md));
	res.add(colHeader(md));
	res.add(record(md, data));
	return res;

  }

  @Override
  public Path to(V data) {
	MetaData md = byClass(data);
	List<String> majorEntity = makeData(data, md);
	List<String> depEntity = md.getDependencies().entrySet()
	  .stream()
	  .flatMap(e -> makeData(md.getValue(data, e.getKey()), e.getValue().getMd()).stream())
	  .collect(Collectors.toList());

	majorEntity.addAll(depEntity);
	for (String s : majorEntity) {
	  System.out.println(s);
	}
	return null;
  }

  @Override
  public V from(Path source) {
	return null;
  }

  private MetaData byClass(V data) {
	return mdStore.byClass(data.getClass())
	  .orElseThrow(ConverterMetadataException::new);
  }

  private String header(MetaData md) {
	return del + md.getHeader().tbl();
  }

  protected String colHeader(MetaData md) {
	String plainCol = md.getPlainColumns()
	  .stream()
	  .map(MetaData.Column::getColumn)
	  .collect(Collectors.joining(";"));

	String id = md.getId().getColumn();
	// TODO: 10.01.2019 encapsulation failed
	String deps = md.getDependencies().values()
	  .stream()
	  .map(MetaData.Dependency::getColumn)
	  .collect(Collectors.joining(";"));

	return id + ";" + plainCol + ";" + deps;
  }

  public String record(MetaData md, Object entity) {
	String id = md.getIdValue(entity).toString();
	String plainCols = md.getPlainColumns()
	  .stream()
	  .map(el -> md.getValue(entity, el.getVal()))
	  .map(el -> Objects.isNull(el) ? "" : el.toString())
	  .collect(Collectors.joining(";"));

	String dep =
	  md.getDependencies().entrySet()
		.stream()
		.map(e -> {
		  Object fieldValue = md.getValue(entity, e.getKey());
		  if (Objects.isNull(fieldValue))
			return "";
		  return e.getValue().getMd().getIdValue(fieldValue);
		})
		.map(Object::toString)
		.collect(Collectors.joining(";"));

	return id + ";" + plainCols + ";" + dep;
  }

  private static class Store{
    private Map<String,List<String>> store= new HashMap<>();
  }
}
