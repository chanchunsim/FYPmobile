package com.example.pairassginment.student

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pairassginment.databinding.ActivityListOfItemBinding
import com.example.pairassginment.student.objectClass.StudentDetail
import com.example.pairassginment.student.objectClass.ThreeTopicsItem
import com.google.firebase.firestore.FirebaseFirestore

class ListOfThreeTopic : AppCompatActivity() {
    private lateinit var binding: ActivityListOfItemBinding
    private lateinit var topicsDetailArray: ArrayList<ThreeTopicsItem>
    private var MY_CODE_REQUEST: Int = 0;
    private var MY_ITEM_CODE_REQUEST: Int = 10;
    private var circleProgress: RelativeLayout? = null

    private var student_detail: StudentDetail? = null;
    private var mDB: FirebaseFirestore = FirebaseFirestore.getInstance()

    // register an intent that will result a result
    private val startItemForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onItemActivityResult(MY_ITEM_CODE_REQUEST, result)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onActivityResult(MY_CODE_REQUEST, result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOfItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        topicsDetailArray = ArrayList();

        circleProgress = binding.circleCenterLayout
        circleProgress!!.visibility = View.GONE

        student_detail = intent.getParcelableExtra<StudentDetail>("student_detail")
        Log.d("Student detail list", student_detail.toString())

        // after sumbit a new one data
        val message = intent!!.getStringExtra("message").toString()
        Log.d("message", message)
        if (message != "null") {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        getStudentNameIDReady()
        getTopicsDetail()
    }

    private fun getStudentNameIDReady(){
        binding.studentNameIdTv.text = student_detail!!.student_name.toString() + " " + student_detail!!.student_id.toString()
    }

    private fun getTopicsDetail(){
        circleProgress!!.visibility = View.VISIBLE
        mDB.collection("Submission")
            .document(student_detail?.submission_id!!)
            .collection("Topics")
            .get()
            .addOnCompleteListener{
                circleProgress!!.visibility = View.GONE
            }
            .addOnSuccessListener { documents ->
                if(documents != null){
                    for (document in documents){
                        if (document != null){
                            val dataMap:MutableMap<String, Any> = document.data

                            val title = dataMap["Title"].toString()
                            val abstract = dataMap["Abstract"].toString()
                            val data_submit = dataMap["Date_Submit"].toString()
                            val status = dataMap["Status"].toString();
                            var supervisor_comment = dataMap["Supervisor_Comment"].toString()
                            var data_feedback = dataMap["Date_Feedback"].toString()
                            var topic_id = dataMap["Topics_ID"].toString();

                            topicsDetailArray.add(ThreeTopicsItem(title, abstract, data_submit, data_feedback, supervisor_comment, status, topic_id ))
                        }
                    }
                }
                Log.d("Go to adapter", topicsDetailArray.toString())
                addItemsListIntoAdapter(topicsDetailArray)
                setBtnOnClickListener()
            }
    }

    private fun setBtnOnClickListener(){
        // set home button listener
        binding.floatingHomeBtn.setOnClickListener{
            val intent = Intent(this, Dashboard::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("role_id", student_detail!!.role_id)
            startActivity(intent);
            finish();
        }

        // set create / add btn listener
        binding.floatingAddBtn.setOnClickListener{
            val intent = Intent(this, TopicsSubmitForm::class.java)
            // after registered and clicked the btn then get the intent launch
            intent.putExtra("student_detail", student_detail)
            startForResult.launch(intent)
        }
    }

    private fun addItemsListIntoAdapter(itemsArray : ArrayList<ThreeTopicsItem>){
        Log.d("Adapter", itemsArray.toString())
        // set linear layout manager
        val layoutManager = LinearLayoutManager(this)
        binding.listOfItemRecycleView.layoutManager = layoutManager

        // add the items list into layout
        val adapter = itemRecycleAdapter(this, itemsArray)
        binding.listOfItemRecycleView.adapter = adapter

        val intent_view_submit_form = Intent(this, ViewTopicsSubmitForm::class.java)
        val intent_topic_submit_form = Intent(this, TopicsSubmitForm::class.java)

        // set each card listener
        adapter.setOnClickListener(object : itemRecycleAdapter.onItemClickListner{
            override fun onItemClick(position: Int) {
                // To do some things, that you want
                // Toast.makeText(this@ListOfThreeTopic, "Topic Clicked: " + itemsArray[position].topicSubmitted, Toast.LENGTH_SHORT).show(
                intent_view_submit_form.putExtra("student_detail", student_detail)
                intent_view_submit_form.putExtra("item_clicked", itemsArray[position])

                intent_topic_submit_form.putExtra("student_detail", student_detail)
                intent_topic_submit_form.putExtra("item_clicked", itemsArray[position])

                Log.d("item clicked", itemsArray[position].toString())
                when(itemsArray[position].status){
                    "PENDING"  -> startActivity(intent_topic_submit_form)
                    else -> startItemForResult.launch(intent_view_submit_form)
                }
            }
        })
    }

    // once the activity is finished than get the result
    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            when (requestCode) {
                MY_CODE_REQUEST -> {
                    val message = intent!!.getStringExtra("message").toString()

                    if(message != "null"){
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // once the activity is finished than get the result
    private fun onItemActivityResult(requestCode: Int, result: ActivityResult){
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            when (requestCode) {
                MY_ITEM_CODE_REQUEST -> {
                    val message = intent!!.getStringExtra("message").toString()

                    if(message != "null"){
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}


