package it.uniupo.ktt.ui.common

import com.google.firebase.Timestamp

fun areMapsEqual(
    map1: Map<String, Timestamp>?,
    map2: Map<String, Timestamp>?
): Boolean {
    if (map1 == null || map2 == null) return false
    if (map1.size != map2.size) return false
    return map1.all { (key, value) ->
        val otherValue = map2[key]
        otherValue != null && value == otherValue
    }
}