package com.konstantinosreppas.navigationcomponent

open class BaseFlow(private val router: Router) {


    val fullScreenFragmentList = mutableListOf<CommonBaseFragment>()
    val customFragmentStack = mutableListOf<MutableList<CommonBaseFragment>>()
    var currentPage = 0


    fun recreateStackedFragmentsAndNavigateToTab(fragmentList: MutableList<CommonBaseFragment>, startingPage: Int = 0) {
        removeAllFragments()
        for (f in fragmentList)
            if (!fragmentStackContainsFragmentWithSameId(f)) {

                customFragmentStack.add(mutableListOf(f))
            }

        if (fragmentList.isNotEmpty()) {
            currentPage = startingPage
            router.navigateToNewStackedFragment(null, getCurrentTopFragment())
        }
    }

    fun changeTab(position: Int) {
        val startingFragment = getCurrentTopFragment()

        updatePageForCurrentTopFragment(position)

        if (!router.fragmentExistsInBackStack(getCurrentTopFragment())) {

            router.navigateToNewStackedFragment(startingFragment, getCurrentTopFragment())
        } else {
            router.navigateToExistingStackedFragment(startingFragment, getCurrentTopFragment())
        }
    }

    fun getCurrentTopFragment(): CommonBaseFragment {
        return customFragmentStack[currentPage].last()
    }


    fun navigateBack() {
        when {
            fullScreenFragmentList.isNotEmpty() -> {
                performBackActionOnFragmentOrRemove()
            }
            getStackFromCurrentPage().size > 1 -> {

                router.removeFragmentsFromBackStack(mutableListOf(customFragmentStack[currentPage].removeAt(customFragmentStack[currentPage].size - 1)))
                router.navigateToExistingStackedFragment(null, getCurrentTopFragment())
            }
            currentPage != 0 -> {
                changeTab(0)
            }
            else -> {
                removeAllFragments()
                router.exitApp()
            }
        }
    }

    fun clearBackStackForCurrentPage() {
        if (getStackFromCurrentPage().size > 1) {

            val sublistToRemove = customFragmentStack[currentPage].subList(1, customFragmentStack[currentPage].size)
            router.removeFragmentsFromBackStack(sublistToRemove)
            sublistToRemove.clear()
            router.navigateToExistingStackedFragment(null, getCurrentTopFragment())
        }
    }

    fun isStackEmpty(): Boolean {
        return customFragmentStack.isEmpty()
    }


    fun addFragmentToCurrentPage(fragment: CommonBaseFragment) {
        val startingFragment = getCurrentTopFragment()
        customFragmentStack[currentPage].add(fragment)
        router.navigateToNewStackedFragment(startingFragment, fragment)
    }


    fun addFullScreenFragment(fragment: CommonBaseFragment) {
        fullScreenFragmentList.add(fragment)
        router.navigateToFullScreenFragment(fragment)
    }


    fun getStackFromCurrentPage(): MutableList<CommonBaseFragment> {
        return if (customFragmentStack.isNotEmpty()) customFragmentStack[
                if (currentPage <= customFragmentStack.size - 1) currentPage
                else currentPage - 1
        ] else
            mutableListOf()
    }

    private fun performBackActionOnFragmentOrRemove() {
        fullScreenFragmentList.last().actionOnBackPress?.run()
                ?: router.removeFragmentsFromBackStack(mutableListOf(fullScreenFragmentList.removeAt(fullScreenFragmentList.size - 1)))
    }

    private fun removeAllFragments() {
        for (i in customFragmentStack) {

            router.removeFragmentsFromBackStack(i)
            i.clear()
        }
        customFragmentStack.clear()
        currentPage = 0
    }

    private fun updatePageForCurrentTopFragment(position: Int) {
        currentPage = if (position >= customFragmentStack.size) customFragmentStack.size - 1 else position
    }

    private fun fragmentStackContainsFragmentWithSameId(fragment: CommonBaseFragment): Boolean {

        for (f in customFragmentStack)
            if (f.first().fragmentIdentifier == fragment.fragmentIdentifier)
                return true
        return false
    }
}