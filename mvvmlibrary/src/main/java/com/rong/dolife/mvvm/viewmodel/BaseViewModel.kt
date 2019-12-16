
package com.rong.dolife.mvvm.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import com.rong.dolife.mvvm.model.IModel

/**
 * Created by rong on 2019/11/28
 */
interface IViewModel{
    /**
     * viewmodel 初始化操作
     */
    fun init()
}


abstract class BaseViewModel (private val model: IModel): ViewModel(),LifecycleObserver,IViewModel{

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun connectModel(){
        //TODO in onReasume lifecycle
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun disconnectModel(){
        //TODO in onPause lifecycle
    }

    override fun onCleared() {
        model.onCleared()
        super.onCleared()
    }
}
