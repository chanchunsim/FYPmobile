package com.example.pairassginment.student.objectClass

data class BatchDeadline (
    var intake_mnt_year: String? = null,

    var topics_begin: String? = null,
    var proposal_ppt_begin: String? = null,
    var proposal_begin: String? = null,
    var final_draft_begin: String? = null,
    var final_ppt_begin: String? = null,
    var final_thesis_begin: String? = null,
    var poster_begin: String? = null,

    var topics_deadline: String? = null,
    var proposal_ppt_dealine: String? = null,
    var proposal_deadline: String? = null,
    var final_draft_deadline: String? = null,
    var final_ppt_deadline: String? = null,
    var final_thesis_deadline: String? = null,
    var poster_deadline: String? = null,
)