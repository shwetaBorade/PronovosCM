package com.pronovoscm.ui.punchlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pronovoscm.PronovosApplication
import com.pronovoscm.data.PunchListProvider
import com.pronovoscm.model.PunchListStatus
import com.pronovoscm.persistence.domain.PunchlistDb
import com.pronovoscm.persistence.repository.PunchListRepository
import javax.inject.Inject

class PunchListViewModel : ViewModel() {

    fun markedComplete(punchList: PunchlistDb) {
        Log.d("Nitin", "markedComplete: ")
        val punchlistDb: PunchlistDb = punchList
        punchlistDb.status = PunchListStatus.Complete.value
        punchlistDb.isSync = false
//        provider.mPunchListRepository.updatePunchListDb(punchlistDb)
    }

}