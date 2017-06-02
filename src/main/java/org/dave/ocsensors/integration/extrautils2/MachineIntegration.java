package org.dave.ocsensors.integration.extrautils2;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.dave.ocsensors.integration.AbstractIntegration;
import org.dave.ocsensors.integration.Integrate;
import org.dave.ocsensors.utility.Logz;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

@Integrate(mod = "extrautils2", minecraft_version = "1.10.2")
public class MachineIntegration extends AbstractIntegration {
    Class XU2_TileMachine = null;
    Class XU2_TileMachineProvider = null;

    public MachineIntegration() {
        try {
            XU2_TileMachine = Class.forName("com.rwtema.extrautils2.machine.TileMachine");
            XU2_TileMachineProvider = Class.forName("com.rwtema.extrautils2.machine.TileMachineProvider");
        } catch (ClassNotFoundException e) {
        }
    }

    @Override
    public String getSectionName() {
        return "extrautils2";
    }

    @Override
    public boolean worksWith(TileEntity entity, @Nullable EnumFacing side) {
        if(XU2_TileMachine == null) {
            return false;
        }

        if(XU2_TileMachine.isAssignableFrom(entity.getClass())) {
            return true;
        }
        return false;
    }

    @Override
    public Object getScanData(TileEntity entity, @Nullable EnumFacing side) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            Method isProcessing = XU2_TileMachine.getDeclaredMethod("isProcessing");
            result.put("processing", isProcessing.invoke(entity));
            result.put("totalTime", XU2_TileMachine.getField("totalTime").get(entity));
            result.put("energyOutput", XU2_TileMachine.getField("energyOutput").get(entity));
            result.put("processTime", ObfuscationReflectionHelper.getPrivateValue(XU2_TileMachine, entity, "processTime"));
        } catch (NoSuchMethodException e) {
            Logz.debug("No such method: %s", e);
        } catch (InvocationTargetException e) {
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
            Logz.info("No such field: %s", e);
        }

        return result;
    }
}
