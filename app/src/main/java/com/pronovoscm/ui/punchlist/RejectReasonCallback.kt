package com.pronovoscm.ui.punchlist

import com.pronovoscm.persistence.domain.punchlist.PunchListRejectReasonAttachments

interface RejectReasonCallback {
    fun openImageCallback()
    fun updatePunchListReasonAttachment(punchListRejectReasonAttachments: PunchListRejectReasonAttachments)
    fun openAttachImage(position: Int)
}