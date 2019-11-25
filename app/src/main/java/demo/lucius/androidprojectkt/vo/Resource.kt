package demo.lucius.androidprojectkt.vo

/**
 * 泛型类用于持有数据以及加载的状态
 */
data class Resource<out T>(val status:Status,val data:T?,val message:String?){
    companion object{
        fun <T>success(data:T?):Resource<T>{
            return Resource(Status.SUCCESS,data,null);
        }
        fun <T>error(msg:String,data:T?):Resource<T>{
            return Resource(Status.ERROR,data,msg);
        }

        fun <T>loading(data:T?):Resource<T>{
            return Resource(Status.LOADING,data,null);
        }
    }
}