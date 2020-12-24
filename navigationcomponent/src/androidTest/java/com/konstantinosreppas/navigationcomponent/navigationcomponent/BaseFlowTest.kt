package com.konstantinosreppas.navigationcomponent.navigationcomponent

import android.os.Bundle
import com.konstantinosreppas.navigationcomponent.BaseFlow
import com.konstantinosreppas.navigationcomponent.CommonBaseFragment
import com.konstantinosreppas.navigationcomponent.Router
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class BaseFlowTest {

    private lateinit var sut: BaseFlow
    private lateinit var router: RouterSpy

    @Before
    fun setup() {
        router = RouterSpy()
        sut = BaseFlow(router)
    }

    @Test
    fun instantiateBottomFragments_withNoBottomBarFragments_doesNotRouteToAnything() {
        sut.recreateStackedFragmentsAndNavigateToTab(mutableListOf())

        Assert.assertEquals(sut.isStackEmpty(), true)
        Assert.assertEquals(sut.currentPage, 0)

        Assert.assertEquals(router.routedList, mutableListOf<Int>())
        Assert.assertEquals(router.hiddenList, mutableListOf<Int>())
        Assert.assertEquals(router.fragmentManagerBackStack, mutableListOf<CommonBaseFragment>())
    }

    @Test
    fun instantiateBottomFragments_withBottomBarFragmentsNoStartingPage_routesToFirstTab() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(sut.getCurrentTopFragment().fragmentIdentifier, 0)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0))
        Assert.assertEquals(router.hiddenList, mutableListOf<Int>())
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack, mutableListOf(createFragmentWithId(0))), true)
    }

    @Test
    fun instantiateBottomFragments_withBottomBarFragmentsStartingPageSecond_routesToSecondTab() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments, 1)

        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(sut.currentPage, 1)
        Assert.assertEquals(sut.getCurrentTopFragment().fragmentIdentifier, 1)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(1))
        Assert.assertEquals(router.hiddenList, mutableListOf<Int>())
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack, mutableListOf(createFragmentWithId(1))), true)
    }

    @Test
    fun instantiateBottomFragments_withDuplicateBottomBarFragments_ignoresDuplicatesAndRoutesToFirstTab() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(0), createFragmentWithId(1))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1))

        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(sut.getCurrentTopFragment().fragmentIdentifier, 0)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0))
        Assert.assertEquals(router.hiddenList, mutableListOf<Int>())
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack, mutableListOf(createFragmentWithId(0))), true)
    }


    @Test
    fun addFragmentToTab_routesToFragmentAndUpdatesStack() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.addFragmentToCurrentPage(createFragmentWithId(3))

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(3))

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0, 3))
        Assert.assertEquals(router.hiddenList, mutableListOf(0))
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack, mutableListOf(createFragmentWithId(0), createFragmentWithId(3))), true)
    }

    @Test
    fun addMoreThanOneFragmentToTab_routesToFragmentAndUpdatesStack() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.addFragmentToCurrentPage(createFragmentWithId(3))
        sut.addFragmentToCurrentPage(createFragmentWithId(4))
        sut.addFragmentToCurrentPage(createFragmentWithId(5))

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(3), createFragmentWithId(4), createFragmentWithId(5))

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0, 3, 4, 5))
        Assert.assertEquals(router.hiddenList, mutableListOf(0, 3, 4))
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack, mutableListOf(createFragmentWithId(0), createFragmentWithId(3), createFragmentWithId(4), createFragmentWithId(5))), true)
    }

    @Test
    fun changeTabWithNoAddedFragmentsOnTopOfRoot_routesToRootFragmentOfTab() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.changeTab(1)

        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(sut.currentPage, 1)
        Assert.assertEquals(sut.getCurrentTopFragment().fragmentIdentifier, 1)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0, 1))
        Assert.assertEquals(router.hiddenList, mutableListOf(0))
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack, mutableListOf(createFragmentWithId(0), createFragmentWithId(1))), true)
    }

    @Test
    fun tryToChangeTabOutOfStackBounds_routesToTopFragmentOfLastTab() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.changeTab(10)

        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(sut.currentPage, 2)
        Assert.assertEquals(sut.getCurrentTopFragment().fragmentIdentifier, 2)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0, 2))
        Assert.assertEquals(router.hiddenList, mutableListOf(0))
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack, mutableListOf(createFragmentWithId(0), createFragmentWithId(2))), true)
    }


    @Test
    fun changeTabBackAndForth_routesToTopFragmentOfTab() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.changeTab(1)
        sut.changeTab(0) //back and forth

        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(sut.getCurrentTopFragment().fragmentIdentifier, 0)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0, 1, 0))
        Assert.assertEquals(router.hiddenList, mutableListOf(0, 1))
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf(createFragmentWithId(0), createFragmentWithId(1))), true)
    }

    @Test
    fun changeTabWithAddedFragmentsOnTopOfRoot_routesToTopFragmentOfTab() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.addFragmentToCurrentPage(createFragmentWithId(3))
        sut.changeTab(1)
        sut.changeTab(0) //back and forth

        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(sut.getCurrentTopFragment().fragmentIdentifier, 3)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0, 3, 1, 3))
        Assert.assertEquals(router.hiddenList, mutableListOf(0, 3, 1))
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf(createFragmentWithId(0), createFragmentWithId(3), createFragmentWithId(1))), true)
    }


    @Test
    fun reselectTab_clearsListAndRoutesToFirstFragmentOfTab() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.addFragmentToCurrentPage(createFragmentWithId(3))
        sut.addFragmentToCurrentPage(createFragmentWithId(4))
        sut.addFragmentToCurrentPage(createFragmentWithId(5))

        sut.clearBackStackForCurrentPage()

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0))

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0, 3, 4, 5, 0))
        Assert.assertEquals(router.hiddenList, mutableListOf(0, 3, 4))
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf(createFragmentWithId(0))), true)
        Assert.assertEquals(router.exitAppWasCalled, false)
    }

    @Test
    fun reselectTabWithoutAddedFragment_doesNothing() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.clearBackStackForCurrentPage()

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0))

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0))
        Assert.assertEquals(router.hiddenList, mutableListOf<Int>())
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf(createFragmentWithId(0))), true)
        Assert.assertEquals(router.exitAppWasCalled, false)
    }

    @Test
    fun backPressedWithFragmentOnTopOfRoot_clearsTopFragmentAndRemovesFromBackStackAndRoutesToPreviousFragmentStaysOnSameTab() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.addFragmentToCurrentPage(createFragmentWithId(3))
        sut.addFragmentToCurrentPage(createFragmentWithId(4))

        sut.navigateBack()

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(3))

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0, 3, 4, 3))
        Assert.assertEquals(router.hiddenList, mutableListOf(0, 3))
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf(createFragmentWithId(0), createFragmentWithId(3))), true)
        Assert.assertEquals(router.exitAppWasCalled, false)
    }

    @Test
    fun backPressed_noFragmentOnTopOfRootOnSecondTab_routesToFirstTab() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.changeTab(1)

        sut.navigateBack()

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0))

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0, 1, 0))
        Assert.assertEquals(router.hiddenList, mutableListOf(0, 1))
        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf(createFragmentWithId(0), createFragmentWithId(1))), true)
        Assert.assertEquals(router.exitAppWasCalled, false)
    }

    @Test
    fun backPressedOnFirstTab_noFragmentOnTop_exitsApp() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.navigateBack()

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf()

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), mutableListOf()), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.isStackEmpty(), true)

        Assert.assertEquals(router.routedList, mutableListOf(0))
        Assert.assertEquals(router.hiddenList, mutableListOf<Int>())
        Assert.assertEquals(router.exitAppWasCalled, true)

        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf()), true)

        val expectedFullScreenFragmentList: MutableList<CommonBaseFragment> = mutableListOf()

        Assert.assertEquals(compareIdsFromFragmentLists(sut.fullScreenFragmentList, expectedFullScreenFragmentList), true)
    }

    @Test
    fun destroyAllFragmentsThenCreateAgain_routesToFirstTab() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.addFragmentToCurrentPage(createFragmentWithId(3))
        sut.changeTab(1)
        sut.addFragmentToCurrentPage(createFragmentWithId(4))

        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0))

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(router.routedList, mutableListOf(0, 3, 1, 4, 0))
        Assert.assertEquals(router.hiddenList, mutableListOf(0, 3, 1))

        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf(createFragmentWithId(0))), true)
    }

    @Test
    fun addFullScreenFragment_routesToFragment() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.addFullScreenFragment(createFragmentWithId(3))

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0))

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.getCurrentTopFragment().fragmentIdentifier, 0)
        Assert.assertEquals(sut.isStackEmpty(), false)

        val expectedFullScreenFragmentList: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(3))

        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf(createFragmentWithId(0), createFragmentWithId(3))), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.fullScreenFragmentList, expectedFullScreenFragmentList), true)

    }
    @Test
    fun addTwoFullScreenFragments_routesToBothFragmentsBackToBack() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.addFullScreenFragment(createFragmentWithId(3))
        sut.addFullScreenFragment(createFragmentWithId(4))

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0))

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.getCurrentTopFragment().fragmentIdentifier, 0)
        Assert.assertEquals(sut.isStackEmpty(), false)

        val expectedFullScreenFragmentList: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(3), createFragmentWithId(4))

        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf(createFragmentWithId(0), createFragmentWithId(3), createFragmentWithId(4))), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.fullScreenFragmentList, expectedFullScreenFragmentList), true)

    }
    @Test
    fun backPressedWithFullscreenFragment_removesFragment() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.addFullScreenFragment(createFragmentWithId(3))

        sut.navigateBack()

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0))

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(sut.getCurrentTopFragment().fragmentIdentifier, 0)

        val expectedFullScreenFragmentList: MutableList<CommonBaseFragment> = mutableListOf()

        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf(createFragmentWithId(0))), true)

        Assert.assertEquals(compareIdsFromFragmentLists(sut.fullScreenFragmentList, expectedFullScreenFragmentList), true)

    }
    @Test
    fun backPressedWithTwoFullscreenFragment_removesTopFragment() {
        val fragments: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0), createFragmentWithId(1), createFragmentWithId(2))
        sut.recreateStackedFragmentsAndNavigateToTab(fragments)

        sut.addFullScreenFragment(createFragmentWithId(3))
        sut.addFullScreenFragment(createFragmentWithId(4))

        sut.navigateBack()

        val expectedFragmentListForTab: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(0))

        Assert.assertEquals(sut.currentPage, 0)
        Assert.assertEquals(compareIdsFromFragmentLists(getRootFragmentsFromFragmentStack(sut.customFragmentStack), fragments), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.getStackFromCurrentPage(), expectedFragmentListForTab), true)
        Assert.assertEquals(sut.isStackEmpty(), false)

        Assert.assertEquals(sut.getCurrentTopFragment().fragmentIdentifier, 0)

        val expectedFullScreenFragmentList: MutableList<CommonBaseFragment> = mutableListOf(createFragmentWithId(3))

        Assert.assertEquals(compareIdsFromFragmentLists(router.fragmentManagerBackStack,
                mutableListOf(createFragmentWithId(0), createFragmentWithId(3))), true)
        Assert.assertEquals(compareIdsFromFragmentLists(sut.fullScreenFragmentList, expectedFullScreenFragmentList), true)

    }


    /** Helpers **/

    private fun getRootFragmentsFromFragmentStack(stack: MutableList<MutableList<CommonBaseFragment>>): MutableList<CommonBaseFragment> {
        val rootFragments = mutableListOf<CommonBaseFragment>()
        for (l in stack) {
            rootFragments.add(l.first())
        }
        return rootFragments
    }

    private fun compareIdsFromFragmentLists(list1: MutableList<CommonBaseFragment>, list2: MutableList<CommonBaseFragment>): Boolean {
        if (list1.size == list2.size) {
            for (i in list1.indices)
                if (list1[i].fragmentIdentifier != list2[i].fragmentIdentifier)
                    return false

            return true

        } else return false
    }


    private fun createFragmentWithId(id: Int = 0): CommonBaseFragment {
        val fragment = CommonBaseFragment()
        fragment.fragmentIdentifier = id
        return fragment
    }

    private class RouterSpy : Router {

        val routedList = mutableListOf<Int>()
        val hiddenList = mutableListOf<Int>()

        val fullScreenList = mutableListOf<Int>()

        var exitAppWasCalled = false


        val fragmentManagerBackStack = mutableListOf<CommonBaseFragment>()

        override fun navigateToNewStackedFragment(startingFragment: CommonBaseFragment?, targetFragment: CommonBaseFragment) {

            if (startingFragment != null)
                hiddenList.add(startingFragment.fragmentIdentifier)

            routedList.add(targetFragment.fragmentIdentifier)

            fragmentManagerBackStack.add(targetFragment)
        }

        override fun navigateToExistingStackedFragment(startingFragment: CommonBaseFragment?, targetFragment: CommonBaseFragment) {

            if (startingFragment != null)
                hiddenList.add(startingFragment.fragmentIdentifier)

            routedList.add(targetFragment.fragmentIdentifier)
        }

        override fun fragmentExistsInBackStack(fragment: CommonBaseFragment): Boolean {
            return fragmentManagerBackStack.contains(fragment)
        }

        override fun removeFragmentsFromBackStack(fragments: MutableList<CommonBaseFragment>) {
            for (f in fragments)
                if (fragmentExistsInBackStack(f))
                    fragmentManagerBackStack.remove(f)
        }

        override fun navigateToFullScreenFragment(fragment: CommonBaseFragment) {
            fullScreenList.add(fragment.fragmentIdentifier)
            fragmentManagerBackStack.add(fragment)
        }

        override fun <T> startActivity(targetClass: Class<T>, extras: Bundle) {
        }

        override fun exitApp() {
            exitAppWasCalled = true
        }

    }


}