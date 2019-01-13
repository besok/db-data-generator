package ru.generator.db.data.converter.file;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */
public class RawUtility {
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
}
