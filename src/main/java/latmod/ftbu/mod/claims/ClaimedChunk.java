package latmod.ftbu.mod.claims;

import net.minecraft.entity.player.EntityPlayer;
import latmod.ftbu.core.LMPlayer;
import latmod.ftbu.core.util.MathHelperLM;

public final class ClaimedChunk
{
	public final Claims claims;
	public final int dim, posX, posZ;
	
	public ClaimedChunk(Claims c, int d, int x, int z)
	{
		claims = c;
		dim = d;
		posX = x;
		posZ = z;
	}
	
	public ClaimedChunk(EntityPlayer ep)
	{ this(LMPlayer.getPlayer(ep).claims, ep.dimension, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ)); }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || (o.getClass() == ClaimedChunk.class && equalsChunk((ClaimedChunk)o))); }
	
	public boolean equalsChunk(int d, int x, int z)
	{ return dim == d && posX == x && posZ == z; }
	
	public boolean equalsChunk(ClaimedChunk c)
	{ return equalsChunk(c.dim, c.posX, c.posZ); }
	
	public double getDistSq(double x, double z)
	{
		double x0 = (posX + 0.5D) * 16D;
		double z0 = (posZ + 0.5D) * 16D;
		return MathHelperLM.distSq(x0, 0D, z0, x, 0D, z);
	}
	
	public double getDistSq(ClaimedChunk c)
	{ return getDistSq((c.posX + 0.5D) * 16D, (c.posZ + 0.5D) * 16D); }
}