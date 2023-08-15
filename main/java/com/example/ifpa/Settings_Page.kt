package com.example.ifpa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class Settings_Page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        //saves the state of the activity so that it can be recreated and not lose any info
        super.onCreate(savedInstanceState)

        //should be the name of the corresponding xml layout file
        setContentView(R.layout.settings_page)

        val currentUser = intent.getStringExtra("User")

        val back = findViewById<Button>(R.id.backButton) as Button
        //Go back to home page if back button is clicked
        back.setOnClickListener{
            val toHomePage = Intent(this@Settings_Page, Home_Page::class.java)
            toHomePage.putExtra("User", currentUser)
            startActivity(toHomePage)
        }

        val logoff = findViewById<Button>(R.id.logoff) as Button
        //Go back to initial page if logoff button is clicked
        logoff.setOnClickListener{
            val toInitial = Intent(this@Settings_Page, Initial_Page::class.java)
            startActivity(toInitial)
        }

        val changePassword = findViewById<Button>(R.id.changePasswordButton) as Button
        //Goes to the change password page when change password button is clicked
        changePassword.setOnClickListener{
            val toChangePasswordPage = Intent(this@Settings_Page, Change_Password_Page::class.java)
            toChangePasswordPage.putExtra("User", currentUser)
            startActivity(toChangePasswordPage)
        }
    }
}