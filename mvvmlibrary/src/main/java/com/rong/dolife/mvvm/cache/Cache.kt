package com.rong.dolife.mvvm.cache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext


public interface Cache<K,V>{

    
    companion object interface Factory<K,V>{

        val DEFAULT_CACHE_SIZE: Int

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
    fun get(key:K) : V?

    /**
     * Caches {@code value} for {@code key}. The value is moved to the head of
     * the queue.
     *
     * @return the previous value mapped by {@code key}.
     */
    fun put(key:K,value:V) : V?

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
    fun remove(key:K):V?


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
    fun sizeOf(key: K,value : V) : Int

    /**
    * For caches that do not override {@link #sizeOf}, this returns the maximum
    * number of entries in the cache. For all other caches, this returns the
    * maximum sum of the sizes of the entries in this cache.
    */
    fun maxSize() : Int
}

public enum class CacheType{

}

public open class LruCache<K,V>(private var maxSize: Int) : Cache<K,V> {

    private val map : LinkedHashMap<K,V>

    private var size : Int = 0
    private val mutex = Mutex()

    private var putCount : Int = 0
    private var createCount : Int = 0
    private var evictionCount : Int = 0
    private var hitCount : Int = 0
    private var missCount : Int = 0


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

    override fun get(key: K): V? {
        if(key == null){
            throw NullPointerException("key == null")
        }
        var mapValue: V? = null;
        runBlocking {
            withContext(Dispatchers.Default){
                mutex.withLock {
                    mapValue = map.getValue(key)
                    mapValue?.let{
                        if(it != null){
                            hitCount++
                            return@withContext
                        }
                        missCount ++
                    }
                }
            }
        }

        if (mapValue != null){
            return mapValue
        }

        val createdValue : V = create(key) ?: return null;

        runBlocking {
            withContext(Dispatchers.Default){
                mutex.withLock {
                    createCount ++
                    mapValue = map.put(key,createdValue);
                    mapValue?.let {
                        if(it != null){
                            map.put(key,mapValue as V)
                        }else{
                            size += safeSizeOf(key,createdValue)
                        }
                    }
                }
            }
        }

        if (mapValue != null){
            entryRemoved(false,key,createdValue, mapValue as V);
            return mapValue
        }else{
            trimToSize(maxSize)
            return createdValue
        }
    }

    protected open fun create(key: K) : V? {
        return null
    }

    protected open fun entryRemoved(evicted:Boolean, key: K?, oldValue:V?, newValue:V?){}

    override fun put(key: K, value: V): V? {
        if(key == null || value == null){
            throw NullPointerException("key == null || value == null")
        }
        var previous : V ?= null
        runBlocking {
            withContext(Dispatchers.Default){
                mutex.withLock {
                    putCount ++
                    size += safeSizeOf(key,value)
                    previous = map.put(key,value)!!
                    if(previous != null){
                        size -= safeSizeOf(key,previous!!)
                    }
                }
            }
        }

        if(previous != null){
            entryRemoved(false,key,previous!!,value)
        }

        trimToSize(maxSize)
        return previous
    }

    override fun trimToSize(maxSize: Int) {
        var running = true
       loop@while (running) {
           var key: K? = null
           var value: V? = null
           runBlocking {
               withContext(Dispatchers.Default) {
                   mutex.withLock {
                       if(size < 0 || (map.isEmpty() && size != 0)){
                           throw IllegalStateException(javaClass.name+".sizeOf() is reporting inconsistent results");
                       }
                       if(size <= maxSize){
                           running = false
                           return@withContext
                       }
                       val toEvict : Map.Entry<K, V>? = map.asIterable().iterator().next()
                       if (toEvict == null){
                          running = false
                           return@withContext
                       }
                       key = toEvict.key
                       value = toEvict.value
                       map.remove(key!!)
                       size -= safeSizeOf(key!!,value!!)
                       evictionCount++
                   }
               }
           }
           if(running){
               entryRemoved(true,key,value,null)
           }else{
               break@loop
           }
       }
    }

    override fun remove(key: K): V? {
        if(key == null){
            throw NullPointerException("key == null")
        }
       var previous : V ?= null
        runBlocking {
            withContext(Dispatchers.Default) {
                mutex.withLock {
                    previous = map.remove(key)!!
                    if(previous != null){
                        size -= safeSizeOf(key,previous!!)
                    }
                }
            }
        }
        if (previous != null){
            entryRemoved(false,key,previous,null)
        }
        return previous
    }


    @Synchronized override fun size(): Int {
        return size
    }

    private fun safeSizeOf(key:K,value: V):Int{
        val result = sizeOf(key,value)
        if(result < 0){
            throw IllegalStateException("Negative size :$key = $value")
        }
        return result
    }

    override fun sizeOf(key: K, value: V) : Int{
        return 1
    }

    @Synchronized override fun maxSize(): Int {
       return maxSize
    }

}

