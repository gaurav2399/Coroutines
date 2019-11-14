import kotlinx.coroutines.*
import java.sql.DriverManager.println
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun main(args: Array<String>) {
    println("start")
    exampleBlocking()
    println("hello")
}
suspend fun printLineDelay(msg:String){
    delay(1000)
    println(msg)
}
suspend fun calculateHardThings(startNum : Int):Int{
    delay(1000)
    return startNum*10
}
fun exampleBlocking() = runBlocking{
    println("One")
    printLineDelay("Two")
    println("Three")
}
fun exampleBlockingDispatcher(){
    runBlocking (Dispatchers.Default){
        println("One - from thread ${Thread.currentThread().name}")
        printLineDelay("two - from thread ${Thread.currentThread().name}")
    }
    //starts after run blocking full executed
    println("three - from thread ${Thread.currentThread().name}")
}

fun exampleLaunchGlobal() = runBlocking{
    println("One - from thread ${Thread.currentThread().name}")
    //does not block main thread
    // used o make not blocking
    GlobalScope.launch {
        printLineDelay("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")
    delay(3000)
}
fun exampleLaunchGlobalWaiting() = runBlocking{
    println("One - from thread ${Thread.currentThread().name}")
    //does not block main thread
    // used o make not blocking
    val job = GlobalScope.launch {
        printLineDelay("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")
    job.join()
}
fun exampleLaunchCoroutineScope() = runBlocking{
    println("One - from thread ${Thread.currentThread().name}")

    val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
    //does not block main thread
    // used o make not blocking
    launch(customDispatcher) {
        printLineDelay("two - from thread ${Thread.currentThread().name}")
    }
    println("three - from thread ${Thread.currentThread().name}")
    (customDispatcher.executor as ExecutorService).shutdown()
}
fun exampleAsyncWait() = runBlocking {
    val stratTime = System.currentTimeMillis()

    //to get value out of coroutine use async
    //async return deferred on which we await to get things
    val deferred1 = async { calculateHardThings(10) }
    val deferred2 = async { calculateHardThings(10) }
    val deferred3 = async { calculateHardThings(10) }

    val sum= deferred1.await()+deferred2.await()+deferred3.await()
    println("aync/awit result = $sum")

    val endTime = System.currentTimeMillis()
    println("Time taken: ${endTime-stratTime} ")
}
suspend fun exampleWithContext(){
    runBlocking {
        val stratTime = System.currentTimeMillis()

        //use withContext to not block main thread
        //no need of awiat it direct return result
        val result1 = withContext(Dispatchers.Default) { calculateHardThings(10) }
        val result2 = withContext(Dispatchers.Default) { calculateHardThings(10) }
        val result3 = withContext(Dispatchers.Default) { calculateHardThings(10) }

        val sum= result1+result2+result3
        println("aync/awit result = $sum")

        val endTime = System.currentTimeMillis()
        println("Time taken: ${endTime-stratTime} ")
    }
}