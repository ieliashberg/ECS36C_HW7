package edu.ucdavis.cs.ecs036c.homework7

/*
 * Class for a priority queue that supports the comparable trait
 * on elements.  It sets up to return the lowest value priority (a min heap),
 * if you want the opposite use a comparable object that is reversed.
 *
 * You could use this for implementing Dijkstra's in O(|V + E| log (V) ) time instead
 * of the default O(V^2) time.
 */
class PriorityQueue<T, P: Comparable<P>> {

    /*
     * Invariants that need to be maintained:
     *
     * priorityData must always be in heap order
     * locationData must map every data element to its
     * corresponding index in the priorityData, and
     * must not include any extraneous entries.
     *
     * You must NOT change these variable names and you MUST
     * maintain these invariants, as the autograder checks that
     * the internal structure is maintained.
     */
    val priorityData = mutableListOf<Pair<T, P>>()
    val locationData = mutableMapOf<T, Int>()

    /*
    * Size function is just the internal size of the priority queue...
    */
    val size : Int
        get() = priorityData.size



    /*
     * This is a secondary constructor that takes a series of
     * data/priority pairs.  It should put the pairs in the heap
     * and then call heapify/ensure the invariants are maintained
     */
    constructor(vararg init: Pair<T, P>) {
        priorityData.addAll(init)
        priorityData.forEachIndexed { index, pair ->
            locationData[pair.first] = index
        }
        heapify()
    }

    /*
     * Heapify should ensure that the constraints are all updated.  This
     * is called by the secondary constructor.
     */
    fun heapify() {
        for (i in size / 2 downTo 0) {
            sink(i)
        }
    }

    /*
     * We support ranged-sink so that this could also be
     * used for heapsort, so sink without it just specifies
     * the range.
     */
    fun sink(i: Int) {
        sink(i, priorityData.size)
    }

    /*
     * The main sink function.  It accepts a range
     * argument, that by default is the full array, and
     * which considers that only indices < range are valid parts
     * of the heap.  This enables sink to be used for heapsort.
     */
    fun sink(i: Int, range: Int) {
        var index = i
        while (index * 2 + 1 < range) {
            var j = index * 2 + 1
            if (j < range - 1 && priorityData[j].second > priorityData[j + 1].second) {
                j++
            }
            if (priorityData[index].second <= priorityData[j].second) {
                break
            }
            swap(index, j)
            index = j
        }
    }

    /*
     * And the swim operation as well...
     */
    fun swim(i: Int) {
        var index = i
        while (index > 0 && priorityData[index].second < priorityData[(index - 1) / 2].second) {
            swap(index, (index - 1) / 2)
            index = (index - 1) / 2
        }
    }


    /*
     * This pops off the data with the lowest priority.  It MUST
     * throw an exception if there is no data left.
     */
    fun pop(): T {
        if (priorityData.isEmpty()) throw NoSuchElementException("Priority queue is empty")
        val result = priorityData.first().first
        swap(0, priorityData.size - 1)
        priorityData.removeAt(priorityData.size - 1)
        locationData.remove(result)
        sink(0)
        return result
    }

    /*
     * And this function enables updating the priority of something in
     * the queue.  It should sink or swim the element as appropriate to update
     * its new priority.
     *
     * If the key doesn't exist it should create a new one
     */
    fun update(data: T, newPriority: P) {
        val index = locationData[data]
        if (index == null) {
            locationData[data] = priorityData.size
            priorityData.add(Pair(data, newPriority))
            swim(priorityData.size - 1)
        } else {
            val oldPriority = priorityData[index].second
            priorityData[index] = Pair(data, newPriority)
            if (newPriority < oldPriority) {
                swim(index)
            } else {
                sink(index)
            }
        }
    }

    /*
     * A convenient shortcut for update, allowing array assignment
     */
    operator fun set(data: T, newPriority: P) {
        update(data, newPriority)
    }

    private fun swap(i: Int, j: Int) {
        val temp = priorityData[i]
        priorityData[i] = priorityData[j]
        priorityData[j] = temp
        locationData[priorityData[i].first] = i
        locationData[priorityData[j].first] = j
    }

    /*
     * You don't need to implement this function but it is
     * strongly advised that you do so for testing purposes, to check
     * that all invariants are correct.
     */
    fun isValid(): Boolean {
        for (i in 1 until priorityData.size) {
            if (priorityData[i].second < priorityData[(i - 1) / 2].second) {
                return false
            }
        }
        return true
    }

}
