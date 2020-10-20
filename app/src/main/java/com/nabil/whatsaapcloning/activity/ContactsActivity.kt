package com.nabil.whatsaapcloning.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nabil.whatsaapcloning.R
import com.nabil.whatsaapcloning.adapter.ContactsAdapter
import com.nabil.whatsaapcloning.listener.ContactsClickListener
import com.nabil.whatsaapcloning.util.Contact
import kotlinx.android.synthetic.main.activity_contacts.*

class ContactsActivity : AppCompatActivity(), ContactsClickListener {

    private val contactsList = ArrayList<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        getContacts()
        setupList()
    }

    private fun setupList() {
        progress_layout.visibility = View.GONE
        val contactsAdapter = ContactsAdapter(contactsList) // memasukkan data ke dalam adapter
        contactsAdapter.setOnItemClickListener(this) // memberikan aksi ketika item kontak diklik
        rv_contacts.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = contactsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun getContacts() {
        progress_layout.visibility = View.VISIBLE
        contactsList.clear() // menghapus data sebelum memasukan data
        val newList = ArrayList<Contact>()
        val phone = contentResolver.query( // query untuk mendapatkan uri dari kontak
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        while (phone!!.moveToNext()){ // me-looping query phone untuk akses semua kontak
            val name = phone.getString(phone // mendapatkan nama sebagai string dari kontak
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phone.getString(phone // mendapatkan nomor kontak sebagai string
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            newList.add(Contact(name, phoneNumber)) // menambahkan nama dan nomor kontak
        } // ke dalam variable newList.
        contactsList.addAll(newList) // mengisi data property contactList dengan
        phone.close() // variable newList berisi array kontak
    }

    override fun onContactClickListener(name: String?, phone: String?) {
        val intent = intent
        intent.putExtra(MainActivity.PARAM_NAME, name)
        intent.putExtra(MainActivity.PARAM_PHONE, phone)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}


