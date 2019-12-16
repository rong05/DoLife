package com.rong.dolife.mvvm.model

/**
 * Created by rong on 2019/11/28
 */
interface IModel{
    /**
     * 进行资源回收
     */
    fun onCleared()
}

abstract class BaseModel:IModel{
    override fun onCleared() {
        //TODO cleared data
    }
}
