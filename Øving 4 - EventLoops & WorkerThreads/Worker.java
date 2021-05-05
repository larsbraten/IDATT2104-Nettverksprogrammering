import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Worker {


  List<Thread> threadPool;
  private AtomicBoolean lock = new AtomicBoolean(false);
  List<Runnable> taskList = new LinkedList<>();
  boolean emptyFlag = taskList.isEmpty();
  boolean stopped = false;


  public Worker(int workerThread) {
    threadPool = Stream.generate(() -> new Thread(() -> {
      /*Flags used for stopping the threads */
      while (!emptyFlag || !stopped) {
        /* Calls the poll method to fetch a task */
        poll()
            .run();
      }
    }))
        .limit(workerThread)
        .collect(Collectors.toList());
  }

  /* Stops the thread by changing the stop flag to true. This allows the thread to
  finish the currently running task before closing, and thus avoiding corruption */
  void stop() {
    stopped = true;
  }

  /* Causes thread to begin execution */
  void startThreads() {
    threadPool.forEach(Thread::start);
  }

  /* Causes the currently executing thread to sleep (temporarily cease execution) for the specified number of milliseconds */
  void postTimeout(Runnable task, int waitTime) throws InterruptedException {
    new Thread(() -> {
      try {
        Thread.sleep(waitTime);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      task.run();
    }).start();


  }

  void joinThreads() throws InterruptedException {
    threadPool.forEach(thread -> {
      try {
        /* Waits for threads to die for at most 5000 milliseconds */
        stop();
        thread.join();

      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }

  Runnable poll() {
    /* Atomically sets the value to a new, specified value, and also returns the old value */
    while (lock.getAndSet(true)) {
    }
    Runnable taskPolled = () -> {
    };
    /* Removes the first element of the list and saves it in a variable */
    if (!taskList.isEmpty()) {
      taskPolled = taskList.remove(0);
    }
    lock.set(false);
    return taskPolled;
  }

  void post(Runnable task) {
    /* Atomically sets the value to a new, specified value, and also returns the old value */
    while (lock.getAndSet(true)) {
    }
    /* Appends the task to the end of the list */
    taskList.add(task);
    lock.set(false);
  }

}

