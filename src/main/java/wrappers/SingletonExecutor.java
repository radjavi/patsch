package wrappers;

import java.util.concurrent.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SingletonExecutor {
  private static SingletonExecutor instance = null;
  private int nrThreads = 1;
  private ExecutorService executor;

  private static final Logger logger = LogManager.getLogger(SingletonExecutor.class);

  private SingletonExecutor(int nrThreads) {
    this.nrThreads = nrThreads;
    this.executor = Executors.newWorkStealingPool(nrThreads);
  }

  public synchronized static SingletonExecutor init(int nrThreads) {
    if (instance != null)
      throw new AssertionError("SingletonExecutor has already been initialized!");
    if (nrThreads < 2)
      return null;

    instance = new SingletonExecutor(nrThreads);
    return instance;
  }

  public static SingletonExecutor getInstance() {
    return instance;
  }

  public int getNrThreads() {
    return nrThreads;
  }

  public ExecutorService getExecutor() {
    return executor;
  }

  public void shutdown() {
    try {
      executor.shutdown();
      executor.awaitTermination(5, TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
        logger.trace("Executor: Tasks interrupted.");
    }
    finally {
        if (!executor.isTerminated()) {
            logger.trace("Executor: Cancelling non-finished tasks.");
        }
        executor.shutdownNow();
        logger.trace("Executor: Shutdown finished.");
    }
  }
}
