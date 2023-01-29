package com.example.pairassginment.student

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.pairassginment.R
import com.example.pairassginment.databinding.ActivityListOfItemBinding
import com.example.pairassginment.databinding.ActivityViewTopicsSubmitFormBinding
import com.example.pairassginment.student.objectClass.StudentDetail
import com.example.pairassginment.student.objectClass.ThreeTopicsItem

class ViewTopicsSubmitForm : AppCompatActivity() {
    private lateinit var binding: ActivityViewTopicsSubmitFormBinding;
    private var item_topics_detail: ThreeTopicsItem? = null;
    private var student_detail: StudentDetail? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewTopicsSubmitFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Log.d("Above item topics detail", intent.getParcelableExtra<ThreeTopicsItem>("item_clicked").toString())

        item_topics_detail = intent.getParcelableExtra("item_clicked")
        student_detail = intent.getParcelableExtra("student_detail")

//        Log.d("TAG", "item: "+ item_topics)
        binding.studentNameIdTv.text = student_detail!!.student_name.toString() + " " + student_detail!!.student_id.toString()
        binding.topicTitleTv.text = item_topics_detail!!.title
        binding.abstractsTv.text = item_topics_detail!!.abstract
        binding.supervisorCommentTv.text = item_topics_detail!!.supervisor_comment
        binding.documentLabelTv.text = "VIEW TITLE SUBMITTED"

        Log.d("item topics detail", item_topics_detail.toString())
        when(item_topics_detail!!.status){
            "APPROVED" ->
            {
                binding.topicTitleTv.setBackgroundColor(this.getColor(R.color.approved_green))
                binding.abstractsTv.setBackgroundColor(this.getColor(R.color.approved_green))
                binding.supervisorCommentTv.setBackgroundColor(this.getColor(R.color.approved_green))
            }

            else ->
            {
                binding.topicTitleTv.setBackgroundColor(this.getColor(R.color.rejected_red))
                binding.abstractsTv.setBackgroundColor(this.getColor(R.color.rejected_red))
                binding.supervisorCommentTv.setBackgroundColor(this.getColor(R.color.rejected_red))
            }
        }
        binding.backBtn.setOnClickListener{
            val intent = Intent(this, ListOfThreeTopic::class.java)
            setResult(Activity.RESULT_OK, intent)
            finish();
        }
    }
}