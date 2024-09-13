package io.yubicolabs.pawskey

/**
 * Abstraction from clipboard
 */
interface ClipboardProvider {
    fun setContent(message: String)
}