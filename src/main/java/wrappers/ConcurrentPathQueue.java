package wrappers;

import models.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class ConcurrentPathQueue {
  private LinkedBlockingQueue<Path> paths;
  private AtomicInteger nrPaths;
  private AtomicInteger nrBlocked;
  private Semaphore sizeSemaphore;

  public ConcurrentPathQueue() {
    paths = new LinkedBlockingQueue<>();
    nrPaths = new AtomicInteger(0);
    nrBlocked = new AtomicInteger(0);
    sizeSemaphore = new Semaphore(1, true);
  }

  public ConcurrentPathQueue(List<Path> initialPaths) throws Exception {
    paths = new LinkedBlockingQueue<>();
    nrPaths = new AtomicInteger(0);
    nrBlocked = new AtomicInteger(0);
    sizeSemaphore = new Semaphore(1, true);
    for (Path p : initialPaths) {
      this.add(p);
    }
  }

  public void add(Path p) throws Exception {
    sizeSemaphore.acquire();
    paths.add(p);
    nrPaths.incrementAndGet();
    sizeSemaphore.release();
  }

  public Path take() throws Exception {
    nrBlocked.incrementAndGet();
    Path p = paths.take();
    sizeSemaphore.acquire();
    nrBlocked.decrementAndGet();
    nrPaths.decrementAndGet();
    sizeSemaphore.release();
    return p;
  }

  public synchronized int size() throws Exception {
    sizeSemaphore.acquire();
    int n = nrPaths.get();
    sizeSemaphore.release();
    return n;
  }

  public synchronized int blockedSize() throws Exception {
    sizeSemaphore.acquire();
    int n = nrBlocked.get();
    sizeSemaphore.release();
    return n;
  }
}