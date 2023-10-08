package com.example.englishlearners.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.englishlearners.Adapter.TopicAdapter
import com.example.englishlearners.Api.RetrofitService
import com.example.englishlearners.Api.TopicApi
import com.example.englishlearners.RequiredAuthenticated
import com.example.englishlearners.Model.Topic
import com.example.englishlearners.R
import com.example.englishlearners.TopicDetailFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class HomeFragment : Fragment() {

    lateinit var listViewTopic : ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootView = getView()

        listViewTopic = rootView!!.findViewById(R.id.list_item_topic)

        // check token
        val accessToken = RequiredAuthenticated.checkToken(this)

        // get api service instance
        val topicService = RetrofitService.getTopicApi(accessToken)
        // get data from server and binding to list
        loadTopicToListView(topicService, listViewTopic)
        // add click event
        setOnClickEventOnTopic(listViewTopic)
    }

    //----------------------------------------------------------------------------------------------
    // HELPER FUNCTIONS
    //----------------------------------------------------------------------------------------------

    private fun loadTopicToListView(topicService: TopicApi, listViewTopic : ListView) {
        topicService.getAll().enqueue(object : Callback<ArrayList<Topic>> {
            override fun onResponse(call: Call<ArrayList<Topic>>, response: Response<ArrayList<Topic>>) {

                if (response.isSuccessful) {
                    Toast.makeText(context , "Get topic ok", Toast.LENGTH_LONG).show()
                    val topics = response.body()
                    if (topics != null) {
                        listViewTopic.adapter = TopicAdapter(requireContext(), topics)
                    }
                } else {
                    Toast.makeText(context , "HTTP error code" + response.code(), Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<Topic>>, t: Throwable) {
                Toast.makeText(context , "Can't get topic", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }
        })
    }

    private fun setOnClickEventOnTopic(listViewTopic : ListView) {
        listViewTopic.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedTopic : Topic = listViewTopic.adapter.getItem(position) as  Topic
            // redirect to topic detail
            // repair topic id
            val args = Bundle()
            args.putInt(TopicDetailFragment.TOPIC_ID_KEY, selectedTopic.id as Int)

            val topicDetailFragment = TopicDetailFragment()
            // assign topic id to fragment
            topicDetailFragment.arguments = args
            // execute redirect
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout, topicDetailFragment)
            fragmentTransaction.commit()
        }
    }
}


