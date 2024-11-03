package dev.enderman.minecraft.plugins.scp.entities

import dev.enderman.minecraft.plugins.scp.SCPPlugin
import dev.enderman.minecraft.plugins.scp.items.SCP018Item
import foundation.esoteric.minecraft.plugins.library.entity.CustomEntity
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.scheduler.BukkitRunnable

class SCP018Entity<T : Entity>(plugin: SCPPlugin) : CustomEntity<T>(plugin, "scp_018", EntityType.SNOWBALL) {

  private val allEntities: MutableList<Snowball> = mutableListOf()

  override fun toEntity(vararg entities: T): Array<out T> {
    super.toEntity(*entities)

    entities.forEach { allEntities.add(it as Snowball) }

    val scpItem = (plugin as SCPPlugin).customItemManager.getItem("scp_018") as SCP018Item
    entities.forEach { (it as Snowball).item = scpItem.createItem() }

    SCP018Runnable(this as SCP018Entity<Snowball>).runTaskTimer(plugin, 0L, 1L)

    return entities
  }

  @EventHandler
  fun onHit(event: ProjectileHitEvent) {
    val projectile = event.entity

    if (!isEntity(projectile)) {
      return
    }

    event.isCancelled = true

    val hitEntity = event.hitEntity

    if (hitEntity != null) {
      if (hitEntity is LivingEntity) {
        hitEntity.damage(projectile.velocity.length() * 5.0F)
      }
    }

    val hitFace = event.hitBlockFace
    if (hitFace != null) {
      val normalVector = hitFace.direction

      val velocity = projectile.velocity
      val newVelocity = velocity.subtract(normalVector.multiply(2.0F * velocity.dot(normalVector)))
      newVelocity.multiply(3F/2F)
      if (newVelocity.length() > 1F) {
        newVelocity.normalize().multiply(1F)
      }

      val newProjectile = createEntity(projectile.location)
      newProjectile.velocity = newVelocity

      projectile.remove()
    }
  }

  private class SCP018Runnable(private val scpEntity: SCP018Entity<Snowball>) : BukkitRunnable() {

    override fun run() {
      scpEntity.allEntities.forEach {
        if (it.isDead) {
          cancel()
          scpEntity.allEntities.remove(it)
        }

        if (!it.location.block.isEmpty) {
          println("SCP-018 stuck in block! At tick " + Bukkit.getServer().currentTick)
        }

        it.world.spawnParticle(
          Particle.BLOCK,
          it.location,
          1,
          0.5, 0.5, 0.5,
          Material.REDSTONE_BLOCK.createBlockData()
        )
      }
    }
  }
}
