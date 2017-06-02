package org.dave.ocsensors.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public class TileEntityBase extends TileEntity implements ITickable {
    private boolean initialized = false;
    protected String customName = "";
    protected UUID owner;

    public TileEntityBase() {
        super();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        customName = compound.getString("CustomName");
        owner = compound.getUniqueId("owner");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("CustomName", customName);

        if(hasOwner()) {
            compound.setUniqueId("owner", this.owner);
        }

        return compound;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getProfileByUUID(getOwner()).getName();
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setOwner(EntityPlayer player) {
        if(player == null) {
            return;
        }

        setOwner(player.getUniqueID());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    protected void initialize() {
    }

    @Override
    public void update() {
        if(!this.initialized && !this.isInvalid()) {
            initialize();
            this.initialized = true;
        }
    }
}
