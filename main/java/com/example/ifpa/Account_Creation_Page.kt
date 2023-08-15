package com.example.ifpa

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class Account_Creation_Page : AppCompatActivity() {

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        //saves the state of the activity so that it can be recreated and not lose any info
        super.onCreate(savedInstanceState)

        //should be the name of the corresponding xml layout file
        setContentView(R.layout.account_creation_page)
        val funds = "DOWJ"
        val submit_button = findViewById<Button>(R.id.submitButton) as Button
        val back_button = findViewById<Button>(R.id.backButton) as Button
        val email = findViewById<EditText>(R.id.emailField) as EditText
        val username = findViewById<EditText>(R.id.usernameField) as EditText
        val pw = findViewById<EditText>(R.id.pwField) as EditText
        val cpw = findViewById<EditText>(R.id.cpwField) as EditText
        val errorText = findViewById<TextView>(R.id.errorText) as TextView
        val login_button = findViewById<Button>(R.id.loginButtonAC) as Button

        //when button associated with login_button_val has been clicked
        submit_button.setOnClickListener {

            //checks validity of information to display appropriate error
            var errorMsg = ""

            if (pw.text.toString()==""){
                errorMsg = "Please enter password"
            }
            else if (pw.text.toString() != cpw.text.toString()){
                errorMsg = "Passwords do not match"
            }
            if (!email.text.toString().isEmailValid()) {
                if (errorMsg != "") {
                    errorMsg = errorMsg + "\nAnd invalid email"
                } else {
                    errorMsg = "Invalid email"
                }
            }
            if (username.text.toString()==""){
                if (errorMsg!=""){
                    errorMsg = errorMsg + "\nAnd please enter username"
                } else {
                    errorMsg= "Please enter username"
                }
            }
            if(email.text.toString() == "emails" || email.text.toString() == "passwords")
            {
                errorMsg = "Invalid email"
            }
            errorText.setTextColor(Color.parseColor("#d60000"))
            if (errorMsg=="") {
                try {
                    var file = File(filesDir.path.toString() + "/emails.txt")
                    file.createNewFile()
                    var file2 = File(filesDir.path.toString() + "/passwords.txt")
                    file.createNewFile()
                    file2.createNewFile()
                    FileOutputStream(file, true).bufferedWriter().use {
                        out -> out.write(email.text.toString() + "\n")
                    }
                    FileOutputStream(file2, true).bufferedWriter().use {
                        out -> out.write(pw.text.toString() + "\n")
                    }

                    //file that holds the index fund symbols
                    var file3 = File(filesDir.path.toString() + "/"
                            + email.text.toString() + ".txt")
                    file3.createNewFile()
                    FileOutputStream(file3, true).bufferedWriter().use {
                        out -> out.write(funds + "\n")
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
                errorMsg = "Success"
                errorText.setTextColor(Color.parseColor("#22ff00"))
                //clear information
                email.setText("")
                username.setText("")
                pw.setText("")
                cpw.setText("")

                submit_button.visibility = View.INVISIBLE
                login_button.visibility = View.VISIBLE
            }
            errorText.text = errorMsg

        }

        //when button associated with create_account_button_val has been clicked
        back_button.setOnClickListener {
            //clear information
            email.setText("")
            username.setText("")
            pw.setText("")
            cpw.setText("")

            //starts the Initial_Page activity
            Intent(this, Initial_Page::class.java).also {
                startActivity(it)
            }
        }

        login_button.setOnClickListener {

            //starts the Login_Page activity
            Intent(this, Login_Page::class.java).also {
                startActivity(it)
            }
        }
    }
}