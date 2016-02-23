package ftb.utils.world;

import ftb.lib.PrivacyLevel;
import ftb.lib.api.net.MessageLM;
import ftb.lib.api.players.*;
import ftb.utils.mod.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.UUID;

public class ChunkType
{
	public static final ChunkType UNLOADED = new ChunkType(0, "unloaded", EnumChatFormatting.DARK_GRAY, 0xFF000000);
	public static final ChunkType SPAWN = new ChunkType(1, "spawn", EnumChatFormatting.AQUA, 0xFF00EFDF);
	public static final ChunkType WILDERNESS = new ChunkType(2, "wilderness", EnumChatFormatting.DARK_GREEN, 0xFF2F9E00);
	public static final ChunkType WORLD_BORDER = new ChunkType(3, "world_border", EnumChatFormatting.RED, 0xFFFF0000);
	
	public static final ChunkType[] UNCLAIMED_VALUES = new ChunkType[] {UNLOADED, SPAWN, WILDERNESS, WORLD_BORDER};
	
	public void write(ByteBuf io)
	{
		io.writeByte(ID);
	}
	
	public static ChunkType read(int dim, ChunkCoordIntPair pos, ByteBuf io)
	{
		byte id = io.readByte();
		
		if(id >= 0 && id <= UNCLAIMED_VALUES.length) return UNCLAIMED_VALUES[id];
		else if(id == 99)
		{
			UUID owner = MessageLM.readUUID(io);
			ClaimedChunk chunk = new ClaimedChunk(owner, dim, pos.chunkXPos, pos.chunkZPos);
			chunk.isChunkloaded = io.readBoolean();
			return new PlayerClaimed(chunk);
		}
		
		return UNLOADED;
	}
	
	public static final class PlayerClaimed extends ChunkType
	{
		public final ClaimedChunk chunk;
		
		public PlayerClaimed(ClaimedChunk o)
		{
			super(99, "claimed", null, 0);
			chunk = o;
		}
		
		public PlayerClaimed asClaimed()
		{ return this; }
		
		public boolean isChunkOwner(LMPlayerMP p)
		{ return chunk.getOwner().equalsPlayer(p); }
		
		public EnumChatFormatting getChatColor(LMPlayer p)
		{ return p.getWorld().getPlayer(chunk.ownerID).isFriend(p) ? EnumChatFormatting.GREEN : EnumChatFormatting.BLUE; }
		
		public int getAreaColor(LMPlayer p)
		{ return p.getWorld().getPlayer(chunk.ownerID).isFriend(p) ? 0xFF00FF21 : 0xFF0094FF; }
		
		public boolean canInteract(LMPlayerMP p, boolean leftClick)
		{
			LMPlayerMP chunkOwner = chunk.getOwner();
			
			if(chunkOwner.equals(p)) return true;
			else if(p.isFake()) return FTBUPlayerDataMP.get(chunkOwner).getFlag(FTBUPlayerData.FAKE_PLAYERS);
			
			PrivacyLevel level = FTBUPermissions.claims_forced_security.getEnum(p.getProfile());
			if(level == null) level = FTBUPlayerDataMP.get(chunkOwner).blocks;
			return level.canInteract(chunkOwner, p);
		}
		
		public void write(ByteBuf io)
		{
			super.write(io);
			MessageLM.writeUUID(io, chunk.ownerID);
			io.writeBoolean(chunk.isChunkloaded);
		}
		
		public boolean equals(Object o)
		{
			return super.equals(o) && ((PlayerClaimed) o).chunk.equalsChunk(chunk);
			
		}
	}
	
	public final int ID;
	public final String lang;
	private final EnumChatFormatting chatColor;
	private final int areaColor;
	
	public ChunkType(int id, String s, EnumChatFormatting c, int col)
	{
		ID = id;
		lang = "chunktype." + s;
		chatColor = c;
		areaColor = col;
	}
	
	public PlayerClaimed asClaimed()
	{ return null; }
	
	public boolean isChunkOwner(LMPlayerMP p)
	{ return this == WILDERNESS; }
	
	public boolean drawGrid()
	{ return this != WILDERNESS && this != UNLOADED; }
	
	public String getIDS()
	{ return FTBU.mod.translate(lang); }
	
	public int hashCode()
	{ return ID; }
	
	public boolean equals(Object o)
	{ return o == this || ID == o.hashCode(); }
	
	public EnumChatFormatting getChatColor(LMPlayer p)
	{ return chatColor; }
	
	public int getAreaColor(LMPlayer p)
	{ return areaColor; }
	
	public boolean canInteract(LMPlayerMP p, boolean leftClick)
	{ return this == WILDERNESS || this == SPAWN; }
}