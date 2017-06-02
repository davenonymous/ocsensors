package org.dave.ocsensors.base;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import org.dave.ocsensors.utility.Logz;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TileEntitySidedEnvironmentBase extends TileEntityBase implements SidedEnvironment, Environment {
    protected Node node = null;
    private List<EnumFacing> invalidSides = null;

    public TileEntitySidedEnvironmentBase(String componentName, List<EnumFacing> invalidSides) {
        this.node = Network.newNode(this, Visibility.Network).withComponent(componentName).create();
        this.invalidSides = invalidSides;
    }

    @Override
    protected void initialize() {
        super.initialize();

        Network.joinOrCreateNetwork(this);
        Logz.info("This network is now: %s", node);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        // Make sure to remove the node from its network when its environment,
        // meaning this tile entity, gets unloaded.
        if (node != null) node.remove();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        // Make sure to remove the node from its network when its environment,
        // meaning this tile entity, gets unloaded.
        if (node != null) node.remove();
    }

    @Override
    public void readFromNBT(final NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        // The host check may be superfluous for you. It's just there to allow
        // some special cases, where getNode() returns some node managed by
        // some other instance (for example when you have multiple internal
        // nodes in this tile entity).
        if (node != null && node.host() == this) {
            // This restores the node's address, which is required for networks
            // to continue working without interruption across loads. If the
            // node is a power connector this is also required to restore the
            // internal energy buffer of the node.
            node.load(nbt.getCompoundTag("oc:node"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        // See readFromNBT() regarding host check.
        if (node != null && node.host() == this) {
            final NBTTagCompound nodeNbt = new NBTTagCompound();
            node.save(nodeNbt);
            nbt.setTag("oc:node", nodeNbt);
        }

        return nbt;
    }

    @Override
    public Node sidedNode(EnumFacing side) {
        if(invalidSides.contains(side)) {
            return null;
        }

        return node;
    }

    @Override
    public boolean canConnect(EnumFacing side) {
        if(invalidSides.contains(side)) {
            return false;
        }

        return true;
    }

    @Override
    public Node node() {
        return node;
    }

    @Override
    public void onConnect(Node node) {

    }

    @Override
    public void onDisconnect(Node node) {

    }

    @Override
    public void onMessage(Message message) {

    }
}
