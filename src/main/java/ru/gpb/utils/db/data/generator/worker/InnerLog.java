package ru.gpb.utils.db.data.generator.worker;
// 2018.07.24 

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;

/**
 *
 * Inner logger.
 * It collects all results and can gathering them to string.
 * Also it has {@link InnerLog#marker} which represents COUNT state for this log.
 *
 * @author Boris Zhguchev
 */
// FIXME: 8/20/2018 !!!!!!! -> на больших сущностях ошибка, так как переполняется лог - выключил его.
public class InnerLog {
  private final AtomicLong marker;
  private final Deque<LogEvent> deque;

  public InnerLog() {
    marker = new AtomicLong(0);
    this.deque = new ArrayDeque<>();
  }
  public InnerLog(InnerLog log) {
    marker = log.marker;
    this.deque = log.deque;
  }

  public void push(String txt) {
    deque.push(new LogEvent(txt));
  }

  public String toString() {
    return deque
        .stream()
        .map(LogEvent::string)
        .collect(
            Collectors.joining(System.lineSeparator())
        );
  }

  public long markerValue() {
    return marker.longValue();
  }


  @Data
  private class LogEvent {

    private String text;
    private long m;
    private LocalDateTime ldt;

    private String string() {
      return "[" + m + "][" + ldt.format(ISO_DATE_TIME) + "][" + text + "]";
    }

    public LogEvent(String text) {
      this.text = text;
      this.ldt = LocalDateTime.now();
      this.m = marker.incrementAndGet();
    }


  }
}
