package com.example.englishlearners

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.example.englishlearners.Adapter.VocabularyAdapter
import com.example.englishlearners.Api.RetrofitService
import com.example.englishlearners.Api.VocabularyApi
import com.example.englishlearners.Model.Vocabulary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopicDetailFragment() : Fragment() {
    companion object {
        const val TOPIC_ID_KEY = "topic_id_key"
    }

    var topicId: Int = -1
    lateinit var listViewVocabulary : ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_topic_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // get data from caller
        if (arguments != null) {
            topicId = arguments?.getInt(TOPIC_ID_KEY) as Int
        }
        listViewVocabulary = requireView().findViewById(R.id.list_item_vocabulary)

        val accessToken: String = RequiredAuthenticated.checkToken(this)

        val vocabularyApi: VocabularyApi = RetrofitService.getVocabularyApi(accessToken)
        loadVocabularyToList(vocabularyApi, listViewVocabulary)
    }

    //----------------------------------------------------------------------------------------------
    // HELPER FUNCTIONS
    //----------------------------------------------------------------------------------------------
    private fun loadVocabularyToList(vocabularyApi: VocabularyApi, listViewVocabulary: ListView) {
        vocabularyApi.getByTopicId(topicId).enqueue(object : Callback<ArrayList<Vocabulary>?> {
            override fun onResponse(
                call: Call<ArrayList<Vocabulary>?>,
                response: Response<ArrayList<Vocabulary>?>
            ) {
                if (response.isSuccessful) {
                    val vocabularies: ArrayList<Vocabulary> = response.body() as ArrayList<Vocabulary>
                    listViewVocabulary.adapter = VocabularyAdapter(requireContext(), vocabularies)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error code ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<Vocabulary>?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}