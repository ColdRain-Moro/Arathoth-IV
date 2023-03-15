package team.redrock.rain.arathoth.impl.kether.actions

import taboolib.library.kether.QuestContext.Frame
import taboolib.module.kether.*
import team.redrock.rain.arathoth.impl.Marco
import java.util.concurrent.CompletableFuture

/**
 * Arathoth
 * team.redrock.rain.arathoth.impl.kether.actions
 *
 * @author 寒雨
 * @since 2023/3/12 上午12:27
 */
object ActionMarco {
    @KetherParser(value = ["marco"], namespace = "arathoth")
    fun parser() = scriptParser { reader ->
        val marco = reader.nextToken()
        reader.mark()
        // 可选参数
        val paramArr = kotlin.runCatching { reader.nextAction<List<Any?>>() }
            .onFailure { reader.reset() }
            .getOrNull()
        val eval: Frame.(List<Any?>) -> CompletableFuture<Any?> = { params ->
            Marco.ketherMarcos[marco]?.let { expansionStmt ->
                KetherShell.eval(expansionStmt) {
                    sender = script().sender
                    // 继承所有变量
                    extend(deepVars())
                    // 设置参数
                    set("params", params)
                }
            } ?: error("unable to expansion marco: the marco called $marco is not exist.")
        }
        actionTake {
            paramArr?.let {
                newFrame(paramArr).run<List<Any?>>().thenCompose { params ->
                    eval(params)
                }
            } ?: eval(listOf())
        }
    }
}