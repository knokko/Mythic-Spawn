package nl.knokko.mythicmobs.spawner.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import nl.knokko.mythicmobs.spawner.plugin.boss.BossSpawn;
import nl.knokko.mythicmobs.spawner.plugin.boss.MessageEntry;
import nl.knokko.mythicmobs.spawner.plugin.command.CommandMythicSpawns;

public class MythicSpawnsPlugin extends JavaPlugin {
	
	private static MythicSpawnsPlugin instance;
	
	public static MythicSpawnsPlugin getInstance() {
		return instance;
	}
	
	private Collection<BossSpawn> spawns;
	private Collection<MessageEntry> priorSpawnMessages;
	
	public Collection<BossSpawn> getSpawns(){
		return spawns;
	}
	
	public Collection<MessageEntry> getMessages(){
		return priorSpawnMessages;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		FileConfiguration config = getConfig();
		ConfigurationSection bosses = config.getConfigurationSection("bosses");
		if (bosses != null) {
			Set<String> keys = bosses.getKeys(false);
			spawns = new ArrayList<BossSpawn>(keys.size());
			for (String key : keys)
				spawns.add(new BossSpawn(bosses.getConfigurationSection(key)));
			Bukkit.getLogger().info("Loaded " + spawns.size() + " mythic bosses spawns.");
		} else {
			spawns = new ArrayList<BossSpawn>(0);
			Bukkit.getLogger().info("Couldn't find bosses in config; assuming this is first time plug-in is used");
		}
		ConfigurationSection messages = config.getConfigurationSection("warn-messages");
		if (messages != null) {
			Set<String> keys = messages.getKeys(false);
			priorSpawnMessages = new ArrayList<MessageEntry>(keys.size());
			for (String key : keys) {
				try {
					priorSpawnMessages.add(new MessageEntry(Integer.parseInt(key), messages.getString(key)));
				} catch (NumberFormatException ex) {
					Bukkit.getLogger().warning("Skipping prior spawn message with invalid time " + key);
				}
			}
		} else {
			priorSpawnMessages = new ArrayList<MessageEntry>(0);
			Bukkit.getLogger().info("Couldn't find prior spawn messages in config; assuming this is first time plug-in is used");
		}
		Bukkit.getPluginManager().registerEvents(new MythicSpawnsEventHandler(), this);
		getCommand("mythicspawns").setExecutor(new CommandMythicSpawns());
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (BossSpawn spawn : spawns) {
				spawn.update();
			}
		}, 20, 20);
	}
	
	@Override
	public void onDisable() {
		FileConfiguration config = getConfig();
		ConfigurationSection bosses = config.createSection("bosses");
		int index = 0;
		for (BossSpawn spawn : spawns)
			spawn.save(bosses.createSection("spawn" + index++));
		ConfigurationSection messages = config.createSection("warn-messages");
		for (MessageEntry message : priorSpawnMessages)
			messages.set(message.getPriorTime() + "", message.getMessageFormat());
		saveConfig();
		instance = null;
	}
}