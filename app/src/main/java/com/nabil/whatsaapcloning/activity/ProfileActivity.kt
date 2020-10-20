package com.nabil.whatsaapcloning.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nabil.whatsaapcloning.R
import com.nabil.whatsaapcloning.util.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private val firebasseDb = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var  imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (userId.isNullOrEmpty()){
            finish()
        }

        progress_layout_profile.setOnTouchListener { v, event -> true }
        btn_apply.setOnClickListener {
            onApply()
        }

        btn_delete_account.setOnClickListener {
            onDelete()
        }

        imbtn_profile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/"
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }

        btn_back.setOnClickListener {
            onBack()
        }

        populateInfo()

    }

    private fun onBack() {
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO){
            storageImage(data?.data)
        }
    }

    private fun storageImage(uri : Uri?) {
        if (uri != null) {
            Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show()
            progress_layout_profile.visibility = View.VISIBLE
            val filePath = firebaseStorage.child(DATA_IMAGE).child(userId!!)//membuat folder

            filePath.putFile(uri)
                .addOnSuccessListener {
                    filePath.downloadUrl
                        .addOnSuccessListener {
                            val url = it.toString()
                            firebasseDb.collection(DATA_USERS)
                                .document(userId)
                                .update(DATA_USER_IMAGE_URL, url)
                                .addOnSuccessListener {
                                    imageUrl = url
                                    populateImage(this, imageUrl, img_profile, R.drawable.ic_user)

                                }
                            progress_layout_profile.visibility = View.GONE
                        }
                        .addOnFailureListener {
                            onUploadFailured()
                        }

                }

                .addOnFailureListener {
                    onUploadFailured()
                }

        }


    }

    private fun onUploadFailured() {
        Toast.makeText(this, "Image upload failed, Please try again...", Toast.LENGTH_SHORT).show()
        progress_layout_profile.visibility = View.GONE
    }


    //untuk menampilkan info akun
    private fun populateInfo() {
        progress_layout_profile.visibility = View.VISIBLE
        firebasseDb.collection(DATA_USERS).document(userId!!).get()//membaca table user
            .addOnSuccessListener {//jika proses berhasil maka data akan di tampung dlu
                val user = it.toObject(User ::class.java)//data di pasang ke EditText
                imageUrl = user?.imageUrl // menampung imgurl
                edt_name_profile.setText(user?.name, TextView.BufferType.EDITABLE)
                edt_email_profile.setText(user?.email , TextView.BufferType.EDITABLE)
                edt_phone_profile.setText(user?.phone, TextView.BufferType.EDITABLE)
                if (imageUrl != null){
                    populateImage(this, user?.imageUrl, img_profile, R.drawable.ic_user)
                }
                progress_layout_profile.visibility = View.GONE
            }

            .addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
    }

    private fun onDelete() {
        progress_layout_profile.visibility = View.VISIBLE
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Apakah anda yakin aka menghapus akun ini?")
            .setPositiveButton("Yes") { dialog, which ->
                firebasseDb.collection(DATA_USERS).document(userId!!).delete()//perintah delete
                firebaseStorage.child(DATA_IMAGE).child(userId).delete()
                firebaseAuth.currentUser?.delete()?.addOnFailureListener {
                    var intent = Intent(this, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }?.addOnFailureListener {
                    finish()
                }//perintah untuk menghapus userid


                progress_layout_profile.visibility = View.GONE

            }

            .setNegativeButton("No"){dialog, which ->
                progress_layout_profile.visibility = View.GONE
            }
            .setCancelable(false)//tombol alertdialog gk ilang kecuali di klick timbol yes/no
            .show()// untuk menampilkan alertdialog yang sudah di buat


    }

    private fun onApply() {
        //data text dalam edit text akan diubah menjadi string lalu di tampung di dalam variable
        progress_layout_profile.visibility = View.VISIBLE
        val name = edt_name_profile.text.toString()
        val email = edt_email_profile.text.toString()
        val phone = edt_phone_profile.text.toString()
        //dijadikan hashmap setelah itu akan dikirimkan kedalam table user di database firestore
        val map = HashMap<String, Any>()
        map[DATA_USER_NAME] = name
        map[DATA_USER_EMAIL] = email
        map[DATA_USER_PHONE] = phone

        firebasseDb.collection(DATA_USERS).document(userId!!).update(map)//update data kedalam database
            .addOnSuccessListener {
                Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show()
                finish()

            }

            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this,"Upadte Failed", Toast.LENGTH_SHORT).show()
                progress_layout_profile.visibility = View.GONE

            }

    }

    override fun onResume() {
        super.onResume()
        if (firebaseAuth.currentUser == null){           //untuk cek user yang sedang aktif
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}




