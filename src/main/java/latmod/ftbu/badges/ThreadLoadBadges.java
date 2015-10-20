package latmod.ftbu.badges;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import cpw.mods.fml.relauncher.*;
import ftb.lib.FTBLib;
import latmod.ftbu.api.client.EventFTBUBadges;
import latmod.ftbu.mod.config.FTBUConfigLogin;
import latmod.lib.*;

@SideOnly(Side.CLIENT)
public class ThreadLoadBadges extends Thread
{
	public static final String DEF_BADGES = "http://latvianmodder.github.io/images/badges/global_badges.json";
	private static final FastMap<String, Badge> urlBadges = new FastMap<String, Badge>();
	private static final FastMap<UUID, Badge> customBadges = new FastMap<UUID, Badge>();
	
	public static void init()
	{ new ThreadLoadBadges().start(); }
	
	public ThreadLoadBadges()
	{
		super("BadgeLoader");
		setDaemon(true);
	}
	
	public void run()
	{
		long msStarted = LMUtils.millis();
		urlBadges.clear();
		
		FTBLib.logger.info("Loading badges...");
		Badge.badges.clear();
		
		int loaded = loadBages(DEF_BADGES);
		
		if(!FTBUConfigLogin.customBadges.get().isEmpty())
			loaded += loadBages(FTBUConfigLogin.customBadges.get());
		
		customBadges.clear();
		new EventFTBUBadges(customBadges).post();
		loaded += Badge.badges.putAll(customBadges);
		
		FTBLib.logger.info("Loaded " + loaded + " badges for " + Badge.badges.size() + " players in " + ((LMUtils.millis() - msStarted) / 1000F) + " ms!");
	}
	
	public int loadBages(String url)
	{
		int loaded = 0;
		
		try
		{
			InputStream is = new URL(url).openStream();
			String raw = LMStringUtils.readString(is).trim();
			
			Badges list = LMJsonUtils.fromJson(raw, Badges.class);
			
			for(String k : list.badges.keySet())
				urlBadges.put(k, new Badge(list.badges.get(k)));
			
			for(UUID id : list.players.keySet())
			{
				Badge b = urlBadges.get(list.players.get(id));
				if(id != null && b != null)
				{
					Badge.badges.put(id, b);
					loaded++;
				}
			}
		}
		catch(Exception e) { e.printStackTrace(); }
		
		return loaded;
	}
}