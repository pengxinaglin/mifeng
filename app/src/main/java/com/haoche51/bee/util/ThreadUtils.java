package com.haoche51.bee.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 线程池 不用每次都重新创建新线程 */
class ThreadUtils {

  /** 初始化线程池线程数 */
  private static final int INIT_THREAD_COUNTS = 2;

  private static ExecutorService mExecutorService =
      Executors.newFixedThreadPool(INIT_THREAD_COUNTS);

  static void execute(Runnable command) {
    mExecutorService.execute(command);
  }
}
