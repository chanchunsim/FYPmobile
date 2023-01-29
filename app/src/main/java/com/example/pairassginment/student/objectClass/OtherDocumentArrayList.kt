package com.example.pairassginment.student.objectClass

import android.widget.Button
import com.transferwise.sequencelayout.SequenceStep

data class OtherDocumentArrayList (
    var other_document_name: String? = null,
    var other_sequence_step: SequenceStep? = null,
    var other_document_submit_btn: Button? = null,
    var other_document_detail_btn: Button? = null,
    var other_document_submit_label: String? = null,
    var other_document_view_label: String? = null,
)