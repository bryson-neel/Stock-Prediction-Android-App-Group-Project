package com.example.ifpa

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


fun getDaily(fundCode: String): String
{
    var result = ""
    val key = "5XUUMTB5HBETQT8R"

    val t = Thread(Runnable {
        val url = ("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" +
                fundCode + "&apikey=" + key)
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        try {
            val streamReader = InputStreamReader(connection.inputStream)
                result = streamReader.readText()
            streamReader.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            connection.disconnect()
        }
    })
    t.start()
    t.join() // wait for thread to finish

    return result
}

fun getMonthly(fundCode: String): String
{
    var result = ""
    val key = "5XUUMTB5HBETQT8R"

    val t = Thread(Runnable {
        val url = ("https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=" +
                fundCode + "&apikey=" + key)
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        try {
            val streamReader = InputStreamReader(connection.inputStream)
            result = streamReader.readText()
            streamReader.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            connection.disconnect()
        }
    })
    t.start()
    t.join() // wait for thread to finish

    return result
}

class Expand_Index_Fund_Page : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        // saves the state of the activity so that it can be recreated and not lose any info
        super.onCreate(savedInstanceState)

        // should be the name of the corresponding xml layout file
        setContentView(R.layout.expand_index_fund_page)

        val currentUser = intent.getStringExtra("User")

        // use an intent to pass the fund code from previous activity to this one
        val code = intent.getStringExtra("Code")

        val addFund = findViewById<ImageButton>(R.id.addFundButton)
        val removeFund = findViewById<ImageButton>(R.id.removeFundButton)

        //val info = findViewById<TextView>(R.id.infoText) as TextView
        val data = findViewById<TextView>(R.id.dataText)

        var shouldBuy = false

        // read funds from file
        var funds = mutableListOf<String>()
        try {
            val inputStream: InputStream = File(filesDir.path.toString() + "/" +
                    currentUser + ".txt").inputStream()
            inputStream.bufferedReader().forEachLine { funds.add(it) }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        for(i in 0 until funds.size) {
            if(funds[i] == code) {
                //if the fund is already in the user's portfolio, hide the add fund button
                addFund.visibility = View.GONE
                removeFund.visibility = View.VISIBLE
            }
        }

        val fundNameVal = findViewById<TextView>(R.id.fundName)

        val lineEntryHigh = ArrayList<Entry>()
        //val monthlyData = ArrayList<Double>()
        val dailyData = ArrayList<Double>()

        val ratio: Double
        //var monthlyTotal = 0.0
        var dailyTotal = 0.0

        var mostRecentDaily = 0.1

        if(code != null) {
            if(code.length > 1) {
                val dailyString = getDaily(code)
                try {
                    val daily = JSONObject(dailyString)
                    if(!daily.isNull("Time Series (Daily)")) {
                        val dailies = daily.getJSONObject("Time Series (Daily)")
                        val dailyKeys: Iterator<String> = dailies.keys()
                        var k = 0
                        while(dailyKeys.hasNext()) {
                            val key: String = dailyKeys.next()
                            if(dailies.get(key) is JSONObject) {
                                if(mostRecentDaily == 0.1) {
                                    mostRecentDaily = (dailies.get(key) as JSONObject)
                                            .getString("2. high").toDouble()
                                    data.text = "Open: " + (dailies.get(key) as JSONObject).getString("1. open") +
                                            "\nHigh: " + (dailies.get(key) as JSONObject).getString("2. high") +
                                            "\nLow: " + (dailies.get(key) as JSONObject).getString("3. low") +
                                            "\nClose: " + (dailies.get(key) as JSONObject).getString("4. close") +
                                            "\nVolume: " + (dailies.get(key) as JSONObject).getString("5. volume")
                                }
                                dailyData.add((dailies.get(key) as JSONObject)
                                        .getString("2. high").toDouble())
                                dailyTotal += (dailies.get(key) as JSONObject)
                                        .getString("2. high").toDouble()
                            }
                            ++k
                        }
                        dailyTotal /= k

                        ratio = mostRecentDaily / dailyTotal
                        if(ratio > 1)
                            shouldBuy = true
                    } else
                        shouldBuy = false



                    // (doesn't get data from today, only previous 30 days)
                    for(i in 0 until dailyData.size) {
                        lineEntryHigh.add(Entry(((i).toFloat()),
                                dailyData[dailyData.size - 1 - i].toFloat()))
                    }

                    // Entries must be sorted by x value, otherwise you get a
                    // negativearraysizeexception
                    val lineDataSetHigh = LineDataSet(lineEntryHigh, "High")

                    lineDataSetHigh.setDrawValues(false)
                    lineDataSetHigh.setDrawCircles(false)

                    val dataSets = ArrayList<ILineDataSet>()
                    dataSets.add(lineDataSetHigh)

                    val lineData = LineData(dataSets)

                    val chart = findViewById<LineChart>(R.id.lineChart)
                    chart.data = lineData

                    chart.description.isEnabled = false
                    val legend: Legend = chart.legend
                    legend.isEnabled = false

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                fundNameVal.text = code

                val prediction = findViewById<TextView>(R.id.predictionText)

                if(shouldBuy) {
                    prediction.text = "Good fund to own"
                    prediction.setTextColor(Color.parseColor("#22ff00"))
                } else {
                    prediction.text = "Bad fund to own"
                    prediction.setTextColor(Color.parseColor("#ff0000"))
                }

                //if the plus button is clicked, add the fund to the user's portfolio
                addFund.setOnClickListener {
                    try {
                        val file = File(filesDir.path.toString() + "/" +
                                        currentUser + ".txt")
                        FileOutputStream(file, true).bufferedWriter().use { out ->
                            out.write(code + "\n")
                        }
                        addFund.visibility = View.GONE
                        removeFund.visibility = View.VISIBLE
                        //info.text = "Fund added to portfolio"
                        val toast = Toast.makeText(applicationContext,
                                "Fund added to portfolio", Toast.LENGTH_SHORT)
                        toast.show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                removeFund.setOnClickListener {
                    val newFunds = mutableListOf<String>()
                    for(i in 0 until funds.size) {
                        if(funds[i] != code) {
                            newFunds.add(funds[i])
                        }
                    }
                    funds = newFunds
                    try {
                        PrintWriter(filesDir.path.toString() + "/" +
                                currentUser + ".txt").close()
                        for(i in 0 until funds.size) {
                            val file = File(filesDir.path.toString() + "/" +
                                    currentUser + ".txt")
                            FileOutputStream(file, true).bufferedWriter().use { out ->
                                out.write(funds[i] + "\n")
                            }
                        }
                        addFund.visibility = View.VISIBLE
                        removeFund.visibility = View.GONE
                        //info.text = "Fund removed from portfolio"
                        val toast = Toast.makeText(applicationContext,
                                "Fund removed from portfolio", Toast.LENGTH_SHORT)
                        toast.show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        val toHomeVal = findViewById<Button>(R.id.toHomeButton)
        toHomeVal.setOnClickListener {
            val toHome = Intent(this@Expand_Index_Fund_Page, Home_Page::class.java)
            //pass current user's email to home page activity
            toHome.putExtra("User", currentUser)
            startActivity(toHome)
        }
    }
}