var TileEntityFurnace = Java.type("net.minecraft.tileentity.TileEntityFurnace");

var getSupportedPrefixes = function() {
    return ['Furnace'];
}

var worksWith = function(tileEntity, side) {
    return tileEntity instanceof TileEntityFurnace;
};

var addScanData = function(scanData, tileEntityFurnace, side) {
    scanData.add("Furnace.IsBurning", tileEntityFurnace.isBurning());
};