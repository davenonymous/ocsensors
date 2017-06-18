package org.dave.ocsensors.integration;

public @interface Integrate {
    String mod() default "";

    String name();
}
