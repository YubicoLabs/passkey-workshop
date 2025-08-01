package io.yubicolabs.pawskey

/**
 * Provide the last used Activity.
 */
interface ActivityProvider {
    fun getActivity(): MainActivity
}

/**
 * Implementation for Pawskeys
 */
class PawskeyActivityProvider : ActivityProvider {
    override fun getActivity(): MainActivity {
        val lastActivity = PawskeyApplication.instance.mainActivity // TODO: 👀👀👀👀
        return lastActivity
    }
}
