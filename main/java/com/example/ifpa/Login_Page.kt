package com.example.ifpa

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class Login_Page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)  //saves the state of the activity so that it can be
        //recreated and not lose any information

        setContentView(R.layout.login_page)

        //read emails from file
        val emails = mutableListOf<String>()
        try {
            val inputStream: InputStream =
                    File(filesDir.path.toString() + "/emails.txt").inputStream()
            inputStream.bufferedReader().forEachLine { emails.add(it) }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //read passwords from file
        val passwords = mutableListOf<String>()
        try {
            val inputStream2: InputStream =
                    File(filesDir.path.toString() + "/passwords.txt").inputStream()
            inputStream2.bufferedReader().forEachLine { passwords.add(it) }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //Print emails and passwords (for testing) (remove for final project)
        //println("Emails:$emails")
        //println("Passwords:$passwords")

        val loginButtonVal = findViewById<Button>(R.id.loginButton) as Button

        val back = findViewById<Button>(R.id.backButton) as Button
        //Go back to home page if back button is clicked
        back.setOnClickListener{
            val toInitial = Intent(this@Login_Page, Initial_Page::class.java)
            startActivity(toInitial)
        }

        var validLogin = false

        //when button associated with login_button_val has been clicked
        loginButtonVal.setOnClickListener {
            val mEmail = findViewById<TextView>(R.id.emailField)
            val emailValue = mEmail.text.toString().trim()
            val mPassword = findViewById<TextView>(R.id.passwordField)
            val passwordValue = mPassword.text.toString().trim()
            for(i in 0 until emails.size) {
                if(emails[i] == emailValue) {
                    if(passwords[i] == passwordValue) {
                        validLogin = true
                        //starts the Home_Page activity
                        val toHome = Intent(this@Login_Page, Home_Page::class.java)

                        //pass current user's email to home page activity
                        toHome.putExtra("User", emails[i])
                        startActivity(toHome)
                    }
                }
            }
            if(!validLogin) {
                //stay on page and let user know the login was invalid
                val mInvalidLogin = findViewById<TextView>(R.id.invalidLoginField)
                mInvalidLogin.setTextColor(Color.parseColor("#d60000"))
                mInvalidLogin.text = "Invalid login information"
            }
        }
    }
}
