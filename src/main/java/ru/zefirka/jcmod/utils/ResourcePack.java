package ru.zefirka.jcmod.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;

import java.util.ArrayList;
import java.util.List;

public class ResourcePack {
    private static final String MAIN_PACK_NAME = "file/JediPack.Eternal.zip";

    @SuppressWarnings("UnstableApiUsage")
    public static void updateRP(Minecraft minecraft) {
        ResourcePackList resourcePackList = minecraft.getResourcePackRepository();
        for (ResourcePackInfo resourcePackInfo : resourcePackList.getSelectedPacks()) {
            final String id = resourcePackInfo.getId();
            if (id.toLowerCase().contains("custom")) return; //custom RP selected
            if (id.equals(MAIN_PACK_NAME)) return; //contains RP
        }
        ResourcePackInfo jediPack = null;
        for (ResourcePackInfo resourcePackInfo : resourcePackList.getAvailablePacks()) {
            final String id = resourcePackInfo.getId();
            if (id.toLowerCase().contains("custom")) return; //custom RP available
            if (resourcePackInfo.getId().equals(MAIN_PACK_NAME)) {
                jediPack = resourcePackInfo;
                break;
            }
        }
        if (jediPack != null) {
            List<ResourcePackInfo> resourcePackInfoCollection = new ArrayList<>(resourcePackList.getSelectedPacks());
            Lists.reverse(resourcePackInfoCollection);
            resourcePackInfoCollection.add(jediPack);
            resourcePackList.setSelected(resourcePackInfoCollection.stream()
                    .map(ResourcePackInfo::getId).collect(ImmutableList.toImmutableList()));
            updatePackList(minecraft, resourcePackList);
        }
    }

    @SuppressWarnings("deprecation")
    private static void updatePackList(Minecraft minecraft, ResourcePackList resourcePackList) {
        List<String> list = ImmutableList.copyOf(minecraft.options.resourcePacks);
        minecraft.options.resourcePacks.clear();
        minecraft.options.incompatibleResourcePacks.clear();

        for(ResourcePackInfo resourcepackinfo : resourcePackList.getSelectedPacks()) {
            if (resourcepackinfo.isFixedPosition()) continue;
            minecraft.options.resourcePacks.add(resourcepackinfo.getId());
            if (!resourcepackinfo.getCompatibility().isCompatible()) {
                minecraft.options.incompatibleResourcePacks.add(resourcepackinfo.getId());
            }
        }

        minecraft.options.save();
        List<String> list1 = ImmutableList.copyOf(minecraft.options.resourcePacks);
        if (!list1.equals(list)) minecraft.reloadResourcePacks();
    }
}
