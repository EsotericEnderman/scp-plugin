package dev.enderman.minecraft.plugins.scp

import dev.enderman.minecraft.plugins.scp.items.SCP018Item
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import foundation.esoteric.minecraft.plugins.library.commands.GiveCustomItemCommand
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntityManager
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntityPlugin
import foundation.esoteric.minecraft.plugins.library.item.CustomItemManager
import foundation.esoteric.minecraft.plugins.library.item.CustomItemPlugin
import org.bukkit.plugin.java.JavaPlugin

class SCPPlugin : JavaPlugin(), CustomItemPlugin, CustomEntityPlugin {

  override lateinit var customItemManager: CustomItemManager
  override lateinit var customEntityManager: CustomEntityManager

  override fun onEnable() {
    customItemManager = CustomItemManager(this)

    dataFolder.mkdir()
    saveDefaultConfig()

    val commandAPIConfig = CommandAPIBukkitConfig(this)

    CommandAPI.onLoad(commandAPIConfig)
    CommandAPI.onEnable()

    SCP018Item(this)

    GiveCustomItemCommand(this)
  }
}
