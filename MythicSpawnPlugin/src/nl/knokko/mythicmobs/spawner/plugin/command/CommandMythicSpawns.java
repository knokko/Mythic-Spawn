package nl.knokko.mythicmobs.spawner.plugin.command;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import nl.knokko.mythicmobs.spawner.plugin.MythicSpawnsPlugin;
import nl.knokko.mythicmobs.spawner.plugin.boss.BossSpawn;
import nl.knokko.mythicmobs.spawner.plugin.boss.MessageEntry;

public class CommandMythicSpawns implements CommandExecutor {

	private void sendUseage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "You should use /mythicspawns add/remove/addmessage/removemessage");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 0) {
			if (args[0].equals("add")) {
				if (sender.hasPermission("mythicspawner.add")) {
					if (args.length == 7) {
						String bossName = args[1];
						String worldName = args[2];
						try {
							int spawnX = Integer.parseInt(args[3]);
							try {
								int spawnY = Integer.parseInt(args[4]);
								try {
									int spawnZ = Integer.parseInt(args[5]);
									try {
										int respawnDelay = Integer.parseInt(args[6]) * 1000;
										BossSpawn spawn = new BossSpawn(bossName, worldName, spawnX, spawnY, spawnZ, respawnDelay);
										MythicSpawnsPlugin.getInstance().getSpawns().add(spawn);
										spawn.spawn();
										sender.sendMessage(ChatColor.GREEN + "The spawn has been added");
									} catch (NumberFormatException ex) {
										sender.sendMessage(ChatColor.RED + "The <respawn delay> (" + args[6] + " must be an integer!");
									}
								} catch (NumberFormatException ex) {
									sender.sendMessage(ChatColor.RED + "The <spawn Z> (" + args[5] + " must be an integer!");
								}
							} catch (NumberFormatException ex) {
								sender.sendMessage(ChatColor.RED + "The <spawn Y> (" + args[4] + " must be an integer!");
							}
						} catch (NumberFormatException ex) {
							sender.sendMessage(ChatColor.RED + "The <spawn X> (" + args[3] + " must be an integer!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You should use /mythicspawns add <boss name> <world name> <spawn X> <spawn Y> <spawn Z> <respawn delay>");
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have access to this command.");
				}
			} else if (args[0].equals("remove")) {
				if (sender.hasPermission("mythicspawner.remove")) {
					if (args.length == 2) {
						String bossName = args[1];
						int counter = 0;
						Collection<BossSpawn> spawns = MythicSpawnsPlugin.getInstance().getSpawns();
						Iterator<BossSpawn> iterator = spawns.iterator();
						while (iterator.hasNext()) {
							if (iterator.next().getBossName().equals(bossName)) {
								counter++;
								iterator.remove();
							}
						}
						if (counter == 0)
							sender.sendMessage(ChatColor.RED + "There is no spawner for boss '" + bossName + "'");
						else if (counter == 1)
							sender.sendMessage(ChatColor.GREEN + "The spawn for boss " + bossName + " has been removed.");
						else
							sender.sendMessage(ChatColor.GREEN + "Removed all " + counter + " spawns for boss " + bossName);
					} else {
						sender.sendMessage(ChatColor.RED + "You should use /mythicspawns remove <boss name>");
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have access to this command.");
				}
			} else if (args[0].equals("addmessage")){
				if (sender.hasPermission("mythicspawner.addmessage")) {
					if (args.length >= 3) {
						try {
							int time = Integer.parseInt(args[1]);
							String message = args[2];
							for (int index = 3; index < args.length; index++)
								message += " " + args[index];
							MythicSpawnsPlugin.getInstance().getMessages().add(new MessageEntry(time, message));
							sender.sendMessage(ChatColor.GREEN + "The message has been added.");
						} catch (NumberFormatException ex) {
							sender.sendMessage(ChatColor.RED + "The time in milliseconds (" + args[1] + ") must be an integer.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You should use /ms addmessage <time in milliseconds> <message>");
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have access to this command.");
				}
			} else if (args[0].equals("removemessage")) {
				if (sender.hasPermission("mythicspawner.removemessage")) {
					if (args.length == 2) {
						try {
							int time = Integer.parseInt(args[1]);
							Collection<MessageEntry> messages = MythicSpawnsPlugin.getInstance().getMessages();
							int counter = 0;
							Iterator<MessageEntry> iterator = messages.iterator();
							while (iterator.hasNext()) {
								if (iterator.next().getPriorTime() == time) {
									iterator.remove();
									counter++;
								}
							}
							if (counter == 0)
								sender.sendMessage(ChatColor.RED + "There is no prior spawn message with time " + time);
							else if (counter == 1)
								sender.sendMessage(ChatColor.GREEN + "The prior spawn message has been removed");
							else
								sender.sendMessage(ChatColor.YELLOW + "All " + counter + " prior spawn messages with time " + time + " have been removed");
						} catch (NumberFormatException ex) {
							sender.sendMessage(ChatColor.RED + "The time in milliseconds (" + args[1] + ") must be an integer.");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You should use /ms removemessage <time in milliseconds>");
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have access to this command.");
				}
			} else {
				sendUseage(sender);
			}
		} else {
			sendUseage(sender);
		}
		return true;
	}
}