package org.dave.ocsensors.command;

public class CommandOCSensors extends CommandMenu {
    @Override
    public void initEntries() {
        this.addSubcommand(new CommandReload());
        this.addSubcommand(new CommandExtractConfigs());
    }

    @Override
    public String getName() {
        return "ocsensors";
    }
}
