package com.example.pairassginment.student

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.pairassginment.R
import com.example.pairassginment.databinding.ActivityDashboardBinding
import com.example.pairassginment.student.objectClass.BatchDeadline
import com.example.pairassginment.student.objectClass.OtherDocumentArrayList
import com.example.pairassginment.student.objectClass.StudentDetail
import com.google.firebase.firestore.FirebaseFirestore
import com.transferwise.sequencelayout.SequenceStep
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    private var mDB: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var student_detail: StudentDetail = StudentDetail()
    private var batch_deadline: BatchDeadline = BatchDeadline()

    var firstStep: SequenceStep? = null
    var secondStep: SequenceStep? = null
    var thirdStep: SequenceStep? = null
    var fourthStep: SequenceStep? = null
    var fifthStep: SequenceStep? = null
    var sixthStep: SequenceStep? = null
    var seventhStep: SequenceStep? = null

    private var topics_submit_btn: Button? = null;
    private var topics_detail_btn: Button? = null;
    private var proposal_ppt_submit_btn: Button? = null;
    private var proposal_ppt_detail_btn: Button? = null;
    private var proposal_submit_btn: Button? = null;
    private var proposal_detail_btn: Button? = null;
    private var final_draft_submit_btn: Button? = null;
    private var final_draft_detail_btn: Button? = null;
    private var final_ppt_submit_btn: Button? = null;
    private var final_ppt_detial_btn: Button? = null;
    private var final_thesis_submit_btn: Button? = null;
    private var final_thesis_detail_btn: Button? = null;
    private var poster_submit_btn: Button? = null;
    private var poster_detial_btn: Button? = null;
    private var other_ducument_tool: ArrayList<OtherDocumentArrayList>? = null;
    private var hasContinue = true
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        topics_submit_btn = binding.topicsSubmitBtn
        topics_detail_btn = binding.topicsDetailBtn
        proposal_ppt_submit_btn = binding.proposalPptSlideSubmitBtn
        proposal_ppt_detail_btn = binding.proposalPptSlideDetailBtn
        final_draft_submit_btn = binding.finalDraftSubmitBtn
        final_draft_detail_btn = binding.finalDraftDetailBtn
        final_ppt_submit_btn = binding.finalPptSlideSubmitBtn
        final_ppt_detial_btn = binding.finalPptSlideDetailBtn

        poster_submit_btn = binding.posterSubmitBtn
        poster_detial_btn = binding.posterDetailBtn

        firstStep = binding.firstStep
        secondStep = binding.secondStep
        fourthStep = binding.fourthStep
        fifthStep = binding.fifthStep
        seventhStep = binding.seventhStep

        other_ducument_tool = ArrayList();
        other_ducument_tool!!.add(OtherDocumentArrayList("Proposal_PPT", secondStep, proposal_ppt_submit_btn, proposal_ppt_detail_btn, "UPLOAD PRESENTATION SLIDE", "VIEW PRESENTATION SLIDE"))
        other_ducument_tool!!.add(OtherDocumentArrayList("Proposal", thirdStep, proposal_submit_btn, proposal_detail_btn, "UPLOAD FINAL PROPOSAL", "VIEW FINAL PROPOSAL"))
        other_ducument_tool!!.add(OtherDocumentArrayList("Final_Draft", fourthStep, final_draft_submit_btn, final_draft_detail_btn, "UPLOAD FINAL THESIS DRAFT", "VIEW FINAL THESIS DRAFT"))
        other_ducument_tool!!.add(OtherDocumentArrayList("Final_PPT", fifthStep, final_ppt_submit_btn, final_ppt_detial_btn, "UPLOAD FINAL THESIS PRESENTATION SLIDES", "VIEW FINAL PRESENTATION SLIDES"))
        other_ducument_tool!!.add(OtherDocumentArrayList("Final_Thesis", sixthStep, final_thesis_submit_btn, final_thesis_detail_btn, "UPLOAD FINAL THESIS REPORT", "VIEW FINAL THESIS REPORT"))
        other_ducument_tool!!.add(OtherDocumentArrayList("Poster", seventhStep, poster_submit_btn, poster_detial_btn, "UPLOAD FYP POSTER", "VIEW FYP POSTER"))


        getStudentDetail()
    }

    private fun getStudentNameIDReady(){
        binding.studentNameIdTv.text = student_detail.student_name.toString() + " " + student_detail.student_id.toString()
    }

    private fun getSequenceStepReady(){
        Log.d("Batch Detail", student_detail.toString())

        mDB.collection("Batch")
            .document(student_detail.batch_id!!)
            .get()
            .addOnCompleteListener{
                Log.d("Batch Detail", "Run complete")
            }
            .addOnSuccessListener { document ->
                Log.d("Batch Detail", ""+document.data)
                if(document != null){
                    batch_deadline.intake_mnt_year = document.data?.get("Intake_MntYear").toString();

                    batch_deadline.topics_begin = document.data?.get("Topics_Begin").toString();
                    batch_deadline.proposal_ppt_begin = document.data?.get("Proposal_PPT_Begin").toString();
                    batch_deadline.proposal_begin = document.data?.get("Proposal_Begin").toString();
                    batch_deadline.final_draft_begin = document.data?.get("Final_Draft_Begin").toString();
                    batch_deadline.final_ppt_begin = document.data?.get("Final_PPT_Begin").toString();
                    batch_deadline.final_thesis_begin = document.data?.get("Final_Thesis_Begin").toString();
                    batch_deadline.poster_begin = document.data?.get("Poster_Begin").toString();

                    batch_deadline.topics_deadline = document.data?.get("Topics_Deadline").toString();
                    batch_deadline.proposal_ppt_dealine = document.data?.get("Proposal_PPT_Deadline").toString();
                    batch_deadline.proposal_deadline = document.data?.get("Proposal_Deadline").toString();
                    batch_deadline.final_draft_deadline = document.data?.get("Final_Draft_Deadline").toString();
                    batch_deadline.final_ppt_deadline = document.data?.get("Final_PPT_Deadline").toString();
                    batch_deadline.final_thesis_deadline = document.data?.get("Final_Thesis_Deadline").toString();
                    batch_deadline.poster_deadline = document.data?.get("Poster_Deadline").toString();

                    setSequenceStepUI()
                    getNotification()
                }
            }
    }

    private fun setSequenceStepUI(){
        firstStep!!.setActive(true)
        firstStep!!.setAnchor(batch_deadline.topics_begin)
        firstStep!!.setSubtitle("DEADLINE: " + batch_deadline.topics_deadline + "\n\n" + "STATUS: WAITING" + "\n\n" + "SUBMITTED: 0/3" )

//        secondStep!!.setActive(false)
        secondStep!!.setAnchor(batch_deadline.proposal_ppt_begin)
        secondStep!!.setSubtitle("DEADLINE: " + batch_deadline.proposal_ppt_dealine + "\n\n" + "STATUS: WAITING" + "\n\n" + "SUBMITTED: 0" )

//        thirdStep!!.setActive(false)
        thirdStep!!.setAnchor(batch_deadline.proposal_begin)
        thirdStep!!.setSubtitle("DEADLINE: " + batch_deadline.proposal_deadline + "\n\n" + "STATUS: WAITING" + "\n\n" + "SUBMITTED: 0" )

//        fourthStep!!.setActive(false)
        fourthStep!!.setAnchor(batch_deadline.final_draft_begin)
        fourthStep!!.setSubtitle("DEADLINE: " + batch_deadline.final_draft_deadline + "\n\n" + "STATUS: WAITING" + "\n\n" + "SUBMITTED: 0" )

//        fifthStep!!.setActive(false)
        fifthStep!!.setAnchor(batch_deadline.final_ppt_begin)
        fifthStep!!.setSubtitle("DEADLINE: " + batch_deadline.final_ppt_deadline + "\n\n" + "STATUS: WAITING" + "\n\n" + "SUBMITTED: 0" )

//        sixthStep!!.setActive(false)
        sixthStep!!.setAnchor(batch_deadline.final_thesis_begin)
        sixthStep!!.setSubtitle("DEADLINE: " + batch_deadline.final_thesis_deadline + "\n\n" + "STATUS: WAITING" + "\n\n" + "SUBMITTED: 0" )

//        seventhStep!!.setActive(false)
        seventhStep!!.setAnchor(batch_deadline.poster_begin)
        seventhStep!!.setSubtitle("DEADLINE: " + batch_deadline.poster_deadline + "\n\n" + "STATUS: WAITING" + "\n\n" + "SUBMITTED: 0" )

        topics_submit_btn?.visibility = View.VISIBLE
        topics_detail_btn?.visibility = View.GONE
        proposal_ppt_submit_btn?.visibility = View.GONE
        proposal_ppt_detail_btn?.visibility = View.GONE
        proposal_submit_btn?.visibility = View.GONE
        proposal_detail_btn?.visibility = View.GONE
        final_draft_submit_btn?.visibility = View.GONE
        final_draft_detail_btn?.visibility = View.GONE
        final_ppt_submit_btn?.visibility = View.GONE
        final_ppt_detial_btn?.visibility = View.GONE
        final_thesis_submit_btn?.visibility = View.GONE
        final_thesis_detail_btn?.visibility = View.GONE
        poster_submit_btn?.visibility = View.GONE
        poster_detial_btn?.visibility = View.GONE

        getTopicSubmissionDetail()
    }

    private fun getTopicSubmissionDetail(){
        if(student_detail.submission_id != null){
            mDB.collection("Submission")
                .document(student_detail.submission_id!!)
                .collection("Topics")
                .get()
                .addOnSuccessListener { documents ->
                    val status_array:ArrayList<String> = ArrayList()
                    var topic_status:String? = null;
                    val topic_size:Int = documents.size()
                    Log.d("Submission Detail", topic_size.toString())

                    for(document in documents){
                        Log.d("Submission Detail", document.data["Status"].toString())
                        status_array!!.add(document.data["Status"].toString())
                    }

                    topic_status = if(status_array!!.contains("APPROVED")){
                                    "APPROVED"
                                }else if (status_array!!.contains("PENDING")){
                                    "PENDING"
                                }else{
                                    "REJECTED"
                                }

                    val intentDetailList = Intent(this@Dashboard, ListOfThreeTopic::class.java)
                    val intentSubmitForm = Intent(this@Dashboard, TopicsSubmitForm::class.java)

                    if(topic_size > 0){
                        firstStep!!.setSubtitle("DEADLINE: " + batch_deadline.topics_deadline + "\n\n" + "STATUS: "+topic_status+ "\n\n" + "SUBMITTED: " + topic_size.toString() + "/3" )

                        if(topic_status == "APPROVED"){
                            topics_detail_btn?.visibility = View.VISIBLE
                            topics_submit_btn?.visibility = View.GONE

                            topics_detail_btn!!.setOnClickListener {
                                intentDetailList.putExtra("student_detail", student_detail)
                                startActivity(intentDetailList)
                            }

                            firstStep!!.setActive(false)
                            secondStep!!.setActive(true)
                            firstStep!!.setTitleTextAppearance(R.style.vertical_progress_bar_title_approved)
                            loopOtherSubmission()

                            Log.d("second step", secondStep.toString())

                        }else{
                            when(topic_status){
                                "PENDING" -> firstStep!!.setTitleTextAppearance(R.style.vertical_progress_bar_title_waiting)
                                else -> firstStep!!.setTitleTextAppearance(R.style.vertical_progress_bar_title_rejected)
                            }

                            topics_detail_btn?.visibility = View.VISIBLE
                            topics_submit_btn?.visibility = View.VISIBLE

                            topics_detail_btn!!.setOnClickListener {
                                intentDetailList.putExtra("student_detail", student_detail)
                                startActivity(intentDetailList)
                            }

                            topics_submit_btn!!.setOnClickListener {
                                intentSubmitForm.putExtra("student_detail", student_detail)
                                startActivity(intentSubmitForm)
                            }
                        }
                    }else{
                        val date_format = SimpleDateFormat("dd MMM yyyy")
                        val current_date = date_format.parse(date_format.format(Date()))
                        val compare_result = date_format.parse(batch_deadline!!.topics_deadline!!).compareTo(current_date)

                        when{
                            compare_result < 0 -> {
                                firstStep!!.setTitleTextAppearance(R.style.vertical_progress_bar_title_overdue)
                                firstStep!!.setSubtitle("DEADLINE: " + batch_deadline.topics_deadline + "\n\n" + "STATUS: OVERDUE\n\n" + "SUBMITTED: " + topic_size.toString() + "/3" )
                            }
                            else ->  firstStep!!.setTitleTextAppearance(R.style.vertical_progress_bar_title_waiting)
                        }
                    }
                }
        }else{
            val date_format = SimpleDateFormat("dd MMM yyyy")
            val current_date = date_format.parse(date_format.format(Date()))
            val compare_result = date_format.parse(batch_deadline!!.topics_deadline!!).compareTo(current_date)

            when{
                compare_result < 0 -> {
                    firstStep!!.setTitleTextAppearance(R.style.vertical_progress_bar_title_overdue)
                    firstStep!!.setSubtitle("DEADLINE: " + batch_deadline.topics_deadline + "\n\n" + "STATUS: OVERDUE\n\n" + "SUBMITTED: 0/3" )
                }
                else ->  firstStep!!.setTitleTextAppearance(R.style.vertical_progress_bar_title_waiting)
            }


            topics_submit_btn!!.setOnClickListener {
                val intentSubmitForm = Intent(this@Dashboard, TopicsSubmitForm::class.java)
                intentSubmitForm.putExtra("student_detail", student_detail)
                Log.d("topics btn", "topics btn")
                startActivity(intentSubmitForm)
            }
        }
    }

     private fun loopOtherSubmission(){

        if (hasContinue){
            hasContinue = false

            if(other_ducument_tool!!.size > index) {
                getOtherSubmissionDetail(index)
                index++
            }
        }
    }

     private fun getOtherSubmissionDetail(index: Int){
         Log.d("has continue 2", hasContinue.toString())
         Log.d("document name", other_ducument_tool!![index].other_document_name!!)
        mDB.collection("Submission")
            .document(student_detail.submission_id!!)
            .collection(other_ducument_tool!![index].other_document_name!!)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(other_ducument_tool!![index].other_document_name!! + " Submission Detail", documents.size().toString())
                if (documents.size() > 0){
                    val status_array:ArrayList<String> = ArrayList()
                    var other_documents_status:String? = null;
                    val other_documents_size:Int = documents.size()
                    Log.d(other_ducument_tool!![index].other_document_name!! + " Submission Detail", other_documents_size.toString())

                    for(document in documents){
                        Log.d(other_ducument_tool!![index].other_document_name!! + " Submission Detail", document.data["Status"].toString())
                        status_array!!.add(document.data["Status"].toString())
                    }

                    other_documents_status = if(status_array!!.contains("APPROVED")){
                        "APPROVED"
                    }else if (status_array!!.contains("PENDING")){
                        "PENDING"
                    }else{
                        "REJECTED"
                    }

                    val intentDetailList = Intent(this@Dashboard, ListOfOtherDocuments::class.java)
                    val intentSubmitForm = Intent(this@Dashboard, OtherSubmitForm::class.java)

                    other_ducument_tool!![index].other_sequence_step!!.setSubtitle("DEADLINE: " + batch_deadline.proposal_ppt_dealine + "\n\n" + "STATUS: "+other_documents_status+ "\n\n" + "SUBMITTED: " + other_documents_size.toString())

                    if(other_documents_status == "APPROVED"){
                        other_ducument_tool!![index].other_document_detail_btn?.visibility = View.VISIBLE
                        other_ducument_tool!![index].other_document_submit_btn?.visibility = View.GONE

                        other_ducument_tool!![index].other_document_detail_btn!!.setOnClickListener {
                            intentDetailList.putExtra("student_detail", student_detail)
                            intentDetailList.putExtra("other_document_name", other_ducument_tool!![index].other_document_name)
                            intentDetailList.putExtra("other_document_submit_label", other_ducument_tool!![index].other_document_submit_label)
                            intentDetailList.putExtra("other_document_view_label", other_ducument_tool!![index].other_document_view_label)
                            startActivity(intentDetailList)
                        }

                        other_ducument_tool!![index].other_sequence_step!!.setActive(false)

                        if(other_ducument_tool!!.size > index+1){
                            other_ducument_tool!![index+1].other_sequence_step!!.setActive(true)
                        }

                        other_ducument_tool!![index].other_sequence_step!!
                            .setTitleTextAppearance(R.style.vertical_progress_bar_title_approved)

                        hasContinue = true
                        loopOtherSubmission()

                    }else{
                        when(other_documents_status){
                            "PENDING" ->  other_ducument_tool!![index].other_sequence_step!!.setTitleTextAppearance(R.style.vertical_progress_bar_title_waiting)
                            else ->  other_ducument_tool!![index].other_sequence_step!!.setTitleTextAppearance(R.style.vertical_progress_bar_title_rejected)
                        }

                        other_ducument_tool!![index].other_document_detail_btn?.visibility = View.VISIBLE
                        other_ducument_tool!![index].other_document_submit_btn?.visibility = View.VISIBLE

                        other_ducument_tool!![index].other_document_detail_btn!!.setOnClickListener {
                            intentDetailList.putExtra("student_detail", student_detail)
                            intentDetailList.putExtra("other_document_name", other_ducument_tool!![index].other_document_name)
                            intentDetailList.putExtra("other_document_submit_label", other_ducument_tool!![index].other_document_submit_label)
                            intentDetailList.putExtra("other_document_view_label", other_ducument_tool!![index].other_document_view_label)
                            startActivity(intentDetailList)
                        }

                        other_ducument_tool!![index].other_document_submit_btn!!.setOnClickListener {
                            intentSubmitForm.putExtra("student_detail", student_detail)
                            intentSubmitForm.putExtra("other_document_name", other_ducument_tool!![index].other_document_name)
                            intentSubmitForm.putExtra("other_document_submit_label", other_ducument_tool!![index].other_document_submit_label)
                            intentSubmitForm.putExtra("other_document_view_label", other_ducument_tool!![index].other_document_view_label)
                            startActivity(intentSubmitForm)
                        }
                    }

                }else {
                    val date_format = SimpleDateFormat("dd MMM yyyy")
                    val current_date = date_format.parse(date_format.format(Date()))
                    val compare_result = date_format.parse(batch_deadline!!.topics_deadline!!).compareTo(current_date)

                    when{
                        compare_result < 0 -> {
                            other_ducument_tool!![index].other_sequence_step!!.setTitleTextAppearance(R.style.vertical_progress_bar_title_overdue)
                            other_ducument_tool!![index].other_sequence_step!!.setSubtitle("DEADLINE: " + batch_deadline.topics_deadline + "\n\n" + "STATUS: OVERDUE\n\n" + "SUBMITTED: 0" )
                        }
                        else ->  other_ducument_tool!![index].other_sequence_step!!.setTitleTextAppearance(R.style.vertical_progress_bar_title_waiting)
                    }

                    other_ducument_tool!![index].other_document_submit_btn?.visibility = View.VISIBLE

                    other_ducument_tool!![index].other_document_submit_btn!!.setOnClickListener {
                        val intentSubmitForm = Intent(this@Dashboard, OtherSubmitForm::class.java)
                        intentSubmitForm.putExtra("student_detail", student_detail)
                        intentSubmitForm.putExtra("other_document_name", other_ducument_tool!![index].other_document_name)
                        intentSubmitForm.putExtra("other_document_submit_label", other_ducument_tool!![index].other_document_submit_label)
                        intentSubmitForm.putExtra("other_document_view_label", other_ducument_tool!![index].other_document_view_label)
                        startActivity(intentSubmitForm)
                    }
                }
            }
    }

    private fun getStudentDetail(){
        student_detail.role_id = intent.getStringExtra("role_id").toString()
        Log.d("Student detail", "Above"+student_detail.role_id!!)

        mDB.collection("Students")
            .document(student_detail.role_id!!)
            .get()
            .addOnCompleteListener {
                Log.d("Student detail", "run completed")
            }
            .addOnSuccessListener { document ->
                Log.d("Student detail", ""+document.data.toString())
                if (document != null){
                    Log.d("Student detail", "Success to retrieve student detail")
                    Log.d("Student detail", document.data.toString())

                    student_detail.student_name = document.data?.get("Name").toString()
                    student_detail.student_id = document.data?.get("Student_ID").toString()

                    val dataMap : MutableMap<String, Any>? = document.data
//                    Log.d("dataMap", dataMap.toString())

                    if(dataMap!!.containsKey("Batch_ID")){
                        student_detail.batch_id = dataMap["Batch_ID"].toString()
                    }

                    if(dataMap!!.containsKey("Mark_ID")){
                        student_detail.mark_id = dataMap["Mark_ID"].toString()
                    }

                    if(dataMap!!.containsKey("Submission_ID")){
                        student_detail.submission_id = dataMap["Submission_ID"].toString()
                    }

                    getStudentNameIDReady()
                    getSequenceStepReady()

                }
            }.addOnFailureListener{
                Log.d("Student detail", "Failure to retrieve student detail")
            }
    }

    private fun overDueSubmission(){

    }

    private fun getNotification(){
        mDB.collection("Batch/")
            .whereEqualTo("Final_Draft_Deadline", "30 Sep 2022")
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null){
                    for(document in documents){
                        Log.d("batch detail id", document.id)
                        Log.d("batch detail", document.data.toString())
                    }
                }
            }


    }
}