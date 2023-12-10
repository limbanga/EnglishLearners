package com.example.englishlearners

import android.util.Log
import com.example.englishlearners.model.Topic
import com.example.englishlearners.model.Vocabulary
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

object FirebaseService {
    private val database = FirebaseDatabase.getInstance()

    suspend fun getVocabularies(topicId: String): ArrayList<Vocabulary> {
        return try {
            val myRef = database.getReference(AppConst.KEY_TOPIC).child(topicId)
                .child(AppConst.KEY_VOCABULARY)
            val dataSnapshot = myRef.get().await()

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
    suspend fun adddTopic(topic: Topic, vocabularyCount: Long): Topic? {
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
                        continuation.resume(topic, null)
                    } else {
                        // Thất bại, trả về null
                        continuation.resume(null, null)
                    }
                }
        }
    }
}
