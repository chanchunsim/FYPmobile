package com.example.pairassginment.student

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pairassginment.R
import com.example.pairassginment.databinding.ActivityViewOtherSubmitFormBinding
import com.example.pairassginment.student.objectClass.OtherDocumentItem
import com.example.pairassginment.student.objectClass.StudentDetail

class ViewOtherSubmitForm : AppCompatActivity() {
    private lateinit var binding: ActivityViewOtherSubmitFormBinding;
    private var other_document_detail: OtherDocumentItem? = null;
    private var student_detail: StudentDetail? = null;
    private var other_document_view_label: String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewOtherSubmitFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        other_document_detail = intent.getParcelableExtra("item_clicked")
        student_detail = intent.getParcelableExtra("student_detail")
        other_document_view_label = intent.getStringExtra("other_document_view_label")

//        Log.d("TAG", "item: "+ item_topics)
        binding.studentNameIdTv.text = student_detail!!.student_name.toString() + " " + student_detail!!.student_id.toString()
        binding.studentCommentTv.text = other_document_detail!!.studentComment
        binding.uploadedFileNameTv.text = other_document_detail!!.uploadedFileOrg
        binding.supervisorCommentTv.text = other_document_detail!!.supervisorComment
        binding.documentLabelTv.text = other_document_view_label

        when(other_document_detail!!.submittedStatus){
            "APPROVED" ->
            {
                binding.studentCommentTv.setBackgroundColor(this.getColor(R.color.approved_green))
                binding.uploadedFileNameTv.setBackgroundColor(this.getColor(R.color.approved_green))
                binding.supervisorCommentTv.setBackgroundColor(this.getColor(R.color.approved_green))
            }

            else ->
            {
                binding.studentCommentTv.setBackgroundColor(this.getColor(R.color.rejected_red))
                binding.uploadedFileNameTv.setBackgroundColor(this.getColor(R.color.rejected_red))
                binding.supervisorCommentTv.setBackgroundColor(this.getColor(R.color.rejected_red))
            }
        }
        binding.backBtn.setOnClickListener{
            val intent = Intent(this, ViewOtherSubmitForm::class.java)
            setResult(Activity.RESULT_OK, intent)
            finish();
        }
    }
}