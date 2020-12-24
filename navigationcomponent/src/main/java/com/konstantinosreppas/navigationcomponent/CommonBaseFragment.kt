package com.konstantinosreppas.navigationcomponent

import androidx.fragment.app.Fragment

open class CommonBaseFragment : Fragment() {
    var fragmentIdentifier: Int = 0

    var actionOnBackPress: Runnable? = null
}