package tracker;

public class trackerThread implements Runnable {
  private Thread t;
  private String threadName;
  private bitcoinTracker tracker;

  public trackerThread(String name){
    threadName = name;
    tracker = new bitcoinTracker();
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
