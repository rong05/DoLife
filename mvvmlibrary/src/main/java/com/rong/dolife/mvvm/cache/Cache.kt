package com.rong.dolife.mvvm.cache

import android.os.Parcel
import android.os.Parcelable
import kotlinx.coroutines.runBlocking
import java.lang.IllegalArgumentException



public interface Cache<K,V>{
    
    companion object interface Factory<K,V>{
        var DEFAULT_CACHE_SIZE: Int;

        fun build(cacheType : CacheType) : Cache<K,V>
    }
    
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


    /**
     * For caches that do not override {@link #sizeOf}, this returns the number
     * of entries in the cache. For all other caches, this returns the sum of
     * the sizes of the entries in this cache.
     */
    fun size() : Int

    /**
     * Returns the size of the entry for {@code key} and {@code value} in
     * user-defined units.  The default implementation returns 1 so that size
     * is the number of entries and max size is the maximum number of entries.
     *
     * <p>An entry's size must not change while it is in the cache.
     */
    fun sizeOf(key: K,value : V)

    /**
    * For caches that do not override {@link #sizeOf}, this returns the maximum
    * number of entries in the cache. For all other caches, this returns the
    * maximum sum of the sizes of the entries in this cache.
    */
    fun maxSize() : Int
}

public enum class CacheType{

}

public class LruCache<K,V>(private var maxSize: Int) : Cache<K,V> {

    private val map : LinkedHashMap<K,V>

    private var size : Int = 0


    init {
        if(maxSize <= 0){
            throw IllegalArgumentException("maxSize <= 0")
        }
        map = LinkedHashMap<K,V>(0,0.17f,true);
    }
    
    override fun resize(maxSize: Int) {
        if(maxSize <= 0){
            throw IllegalArgumentException("maxSize <= 0")
        }
        runBlocking {
            this@LruCache.maxSize = maxSize;
        }
        trimToSize(maxSize)
    }

    override fun get(key: K): V {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun put(key: K, value: V): V {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun trimToSize(maxSize: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(key: K): V {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun size(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sizeOf(key: K, value: V) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun maxSize(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

