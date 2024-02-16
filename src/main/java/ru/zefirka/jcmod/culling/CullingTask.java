package ru.zefirka.jcmod.culling;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;

import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import com.logisticscraft.occlusionculling.util.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import ru.zefirka.jcmod.utils.RenderUtils;

public class CullingTask implements Runnable {

    public boolean requestCull = false;
    public boolean disableEntityCulling = false;
    public boolean disableBlockEntityCulling = false;

    private final OcclusionCullingInstance culling;
    private final EternalOptimizer eternalOptimizer;
    private final Minecraft client = Minecraft.getInstance();
    private final int sleepDelay;
    private final int hitboxLimit;
    private final Set<TileEntityType<?>> blockEntityWhitelist;
    private final Set<EntityType<?>> entityWhistelist;
    public long lastTime = 0;
    private int timer;

    // reused preallocated vars
    private Vec3d lastPos = new Vec3d(0, 0, 0);
    private Vec3d aabbMin = new Vec3d(0, 0, 0);
    private Vec3d aabbMax = new Vec3d(0, 0, 0);

    public CullingTask(OcclusionCullingInstance culling, Set<TileEntityType<?>> blockEntityWhitelist,
                       Set<EntityType<?>> entityWhistelist, EternalOptimizer eternalOptimizer) {
        this.eternalOptimizer = eternalOptimizer;
        this.sleepDelay = eternalOptimizer.config.sleepDelay;
        this.hitboxLimit = eternalOptimizer.config.hitboxLimit;
        this.culling = culling;
        this.blockEntityWhitelist = blockEntityWhitelist;
        this.entityWhistelist = entityWhistelist;
    }

    @Override
    public void run() {
        while (client.isRunning()) { // client.isRunning() returns false at the start?!?
            try {
                Thread.sleep(sleepDelay);
				/*timer++;
				if (timer == 50) {
					String fpsString = Minecraft.getInstance().fpsString;
					int fps = Integer.parseInt(fpsString.substring(fpsString.indexOf("/")));
					fpsManager.add(fps);
					timer = 0;
				} */ //AUTO CHANGE???
                if (EternalOptimizer.enabled && client.level != null && client.player != null
                        && client.player.tickCount > 10) {
                    Vector3d cameraMC = eternalOptimizer.config.debugMode
                            ? client.player.getEyePosition(client.getDeltaFrameTime())
                            : client.gameRenderer.getMainCamera().getPosition();

                    if (requestCull
                            || !(cameraMC.x == lastPos.x && cameraMC.y == lastPos.y && cameraMC.z == lastPos.z)) {
                        long start = System.currentTimeMillis();
                        requestCull = false;
                        lastPos.set(cameraMC.x, cameraMC.y, cameraMC.z);
                        Vec3d camera = lastPos;
                        culling.resetCache();
                        boolean spectator = client.player.isSpectator();
                        cullBlockEntities(cameraMC, camera, spectator);
                        cullEntities(cameraMC, camera, spectator);
                        lastTime = (System.currentTimeMillis() - start);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Shutting down culling task!");
    }

    private void cullEntities(Vector3d cameraMC, Vec3d camera, boolean spectator) {
        if (disableEntityCulling) {
            return;
        }
        Entity entity = null;
        Iterator<Entity> iterable = client.level.entitiesForRendering().iterator();
        while (iterable.hasNext()) {
            try {
                entity = iterable.next();
            } catch (NullPointerException | ConcurrentModificationException ex) {
                break; // We are not synced to the main thread, so NPE's/CME are allowed here and way
                // less
                // overhead probably than trying to sync stuff up for no really good reason
            }
            if (entity == null || !(entity instanceof Cullable)) {
                continue; // Not sure how this could happen outside from mixin screwing up the inject into
                // Entity
            }
            if (entity == client.player) {
                continue;
            }
            if (entityWhistelist.contains(entity.getType())) {
                continue;
            }
            if (eternalOptimizer.isDynamicWhitelisted(entity)) {
                continue;
            }
            Cullable cullable = (Cullable) entity;
            if (!cullable.isCheckTimeout()) {
                double distance = Math.sqrt(entity.distanceToSqr(cameraMC));
                if (spectator || entity.isGlowing() || distance < 5) {
                    cullable.setCulled(false);
                    continue;
                }
                if (distance >= eternalOptimizer.config.tracingDistance) {
                    cullable.setCulled(false); // If your entity view distance is larger than tracingDistance just
                    // render it
                    continue;
                }
                if (entity instanceof ArmorStandEntity) {
                    cullable.setCulled(distance >= (((ArmorStandEntity) entity).isMarker() ? 26 : 48));
                    continue;
                }
                AxisAlignedBB boundingBox = entity.getBoundingBoxForCulling();
                if (boundingBox.getXsize() > hitboxLimit || boundingBox.getYsize() > hitboxLimit
                        || boundingBox.getZsize() > hitboxLimit) {
                    cullable.setCulled(false); // To big to bother to cull
                    continue;
                }
                aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
                aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
                cullable.setCulled(!visible);
            }
        }
    }

    private void cullBlockEntities(Vector3d cameraMC, Vec3d camera, boolean spectator) {
        if (disableBlockEntityCulling) {
            return;
        }
        Vector3d position = cameraMC;
        Vector3d visionVec = client.player.getLookAngle();

        TileEntity entity = null;
        double fov = client.options.fov * client.player.getFieldOfViewModifier() * 1.05;

        Iterator<TileEntity> tileEntityIterator = client.level.blockEntityList.iterator();
        while (tileEntityIterator.hasNext()) {
            try {
                entity = tileEntityIterator.next();
            } catch (NullPointerException | ConcurrentModificationException ex) {
                break; // We are not synced to the main thread, so NPE's/CME are allowed here and way
                // less
                // overhead probably than trying to sync stuff up for no really good reason
            }
            if (blockEntityWhitelist.contains(entity.getType())) {
                continue;
            }
            if (eternalOptimizer.isDynamicWhitelisted(entity)) {
                continue;
            }
            Cullable cullable = (Cullable) entity;
            if (!cullable.isCheckTimeout()) {
                if (spectator) {
                    cullable.setCulled(false);
                    continue;
                }
                BlockPos pos = entity.getBlockPos();
                double distance = RenderUtils.distSqr(pos, cameraMC);
                if (RenderUtils.closerThan(distance, 6)) {
                    cullable.setCulled(false);
                    cullable.addCheckTimeout(500);
                    continue;
                }
                if (!RenderUtils.isPlayerLookingAtEntity(visionVec, position, pos, fov)) {
                    cullable.setCulled(true);
                    continue;
                }
                if (RenderUtils.closerThan(distance, eternalOptimizer.config.tracingTileDistance)) { // max tile view distance
                    AxisAlignedBB boundingBox = eternalOptimizer.setupAABB(entity, pos);
                    if (boundingBox.getXsize() > hitboxLimit || boundingBox.getYsize() > hitboxLimit
                            || boundingBox.getZsize() > hitboxLimit) {
                        cullable.setCulled(false); // To big to bother to cull
                        continue;
                    }
                    aabbMin.set(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
                    aabbMax.set(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
                    boolean visible = culling.isAABBVisible(aabbMin, aabbMax, camera);
                    cullable.setCulled(!visible);
                }
            }
        }
    }
}