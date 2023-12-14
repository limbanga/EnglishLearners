package com.example.englishlearners

import android.net.Uri
import android.util.Log
import com.example.englishlearners.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import androidx.lifecycle.lifecycleScope
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList


object FirebaseService {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val database = FirebaseDatabase.getInstance()
    // Lấy tham chiếu đến Firebase Storage
    private val storage = Firebase.storage
    private val storageRef = storage.reference

    suspend fun uploadImage(uri: Uri): String? {
        return suspendCoroutine { continuation ->
            val imageRef = storageRef.child("images/${UUID.randomUUID()}")
            val uploadTask = imageRef.putFile(uri)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    continuation.resume(imageUrl)
                }.addOnFailureListener {
                    continuation.resume(null)
                }
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }

    suspend fun deleteFolder(folderId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val foldersRef = database.getReference(AppConst.KEY_FOLDER).child(folderId)
            foldersRef.removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

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

    suspend fun removeTopicFromFolder(folderId: String): Boolean {
        return suspendCoroutine { continuation ->
            val topicsFoldersRef = FirebaseDatabase.getInstance().getReference("topics_folders")
            val query = topicsFoldersRef.orderByChild("folderId").equalTo(folderId)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var deletedAll = true

                    for (snapshot in dataSnapshot.children) {
                        snapshot.ref.removeValue().addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                deletedAll = false
                            }
                        }
                    }

                    continuation.resume(deletedAll)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    continuation.resume(false)
                }
            })
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

    suspend fun updateFolder(folder: Folder, folderId: String): Folder? {
        return suspendCoroutine { continuation ->
            val database = Firebase.database
            val myRef = database.getReference(AppConst.KEY_FOLDER).child(folderId)

            val data = mapOf(
                "name" to folder.name,
                "desc" to folder.desc,
                "ownerId" to folder.ownerId,
                "updated" to System.currentTimeMillis(),
            )

            myRef.updateChildren(data) { databaseError, _ ->
                if (databaseError != null) {
                    continuation.resume(null) // Trả về null khi thất bại
                } else {
                    continuation.resume(folder) // Trả về folder khi thành công
                }
            }
        }
    }


    suspend fun getFolder(folderId: String): Folder? {
        return suspendCancellableCoroutine { continuation ->
            val myRef = database.getReference(AppConst.KEY_FOLDER).child(folderId)

            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        continuation.resume(null) // Trả về null nếu không tồn tại
                    } else {
                        val folder = dataSnapshot.getValue(Folder::class.java) as Folder
                        continuation.resume(folder) // Trả về folder nếu tồn tại
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    continuation.resumeWithException(databaseError.toException())
                }
            })
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
                if (vocabulary.id.isBlank()) {
                    reference.push().setValue(data).await()
                } else {
                    reference.child(vocabulary.id).setValue(data).await()
                }
            }

            true // Trả về true nếu thêm thành công
        } catch (e: Exception) {
            e.printStackTrace()
            false // Trả về false nếu có lỗi xảy ra
        }
    }

    suspend fun deleteVocabularies(vocabularies: ArrayList<Vocabulary>): Boolean {
        return try {
            val reference = database.getReference(AppConst.KEY_VOCABULARY)

            for (vocabulary in vocabularies) {
                reference.child(vocabulary.id).removeValue().await()
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
                    Log.e(
                        AppConst.DEBUG_TAG,
                        "Failed to read value. ${error.message}",
                        error.toException()
                    )
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    suspend fun getTopicsByFolderId(folderId: String): ArrayList<Topic> {
        return suspendCoroutine { continuation ->
            val topicsFoldersRef = database.getReference(AppConst.KEY_TOPICS_FOLDERS)
            val topicsList = ArrayList<Topic>()
            val deferredList = mutableListOf<Deferred<Topic?>>()

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val topicIds = ArrayList<String>()

                    for (snapshot in dataSnapshot.children) {
                        val topicFolder = snapshot.getValue(TopicFolder::class.java)
                        if (topicFolder != null && topicFolder.folderId == folderId) {
                            val topicId = topicFolder.topicId
                            topicIds.add(topicId)
                        }
                    }

                    for (topicId in topicIds) {
                        val deferred = ioScope.async {
                            getTopic(topicId)
                        }
                        deferredList.add(deferred)
                    }

                    ioScope.launch {
                        val results = deferredList.awaitAll()
                        topicsList.addAll(results.filterNotNull())

                        continuation.resume(topicsList)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    continuation.resumeWithException(databaseError.toException())
                }
            }

            topicsFoldersRef.addListenerForSingleValueEvent(valueEventListener)
        }
    }


    fun addTopicsToFolder(topics: ArrayList<Topic>, folderId: String): Boolean {
        val databaseRef = database.getReference(AppConst.KEY_TOPICS_FOLDERS)
        var allTopicsAddedSuccessfully = true

        for (topic in topics) {
            val newTopicFolderRef = databaseRef.push()
            val topicFolderMap = mapOf(
                "topicId" to topic.id,
                "folderId" to folderId
            )

            newTopicFolderRef.setValue(topicFolderMap)
                .addOnFailureListener {
                    allTopicsAddedSuccessfully = false
                }
        }

        return allTopicsAddedSuccessfully
    }

    suspend fun getTopicsByOwnerId(ownerId: String): ArrayList<Topic> {
        return suspendCoroutine { continuation ->
            val topicsRef = database.getReference(AppConst.KEY_TOPIC)
            val topicsList = ArrayList<Topic>()
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val topic = snapshot.getValue(Topic::class.java)
                        if (topic != null && topic.ownerId == ownerId) {
                            topic.id = snapshot.key.toString()
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
                        continuation.resume(topic, null) // Trả về topic khi thành công
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
