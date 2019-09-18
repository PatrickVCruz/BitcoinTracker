package application;

import java.util.TreeMap;
import tracker.bitcoinTracker;

public class Main {

  public static void main(String[] args) {

    TreeMap<String, String> x = new bitcoinTracker("2017-12-16", "2017-12-19").getHistoricalPrice();

    for (String i : x.keySet()) {
      System.out.println("Date: " + i + " price: " + x.get(i));
    }

    String price = new bitcoinTracker("GBP").getCurrentPrice();
    System.out.println(price);

  }

}
