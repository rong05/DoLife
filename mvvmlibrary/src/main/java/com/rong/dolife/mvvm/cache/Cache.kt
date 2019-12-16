package com.rong.dolife.mvvm.cache


public interface Cache<K,V>{
    /**
     * Sets the size of the cache.
     * @param maxSize The new maximum size.
     */
    fun resize(maxSize:Int)

    /**
     * Returns the value for {@code key} if it exists in the cache or can be
     * created by {@code #create}. If a value was returned, it is moved to the
     * head of the queue. This returns null if a value is not cached and cannot
     * be created.
     */
    fun get(key:K) : V

    /**
     * Caches {@code value} for {@code key}. The value is moved to the head of
     * the queue.
     *
     * @return the previous value mapped by {@code key}.
     */
    fun put(key:K,value:V) : V

    /**
     * Remove the eldest entries until the total of remaining entries is at or
     * below the requested size.
     *
     * @param maxSize the maximum size of the cache before returning. May be -1
     *            to evict even 0-sized elements.
     */
    fun trimToSize(maxSize: Int)

    /**
     * Removes the entry for {@code key} if it exists.
     *
     * @return the previous value mapped by {@code key}.
     */
    fun remove(key:K):V

    fun size() : Int

    fun sizeOf(key: K,value : V)

    fun maxSize() : Int


}