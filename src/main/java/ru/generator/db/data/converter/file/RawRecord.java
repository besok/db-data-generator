package ru.generator.db.data.converter.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Boris Zhguchev on 13/01/2019
 */

class RawRecord {
  String header;
  String[] colHeaders;
  List<String[]> values;
  RawRecord() {
	values = new ArrayList<>();
  }

  Optional<String[]> findRecordBy(String col, String value) {
	int idx = idx(col);
	if (idx < 0)
	  return Optional.empty();

	for (String[] records : values) {
	  if (Objects.equals(records[idx], value)) {
		return Optional.of(records);
	  }
	}
	return Optional.empty();
  }
  Optional<String> findValueBy(String col,String[] record){
	int idx = idx(col);
	if (idx < 0)
	  return Optional.empty();

	return Optional.of(record[idx]);
  }

  private int idx(String col) {
	int i = 0;
	for (String colHeader : colHeaders) {
	  if (Objects.equals(col, colHeader))
		return i;
	  i++;
	}
	return -1;
  }

}
