package nl.knokko.mythicmobs.spawner.plugin.boss;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class BossSpawn {
	
	private final String bossName;
	
	private final String worldName;
	private final int spawnX;
	private final int spawnY;
	private final int spawnZ;
	
	private final int respawnDelay;
	
	private long timeOfDeath;
	private UUID current;
	
	private boolean isSpawning;
	
	public BossSpawn(String bossName, String worldName, int spawnX, int spawnY, int spawnZ, int deathDelay) {
		this.bossName = bossName;
		this.worldName = worldName;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.spawnZ = spawnZ;
		this.respawnDelay = deathDelay;
	}
	
	public BossSpawn(ConfigurationSection section) {
		this.bossName = section.getString("bossName");
		this.worldName = section.getString("worldName");
		this.spawnX = section.getInt("spawnX");
		this.spawnY = section.getInt("spawnY");
		this.spawnZ = section.getInt("spawnZ");
		this.respawnDelay = section.getInt("respawnDelay");
		this.timeOfDeath = section.getInt("timeOfDeath");
		if (timeOfDeath == 0) {
			current = new UUID(section.getLong("mostID"), section.getLong("leastID"));
		}
	}
	
	public void save(ConfigurationSection section) {
		section.set("bossName", bossName);
		section.set("worldName", worldName);
		section.set("spawnX", spawnX);
		section.set("spawnY", spawnY);
		section.set("spawnZ", spawnZ);
		section.set("respawnDelay", respawnDelay);
		section.set("timeOfDeath", timeOfDeath);
		if (timeOfDeath == 0) {
			section.set("mostID", current.getMostSignificantBits());
			section.set("leastID", current.getLeastSignificantBits());
		} else {
			section.set("mostID", null);
			section.set("leastID", null);
		}
	}
	
	public String getBossName() {
		return bossName;
	}
	
	public void spawn() {
		World world = Bukkit.getWorld(worldName);
		if (world != null) {
			int chunkX = spawnX / 16;
			int chunkZ = spawnZ / 16;
			if (spawnX < 0) chunkX--;
			if (spawnZ < 0) chunkZ--;
			world.loadChunk(chunkX, chunkZ);
			isSpawning = true;
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm mobs spawn " + bossName + " 1 " + worldName + "," + spawnX + "," + spawnY + "," + spawnZ);
			isSpawning = false;
			timeOfDeath = 0;
		} else {
			Bukkit.broadcastMessage("Can't spawn " + bossName + " because world " + worldName + " can't be found.");
		}
	}
	
	public void onSpawn(LivingEntity entity) {
		if (isSpawning) {
			Location loc = entity.getLocation();
			if (loc.getX() == spawnX && loc.getY() == spawnY && loc.getZ() == spawnZ && loc.getWorld().getName().equals(worldName)) {
				entity.setRemoveWhenFarAway(false);
				current = entity.getUniqueId();
			}
		}
	}
	
	public void onDeath(Entity entity) {
		if (entity.getUniqueId().equals(current)) {
			timeOfDeath = System.currentTimeMillis();
			current = null;
		}
	}
	
	public void update() {
		if (timeOfDeath != 0 && System.currentTimeMillis() - timeOfDeath >= respawnDelay) {
			spawn();
		}
	}
	
	public UUID getCurrentID() {
		return current;
	}
}