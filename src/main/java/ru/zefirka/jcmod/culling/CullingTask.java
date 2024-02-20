package ru.zefirka.jcmod.culling;

import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import com.logisticscraft.occlusionculling.util.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import ru.zefirka.jcmod.config.EternalModConfig;
import ru.zefirka.jcmod.utils.RenderUtils;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class CullingTask implements Runnable {

    public boolean requestCull = false;
    public boolean disableEntityCulling = false;
    public boolean disableBlockEntityCulling = false;

    private final OcclusionCullingInstance culling, cullingTiles;
    private final Minecraft client = Minecraft.getInstance();
    private final int sleepDelay;
    private final EternalModConfig eternalModConfig;
    private final int hitboxLimit;


    private Vec3d lastPos = new Vec3d(0, 0, 0);
    private Vec3d aabbMin = new Vec3d(0, 0, 0);
    private Vec3d aabbMax = new Vec3d(0, 0, 0);

    public CullingTask(OcclusionCullingInstance culling, OcclusionCullingInstance cullingTiles, EternalModConfig eternalModConfig) {
        this.eternalModConfig = eternalModConfig;
        this.sleepDelay = this.eternalModConfig.sleepDelay;
        this.hitboxLimit = this.eternalModConfig.hitboxLimit;
        this.culling = culling;
        this.cullingTiles = cullingTiles;
    }

    @Override
    public void run() {
        while (client.isRunning()) {
            try {
                Thread.sleep(sleepDelay);
                if (client.level != null && client.player != null
                        && client.player.tickCount > 10) {
                    Vector3d cameraMC = eternalModConfig.debugMode
                            ? client.player.getEyePosition(client.getDeltaFrameTime())
                            : client.gameRenderer.getMainCamera().getPosition();

                    if (requestCull || !(cameraMC.x == lastPos.x && cameraMC.y == lastPos.y && cameraMC.z == lastPos.z)) {
                        long start = System.currentTimeMillis();
                        requestCull = false;
                        lastPos.set(cameraMC.x, cameraMC.y, cameraMC.z);
                        culling.resetCache();
                        cullingTiles.resetCache();
                        boolean spectator = client.player.isSpectator();
                        cullBlockEntities(cameraMC, lastPos, spectator);
                        cullEntities(cameraMC, lastPos, spectator);
                        DebugStats.lastTime = (System.currentTimeMillis() - start);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Shutting down culling task!");
    }

    private void cullEntities(final Vector3d cameraMC, final Vec3d camera, final boolean spectator) {
        if (disableEntityCulling) return;

        int tracingDistance = eternalModConfig.cullingEntitiesDistance; //make it dynamic?
        Entity entity;
        Iterator<Entity> iterable = client.level.entitiesForRendering().iterator();
        while (iterable.hasNext()) {
            try {
                entity = iterable.next();
            } catch (NullPointerException | ConcurrentModificationException ex) {
                break;
            }
            if (entity == client.player) {
                continue;
            }
            Cullable cullable = (Cullable) entity;
            if (!entity.isAlive() || ((CullableType) entity.getType()).isCullWhitelisted()) {
                continue;
            }
            if (!cullable.isForcedVisible()) {
                double distance = cameraMC.distanceTo(entity.position());
                if (spectator || entity.isGlowing() || distance < 5) {
                    cullable.setCulled(false);
                    continue;
                }
                if (distance >= tracingDistance) {
                    cullable.setCulled(true);
                    continue;
                }
                if (entity.getType() == EntityType.ARMOR_STAND) {
                    cullable.setCulled(distance >= (((ArmorStandEntity) entity).isMarker() ? 24 : 40));
                    continue;
                }
                AxisAlignedBB boundingBox = entity.getBoundingBoxForCulling();
                if (boundingBox.getXsize() > hitboxLimit || boundingBox.getYsize() > hitboxLimit
                        || boundingBox.getZsize() > hitboxLimit) {
                    cullable.setCulled(false);
                    continue;
                }
                aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
                aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
                cullable.setCulled(!visible);
            }
        }
    }

    private void cullBlockEntities(final Vector3d cameraMC, final Vec3d camera, final boolean spectator) {
        if (disableBlockEntityCulling) return;

        TileEntity entity;
        final double fov = client.options.fov * client.player.getFieldOfViewModifier() * 1.05;
        final Vector3d direction = client.player.getLookAngle();
        final boolean thirdPerson = client.options.getCameraType() == PointOfView.THIRD_PERSON_FRONT;

        Iterator<TileEntity> tileEntityIterator = client.level.blockEntityList.iterator();
        while (tileEntityIterator.hasNext()) {
            try {
                entity = tileEntityIterator.next();
            } catch (NullPointerException | ConcurrentModificationException ex) {
                ex.printStackTrace();
                break;
            }
            if (entity.isRemoved() || ((CullableType) entity.getType()).isCullWhitelisted()) {
                continue;
            }
            Cullable cullable = (Cullable) entity;
            if (!cullable.isForcedVisible() && !cullable.isCheckTimeout()) {
                if (spectator) {
                    cullable.setCulled(false);
                    continue;
                }
                BlockPos pos = entity.getBlockPos();
                double distance = RenderUtils.dist(pos, cameraMC);
                if (distance < 7) {
                    cullable.setCulled(false);
                    cullable.setOffScreen(false);
                    cullable.addForcedVisible(500);
                    continue;
                }
                if (!thirdPerson && !RenderUtils.isPlayerLookingAtEntity(direction, cameraMC, pos, fov)) {
                    cullable.setCulled(true);
                    cullable.setOffScreen(true);
                    continue;
                }
                cullable.setOffScreen(false);
                if (distance < 68) { // max tile view distance
                    AxisAlignedBB boundingBox = entity.getRenderBoundingBox();
                    if (boundingBox.getXsize() > hitboxLimit || boundingBox.getYsize() > hitboxLimit
                            || boundingBox.getZsize() > hitboxLimit) {
                        cullable.setCulled(false);
                        continue;
                    }
                    aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
                    aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                    boolean visible = cullingTiles.isAABBVisible(aabbMin, aabbMax, camera);
                    cullable.setCulled(!visible);
                } else {
                    cullable.setCulled(true);
                    cullable.addCheckTimeout(200);
                }
            }
        }
    }
}