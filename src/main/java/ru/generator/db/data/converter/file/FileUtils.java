package ru.generator.db.data.converter.file;

import ru.generator.db.data.worker.MetaData;
import ru.generator.db.data.worker.MetaDataList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
public class FileUtils {
  static boolean isHeader(String record) {
	return record.contains("@@@");
  }

  static String getHeader(String record) {
	return record.split("@@@")[1].trim();
  }

  static String validateHeader(String header) {
	String[] split = header.split(".");
	if (split.length == 2) {
	  return header;
	}

	throw new ConverterMetadataException("header " + header + " has a wrong format.");
  }

  static String[] getColHeaders(String rec) {
	return rec.split(";");
  }

  static String[] getRecords(String rec, String[] colHeaders) {
	String[] values = rec.split(";");
	if (values.length != colHeaders.length)
	  throw new ConverterMetadataException(" different count from column headers and column balues , rec = " + rec);

	return values;
  }

  static Optional<Object> findObjectById(List<Object> objects, Object id, MetaData md) {
	return objects.stream().filter(o -> Objects.equals(md.getIdValue(o), id)).findAny();
  }

  static Optional<SmartRecord> findRawRecordMdByMd(MetaData md, List<SmartRecord> smartRecords) {
	return smartRecords.stream().filter(sr -> Objects.equals(sr.md, md)).findAny();
  }

  static List<SmartRecord> matchRawWithMd(MetaDataList mdList, List<RawRecord> records) {
	return records.stream()
	  .map(SmartRecord::new)
	  .map(r -> {
		String[] schTbl = r.record.header.split("\\.");
		mdList.bySchemaTable(schTbl[0], schTbl[1]).ifPresent(r::setMd);
		return r;
	  })
	  .collect(Collectors.toList());
  }

  static List<RawRecord> parseRaw(List<String> records) {

	List<RawRecord> raws = new ArrayList<>();
	RawRecord r = new RawRecord();
	boolean f = false;
	for (String record : records) {
	  if (isHeader(record)) {
		raws.add(r);
		r = new RawRecord();
		r.header = validateHeader(getHeader(record));
		f = true;
	  } else {
		if (f) {
		  r.colHeaders = getColHeaders(record);
		  f = false;
		} else {
		  r.values.add(getRecords(record, r.colHeaders));
		}
	  }
	}
	raws.add(r);
	raws.removeIf(el -> Objects.isNull(el.header));
	return raws;
  }


}
