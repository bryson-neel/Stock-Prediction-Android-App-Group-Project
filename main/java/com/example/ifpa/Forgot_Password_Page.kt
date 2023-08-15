package com.example.ifpa

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.io.InputStream


class Forgot_Password_Page : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        //saves the state of the activity so that it can be recreated and not lose any info
        super.onCreate(savedInstanceState)

        //should be the name of the corresponding xml layout file
        setContentView(R.layout.forgot_password_page)

        //read emails from file
        val emails = mutableListOf<String>()
        try {
            val inputStream1: InputStream = File("/data/data/com.example.ifpa/files/emails.txt").inputStream()
            inputStream1.bufferedReader().forEachLine { emails.add(it) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        println("Emails:$emails")

        val requestPassword = findViewById<Button>(R.id.requestPasswordButton) as Button

        var validEmail = false

        //Display success/fail msg if email is valid/invalid if request password is clicked
        requestPassword.setOnClickListener {
            val msg = findViewById<TextView>(R.id.displayMessage) as TextView
            val email = findViewById<EditText>(R.id.emailEditText) as EditText
            val emailValue = email.text.toString().trim()

            //validate the email
            for (i in 0 until emails.size)
            {
                if (emails[i] == emailValue)
                {
                    validEmail = true //stay on same page and display message
                    msg.text = "Email sent!"
                    msg.setTextColor(Color.parseColor("#4CAF50"))

                }
            }

            if (!validEmail) //stay on same page and display invalid email message
            {
                msg.text = "Invalid Email."
                msg.setTextColor(Color.parseColor("#FF0000"))
            }

        }
        
        val back = findViewById<Button>(R.id.backButton) as Button

        //Go back to initial page if back button is clicked
        back.setOnClickListener{
            val toInitial = Intent(this@Forgot_Password_Page, Initial_Page::class.java)
            startActivity(toInitial)
        }
    }
}
