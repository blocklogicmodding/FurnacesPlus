package com.blocklogic.furnacesplus.item;

import com.blocklogic.furnacesplus.FurnacesPlus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class FPItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FurnacesPlus.MODID);

    public static void register (IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
