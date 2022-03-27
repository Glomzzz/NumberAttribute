package com.skillw.numberatt

import com.skillw.numberatt.api.manager.OperationManager
import com.skillw.numberatt.internal.manager.NAConfig
import com.skillw.pouvoir.api.annotation.PManager
import com.skillw.pouvoir.api.manager.ManagerData
import com.skillw.pouvoir.api.plugin.SubPouvoir
import com.skillw.pouvoir.api.thread.BasicThreadFactory
import com.skillw.pouvoir.util.FileUtils
import com.skillw.pouvoir.util.Pair
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.platform.BukkitPlugin
import java.io.File
import java.util.concurrent.ScheduledThreadPoolExecutor

object NumberAttribute : Plugin(), SubPouvoir {

    override val key = "NumberAttribute"
    override lateinit var managerData: ManagerData
    override val plugin by lazy {
        BukkitPlugin.getInstance()
    }
    override val poolExecutor: ScheduledThreadPoolExecutor by lazy {
        ScheduledThreadPoolExecutor(
            20,
            BasicThreadFactory.Builder().daemon(true).namingPattern("number-system-schedule-pool-%d").build()
        )
    }

    override fun getConfigs(): MutableMap<String, Pair<File, YamlConfiguration>> {
        return mutableMapOf(
            "config" to Pair(config.file!!, FileUtils.loadConfigFile(config.file!!)!!)
        )
    }

    /**
     * Configs
     */

    @Config("config.yml")
    lateinit var config: ConfigFile

    /**
     * Managers
     */

    @JvmStatic
    @PManager
    lateinit var configManager: NAConfig

    @JvmStatic
    @PManager
    lateinit var operationManager: OperationManager

    override fun onLoad() {
        load()
        info("&d[&9NumberAttribute&d] &aNumberAttribute is loaded...")
    }

    override fun onEnable() {
        enable()
        info("&d[&9NumberAttribute&d] &aNumberAttribute is enabled...")
    }

    override fun onActive() {
        active()
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach {
            it.kickPlayer("Server is closed..")
        }
        disable()
        Bukkit.getScheduler().cancelTasks(this.plugin)
        info("&d[&9NumberAttribute&d] &aNumberAttribute is disabled...")
    }

}