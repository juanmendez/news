package com.example.news.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

/**
 * Extension function that combines the [LiveData] with a second [LiveData]
 * @param A the type of the [LiveData]
 * @param B the type of the second [LiveData]
 * @param T the type of the output [LiveData]
 * @param other the second [LiveData]
 * @param onChange the combination used to obtain the output [LiveData]
 * @return the combined [LiveData]
 */
fun <T, A, B> LiveData<A>.combineAndCompute(
    other: LiveData<B>,
    onChange: (A, B) -> T
): MediatorLiveData<T> {

    var source1emitted = false
    var source2emitted = false

    val result = MediatorLiveData<T>()

    val mergeFunction = {
        val source1Value = this.value
        val source2Value = other.value

        if (source1emitted && source2emitted) {
            result.value = onChange.invoke(source1Value!!, source2Value!!)
        }
    }

    result.addSource(this) { source1emitted = true; mergeFunction.invoke() }
    result.addSource(other) { source2emitted = true; mergeFunction.invoke() }

    return result
}

/**
 * [MediatorLiveData] with two sources of [LiveData]
 * @param A type of the first source of [LiveData]
 * @param B type of the second source of [LiveData]
 */
class DoubleTrigger<A, B>(
    a: LiveData<A>,
    b: LiveData<B>
) : MediatorLiveData<Pair<A?, B?>>() {
    init {
        addSource(a) { value = it to b.value }
        addSource(b) { value = a.value to it }
    }
}
