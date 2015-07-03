package latmod.ftbu.mod.claims;

import java.util.List;

import latmod.ftbu.core.LMPlayer;
import latmod.ftbu.core.util.MathHelperLM;
import latmod.ftbu.mod.FTBU;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.relauncher.*;

public enum ChunkType
{
	UNLOADED("unloaded", EnumChatFormatting.DARK_GRAY, 0x000000),
	SPAWN("spawn", EnumChatFormatting.AQUA, 0x00EFDF),
	WORLD_BORDER("world_border", EnumChatFormatting.RED, 0xFF0000),
	WILDERNESS("wilderness", EnumChatFormatting.DARK_GREEN, 0x2F9E00),
	CLAIMED_OTHER("claimed", EnumChatFormatting.BLUE, 0x0094FF),
	CLAIMED_SELF("claimed", EnumChatFormatting.GREEN, 0x00FF21),
	CLAIMED_FRIEND("claimed", EnumChatFormatting.GREEN, 0x00FF21),
	
	; public static final ChunkType[] VALUES = values();
	
	public final int ID;
	public final String lang;
	public final EnumChatFormatting chatColor;
	public final int areaColor;
	
	ChunkType(String s, EnumChatFormatting c, int col)
	{
		ID = ordinal();
		lang = s;
		chatColor = c;
		areaColor = col;
	}
	
	public boolean isFriendly()
	{ return this == WILDERNESS || this == CLAIMED_SELF || this == CLAIMED_FRIEND; }
	
	public boolean isClaimed()
	{ return this == CLAIMED_OTHER || this == CLAIMED_SELF || this == CLAIMED_FRIEND; }
	
	public boolean canClaim(boolean admin)
	{ return this == WILDERNESS || (admin && (this == SPAWN || isClaimed())); }
	
	public boolean drawGrid()
	{ return this != WILDERNESS && this != UNLOADED; }
	
	@SideOnly(Side.CLIENT)
	public String getIDS()
	{ return FTBU.mod.translateClient("chunktype." + lang); }
	
	public static ChunkType get(int dim, int cx, int cz, LMPlayer p)
	{
		//if(!LatCoreMC.isDedicatedServer()) return WILDERNESS;
		
		WorldServer w = DimensionManager.getWorld(dim);
		if(w != null && !w.getChunkProvider().chunkExists(cx, cz)) return UNLOADED;
		
		if(Claims.isInSpawn(dim, cx, cz)) return SPAWN;
		if(Claims.isOutsideWorldBorder(dim, cx, cz)) return WORLD_BORDER;
		ClaimedChunk c = Claims.get(dim, cx, cz);
		if(c == null) return WILDERNESS;
		if(p == null) return CLAIMED_OTHER;
		else if(c.claims.owner.equalsPlayer(p)) return CLAIMED_SELF;
		else if(c.claims.owner.isFriend(p)) return CLAIMED_FRIEND;
		else return CLAIMED_OTHER;
	}
	
	public static ChunkType getD(int dim, double x, double z, LMPlayer p)
	{ return get(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z), p); }
	
	@SideOnly(Side.CLIENT)
	public static void getMessage(int dim, int cx, int cz, LMPlayer p, List<String> l, boolean shift)
	{
		ChunkType t = get(dim, cx, cz, p);
		
		if(t == UNLOADED || t == SPAWN || t == WILDERNESS || t == WORLD_BORDER) l.add(t.chatColor + t.getIDS());
		else
		{
			l.add(t.chatColor + t.getIDS());
			if(shift)
			{
				ClaimedChunk c = Claims.get(dim, cx, cz);
				if(!c.claims.desc.isEmpty())
					l.add(c.claims.desc);
				l.add(c.claims.owner.getName());
			}
		}
	}
}