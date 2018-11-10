package nl.knokko.mythicmobs.spawner.plugin;

import java.util.Collection;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import nl.knokko.mythicmobs.spawner.plugin.boss.BossSpawn;

public class MythicSpawnsEventHandler implements Listener {
	
	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent event) {
		LivingEntity entity = event.getEntity();
		Collection<BossSpawn> spawns = MythicSpawnsPlugin.getInstance().getSpawns();
		for (BossSpawn spawn : spawns)
			spawn.onSpawn(entity);
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		Collection<BossSpawn> spawns = MythicSpawnsPlugin.getInstance().getSpawns();
		for (BossSpawn spawn : spawns)
			spawn.onDeath(entity);
	}
}