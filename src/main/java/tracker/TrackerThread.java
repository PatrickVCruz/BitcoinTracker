package tracker;

public class TrackerThread implements Runnable {
  private Thread t;
  private String threadName;
  private BitcoinTracker tracker;

  public TrackerThread(String name){
    threadName = name;
    tracker = new BitcoinTracker();
    System.out.println("Creating " +  threadName );
  }

  public void run() {
    System.out.println("Running " +  threadName );
    tracker.getCurrentPrice();
    System.out.println("Thread " +  threadName + " exiting.");
  }

  public void start () {
    System.out.println("Starting " +  threadName );
    if (t == null) {
      t = new Thread (this, threadName);
      t.start ();
    }
  }

}
