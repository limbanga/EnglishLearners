package com.example.englishlearners

import android.util.Log
import com.example.englishlearners.model.AppUser
import com.example.englishlearners.model.Folder
import com.example.englishlearners.model.Topic
import com.example.englishlearners.model.Vocabulary
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseService {
    private val database = FirebaseDatabase.getInstance()

    suspend fun getUser(userId: String): AppUser? {
        return try {
            val myRef = database.getReference(AppConst.KEY_USER).child(userId)
            val snapshot = myRef.get().await()

            if (snapshot.exists()) {
                val user = snapshot.getValue(AppUser::class.java)
                user
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addFolder(folder: Folder): Folder? {
        return suspendCoroutine { continuation ->
            val database = Firebase.database
            val myRef = database.getReference(AppConst.KEY_FOLDER)
            val newRef = myRef.push()

            val data = mapOf(
                "name" to folder.name,
                "desc" to folder.desc,
                "ownerId" to folder.ownerId,
                "created" to System.currentTimeMillis(),
                "updated" to System.currentTimeMillis(),
            )

            newRef.setValue(data) { databaseError, _ ->
                if (databaseError != null) {
                    continuation.resume(null) // Trả về null khi thất bại
                } else {
                    continuation.resume(folder) // Trả về folder khi thành công
                }
            }
        }
    }

    suspend fun addVocabularies(vocabularies: ArrayList<Vocabulary>, topicId: String): Boolean {
        return try {
            val reference = database.getReference(AppConst.KEY_VOCABULARY)

            for (vocabulary in vocabularies) {
                // format data
                 val data = mapOf(
                     "term" to vocabulary.term,
                     "definition" to vocabulary.definition,
                     "topicId" to topicId,
                 )
                reference.push().setValue(data).await()
            }

            true // Trả về true nếu thêm thành công
        } catch (e: Exception) {
            e.printStackTrace()
            false // Trả về false nếu có lỗi xảy ra
        }
    }

    suspend fun getTopics(): ArrayList<Topic> {
        return suspendCoroutine { continuation ->
            val topicsList = ArrayList<Topic>()
            val myRef = database.getReference(AppConst.KEY_TOPIC)

            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children.reversed()) {
                        val itemValue = snapshot.getValue(Topic::class.java)
                        val itemKey = snapshot.key.toString()

                        if (itemValue != null) {
                            itemValue.id = itemKey
                            topicsList.add(itemValue)
                        } else {
                            Log.d(AppConst.DEBUG_TAG, "$itemKey is null")
                        }
                    }
                    continuation.resume(topicsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(AppConst.DEBUG_TAG, "Failed to read value. ${error.message}", error.toException())
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    suspend fun getTopicsByFolderId(folderId: String): ArrayList<Topic> {
        return suspendCoroutine { continuation ->
            val topicsRef = database.getReference(AppConst.KEY_TOPIC)
            val topicsList = ArrayList<Topic>()
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val topic = snapshot.getValue(Topic::class.java)
                        if (topic != null && topic.folderId == folderId) {
                            topicsList.add(topic)
                        }
                    }
                    continuation.resume(topicsList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    continuation.resumeWithException(databaseError.toException())
                }
            }

            topicsRef.addListenerForSingleValueEvent(valueEventListener)
        }
    }
    suspend fun getVocabularies(topicId: String): ArrayList<Vocabulary> {
        return try {
            val myRef = database.getReference(AppConst.KEY_VOCABULARY)
            val dataSnapshot = myRef.orderByChild("topicId").equalTo(topicId).get().await()
            val vocabList = ArrayList<Vocabulary>()

            for (snapshot in dataSnapshot.children) {
                val itemValue = snapshot.getValue(Vocabulary::class.java)
                val itemKey = snapshot.key.toString()

                if (itemValue != null) {
                    itemValue.id = itemKey
                    vocabList.add(itemValue)
                } else {
                    Log.d(AppConst.DEBUG_TAG, "$itemKey is null")
                }
            }
            vocabList
        } catch (e: Exception) {
            Log.e(AppConst.DEBUG_TAG, "Error getting vocabulary list: ${e.message}", e)
            arrayListOf() // Trả về ArrayList trống trong trường hợp xảy ra lỗi
        }
    }

    suspend fun getTopic(topicId: String): Topic? {
        return try {
            val myRef = database.getReference(AppConst.KEY_TOPIC).child(topicId)
            val documentSnapshot = myRef.get().await()

            if (documentSnapshot.exists()) {
                val topic = documentSnapshot.getValue(Topic::class.java) as Topic
                topic.id = documentSnapshot.key.toString()
                topic
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(AppConst.DEBUG_TAG, "Error getting topic: ${e.message}", e)
            null
        }
    }

    suspend fun deleteTopic(topicId: String): Boolean {
        return try {
            val myRef = database.getReference(AppConst.KEY_TOPIC).child(topicId)
            myRef.removeValue().await()
            true // Trả về true khi xóa thành công
        } catch (e: Exception) {
            e.printStackTrace()
            false // Trả về false khi xảy ra lỗi
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
     suspend fun updateTopic(topic: Topic, vocabularyCount: Long = 0): Topic? {
        return suspendCancellableCoroutine { continuation ->
            val topicRef = database.getReference(AppConst.KEY_TOPIC)
            val nodeRef = topicRef.child(topic.id)

            val updateMap = mutableMapOf<String, Any?>(
                "title" to topic.title,
                "desc" to topic.desc,
                "ownerId" to topic.ownerId,
                "created" to topic.created,
                "vocabularyCount" to vocabularyCount,
                "updated" to System.currentTimeMillis(),
            )

            nodeRef.updateChildren(updateMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(topic,null) // Trả về topic khi thành công
                    } else {
                        continuation.resume(null, null) // Trả về null khi thất bại
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun addTopic(topic: Topic, vocabularyCount: Long): Topic? {
        return suspendCancellableCoroutine { continuation ->
            val myRef = database.getReference(AppConst.KEY_TOPIC)
            val newRef = myRef.push()

            val data = mapOf(
                "title" to topic.title,
                "desc" to topic.desc,
                "ownerId" to topic.ownerId,
                "created" to System.currentTimeMillis(),
                "updated" to System.currentTimeMillis(),
                "vocabularyCount" to vocabularyCount,
            )

            newRef.setValue(data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Thêm thành công, trả về topic
                        topic.id = newRef.key.toString()
                        continuation.resume(topic, null)
                    } else {
                        // Thất bại, trả về null
                        continuation.resume(null, null)
                    }
                }
        }
    }
}
