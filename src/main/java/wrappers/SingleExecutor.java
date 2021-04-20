package wrappers;

import java.util.concurrent.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SingleExecutor {
  private static SingleExecutor instance = null;
  private int nrThreads = 1;
  private ExecutorService executor;

  private static final Logger logger = LogManager.getLogger(SingleExecutor.class);

  private SingleExecutor(int nrThreads) {
    this.nrThreads = nrThreads;
    this.executor = Executors.newWorkStealingPool(nrThreads);
  }

  public synchronized static SingleExecutor init(int nrThreads) {
    if (instance != null)
      throw new AssertionError("SingleExecutor has already been initialized!");
    if (nrThreads < 2)
      return null;

    instance = new SingleExecutor(nrThreads);
    return instance;
  }

  public static SingleExecutor getInstance() {
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
