package dev.autobuilder;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoBuilder {
    public static boolean enabled = false;
    public static int delay = 2;
    public static int perTick = 1;
    public static double reach = 4.0;

    private static int cooldown = 0;

    public static void tick(MinecraftClient mc) {
        if (!enabled || mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (cooldown-- > 0) return;
        cooldown = delay;

        WorldSchematic schematic = SchematicWorldHandler.getSchematicWorld();
        SchematicPlacement placement = DataManager.getSchematicPlacementManager().getSelectedSchematicPlacement();
        if (schematic == null || placement == null) return;

        var bb = placement.getEclosingBox();
        BlockPos pp = mc.player.getBlockPos();
        int r = (int) Math.ceil(reach);

        int minX = Math.max(bb.minX, pp.getX() - r), maxX = Math.min(bb.maxX, pp.getX() + r);
        int minY = Math.max(bb.minY, pp.getY() - r), maxY = Math.min(bb.maxY, pp.getY() + r);
        int minZ = Math.max(bb.minZ, pp.getZ() - r), maxZ = Math.min(bb.maxZ, pp.getZ() + r);

        int placed = 0;
        Vec3d eye = mc.player.getEyePos();

        // bottom-to-top so blocks always have something to attach to
        for (int y = minY; y <= maxY && placed < perTick; y++) {
            for (int x = minX; x <= maxX && placed < perTick; x++) {
                for (int z = minZ; z <= maxZ && placed < perTick; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (eye.squaredDistanceTo(Vec3d.ofCenter(pos)) > reach * reach) continue;

                    BlockState want = schematic.getBlockState(pos);
                    if (want.isAir()) continue;

                    BlockState have = mc.world.getBlockState(pos);
                    if (!have.isAir() && !have.isReplaceable()) continue;
                    if (have.getBlock() == want.getBlock()) continue;
                    if (!mc.world.canPlace(want, pos, ShapeContext.absent())) continue;

                    int slot = findHotbarSlot(mc, want);
                    if (slot < 0) continue;

                    for (Direction d : Direction.values()) {
                        BlockPos neighbor = pos.offset(d);
                        BlockState ns = mc.world.getBlockState(neighbor);
                        if (ns.isAir() || ns.isReplaceable()) continue;

                        Direction face = d.getOpposite();
                        Vec3d hitVec = Vec3d.ofCenter(neighbor).add(Vec3d.of(face.getVector()).multiply(0.5));
                        BlockHitResult hit = new BlockHitResult(hitVec, face, neighbor, false);

                        mc.player.getInventory().setSelectedSlot(slot);
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        placed++;
                        break;
                    }
                }
            }
        }
    }

    private static int findHotbarSlot(MinecraftClient mc, BlockState state) {
        for (int i = 0; i < 9; i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (!s.isEmpty() && s.getItem() == state.getBlock().asItem()) return i;
        }
        return -1;
    }
}
