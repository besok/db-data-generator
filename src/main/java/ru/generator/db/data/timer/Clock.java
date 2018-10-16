package ru.generator.db.data.timer;
// 2018.07.24 

/**
 *
 * Class for getting current time in nanos. It needs for a metronome.
 *
 */
public interface Clock {

  default long currentTimeInNanos() {
    return currentTimeInMillis() * 1000000L;
  }

  long currentTimeInMillis();


  /**
   * default implementation from system time
   * */
  static Clock system() {
    return new Clock() {
      @Override
      public long currentTimeInMillis() {
        return System.currentTimeMillis();
      }

      @Override
      public long currentTimeInNanos() {
        return System.nanoTime();
      }
    };
  }




}
