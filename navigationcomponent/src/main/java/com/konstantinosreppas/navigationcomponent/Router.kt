package com.konstantinosreppas.navigationcomponent

import android.os.Bundle

interface Router {

    fun navigateToNewStackedFragment(startingFragment: CommonBaseFragment?, targetFragment: CommonBaseFragment)
    fun navigateToExistingStackedFragment(startingFragment: CommonBaseFragment?, targetFragment: CommonBaseFragment)
    fun fragmentExistsInBackStack(fragment: CommonBaseFragment): Boolean
    fun removeFragmentsFromBackStack(fragments: MutableList<CommonBaseFragment>)

    fun navigateToFullScreenFragment(fragment: CommonBaseFragment)

    fun <T> startActivity(targetClass: Class<T>, extras: Bundle = Bundle())

    fun exitApp()
}