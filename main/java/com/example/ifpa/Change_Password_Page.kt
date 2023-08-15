package com.example.ifpa

import android.R.attr.data
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class Change_Password_Page : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        //saves the state of the activity so that it can be recreated and not lose any info
        super.onCreate(savedInstanceState)

        //should be the name of the corresponding xml layout file
        setContentView(R.layout.change_password_page)

        //get current user from previous activity
        var currentUser = intent.getStringExtra("User")

        val back = findViewById<Button>(R.id.backButton) as Button
        //Go back to home page if back button is clicked
        back.setOnClickListener{
            val toSettings = Intent(this@Change_Password_Page, Settings_Page::class.java)
            toSettings.putExtra("User", currentUser)
            startActivity(toSettings)
        }

        //read emails and passwords from file
        val emails = mutableListOf<String>()
        val passwords = mutableListOf<String>()
        try {
            val inputStream: InputStream =
                    File(filesDir.path.toString() + "/emails.txt").inputStream()
            inputStream.bufferedReader().forEachLine { emails.add(it) }

            val inputStream2: InputStream =
                    File(filesDir.path.toString() + "/passwords.txt").inputStream()
            inputStream2.bufferedReader().forEachLine { passwords.add(it) }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val emailVal = findViewById<TextView>(R.id.emailText)

        emailVal.text = currentUser

        val buttonVal = findViewById<Button>(R.id.changePasswordButton) as Button

        //true if the user entered the correct current password and a valid new password
        var valid = false

        //when button associated with buttonVal has been clicked
        buttonVal.setOnClickListener {
            val outputVal = findViewById<TextView>(R.id.outputText)

            val currentPassword = findViewById<TextView>(R.id.currentPasswordField)
            val currentPasswordValue = currentPassword.text.toString().trim()
            val newPassword = findViewById<TextView>(R.id.newPasswordField)
            val newPasswordValue = newPassword.text.toString().trim()
            if(currentPasswordValue == newPasswordValue) {
                outputVal.text = "New password can't be the same as old password"
            } else {
                for(i in 0 until emails.size) {
                    if(emails[i] == currentUser) {
                        if(passwords[i] == currentPasswordValue) {
                            //the current password the user entered was correct
                            valid = true
                            passwords[i] = newPasswordValue
                        }
                    }
                }
                if(!valid) {
                    outputVal.setTextColor(Color.parseColor("#ff0000"))
                    outputVal.text = "Old password incorrect"
                } else {
                    try {
                        //replace old password with new password in file
                        var file = File(filesDir.path.toString() +
                                "/passwords.txt")
                        file.createNewFile()
                        FileOutputStream(file, false).bufferedWriter().use { out ->
                            passwords.forEach {out.write(it + "\n")}
                        }
                        outputVal.setTextColor(Color.parseColor("#22ff00"))
                        outputVal.text = "Password change successful"
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}