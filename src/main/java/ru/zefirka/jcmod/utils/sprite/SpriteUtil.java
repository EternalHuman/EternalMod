package ru.zefirka.jcmod.utils.sprite;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SpriteUtil {
    public static void markSpriteActive(TextureAtlasSprite sprite) {
        if (sprite instanceof SpriteExtended) {
            ((SpriteExtended) sprite).markActive();
        }
    }
}
