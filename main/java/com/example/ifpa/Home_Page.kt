package com.example.ifpa

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime

fun getDailyFunds(fundCode: String): String
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

fun getPercentage(fund: String): Double {
    var dailyAverage = 0.0
    var total = 0.0
    var mostRecentDaily = 0.1

    try {
        val daily = JSONObject(getDailyFunds(fund))
        if(!daily.isNull("Time Series (Daily)")) {
            val dailies = daily.getJSONObject("Time Series (Daily)")
            val dailyKeys: Iterator<String> = dailies.keys()
            while(dailyKeys.hasNext()) {
                val key: String = dailyKeys.next()
                if (dailies.get(key) is JSONObject) {
                    if(mostRecentDaily == 0.1)
                        mostRecentDaily = (dailies.get(key) as JSONObject)
                                .getString("2. high").toDouble()
                    dailyAverage += (dailies.get(key) as JSONObject)
                            .getString("2. high").toDouble()
                    ++total
                }
            }
            dailyAverage /= total
        }
    } catch(e: JSONException) {
        e.printStackTrace()
    }

    if(dailyAverage != 0.0)
        return mostRecentDaily / dailyAverage

    return 0.0
}

class Home_Page : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        // saves the state of the activity so that it can be recreated and not lose any info
        super.onCreate(savedInstanceState)

        // should be the name of the corresponding xml layout file
        setContentView(R.layout.home_page)

        // get current user from previous activity
        val currentUser = intent.getStringExtra("User")

        // format: DD-MM-YYYY
        val todayDate = (LocalDateTime.now().toString()).subSequence(0, 10).toString()

        // holds the date and the fund of the day
        val lines = arrayListOf<String>()

        // the index fund of the day is stored in a file called FOTD.txt
        try {
            val file = File(filesDir.path.toString() + "/FOTD.txt")
            file.createNewFile()
            val inputStream: InputStream =
                    File(filesDir.path.toString() + "/FOTD.txt").inputStream()
            inputStream.bufferedReader().forEachLine { lines.add(it) }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if(lines.isNotEmpty()) {
            if(lines[0] != todayDate)
                lines.clear()
        }

        val funds = mutableListOf<String>()
        try {
            val inputStream2: InputStream =
                    File(filesDir.path.toString() + "/"+currentUser+".txt").inputStream()
            inputStream2.bufferedReader().forEachLine { funds.add(it) }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val toggleFOTD = findViewById<Button>(R.id.toggleFOTDButton)
        val fotd = findViewById<Button>(R.id.fundOfTheDayButton)

        // 50 largest US funds
        val fundList = arrayListOf("SPY", "IVV", "VTI", "VOO", "QQQ", "AGG", "VTV", "VUG",
                "BND", "IWM", "IJR", "IWF", "IJH", "VIG", "IWD", "VO", "VB", "VGT", "VCIT", "LQD",
                "XLK", "XLF", "VCSH", "ITOT", "VNQ", "VYM", "IVW", "BSV", "DIA", "SCHX", "USMV",
                "IWB", "IWR", "TIP", "XLV", "RSP", "MBB", "IGSB", "ARKK", "VV", "VBR", "SCHD",
                "HYG", "IVE", "XLE", "MUB", "MDY", "QUAL", "XLI", "SCHB"
        )

        val percentages = arrayListOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        )

        if(lines.isEmpty()) {
            // 2 extra threads calculate the percentages for the index funds to find out the
            // index fund of the day
            val t1 = Thread(Runnable {
                for(a in 0 until fundList.size / 3)
                    percentages[a] = getPercentage(fundList[a])
            })
            t1.start()

            val t2 = Thread(Runnable {
                for(b in fundList.size / 3 until fundList.size * (2 / 3)) {
                    percentages[b] = getPercentage(fundList[b])
                }
            })
            t2.start()

            val t3 = Thread(Runnable {
                for(c in fundList.size * (2 / 3) until fundList.size) {
                    percentages[c] = getPercentage(fundList[c])
                }
            })
            t3.start()

            // clicking this will calculate the index fund of the day
            toggleFOTD.setOnClickListener {
                val toast = Toast.makeText(applicationContext, "Please wait...", LENGTH_LONG)
                toast.show()
                // wait for other threads to finish before continuing
                t1.join()
                t2.join()
                t3.join()
                toast.cancel()

                var index = 0    // index in fundList that fundOfTheDay is at
                for(i in 0 until fundList.size) {
                    if(percentages[i] > percentages[index] && percentages[i] > 1) {
                        index = i
                        lines.add(todayDate)
                        lines.add(fundList[i])
                    }
                }

                // write today's date and today's fotd to the file FOTD.txt
                try {
                    val file = File(filesDir.path.toString() + "/FOTD.txt")
                    file.createNewFile()
                    FileOutputStream(file, false).bufferedWriter().use {
                        out -> out.write(lines[0] + "\n" + lines[1])
                    }
                } catch(io: IOException) {
                    io.printStackTrace()
                }
                fotd.visibility = View.VISIBLE
                fotd.text = lines[1]
            }
        } else {
            toggleFOTD.setOnClickListener {
                fotd.text = lines[1]
                if(fotd.visibility == View.GONE)
                    fotd.visibility = View.VISIBLE
                else
                    fotd.visibility = View.GONE

            }
        }

        fotd.setOnClickListener {
            if(lines.isNotEmpty()) {
                // starts the Expand_Index_Fund_Page activity
                Intent(this, Expand_Index_Fund_Page::class.java).also {
                    it.putExtra("Code", lines[1])
                    it.putExtra("User", currentUser)
                    startActivity(it)
                }
            }
        }



        val mainLL = findViewById<LinearLayout>(R.id.myLayoutId)
        val settings = findViewById<Button>(R.id.settings)
        val search = findViewById<Button>(R.id.search)

        settings.setOnClickListener {

            //starts the Settings_Page activity
            Intent(this, Settings_Page::class.java).also {
                it.putExtra("User", currentUser)
                startActivity(it)
            }
        }

        search.setOnClickListener {

            //starts the Search_Page activity
            Intent(this, Search_Page::class.java).also {
                it.putExtra("User", currentUser)
                startActivity(it)
            }
        }

        for (x in funds){
            val ll = LinearLayout(this)
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(0, 10, 0, 10)
            val text = TextView(this)
            //val but = Button(this)
            ll.layoutDirection = (View.LAYOUT_DIRECTION_LTR)
            ll.setPadding(20,20,20,20)
            ll.setBackgroundColor(Color.parseColor("#C0C0C0"))
            text.text = x
            text.textSize = 20.toFloat()
            text.setTextColor(Color.parseColor("#000000"))
            ll.addView(text)
            //but.setText("Expand")
            //but.setTextSize(10.toFloat())
            //but.setBackgroundColor(Color.parseColor("#228B22"))
            //LL.addView(but)
            ll.isClickable = true
            mainLL.addView(ll, params)

            ll.setOnClickListener {

                // starts the Expand_Index_Fund_Page activity
                Intent(this, Expand_Index_Fund_Page::class.java).also {

                    it.putExtra("Code", x)
                    it.putExtra("User", currentUser)
                    startActivity(it)
                }
            }
        }

    }
}