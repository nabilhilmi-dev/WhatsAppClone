package com.nabil.whatsaapcloning.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nabil.whatsaapcloning.R
import com.nabil.whatsaapcloning.listener.ChatClickListener
import com.nabil.whatsaapcloning.util.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat.*

class ChatAdapter(val chats: ArrayList<String>) :
RecyclerView.Adapter<ChatAdapter.ChatViewHolder>(){

    class ChatViewHolder (override val containerView: View):
    RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val firebaseDB  = FirebaseFirestore.getInstance()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid
        private var partnerId: String? = null
        private var chatName: String? = null
        private var chatImage: String? = null

        fun bindItem(chatId: String, listener: ChatClickListener?) {
           progress_layout.visibility = View.VISIBLE
            progress_layout.setOnTouchListener { v, event -> true }

            firebaseDB.collection(DATA_CHATS)
                .document(chatId)
                .get()
                .addOnSuccessListener {
                    val chatParticipants  = it[DATA_CHAT_PARTICIPANTS]
                    if (chatParticipants !=null){
                        for (participants in chatParticipants as ArrayList<String>){
                            if (participants != null && !participants.equals(userId)){
                                partnerId = participants
                                firebaseDB.collection(DATA_USERS).document(partnerId!!).get()
                                    .addOnSuccessListener {
                                        val user = it.toObject(User::class.java)
                                        chatImage = user?.imageUrl
                                        chatName = user?.name
                                        populateImage(img_chats.context,user?.imageUrl,img_chats,R.drawable.ic_user)
                                        progress_layout.visibility = View.GONE
                                    }

                                    .addOnFailureListener {
                                        it.printStackTrace()
                                        progress_layout.visibility = View.GONE
                                    }
                            }
                        }
                    }

                    progress_layout.visibility = View.GONE
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    progress_layout.visibility = View.GONE
                }

            itemView.setOnClickListener {
                listener?.onChatClicked(chatId,userId,chatImage, chatName)
            }

        }

    }

    private var chatClickListener: ChatClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_chat, parent, false
        )
    )

    override fun getItemCount() = chats.size

    override fun onBindViewHolder(holder: ChatAdapter.ChatViewHolder, position: Int) {
        holder.bindItem(chats[position], chatClickListener)
    }

    fun setOnItemClickListener(listener: ChatClickListener) {
        chatClickListener = listener
        notifyDataSetChanged()
    }

    fun updateChats(updatedChats: ArrayList<String>){
        chats.clear()
        chats.addAll(updatedChats)
        notifyDataSetChanged()
    }

    class ChatsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val firebaseDb = FirebaseFirestore.getInstance()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid
        private var partnerId: String? = null
        private var chatName: String? = null
        private var chatImageUrl: String? = null

        fun bindItem(chatId: String, listener: ChatClickListener?) {
            progress_layout.visibility = View.VISIBLE
            progress_layout.setOnTouchListener { v, event -> true }

            firebaseDb.collection(DATA_CHATS).document(chatId).get()
                .addOnSuccessListener {
                    val chatParticipants = it[DATA_CHAT_PARTICIPANTS]
                    if (chatParticipants != null) {
                        for (participant in chatParticipants as ArrayList<String>) {
                            if (participant != null && !participant.equals(userId)) {
                                partnerId = participant
                                firebaseDb.collection(DATA_USERS)
                                    .document(partnerId!!)
                                    .get()
                                    .addOnSuccessListener {
                                        val user = it.toObject(User::class.java)
                                        chatImageUrl = user?.imageUrl
                                        chatName = user?.name

                                        txt_chats.text = user?.name
                                        populateImage(
                                            img_chats.context,
                                            user?.imageUrl,
                                            img_chats,
                                            R.drawable.ic_user
                                        )
                                        progress_layout.visibility = View.GONE
                                    }
                                    .addOnFailureListener { e ->
                                        e.printStackTrace()
                                        progress_layout.visibility = View.GONE
                                    }
                            }
                        }
                    }
                    progress_layout.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    progress_layout.visibility = View.GONE
                }
            itemView.setOnClickListener {
                listener?.onChatClicked(chatId, partnerId, chatImageUrl, chatName)
            }
        }
    }
}