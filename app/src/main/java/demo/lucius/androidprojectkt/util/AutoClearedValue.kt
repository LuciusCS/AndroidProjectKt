package demo.lucius.androidprojectkt.util


import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * 一个懒加载属性，当fragment destory的时候，其值会被清除
 */
class AutoClearedValue <T:Any>(val fragment: LifecycleOwner):ReadWriteProperty< LifecycleOwner,T>{

    private var _value:T?=null

    init {
        fragment.lifecycle.addObserver(object :LifecycleObserver{
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy(){
                _value=null
            }
        })
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T {
        return _value?:throw IllegalStateException("should never call auto-cleared-value get when it might not be available")
    }

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T) {
        _value=value
    }

//    override fun getValue(thisRef: Any, property: KProperty<*>): T {
//
//    }
//
//    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
//        _value=value
//    }

//    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
//        return _value?:throw IllegalStateException("should never call auto-cleared-value get when it might not be available")
//    }
//
//    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
//        _value=value
//    }

}

/**
 * 创建一个与Fragment关联的[AutoClearedValue]
 */
fun<T:Any>LifecycleOwner.autoCleared() = AutoClearedValue<T>(this)