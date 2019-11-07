package tracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeMap;
import org.json.JSONObject;

public class BitcoinTracker {

  private String bitcoinEndpoint;
  private String currentPrice = "";
  private String currentTime = "";
  private String currency;
  private String startTime;
  private String endTime;
  private String jsonResult;

  public BitcoinTracker() {
    currency = "EUR";
    bitcoinEndpoint = "https://api.coindesk.com/v1/bpi/currentprice/" + currency + ".json";
  }

  public BitcoinTracker(String currency) {
    if (currency.length() < 3) {
      this.currency = "EUR";
    }
    this.currency = currency;
    bitcoinEndpoint = "https://api.coindesk.com/v1/bpi/currentprice/" + currency + ".json";
  }

  public BitcoinTracker(String startTime, String endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
    bitcoinEndpoint =
        "https://api.coindesk.com/v1/bpi/historical/close.json" + "?start=" + this.startTime
            + "&end=" + this.endTime;
  }

  private void checkPrice() {
    HttpURLConnection urlConnection = null;
    StringBuilder result = new StringBuilder();

    try {
      URL url = new URL(bitcoinEndpoint);
      urlConnection = (HttpURLConnection) url.openConnection();
      int code = urlConnection.getResponseCode();

      if (code == 200) {
        try (InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))) {
          String line;
          while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      assert urlConnection != null;
      urlConnection.disconnect();
    }
    jsonResult = String.valueOf(result);

  }

  public String getCurrentPrice() {
    String result = parseResult();
    JSONObject obj = new JSONObject(result);

    currentPrice = obj.getJSONObject("bpi").getJSONObject(currency).getString("rate");
    currentTime = obj.getJSONObject("time").getString("updated");

    return currentPrice;
  }

  public TreeMap<String, String> getHistoricalPrice() {
    String result = parseResult();

    System.out.println(result);

    JSONObject obj = new JSONObject(result);
    String x = String.valueOf(obj.getJSONObject("bpi"))
        .replace("{", "")
        .replace("}", "")
        .replace("\"", "");

    TreeMap<String, String> historicalPrices = new TreeMap<>();
    String[] prices = x.split(",");
    for (String price : prices) {
      historicalPrices.put(price.split(":")[0], price.split(":")[1]);
    }

    return historicalPrices;
  }

  private String parseResult() {
    checkPrice();
    JsonParser jParser = new JsonParser();
    JsonElement jsonElement = jParser.parse(jsonResult);
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    return gson.toJson(jsonElement);

  }

  public String getStartTime() {
    return startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public String getCurrency() {
    return currency;
  }

  public String getCurrentTime() {
    return currentTime;
  }

}
