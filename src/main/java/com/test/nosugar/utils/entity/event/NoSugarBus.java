package com.test.nosugar.utils.entity.event;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;

public class NoSugarBus {
    public static final IEventBus BUS = BusBuilder.builder().build();
}
