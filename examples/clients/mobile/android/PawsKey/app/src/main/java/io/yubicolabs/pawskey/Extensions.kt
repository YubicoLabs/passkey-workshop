package io.yubicolabs.pawskey

/**
 * @warn pollutes all classes.
 * @return name for this class, or a placeholder.
 */
inline val Any.tagForLog: String
    get() = javaClass.simpleName ?: "YubiSample"