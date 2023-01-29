package com.example.pairassginment.student

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.pairassginment.R
import com.example.pairassginment.databinding.ActivityTopicsSubmitFormBinding
import com.example.pairassginment.student.objectClass.StudentDetail
import com.example.pairassginment.student.objectClass.ThreeTopicsItem
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class TopicsSubmitForm : AppCompatActivity() {
    private lateinit var binding: ActivityTopicsSubmitFormBinding
    private var student_detail: StudentDetail? = null;
    private var item_topics_detail: ThreeTopicsItem? = null;
    private var topics_title_et: EditText? = null;
    private var abstract_et: EditText? =null;
    private var mDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var circleProgress: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicsSubmitFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topics_title_et = binding.topicTitleEt
        abstract_et = binding.abstractsEt
        circleProgress = binding.circleCenterLayout
        circleProgress!!.visibility = View.GONE

        student_detail = intent.getParcelableExtra<StudentDetail>("student_detail")
        item_topics_detail = intent.getParcelableExtra<ThreeTopicsItem>("item_clicked")

        Log.d("student_detail", student_detail.toString())
        Log.d("item_topics detail", item_topics_detail.toString())

        getStudentNameIDReady()
        getTopicsItemReadyWhenPending()
        setBtnListener()
    }

    private fun getTopicsItemReadyWhenPending(){
        if (item_topics_detail != null){
            topics_title_et!!.setText(item_topics_detail!!.title.toString())
            abstract_et!!.setText(item_topics_detail!!.abstract.toString())
        }
    }

    private fun getStudentNameIDReady(){
        binding.studentNameIdTv.text = student_detail!!.student_name.toString() + " " + student_detail!!.student_id.toString()
    }

    private fun setBtnListener(){
        binding.submitBtn.setOnClickListener{
            Log.d("Submit check", submitOperation().toString())
            when (submitOperation()){
                true -> {
                    circleProgress!!.visibility = View.VISIBLE
                    updateOrAddTopicsDataToDB()
                }
                false -> {
                    Toast.makeText(this, "All filed must be fill up, empty is not allowed!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(this@TopicsSubmitForm, ListOfThreeTopic::class.java)
            intent.putExtra("message", "Nothing updated")
            setResult(Activity.RESULT_OK, intent);
            finish()
        }
    }

    private fun updateOrAddTopicsDataToDB(){
        var randomNumber: String? = null;

        if(student_detail!!.submission_id == null){
            randomNumber = UUID.randomUUID().toString();
        }else{
            randomNumber = student_detail!!.submission_id
        }

        val title = binding.topicTitleEt.text.toString()
        val abstract = binding.abstractsEt.text.toString()
        val date_submit = SimpleDateFormat("dd MMM yyyy").format(Date())
        val topics_collection = mDB.collection("Submission")
//                                    .document(student_detail!!.submission_id!!)
//                                    .collection("Topics")

        Log.d("Update DB", item_topics_detail.toString())

        if(item_topics_detail != null){
            topics_collection
                .document(student_detail!!.submission_id!!)
                .collection("Topics")
                .document(item_topics_detail!!.topic_id!!)
                .update("Title", title, "Abstract", abstract, "Data_Submit", date_submit)
                .addOnCompleteListener{
                    circleProgress!!.visibility = View.GONE
                }
                .addOnSuccessListener {
                    val intent = Intent(this@TopicsSubmitForm, ListOfThreeTopic::class.java)
                    intent.putExtra("message", "Document updated")
                    intent.putExtra("student_detail", student_detail)
                    startActivity(intent)
                    finish()
                }
        }else {
            val topicData = hashMapOf<String, Any>(
                "Abstract" to abstract,
                "Date_Submit" to date_submit,
                "Title" to title,
                "Status" to "PENDING"
            )

            topics_collection
                .document(randomNumber!!)
                .collection("Topics")
                .add(topicData)
                .addOnSuccessListener { document ->
                    val document_id = document.id
                    Log.d("Document id", document_id)
                    topics_collection
                        .document(randomNumber)
                        .collection("Topics")
                        .document(document_id)
                        .update("Topics_ID", document_id)
                        .addOnSuccessListener {
                            mDB.collection("Students")
                                .document(student_detail!!.role_id!!)
                                .update("Submission_ID", randomNumber)
                                .addOnCompleteListener{
                                    circleProgress!!.visibility = View.GONE
                                }
                                .addOnSuccessListener {
                                    val intent = Intent(this@TopicsSubmitForm, ListOfThreeTopic::class.java)
                                    student_detail!!.submission_id = randomNumber;
                                    intent.putExtra("message", "Document updated")
                                    intent.putExtra("student_detail", student_detail)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                }
        }
    }

    // whatever success or fail, pass a message to notice
    private fun submitOperation(): Boolean{
        Log.d("TAG", "title: "+binding.topicTitleEt.text.toString().trim());
        Log.d("TAG", "abstract: "+binding.abstractsEt.text.toString().trim());
        if(binding.topicTitleEt.text.toString().trim().isEmpty() ||
                binding.abstractsEt.text.toString().trim().isEmpty()){
            return false
        }
        return true
    }
}