public class Main {

  public static void main(String[] args) throws InterruptedException {
    Worker workerThread = new Worker(4);
    Worker eventLoop = new Worker(1);
    System.out.println("Starting threads");
    workerThread.startThreads();
    eventLoop.startThreads();
    System.out.println(workerThread);
    System.out.println(eventLoop);
    workerThread.postTimeout(() -> System.out.println("postTimeout workerThread"), 5000);
    eventLoop.postTimeout(() -> System.out.println("postTimeout eventLoop"), 5000);
    workerThread.post(() -> System.out.println("workerThread 1"));

    workerThread.post(() -> System.out.println("workerThread 2"));

    workerThread.post(() -> System.out.println("workerThread 3"));

    workerThread.post(() -> System.out.println("workerThread 4"));

    eventLoop.post(() -> System.out.println("eventLoop 1"));

    eventLoop.post(() -> System.out.println("eventLoop 2"));
    System.out.println("Joining threads");
    workerThread.joinThreads();
    eventLoop.joinThreads();
    System.out.println("Threads joined");

  }
}