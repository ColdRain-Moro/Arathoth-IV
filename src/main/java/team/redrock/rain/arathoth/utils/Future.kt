package team.redrock.rain.arathoth.utils

import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Arathoth
 * team.redrock.rain.arathoth.utils
 *
 * @author 寒雨
 * @since 2023/3/15 下午6:59
 */
suspend fun <T> CompletableFuture<T>.await(): T = suspendCoroutine {
    thenAccept { ret ->
        it.resume(ret)
    }
}