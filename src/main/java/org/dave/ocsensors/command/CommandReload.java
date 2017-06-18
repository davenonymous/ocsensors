package org.dave.ocsensors.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import org.dave.ocsensors.integration.IntegrationRegistry;

public class CommandReload extends CommandBaseExt {
    @Override
    public String getCommandName() {
        return "reload";
    }

    @Override
    public boolean isAllowed(EntityPlayer player, boolean creative, boolean isOp) {
        return isOp;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        IntegrationRegistry.reloadIntegrations();
        sender.addChatMessage(new TextComponentString("Reloaded integration configs"));
    }
}
