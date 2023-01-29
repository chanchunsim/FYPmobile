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

import com.example.pairassginment.student.objectClass.OtherDocumentItem
import com.example.pairassginment.student.objectClass.StudentDetail
import com.google.firebase.firestore.FirebaseFirestore

class ListOfOtherDocuments : AppCompatActivity() {

    private lateinit var otherDocumentDetailArray: ArrayList<OtherDocumentItem>
    private val MY_CODE_REQUEST: Int = 0;
    private val MY_ITEM_CODE_REQUEST: Int = 10;
    private var student_detail: StudentDetail? = null;
    private var other_document_name: String? = null;
    private var other_document_submit_label: String? = null;
    private var other_document_view_label: String? = null;
    private var mDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var circleProgress: RelativeLayout? = null

    // register an intent that will result a result
    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onActivityResult(MY_CODE_REQUEST, result)
    }

    val startItemForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onItemActivityResult(MY_ITEM_CODE_REQUEST, result)
        Log.d("Result item ", "Running")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityListOfItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        circleProgress = binding.circleCenterLayout
        circleProgress!!.visibility = View.GONE

        otherDocumentDetailArray = ArrayList()
//        addItemsListIntoAdapter(itemsArray);

        student_detail = intent.getParcelableExtra<StudentDetail>("student_detail")
        other_document_name = intent.getStringExtra("other_document_name")
        other_document_view_label = intent.getStringExtra("other_document_view_label")
        other_document_submit_label = intent.getStringExtra("other_document_submit_label")

        Log.d("document label", other_document_view_label.toString())
        Log.d("other_document_submit_label", other_document_submit_label.toString())

        // after sumbit a new one data
        val message = intent!!.getStringExtra("message").toString()
        Log.d("message", message)
        if (message != "null") {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        getStudentNameIDReady()
        getProposalPPTDetail()
    }

    private fun getStudentNameIDReady(){
        binding.studentNameIdTv.text = student_detail!!.student_name.toString() + " " + student_detail!!.student_id.toString()
    }

    private fun getProposalPPTDetail(){
        circleProgress!!.visibility = View.VISIBLE
        mDB.collection("Submission")
            .document(student_detail?.submission_id!!)
            .collection(other_document_name.toString())
            .get()
            .addOnCompleteListener {
                circleProgress!!.visibility = View.GONE
            }
            .addOnSuccessListener { documents ->
                if(documents != null){
                    for (document in documents){
                        if (document != null){
                            val dataMap:MutableMap<String, Any> = document.data

                            val date_submit = dataMap["Date_Submit"].toString()
                            val student_comment = dataMap["Student_Comment"].toString()
                            val file_submitted_org = dataMap["File_Submitted_Org"].toString()
                            val file_submitted = dataMap["File_Submitted"].toString();
                            var supervisor_comment = dataMap["Supervisor_Comment"].toString()
                            var status = dataMap["Status"].toString();
                            var data_feedback = dataMap["Date_Feedback"].toString()
                            var document_id = dataMap[other_document_name.toString()+"_ID"].toString();
                            var document_type = other_document_name.toString()

                            otherDocumentDetailArray.add(OtherDocumentItem(student_comment, date_submit, data_feedback, file_submitted_org, file_submitted,
                                status, supervisor_comment, document_id, document_type))
                        }
                    }
                }
                Log.d("Go to adapter", otherDocumentDetailArray.toString())
                addItemsListIntoAdapter(otherDocumentDetailArray)
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
            val intent = Intent(this, OtherSubmitForm::class.java)
            // after registered and clicked the btn then get the intent launch
            intent.putExtra("student_detail", student_detail)
            intent.putExtra("other_document_name", other_document_name)
            intent.putExtra("other_document_submit_label", other_document_submit_label)
            startForResult.launch(intent)
        }
    }



    fun addItemsListIntoAdapter(itemsArray : ArrayList<OtherDocumentItem>){
        // set linear layout manager
        val layoutManager = LinearLayoutManager(this)
        binding.listOfItemRecycleView.layoutManager = layoutManager

        // add the items list into layout
        val adapter = OtherDocumentAdapter(this, itemsArray)
        binding.listOfItemRecycleView.adapter = adapter


        val intent_view_other_submit_form = Intent(this, ViewOtherSubmitForm::class.java)
        val intent_other_submit_form = Intent(this, OtherSubmitForm::class.java)

        // set each card listener
        adapter.setOnClickListener(object : OtherDocumentAdapter.onItemClickListner{
            override fun onItemClick(position: Int) {
                // To do some things, that you want
                // Toast.makeText(this@ListOfThreeTopic, "Topic Clicked: " + itemsArray[position].topicSubmitted, Toast.LENGTH_SHORT).show(
                intent_view_other_submit_form.putExtra("item_clicked", itemsArray[position])
                intent_view_other_submit_form.putExtra("student_detail", student_detail)
                intent_view_other_submit_form.putExtra("other_document_view_label", other_document_view_label)

                intent_other_submit_form.putExtra("item_clicked", itemsArray[position])
                intent_other_submit_form.putExtra("student_detail", student_detail)
                intent_other_submit_form.putExtra("other_document_name", other_document_name)
                intent_other_submit_form.putExtra("other_document_submit_label", other_document_submit_label)

                when(itemsArray[position].submittedStatus){
                    "PENDING"  -> startForResult.launch(intent_other_submit_form)
                    else -> startItemForResult.launch(intent_view_other_submit_form)
                }
            }
        })
    }

    // once the activity is finished than get the result
    private fun onItemActivityResult(requestCode: Int, result: ActivityResult){
        Log.d("result.resultCode", result.resultCode.toString())
        Log.d("requestCode", requestCode.toString())
        Log.d("Activity.RESULT_OK", Activity.RESULT_OK.toString())

        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            when (requestCode) {
                MY_ITEM_CODE_REQUEST -> {
                    val message = "Update Successfully"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // once the activity is finished than get the result
    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            when (requestCode) {
                MY_CODE_REQUEST -> {
                    val message = intent!!.getStringExtra("message").toString()
                    Log.d("Message", message)

                    if (message != "null") {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}