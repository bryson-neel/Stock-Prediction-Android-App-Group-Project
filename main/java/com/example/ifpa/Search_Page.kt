package com.example.ifpa

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

//function to get a list of best matching funds
fun getFunds(fundCode: String): String
{

    var result = ""
    var key = "5XUUMTB5HBETQT8R"

    val t = Thread(Runnable {
        var url = ("https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" +
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
    //println(result)
    return result
}
class Search_Page : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {

        //saves the state of the activity so that it can be recreated and not lose any info
        super.onCreate(savedInstanceState)

        //should be the name of the corresponding xml layout file
        setContentView(R.layout.search_page)

        //get current user from previous activity
        var currentUser = intent.getStringExtra("User")

        //list of the user's currently added funds
        var userFunds = mutableListOf<String>()

        //list of search results
        var funds = mutableListOf<String>()

        //read userFunds from file with format /currentUser.txt
        try {
            val inputStream2: InputStream = File(filesDir.path.toString() + "/"+currentUser+".txt").inputStream()
            inputStream2.bufferedReader().forEachLine{
                userFunds.add(it)
            }
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }

        //button that displays the search results
        val goBut = findViewById<Button>(R.id.goButton) as Button

        //button that resets the search screen so users can search multiple times
        val clearBut = findViewById<Button>(R.id.clearButton) as Button

        //when clearBut is clicked, clear the search bar and restart activity
        clearBut.setOnClickListener{
            val searchBarText = findViewById<EditText>(R.id.searchEditText) as EditText
            searchBarText.text.clear()
            this.recreate()
        }

        // when goBut is clicked
        // 1) format the linear layout
        // 2) display results with check or plus symbol
        goBut.setOnClickListener {
            //save user input
            val searchBarText = findViewById<EditText>(R.id.searchEditText) as EditText
            //get best matches to user's search input
            var listFunds = getFunds(searchBarText.text.toString().trim())
            var l_funds = JSONObject(listFunds)
            var matches = l_funds.getJSONArray("bestMatches")
            //parse jsonarray and json object
            var i = 0
            while(i!=matches.length()-1) {
                funds.add(matches.getJSONObject(i).getString("1. symbol"))
                i++
                println(funds)
            }

            val mainLL = findViewById<LinearLayout>(R.id.myLayoutId) as LinearLayout


            for (x in funds)
            {
                //println(x)
                val LL = LinearLayout(this)
                var params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 10)
                var text = TextView(this)

                //format layout for each fund and add text
                LL.layoutDirection = (View.LAYOUT_DIRECTION_LTR)
                LL.setOrientation(LinearLayout.VERTICAL);
                LL.setPadding(20, 20, 20, 20)
                LL.setBackgroundColor(Color.parseColor("#C0C0C0"))
                var y = x + "\n"
                text.setText(y)
                text.setTextSize(20.toFloat())
                text.setTextColor(Color.parseColor("#000000"))
                LL.addView(text)
                LL.isClickable = true

                //format has & add fund buttons with parameters
                val hasFund = ImageButton(this)
                val addFund = ImageButton(this)
                var hasParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                hasParams.setMargins(0,0,0,5)
                var addParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                addParams.setMargins(0,0,0,5)
                //ic_input_check is an image asset that must be added to the drawable folder
                hasFund.setImageResource(R.drawable.ic_input_check)
                //ic_input_add is an image asset that must be added to the drawable folder
                addFund.setImageResource(R.drawable.ic_input_add)
                //test if user already has fund
                var has = false
                for(j in 0 until userFunds.size) {
                    if (userFunds[j] == x) {
                        has = true
                    }
                }
                //display check or plus appropriately
                if(has==true)
                {
                    LL.addView(hasFund)
                }
                else if(has==false)
                {
                    LL.addView(addFund)
                }

                hasFund.setOnClickListener {
                    LL.addView(addFund)
                    LL.removeView(hasFund)
                    var newFunds = mutableListOf<String>()
                    for(i in 0 until userFunds.size) {
                        if(userFunds[i] != x) {
                            newFunds.add(userFunds[i])
                        }
                    }
                    userFunds = newFunds
                    try {
                        PrintWriter(filesDir.path.toString() + "/" +
                                currentUser + ".txt").close();
                        for(i in 0 until userFunds.size) {
                            val file = File(filesDir.path.toString() + "/" +
                                    currentUser + ".txt")
                            FileOutputStream(file, true).bufferedWriter().use { out ->
                                out.write(userFunds[i] + "\n")
                            }
                        }
                        val toast = Toast.makeText(applicationContext, "Fund removed from portfolio", Toast.LENGTH_SHORT)
                        toast.show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                //if user clicks on add button, it will add fund to list
                addFund.setOnClickListener {
                    LL.addView(hasFund)
                    LL.removeView(addFund)
                    try {
                        val file = File(filesDir.path.toString() + "/" +
                                currentUser + ".txt")
                        FileOutputStream(file, true).bufferedWriter().use { out ->
                            out.write(x + "\n")
                        }
                        val toast = Toast.makeText(applicationContext, "Fund added to portfolio", Toast.LENGTH_SHORT)
                        toast.show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                mainLL.addView(LL, params)


                LL.setOnClickListener {

                    //starts the Expand_Index_Fund_Page activity
                    Intent(this, Expand_Index_Fund_Page::class.java).also {

                        it.putExtra("Code", x)
                        it.putExtra("User", currentUser)
                        startActivity(it)
                    }
                }
            }

            //when home button is clicked, go to Home_Page and pass current user
            val toHomeVal = findViewById<Button>(R.id.toHomeButton) as Button
            toHomeVal.setOnClickListener {
                val toHome = Intent(this@Search_Page, Home_Page::class.java)
                toHome.putExtra("User", currentUser)
                startActivity(toHome)
            }
        }
    }
}
