package com.rong.dolife.mvvm.model


/*interface IModel{
    *//**
     * 进行资源回收
     *//*
    fun onCleared()
}*/

abstract class BaseModel:IModel{
    override fun onCleared() {
        //TODO cleared data
    }
}
