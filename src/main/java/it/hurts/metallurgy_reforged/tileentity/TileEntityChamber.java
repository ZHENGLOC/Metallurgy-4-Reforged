package it.hurts.metallurgy_reforged.tileentity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import it.hurts.metallurgy_reforged.Metallurgy;
import it.hurts.metallurgy_reforged.block.BlockChamber;
import it.hurts.metallurgy_reforged.recipe.BlockSublimationRecipes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileEntityChamber extends TileEntityLockable implements ITickable, ISidedInventory{

	public static final int METAL_SLOT = 0;
	public static final int FUEL_SLOT = 1;


	private static final int[] SLOTS_TOP = new int[] {METAL_SLOT};
	private static final int[] SLOTS_BOTTOM = new int[] {FUEL_SLOT};
	private static final int[] SLOTS_SIDES = new int[] {FUEL_SLOT};

	private final IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
	private final IItemHandler handlerBottom = new SidedInvWrapper(this,EnumFacing.DOWN);
	private final IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);


	private NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);

	private String chamberCustomName;

	public int fuelTime = 0;
	private int activeTime = 0;
	public PotionEffect potionEffect = null;

	public List<UUID> affectedPlayers = Lists.newArrayList();


	@Override
	public int getSizeInventory() 
	{
		return this.inventory.size();
	}

	@Override
	public boolean isEmpty() 
	{
		return this.inventory.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) 
	{
		return this.inventory.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		return ItemStackHelper.getAndSplit(this.inventory, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) 
	{
		return ItemStackHelper.getAndRemove(this.inventory, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) 
	{

		ItemStack itemstack = this.inventory.get(index);
		boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
		this.inventory.set(index, stack);

		if (stack.getCount() > this.getInventoryStackLimit())
		{
			stack.setCount(this.getInventoryStackLimit());
		}

		if (index == METAL_SLOT && !flag)
			this.markDirty();
	}

	public void setPotionEffect(PotionEffect effect)
	{
		this.potionEffect = effect;
		this.updateBlock();
	}

	public void updateBlock()
	{
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 4);
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) 
	{
		if (this.world.getTileEntity(this.pos) != this)
		{
			return false;
		}
		else
		{
			return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	public void openInventory(EntityPlayer player) 
	{
	}

	public void closeInventory(EntityPlayer player)
	{
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) 
	{
		if (index == FUEL_SLOT)
		{
			ItemStack itemstack = this.inventory.get(FUEL_SLOT);
			return TileEntityFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && itemstack.getItem() != Items.BUCKET;
		}
		else
		{
			ItemStack itemstack = this.inventory.get(METAL_SLOT);
			BlockSublimationRecipes recipes = BlockSublimationRecipes.getInstance();		
			int recipeAmount = recipes.getSublimationBlockAmount(stack);
			return itemstack.getCount() < recipeAmount;
		}
	}

	@Override
	public int getField(int id) 
	{
		return 0;
	}

	@Override
	public void setField(int id, int value) 
	{

	}

	@Override
	public int getFieldCount() 
	{
		return 0;
	}

	@Override
	public void clear() 
	{
		this.inventory.clear();
	}

	@Override
	public String getName() 
	{
		return this.hasCustomName() ? this.chamberCustomName : "container.chamber";
	}

	@Override
	public boolean hasCustomName() 
	{
		return this.chamberCustomName != null && !this.chamberCustomName.isEmpty();
	}

	public void setChamberCustomName(String chamberCustomName)
	{
		this.chamberCustomName = chamberCustomName;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) 
	{
		return null;
	}

	@Override
	public String getGuiID()
	{
		return Metallurgy.MODID + ":chamber";
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) 
	{
		if (side == EnumFacing.DOWN)
		{
			return SLOTS_BOTTOM;
		}
		else
		{
			return side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES;
		}
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) 
	{	
		return this.isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) 
	{
		return index == FUEL_SLOT;
	}

	@Override
	public void update() 
	{

		ItemStack METAL_STACK = getStackInSlot(METAL_SLOT);
		ItemStack FUEL_STACK = getStackInSlot(FUEL_SLOT);

		PotionEffect potionEffect = this.potionEffect;
		if(potionEffect == null)
			potionEffect = BlockSublimationRecipes.getInstance().getSublimationResult(METAL_STACK);

		if(this.fuelTime > 0)
			this.fuelTime--;

		int fuelValue = TileEntityFurnace.getItemBurnTime(FUEL_STACK);


		if(this.fuelTime > 0)
		{
			if(potionEffect != null)
			{
				if(!this.isActive())
				{
					this.activeTime = 1;
					this.setState(true);
					this.setPotionEffect(potionEffect);
				}
				else
				{
					if(this.activeTime < potionEffect.getDuration())
					{
						this.activeTime++;


						int range = 6;

						AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos,pos).grow(range);

						List<EntityPlayer> entityPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, axisAlignedBB);
						for(EntityPlayer player : entityPlayers)
						{	
							if(!player.isPotionActive(potionEffect.getPotion())) 
							{
								player.addPotionEffect(new PotionEffect(potionEffect.getPotion(),potionEffect.getDuration() - this.activeTime));
								UUID uuid = player.getUniqueID();
								if(!this.affectedPlayers.contains(uuid))
									this.affectedPlayers.add(uuid);
							}
						}

					}
					else
					{
						setInventorySlotContents(METAL_SLOT, ItemStack.EMPTY);
						this.setPotionEffect(null);
						this.setState(false);
						this.activeTime = 0;
					}
				}
			}
		}
		else if(fuelValue > 0 && potionEffect != null)
		{
			FUEL_STACK.shrink(1);
			this.fuelTime = fuelValue;
		}	
	}



	public boolean isActive()
	{
		return this.activeTime > 0;
	}


	public void setState(boolean active)
	{
		this.world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockChamber.ACTIVE, active));

	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{	

		super.writeToNBT(compound);
		this.writeChamberToNBT(compound);
		return compound;
	}

	public void writeChamberToNBT(NBTTagCompound compound)
	{
		compound.setInteger("activeTime", this.activeTime);
		compound.setInteger("fuelTime", this.fuelTime);
		if(this.potionEffect != null)
			this.potionEffect.writeCustomPotionEffectToNBT(compound);

		ItemStackHelper.saveAllItems(compound, this.inventory);

		if (this.hasCustomName())
		{
			compound.setString("CustomName", this.chamberCustomName);
		}
	}

	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.readChamberFromNBT(compound);
	}

	public void readChamberFromNBT(NBTTagCompound compound)
	{

		this.inventory = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(compound, this.inventory);

		this.fuelTime = compound.getInteger("fuelTime");
		this.activeTime = compound.getInteger("activeTime");
		this.potionEffect = PotionEffect.readCustomPotionEffectFromNBT(compound);

		if (compound.hasKey("CustomName", 8))
		{
			this.chamberCustomName = compound.getString("CustomName");
		}

	}

	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		return oldState.getBlock() != newState.getBlock();
	}


	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			if (facing == EnumFacing.DOWN)
				return (T) handlerBottom;
			else if (facing == EnumFacing.UP)
				return (T) handlerTop;
			else
				return (T) handlerSide;
		return super.getCapability(capability, facing);
	}

	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(this.pos, 1, this.getUpdateTag());
	}

	public NBTTagCompound getUpdateTag() 
	{
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		NBTTagCompound tag = pkt.getNbtCompound();
		readChamberFromNBT(tag);
	}

}
