package ru.generator.db.data.converter.file;

import org.hibernate.Hibernate;
import ru.generator.db.data.worker.MetaData;
import ru.generator.db.data.worker.MetaDataList;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by Boris Zhguchev on 10/01/2019
 */
public class FileConverter {

  private final MetaDataList mdStore;
  private ToFileStore toFileStore;
  private FromFileStore fromFileStore;
  private Path path;
  private StringTransformer transformer;

  public FileConverter(MetaDataList mdStore, Path p) {
	this.transformer = new DummyStringTransformer();
	this.mdStore = mdStore;
	this.path = p;
	this.toFileStore = new ToFileStore();
  }

  public FileConverter(MetaDataList mdStore, Path p, StringTransformer transformer) {
	this.transformer = Objects.isNull(transformer) ? new DummyStringTransformer() : transformer;
	this.mdStore = mdStore;
	this.path = p;
	this.toFileStore = new ToFileStore();
  }


  private List<String> makeData(Object data, MetaData md) {
	List<String> res = new ArrayList<>();
	res.add(header(md));
	res.add(colHeader(md));
	res.add(record(md, unproxy(data)));
	return res;

  }


  private <P> P unproxy(P obj) {
	return (P) Hibernate.unproxy(obj);
  }

  public <V> Path to(V data) throws IOException {
	if (Objects.isNull(data)) throw new ConverterNullPonterException();
	MetaData md = byClass(data);
	List<String> majorRecords = makeData(data, md);
	List<String> depRecords = md.getDependencies().entrySet()
	  .stream()
	  .flatMap(e -> makeData(md.getValue(data, e.getKey()), e.getValue().getMd()).stream())
	  .collect(Collectors.toList());

	return Files.write(path, toFileStore.prepare(), CREATE, WRITE);
  }

  public <V> List<V> from(Class<V> vClass) throws IOException {

	List<String> rawRecords = Files.readAllLines(path);
	this.fromFileStore = FromFileStore.init(mdStore, rawRecords, transformer);

	return this.fromFileStore.find(vClass);
  }

  private MetaData byClass(Object data) {
	return mdStore.byClass(data.getClass())
	  .orElseThrow(ConverterMetadataException::new);
  }

  private String header(MetaData md) {
	String h = md.getHeader().tbl();
	toFileStore.init(h);
	return h;
  }

  protected String colHeader(MetaData md) {
	String plainCol = md.getPlainColumns()
	  .stream()
	  .filter(el -> isNotCol(el.getVal()))
	  .map(MetaData.Column::getColumn)
	  .collect(Collectors.joining(";"));

	String id = md.getId().getColumn();
	// TODO: 10.01.2019 it looks like the encapsulation is failed
	String deps = md.getDependencies().values()
	  .stream()
	  .map(MetaData.Dependency::getColumn)
	  .collect(Collectors.joining(";"));
	String colHeader = id + ";" + plainCol + ";" + deps;
	toFileStore.addColHeader(md.getHeader().tbl(), colHeader);
	return colHeader;
  }

  public String record(MetaData md, Object entity) {
	if (Objects.isNull(entity)) return "";
	String id = toString(md.getIdValue(entity));
	String plainCols = md.getPlainColumns()
	  .stream()
	  .filter(el -> isNotCol(el.getVal()))
	  .map(el -> md.getValue(entity, el.getVal()))
	  .map(this::toString)
	  .collect(Collectors.joining(";"));

	String dep = md.getDependencies().entrySet()
	  .stream()
	  .map(e -> {
		Object fieldValue = unproxy(md.getValue(entity, e.getKey()));
		if (Objects.isNull(fieldValue))
		  return "";
		return toString(e.getValue().getMd().getIdValue(fieldValue));
	  })
	  .collect(Collectors.joining(";"));

	String record = id + ";" + plainCols + ";" + dep;
	toFileStore.addRecord(md.getHeader().tbl(), record);
	return record;
  }

  private String toString(Object obj) {
	return Objects.isNull(obj) ? "" : obj.toString();
  }

  // TODO: 12.01.2019 It is a temporary solution. We skip all OnetoMany relations.
  private boolean isNotCol(Field f) {
	return !Collection.class.isAssignableFrom(f.getType());
  }


}
