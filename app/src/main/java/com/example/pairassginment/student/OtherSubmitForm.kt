package com.example.pairassginment.student

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pairassginment.databinding.ActivityOtherSubmitFormBinding
import com.example.pairassginment.student.objectClass.OtherDocumentItem
import com.example.pairassginment.student.objectClass.StudentDetail
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class OtherSubmitForm : AppCompatActivity() {
    private lateinit var binding: ActivityOtherSubmitFormBinding
    private var student_detail: StudentDetail? = null;
    private var mDB: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storageReference = FirebaseStorage.getInstance()
    private var fileUrl : Uri? = null
    private var selectFileBtnClick : Int = 0;
    private var editSubmittedDocument : Int = 0;
    private var fileNameOnly: String? = null;
    private var item_other_detail: OtherDocumentItem? = null;
    private var other_document_name: String? = null;
    private var other_document_submit_label: String? = null;
    private var circleProgress: RelativeLayout? = null

    private var MY_CODE_REQUEST: Int = 190;
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onActivityResult(MY_CODE_REQUEST, result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherSubmitFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        circleProgress = binding.circleCenterLayout
        circleProgress!!.visibility = View.GONE

        student_detail = intent.getParcelableExtra("student_detail")
        item_other_detail = intent.getParcelableExtra("item_clicked")
        other_document_name = intent.getStringExtra("other_document_name")
        other_document_submit_label = intent.getStringExtra("other_document_submit_label")

        Log.d("Student detail list", student_detail.toString())

        getStudentNameIDReady()
        getSubmissionFormLabelReady()
        getDocumentItemReadyWhenPending()
        setBtnOnClickListener()
    }

    private fun getDocumentItemReadyWhenPending(){
        if (item_other_detail != null){
            selectFileBtnClick+=1
            editSubmittedDocument+=1
            Log.d("select file btn click above", selectFileBtnClick.toString())
            binding.uploadedFileNameTv.setText(item_other_detail!!.uploadedFileOrg.toString())
            binding.studentCommentEt.setText(item_other_detail!!.studentComment.toString())
        }
    }

    private fun getSubmissionFormLabelReady(){
        binding.topicLabelTv.setText(other_document_submit_label)
    }

    private fun getStudentNameIDReady(){
        binding.studentNameIdTv.text = student_detail!!.student_name.toString() + " " + student_detail!!.student_id.toString()
    }

    private fun setBtnOnClickListener(){
        binding.uploadBtn.setOnClickListener {
            selectFile();
        }

        binding.submitBtn.setOnClickListener {
            Log.d("Image Name", fileUrl.toString())

            if(!isEmptyInput()){
                circleProgress!!.visibility = View.VISIBLE
                uploadFile();
            }else{
                Toast.makeText(this, "Must write down your comment and upload a file.", Toast.LENGTH_SHORT).show();
            }
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent()
            intent.putExtra("message", "Nothing updated")
            setResult(Activity.RESULT_OK, intent);
            finish()
        }
    }

    private fun isEmptyInput(): Boolean{
        Log.d("TAG", "abstract: "+binding.studentCommentEt.text.toString().trim());
        if(binding.studentCommentEt.text.toString().trim().isEmpty()){
            return true
        }

        if(selectFileBtnClick!! <= 0){
            Log.d("select file btn click below", selectFileBtnClick.toString())
            return true
        }

        return false
    }

    private fun uploadFile(){

        var fileRef : StorageReference? = storageReference.reference

        // it will get the file name only, if got fileUrl.lastPathSegment
        Log.d("Upload file url", fileUrl.toString())

        val student_comment = binding.studentCommentEt.text.toString()
        val date_submit = SimpleDateFormat("dd MMM yyyy").format(Date())

        if(fileUrl.toString() != "null"){
            var documentRef = fileRef!!.child("uploadedFile/${fileUrl!!.lastPathSegment}")
            val uploaded_file_org_name = fileNameOnly
            val uploaded_file_firebase = fileUrl!!.lastPathSegment
            // It will upload the file based on the url that we uploaded
            documentRef.putFile(fileUrl!!)
                .addOnSuccessListener {

                    val other_document_data = hashMapOf<String, Any>(
                        "Date_Submit" to date_submit,
                        "Student_Comment" to student_comment,
                        "File_Submitted_Org" to uploaded_file_org_name!!,
                        "File_Submitted" to uploaded_file_firebase!!,
                        "Status" to "PENDING"
                    )

                    uploadDataToDB(other_document_data)

                }
        }else if (editSubmittedDocument > 0){

            val other_document_data = hashMapOf<String, Any>(
                "Date_Submit" to date_submit,
                "Student_Comment" to student_comment,
                "Status" to "PENDING"
            )

            uploadDataToDB(other_document_data)
        }
    }

    private fun uploadDataToDB(other_document_data: HashMap<String, Any>){
        val other_document_collection = mDB.collection("Submission")

        if(editSubmittedDocument <= 0) {
            Log.d("testing above", editSubmittedDocument.toString())
            other_document_collection
                .document(student_detail!!.submission_id!!)
                .collection(other_document_name.toString())
                .add(other_document_data)
                .addOnSuccessListener { document ->
                    val document_id = document.id

                    other_document_collection
                        .document(student_detail!!.submission_id!!)
                        .collection(other_document_name.toString())
                        .document(document_id!!)
                        .update(other_document_name.toString() + "_ID", document_id)
                        .addOnCompleteListener {
                            circleProgress!!.visibility = View.GONE
                        }
                        .addOnSuccessListener {
                            val intent = Intent(this, ListOfOtherDocuments::class.java)
                            intent.putExtra("message", "Document Created")
                            intent.putExtra("other_document_name", other_document_name)
                            intent.putExtra("student_detail", student_detail)
                            intent.putExtra(
                                "other_document_submit_label",
                                other_document_submit_label
                            )
                            startActivity(intent)
                            finish()
                        }
                }
        }else{
            Log.d("testing below", editSubmittedDocument.toString())
            Log.d("testing submission id", student_detail!!.submission_id!!.toString())
            Log.d("testing document id", item_other_detail!!.documentID.toString())

            other_document_collection
                .document(student_detail!!.submission_id!!)
                .collection(item_other_detail!!.documentType.toString())
                .document(item_other_detail!!.documentID.toString())
                .update(other_document_data)
                .addOnCompleteListener {
                    circleProgress!!.visibility = View.GONE
                }
                .addOnSuccessListener {
                    val intent = Intent(this, ListOfOtherDocuments::class.java)
                    intent.putExtra("message", "Document updated")
                    intent.putExtra("other_document_name", other_document_name)
                    intent.putExtra("student_detail", student_detail)
                    intent.putExtra(
                        "other_document_submit_label",
                        other_document_submit_label
                    )
                    startActivity(intent)
                    finish()

                }.addOnFailureListener{
                    Log.d("Failed", "Failde to upload a file")
                }
        }
    }

    private fun selectFile(){
        var intent = Intent();

        // if you want find image => "image/* , word or pdf => "application/*
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startForResult.launch(intent)
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            when (requestCode) {
                MY_CODE_REQUEST -> {
                    fileUrl = intent!!.data
                    fileNameOnly = getFileNameFromUri(this, fileUrl!!)
                    binding.uploadedFileNameTv.text = fileNameOnly
                    selectFileBtnClick++
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val fileName: String?
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor?.close()
        return fileName
    }
}
