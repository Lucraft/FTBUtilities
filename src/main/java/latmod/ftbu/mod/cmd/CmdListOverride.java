package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.FastList;
import latmod.ftbu.core.world.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdListOverride extends CommandLM
{
	public CmdListOverride()
	{ super("list", CommandLevel.ALL); }
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "[uuid]");
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		FastList<EntityPlayerMP> players = LatCoreMC.getAllOnlinePlayers().values;
		boolean printUUID = args.length > 0 && args[0].equals("uuid");
		
		LatCoreMC.printChat(ics, "Players currently online: [ " + players.size() + " ]");
		for(int i = 0; i < players.size(); i++)
		{
			EntityPlayerMP ep = players.get(i);
			LMPlayer p = LMWorldServer.inst.getPlayer(ep);
			
			if(printUUID)
				LatCoreMC.printChat(ics, p.getName() + " :: " + ep.getUniqueID());
			else
				LatCoreMC.printChat(ics, p.getName());
		}
		
		return null;
	}
}