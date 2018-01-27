package org.dave.ocsensors.converter;

import li.cil.oc.api.driver.Converter;
import net.minecraftforge.fluids.Fluid;

import java.util.Map;

public class ConverterFluid implements Converter {
    @Override
    public void convert(Object value, Map<Object, Object> output) {
        if(value instanceof Fluid) {
            Fluid fluid = (Fluid) value;
            output.put("name", fluid.getName());
            output.put("density", fluid.getDensity());
            output.put("temperature", fluid.getTemperature());
            output.put("viscosity", fluid.getViscosity());
        }
    }
}
