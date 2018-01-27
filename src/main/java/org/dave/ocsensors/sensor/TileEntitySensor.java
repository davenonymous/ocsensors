package org.dave.ocsensors.sensor;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.dave.ocsensors.base.TileEntitySidedEnvironmentBase;
import org.dave.ocsensors.integration.AbstractIntegration;
import org.dave.ocsensors.integration.IntegrationRegistry;
import org.dave.ocsensors.misc.ConfigurationHandler;
import org.dave.ocsensors.utility.Logz;

import java.util.*;

public class TileEntitySensor extends TileEntitySidedEnvironmentBase {

    public TileEntitySensor() {
        super("sensor", Arrays.asList(EnumFacing.UP, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH));
    }

    @Callback(getter = true, doc = "Maximum range the sensor can scan blocks")
    public Object[] range(final Context context, final Arguments args) {
        return new Object[] {ConfigurationHandler.SensorSettings.maxRange};
    }

    @Callback(getter = true, doc = "Maximum range the sensor can search for specific blocks")
    public Object[] searchRange(final Context context, final Arguments args) {
        return new Object[] {ConfigurationHandler.SensorSettings.maxSearchRange};
    }

    @Callback(limit = 1, doc = "function(x1:number, y1:number, z1:number, x2:number, y2:number, z3:number):table -- Scans a region relative to the sensor for entities")
    public Object[] searchEntities(final Context context, final Arguments args) {
        int xDelta1 = args.checkInteger(0);
        int yDelta1 = args.checkInteger(1);
        int zDelta1 = args.checkInteger(2);
        int xDelta2 = args.checkInteger(3);
        int yDelta2 = args.checkInteger(4);
        int zDelta2 = args.checkInteger(5);

        BlockPos startPoint = this.getPos().add(xDelta1, yDelta1, zDelta1);
        BlockPos endPoint = this.getPos().add(xDelta2, yDelta2, zDelta2);
        AxisAlignedBB boundingBox = new AxisAlignedBB(startPoint, endPoint);

        List<Map<String, Object>> result = new ArrayList<>();
        for(Entity entity : this.getWorld().getEntitiesWithinAABB(Entity.class, boundingBox)) {
            if(!ConfigurationHandler.SensorSettings.disableScanPause) {
                context.pause(ConfigurationHandler.SensorSettings.pauseForEntity);
            }

            if(ConfigurationHandler.SensorSettings.hideSneakingEntities && entity instanceof EntityLivingBase && entity.isSneaking()) {
                continue;
            }

            Map<String, Object> entityData = IntegrationRegistry.getDataForEntity(entity);
            if(!entityData.containsKey("type")) {
                if (entity instanceof EntityItem) {
                    entityData.put("type", "item");
                }
                if (entity instanceof EntityLivingBase) {
                    entityData.put("type", "neutral");
                }
                if (entity instanceof EntityMob) {
                    entityData.put("type", "mob");
                }
                if (entity instanceof EntityPlayer) {
                    entityData.put("type", "player");
                }
            }

            if(!entityData.containsKey("type")) {
                continue;
            }

            HashMap<String, Double> posMap = new HashMap<>();
            posMap.put("x", entity.getPositionVector().x - this.getPos().getX());
            posMap.put("y", entity.getPositionVector().y - this.getPos().getY());
            posMap.put("z", entity.getPositionVector().z - this.getPos().getZ());

            entityData.put("pos", posMap);
            result.add(entityData);
        }

        return new Object[] { result };
    }

