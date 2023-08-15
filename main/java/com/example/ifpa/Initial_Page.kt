package com.example.ifpa

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.Thread.sleep
import kotlin.system.exitProcess

class Initial_Page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // saves the state of the activity so that it can be recreated and not lose any info
        super.onCreate(savedInstanceState)

        // should be the name of the corresponding xml layout file
        setContentView(R.layout.initial_page)

        // vals associated with the buttons on the layout file
        val loginButtonVal = findViewById<Button>(R.id.login_button)
        val accountCreationButtonVal = findViewById<Button>(R.id.create_account_button)
        val forgotPasswordButtonVal = findViewById<Button>(R.id.forgot_password_button)


        // if program doesn't have permission to access the internet, request it
        val internetPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
        if(internetPermission != PackageManager.PERMISSION_GRANTED) {
            println("Internet permission denied")
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.INTERNET), 101)
        } else {
            println("Internet permission granted")
        }

        // if program doesn't have permission to read external storage, request it
        val readExternalStoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        if(readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            println("Read permission denied")
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 102)
        } else {
            println("Read permission granted")
        }

        // if program doesn't have permission to write external storage, request it
        val writeExternalStoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            println("Write permission denied")
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 103)
        } else {
            println("Write permission granted")
        }

        // if any of the 3 permissions were denied, close the app
        if(internetPermission != PackageManager.PERMISSION_GRANTED
                || readExternalStoragePermission != PackageManager.PERMISSION_GRANTED
                || writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            val lView = LinearLayout(this)

            val t = TextView(this)
            t.text = "You must accept all permissions for app to work\nApp closing..."
            lView.addView(t)
            setContentView(lView)
            sleep(4000)
            exitProcess(0)
        }

        // when button associated with login_button_val has been clicked
        loginButtonVal.setOnClickListener {

            // starts the Login_Page activity
            Intent(this, Login_Page::class.java).also {
                startActivity(it)
            }
        }

        // when button associated with create_account_button_val has been clicked
        accountCreationButtonVal.setOnClickListener {

            // starts the Account_Creation_Page activity
            Intent(this, Account_Creation_Page::class.java).also {
                startActivity(it)
            }
        }

        // when button associated with forgot_password_button_val has been clicked
        forgotPasswordButtonVal.setOnClickListener {

            // starts the Forgot_Password_Page activity
            Intent(this, Forgot_Password_Page::class.java).also {
                startActivity(it)
            }
        }
    }
}