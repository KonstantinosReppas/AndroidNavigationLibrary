package com.konstantinosreppas.navigationcomponent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class CustomRouter(private val activity: Activity, private val stackedFragmentWrapper: StackedFragmentWrapper, private val fragmentManager: FragmentManager) :
    Router {

    override fun navigateToNewStackedFragment(startingFragment: CommonBaseFragment?, targetFragment: CommonBaseFragment) {
        hideCurrentVisibleFragment(startingFragment)

        fragmentManager.beginTransaction()
                .add(stackedFragmentWrapper.getStackedFragmentFrameId(), targetFragment, targetFragment.fragmentIdentifier.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss()
    }

    override fun navigateToExistingStackedFragment(startingFragment: CommonBaseFragment?, targetFragment: CommonBaseFragment) {
        hideCurrentVisibleFragment(startingFragment)

        if (fragmentManager.findFragmentByTag(targetFragment.fragmentIdentifier.toString()) != null) {

            fragmentManager.beginTransaction()
                    .show(fragmentManager.findFragmentByTag(targetFragment.fragmentIdentifier.toString())!!)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
        }
    }

    private fun hideCurrentVisibleFragment(startingFragment: CommonBaseFragment?) {
        if (startingFragment != null && fragmentManager.findFragmentByTag(startingFragment.fragmentIdentifier.toString()) != null) {
            fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentByTag(startingFragment.fragmentIdentifier.toString())!!)
                    .commit()
        }
    }

    override fun fragmentExistsInBackStack(fragment: CommonBaseFragment): Boolean {
        return fragmentManager.findFragmentByTag(fragment.tag) != null
    }

    override fun removeFragmentsFromBackStack(fragments: MutableList<CommonBaseFragment>) {
        val transaction = fragmentManager.beginTransaction()
        for (f in fragments) {

            if (fragmentManager.findFragmentByTag(f.fragmentIdentifier.toString()) != null)
                transaction.remove(fragmentManager.findFragmentByTag(f.fragmentIdentifier.toString())!!)
        }
        transaction.commitAllowingStateLoss()

    }

    override fun navigateToFullScreenFragment(fragment: CommonBaseFragment) {
        fragmentManager.beginTransaction()
                .add(stackedFragmentWrapper.getFullScreenFragmentFrameId(), fragment, fragment.fragmentIdentifier.toString())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }

    override fun <T> startActivity(targetClass: Class<T>, extras: Bundle) {
        val intent = Intent(activity, targetClass)
        intent.putExtras(extras)
        activity.startActivity(intent)
    }

    override fun exitApp() {
        activity.finish()
    }
}