    @Callback(limit = 1, doc = "function(x:number, y:number, z:number, [side:number]):table -- Scans a block relative to the sensor")
    public Object[] scan(final Context context, final Arguments args) {
        int xDelta = args.checkInteger(0);
        int yDelta = args.checkInteger(1);
        int zDelta = args.checkInteger(2);

        double length = Math.sqrt(xDelta * xDelta + yDelta * yDelta + zDelta * zDelta);
        if(length > ConfigurationHandler.SensorSettings.maxRange) {
            return new Object[] { null, "out of range"};
        }

        int sideInt = args.optInteger(3, -1);
        EnumFacing side = sideInt == -1 ? null : EnumFacing.getFront(sideInt);

        BlockPos pos = this.getPos().add(xDelta, yDelta, zDelta);
        IBlockState state = this.getWorld().getBlockState(pos);
        Block block = state.getBlock();

        Map<String, Object> blockInfo = new HashMap<>();
        blockInfo.put("name", block.getRegistryName());
        blockInfo.put("meta", block.getMetaFromState(state));

        int redstonePower = getWorld().getRedstonePower(pos, side != null ? side : EnumFacing.UP);
        if(redstonePower > 0) {
            blockInfo.put("redstonePower", redstonePower);
        }


        if(!this.getWorld().isAirBlock(pos)) {
            if(!ConfigurationHandler.SensorSettings.disableScanPause) {
                context.pause(ConfigurationHandler.SensorSettings.pauseForBlock);
            }
            ItemStack stack = block.getPickBlock(state, null, this.getWorld(), pos, null);
            if(!stack.isEmpty()) {
                blockInfo.put("label", stack.getDisplayName());
            } else {
                blockInfo.put("label", block.getRegistryName());
            }

        } else {
            if(!ConfigurationHandler.SensorSettings.disableScanPause) {
                context.pause(ConfigurationHandler.SensorSettings.pauseForAirBlock);
            }
        }

        TileEntity entity = this.getWorld().getTileEntity(pos);
        Map<String, Object> blockData = null;
        if(entity != null) {
            if(!ConfigurationHandler.SensorSettings.disableScanPause) {
                context.pause(ConfigurationHandler.SensorSettings.pauseForTileEntity);
            }
            blockData = IntegrationRegistry.getDataForTileEntity(entity, side);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("block", blockInfo);
        result.put("data", blockData);

        return new Object[]{ result };
    }

    @Callback(limit = 1, doc = "function([name:string=\"\"], [meta:number=-1], [section:string=\"\"], [range:number=<max>]):table -- Search for blocks matching the given criteria in the given range")
    public Object[] search(final Context context, final Arguments args) {
        if(ConfigurationHandler.SensorSettings.disableSearch) {
            return new Object[] { null, "command is disabled in config" };
        }

        String name = args.optString(0, "");
        int meta = args.optInteger(1, -1);
        String section = args.optString(2, "");
        double rangeD = (int)Math.floor(Math.min(
                ConfigurationHandler.SensorSettings.maxSearchRange,
                args.optDouble(3, ConfigurationHandler.SensorSettings.maxSearchRange)
        ));

        if(name.length() == 0 && meta == -1 && section.length() == 0) {
            return new Object[] { null, "no search criteria specified" };
        }

        AbstractIntegration wantedIntegration = null;
        if(section.length() > 0) {
            wantedIntegration = IntegrationRegistry.getIntegrationByName(section);

            if(wantedIntegration == null) {
                return new Object[] { null, "invalid search criteria, section does not exist" };
            }
        }

        int range = (int)Math.floor(rangeD);

        float totalSleep = 0.0f;
        ArrayList<HashMap<String, Integer>> result = new ArrayList<>();
        for(int x = this.getPos().getX() - range; x <= this.getPos().getX() + range; x++) {
            for(int y = this.getPos().getY() - range; y <= this.getPos().getY() + range; y++) {
                for(int z = this.getPos().getZ() - range; z <= this.getPos().getZ() + range; z++) {
                    int xDelta = x - this.getPos().getX();
                    int yDelta = y - this.getPos().getY();
                    int zDelta = z - this.getPos().getZ();

                    if(xDelta == 0 && yDelta == 0 && zDelta == 0) {
                        continue;
                    }

                    double length = Math.sqrt(xDelta * xDelta + yDelta * yDelta + zDelta * zDelta);
                    if(length > rangeD) {
                        continue;
                    }

                    totalSleep += ConfigurationHandler.SensorSettings.pauseForSearchPerBlock;

                    BlockPos pos = new BlockPos(x,y,z);
                    IBlockState state = this.getWorld().getBlockState(pos);
                    Block block = state.getBlock();

                    if(name.length() > 0 && !name.equalsIgnoreCase(block.getRegistryName().toString())) {
                        continue;
                    }

                    if(meta > -1 && meta != block.getMetaFromState(state)) {
                        continue;
                    }

                    if(section.length() > 0) {
                        TileEntity entity = this.getWorld().getTileEntity(pos);
                        if(entity == null) {
                            continue;
                        }

                        if(!wantedIntegration.worksWith(entity, null)) {
                            continue;
                        }
                    }

                    HashMap<String, Integer> posMap = new HashMap<>();
                    posMap.put("x", xDelta);
                    posMap.put("y", yDelta);
                    posMap.put("z", zDelta);
                    result.add(posMap);
                }
            }
        }

        if(!ConfigurationHandler.SensorSettings.disableSearchPause) {
            context.pause(totalSleep);
        }

        return new Object[]{ result };
    }

}
