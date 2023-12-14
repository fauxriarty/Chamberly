package com.company.chamberly
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ActiveChambersActivity : AppCompatActivity() {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore


    // Using View Binding to reference the views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activechambers)
        val homeButton = findViewById<ImageButton>(R.id.homeButton)

        val recyclerView = findViewById<RecyclerView>(R.id.rvChambers)
        val emptyStateView = findViewById<RelativeLayout>(R.id.emptyStateView)
        val addchamber = findViewById<ImageButton>(R.id.btnAddChamber)


        //todo: check if user is in any chambers
        //todo: if user is in any chambers, show the chambers
        //todo: if user is not in any chambers, show the empty state view

        //todo: uncomment this function call once you've implemented the above
       /* checkForActiveChambers { hasActiveChambers ->
            if (hasActiveChambers) {
                recyclerView.visibility = View.VISIBLE
                emptyStateView.visibility = View.GONE
            } else {
                recyclerView.visibility = View.GONE
                emptyStateView.visibility = View.VISIBLE
            }
        } */



        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val profilePicButton = findViewById<ImageButton>(R.id.btnProfilePic)
        profilePicButton.setOnClickListener {
            showProfileOptionsPopup()
        }

        addchamber.setOnClickListener{
            goToCreateActivity()
        }

        val sampleChambers = mutableListOf(
            Chamber(groupTitle = "Sample Chamber 1", lastMessage = "Last message from Chamber 1"),
            Chamber(groupTitle = "Sample Chamber 2", lastMessage = "Last message from Chamber 2"),
            Chamber(groupTitle = "Sample Chamber 3", lastMessage = "Last message from Chamber 3"),

            // Add more sample chambers as needed
        )

        // for showing the sample chambers
        val adapter = SampleChamberAdapter(sampleChambers)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.visibility = View.VISIBLE
        emptyStateView.visibility = View.GONE

        val swipeToDeleteCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // No move functionality
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val chamberToDelete = sampleChambers[position]

                if (chamberToDelete.groupChatId.isNotBlank()) {
                    deleteChamber(chamberToDelete.groupChatId)
                }

                // Remove the chamber from the list and update the RecyclerView
                sampleChambers.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        val btnFindChamber = findViewById<Button>(R.id.btnFindChamber)
        val btnCreateChamber = findViewById<Button>(R.id.btnCreateChamber)
        btnFindChamber.setOnClickListener {
            goToSearchActivity()
        }

        btnCreateChamber.setOnClickListener {
            goToCreateActivity()
        }

    }

    private fun showProfileOptionsPopup() {
        val options = arrayOf("Delete Account", "Show Privacy Policy")
        val builder = AlertDialog.Builder(this)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> deleteAccount() // Delete account option
                1 -> showPrivacyPolicy() // Show privacy policy option
            }
        }
        builder.show()
    }

    private fun showPrivacyPolicy() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.chamberly.net/privacy-policy"))
        startActivity(browserIntent)
    }

    private fun deleteAccount() {
        val user = auth.currentUser
        if (user != null) {
            // Optional: Delete user's associated data from Firestore

            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        clear()
                        apply()
                    }
                    Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                    redirectToWelcomeActivity()
                } else {
                    Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun redirectToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun checkForActiveChambers(callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("chambers")
                .whereArrayContains("members", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    callback(!querySnapshot.isEmpty) // True if the user is in any chambers
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error checking active chambers: ${exception.message}", Toast.LENGTH_SHORT).show()
                    callback(false) // Handle the error case
                }
        } else {
            callback(false) // No user logged in
        }
    }

    private fun deleteChamber(groupChatId: String) {
        // Attempt to delete the chamber from Firestore
        firestore.collection("chambers").document(groupChatId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Chamber deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting chamber: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun goToSearchActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    private fun goToCreateActivity() {
        val intent = Intent(this, CreateActivity::class.java)
        startActivity(intent)
    }


}