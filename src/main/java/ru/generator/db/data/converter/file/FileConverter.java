package ru.generator.db.data.converter.file;

import org.hibernate.Hibernate;
import ru.generator.db.data.worker.MetaData;
import ru.generator.db.data.worker.MetaDataList;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.*;
import static java.time.ZoneOffset.*;

/**
 * Created by Boris Zhguchev on 10/01/2019
 */
public class FileConverter {

  private final MetaDataList mdStore;
  private TempStore store;
  private Path path;


  public FileConverter(MetaDataList mdStore, Path p) {
	this.mdStore = mdStore;
	this.path = p;
	this.store = new TempStore();
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

	return Files.write(path, store.prepare(), CREATE, WRITE);
  }
  public <V> V from(Path path, Class<V> vClass) throws IOException {

	List<String> rawRecords = Files.readAllLines(path);
	return null;
  }

  private MetaData byClass(Object data) {
	return mdStore.byClass(data.getClass())
	  .orElseThrow(ConverterMetadataException::new);
  }
  private String header(MetaData md) {
	String h = md.getHeader().tbl();
	store.init(h);
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
	store.addColHeader(md.getHeader().tbl(), colHeader);
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
	store.addRecord(md.getHeader().tbl(), record);
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
