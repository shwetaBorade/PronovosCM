package com.pronovoscm.utils

import com.pronovoscm.model.view.OptionList

class CustomFieldMessageEvent {
    var selectedOptions: Triple<Int, Int, ArrayList<OptionList>> = Triple(0,0, arrayListOf())
    var selectedOptionPosition:Int = -1
}