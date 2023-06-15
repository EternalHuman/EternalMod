package ru.zefirka.jcmod.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;

import java.util.ArrayList;
import java.util.List;

public class ResourcePack {
    public static final String MAIN_PACK_NAME = "file/JediPack Eternal.zip";

    public static void updateRP(Minecraft minecraft) {
        ResourcePackList resourcePackList = minecraft.getResourcePackRepository();
        System.out.println("SIZE: " + resourcePackList.getSelectedPacks().size());
        boolean containsMainPack = false, custom = false;
        for (ResourcePackInfo resourcePackInfo : resourcePackList.getSelectedPacks()) {
            System.out.println("- " + resourcePackInfo.getId());
            if (resourcePackInfo.getId().toLowerCase().startsWith("custom")) {
                custom = true;
                break;
            }
            if (resourcePackInfo.getId().equals(MAIN_PACK_NAME)) {
                containsMainPack = true;
                break;
            }
        }
        if (custom) return;

        if (!containsMainPack) {
            ResourcePackInfo jediPack = null;
            for (ResourcePackInfo resourcePackInfo : resourcePackList.getAvailablePacks()) {
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
    }

    private static void updatePackList(Minecraft minecraft, ResourcePackList resourcePackList) {
        List<String> list = ImmutableList.copyOf(minecraft.options.resourcePacks);
        minecraft.options.resourcePacks.clear();
        minecraft.options.incompatibleResourcePacks.clear();

        for(ResourcePackInfo resourcepackinfo : resourcePackList.getSelectedPacks()) {
            if (!resourcepackinfo.isFixedPosition()) {
                minecraft.options.resourcePacks.add(resourcepackinfo.getId());
                if (!resourcepackinfo.getCompatibility().isCompatible()) {
                    minecraft.options.incompatibleResourcePacks.add(resourcepackinfo.getId());
                }
            }
        }

        minecraft.options.save();
        List<String> list1 = ImmutableList.copyOf(minecraft.options.resourcePacks);
        if (!list1.equals(list)) {
            minecraft.reloadResourcePacks();
        }
    }
}
