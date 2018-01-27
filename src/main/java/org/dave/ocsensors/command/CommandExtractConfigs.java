package org.dave.ocsensors.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import org.dave.ocsensors.misc.ConfigurationHandler;
import org.dave.ocsensors.utility.JarExtract;

public class CommandExtractConfigs extends CommandBaseExt {
    @Override
    public String getName() {
        return "extract-configs";
    }

    @Override
    public boolean isAllowed(EntityPlayer player, boolean creative, boolean isOp) {
        return isOp;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        int countReflection = JarExtract.copy("assets/ocsensors/config/reflection", ConfigurationHandler.reflectionDataDir);
        sender.sendMessage(new TextComponentString("Extracted "+countReflection+" reflection integration configs"));

        int countNbt = JarExtract.copy("assets/ocsensors/config/nbt", ConfigurationHandler.nbtDataDir);
        sender.sendMessage(new TextComponentString("Extracted "+countNbt+" nbt integration configs"));
    }
}
