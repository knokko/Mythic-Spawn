package nl.knokko.mythicmobs.spawner.plugin.boss;

public class MessageEntry {
	
	private final int time;
	
	private final String message;
	
	public MessageEntry(int time, String message) {
		this.time = time;
		this.message = message.replaceAll("&", "§");
	}
	
	public int getPriorTime() {
		return time;
	}
	
	public String getMessageFormat() {
		return message;
	}
	
	public String getMessage(String bossName) {
		return message.replaceAll("<mob.name>", bossName);
	}
}