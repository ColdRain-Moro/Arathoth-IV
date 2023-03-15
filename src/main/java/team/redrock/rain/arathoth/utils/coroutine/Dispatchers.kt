package team.redrock.rain.arathoth.utils.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import taboolib.platform.BukkitPlugin
import team.redrock.rain.arathoth.Arathoth
import kotlin.coroutines.CoroutineContext

/**
 * Arathoth
 * team.redrock.rain.arathoth.utils.coroutine
 *
 * @author 寒雨
 * @since 2023/3/15 下午6:49
 */
internal open class MinecraftCoroutineDispatcher : CoroutineDispatcher() {

    private val plugin: BukkitPlugin = Arathoth.pluginInst

    /**
     * Returns `true` if the execution of the coroutine should be performed with [dispatch] method.
     * The default behavior for most dispatchers is to return `true`.
     * This method should generally be exception-safe. An exception thrown from this method
     * may leave the coroutines that use this dispatcher in the inconsistent and hard to debug state.
     */
    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return !plugin.server.isPrimaryThread && plugin.isEnabled
    }

    /**
     * Handles dispatching the coroutine on the correct thread.
     */
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!plugin.isEnabled) {
            return
        }

        val timedRunnable = context[CoroutineTimings.Key]
        if (timedRunnable == null) {
            plugin.server.scheduler.runTask(plugin, block)
        } else {
            timedRunnable.queue.add(block)
            plugin.server.scheduler.runTask(plugin, timedRunnable)
        }
    }
}

/**
 * CraftBukkit Async ThreadPool Dispatcher. Dispatches only if the call is at the primary thread.
 */
internal open class AsyncCoroutineDispatcher : CoroutineDispatcher() {

    private val plugin: BukkitPlugin = Arathoth.pluginInst

    /**
     * Returns `true` if the execution of the coroutine should be performed with [dispatch] method.
     * The default behavior for most dispatchers is to return `true`.
     * This method should generally be exception-safe. An exception thrown from this method
     * may leave the coroutines that use this dispatcher in the inconsistent and hard to debug state.
     */
    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return true
    }

    /**
     * Handles dispatching the coroutine on the correct thread.
     */
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, block)
    }
}

internal val Dispatchers.BukkitMain: MinecraftCoroutineDispatcher by lazy { MinecraftCoroutineDispatcher() }
internal val Dispatchers.BukkitAsync: AsyncCoroutineDispatcher by lazy { AsyncCoroutineDispatcher() }