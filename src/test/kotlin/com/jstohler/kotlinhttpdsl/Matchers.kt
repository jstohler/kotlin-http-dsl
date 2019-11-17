package com.jstohler.kotlinhttpdsl

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.has

object Matchers {
    fun headerContains(expected: String) = Matcher(Set<String>::contains, expected)
    fun headerSize(expected: Matcher<Int>) = has(Map<String, List<String>>::size, expected)
    fun containsKey(expected: String) = Matcher(Map<String, List<String>>::containsKey, expected)
    fun containsValue(expected: String) = Matcher(List<String>::contains, expected)
}