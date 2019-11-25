package demo.lucius.androidprojectkt.vo


/**
 * 需要加载到UI界面的资源的状态
 *
 *
 * 通常在Repository类里，返回LiveData<Resource<T>>，使得UI更新为最新的数据
 */
enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}