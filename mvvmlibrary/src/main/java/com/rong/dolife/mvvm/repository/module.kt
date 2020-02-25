package com.rong.dolife.mvvm.repository

import android.app.Application
import android.content.Context
import androidx.room.RoomDatabase
import com.rong.dolife.mvvm.cache.Cache
import com.rong.dolife.mvvm.cache.CacheType
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton

@Module
public class DatabaseModule{

    interface RoomConfiguration<DB : RoomDatabase>{

        fun configRoom(context: Context,builder:RoomDatabase.Builder<DB>)

        companion object EMPTY : RoomConfiguration <RoomDatabase>{
            override fun configRoom(context: Context, builder: RoomDatabase.Builder<RoomDatabase>) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }
}

@Module
public class RepositoryModule(private val application: Application){


    @Singleton
    @Provides
    fun provideRepositoryManager(cacheFactory: Cache.Factory<String, Any>,roomConfiguration:DatabaseModule.RoomConfiguration<RoomDatabase>):IPepositoryManager{
        return  RepositoryManager();
    }


    @Singleton
    @Provides
    fun provideExtras(cacheFactory:Cache.Factory<String,Any>):Cache<String,Any>{
        return cacheFactory.build(CacheType.EXTRAS_CACHE_TYPE);
    }
}