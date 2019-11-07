package tracker

import com.google.gson.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class BitcoinTracker {

    var bitcoinEndpoint:String = ""
    var marketPrice:String = ""
    var currentTime:String = ""
    var currency: String = ""
    var startTime: String = ""
    var endTime: String = ""
    var jsonResult: String = ""

    constructor()   {
        this.currency = "EUR"
        bitcoinEndpoint = "https://api.coindesk.com/v1/bpi/currentprice/" + this.currency +".json"
    }

    constructor(currency: String)   {
        if(currency.length < 3) {
            this.currency = "EUR"
        }
        this.currency = currency
        bitcoinEndpoint = "https://api.coindesk.com/v1/bpi/currentprice/" + this.currency +".json"
    }

    constructor(startTime: String,  endTime: String)    {
        this.startTime = startTime
        this.endTime = endTime
        bitcoinEndpoint = "https://api.coindesk.com/v1/bpi/historical/close.json" + "?start=" + this.startTime + "&end=" + this.endTime
    }

    private fun checkPrice()    {
        var urlConnection:HttpURLConnection? = null
        var result:StringBuilder = StringBuilder()

        try {
            var url = URL(bitcoinEndpoint)
            urlConnection = url.openConnection() as HttpURLConnection
            var codeResponse: Int = urlConnection.responseCode

            if (codeResponse == 200) {
                BufferedInputStream(urlConnection.inputStream).use { `in` ->
                    BufferedReader(InputStreamReader(`in`)).use { bufferedReader ->
                        result.append(bufferedReader.readLine())
                    }
                }
            }
        }   catch (e: IOException)   {
            e.printStackTrace()
        }   finally {
            urlConnection?.disconnect()
        }
        jsonResult =result.toString()
    }

    fun getCurrentPrice(): String   {
        val result = parseResponse()
        val obj = JSONObject(result)

        marketPrice = obj.getJSONObject("bpi").getJSONObject(currency).getString("rate")
        currentTime = obj.getJSONObject("time").getString("updated")

        return marketPrice
    }

    private fun parseResponse(): String {
        checkPrice()
        val jParser = JsonParser()
        val jsonElement = jParser.parse(jsonResult)
        val gSon:Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        return gSon.toJson(jsonElement)
    }

    fun getHistoricalPrice(): TreeMap<String, String> {
        val result = parseResponse()
        val obj = JSONObject(result)
        val x:String = obj.getJSONObject("bpi").toString()
            .replace("{", "")
            .replace("}", "")
            .replace("\"", "")

        val historicalPrices = TreeMap<String, String>()
        val prices = x.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        for (price in prices) {
            historicalPrices[price.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]] = price.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
        }

        return historicalPrices
    }

}