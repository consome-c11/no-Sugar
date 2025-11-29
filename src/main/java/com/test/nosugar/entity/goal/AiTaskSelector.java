package com.test.nosugar.entity.goal;

import com.test.nosugar.entity.Valine3xg;
import com.google.common.collect.Lists;
import com.test.nosugar.entity.goal.AiTask;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AiTaskSelector {
    private final Valine3xg owner;
    private final List<AiTask> tasks = Lists.newArrayList();
    private AiTask currentTask = null;

    private Player targetPlayer = null;
    private Vec3 lastKnownPosition = null;
    int targetSearchCooldown = 0;
    private static final int TARGET_SEARCH_INTERVAL = 40; //2sec

    public AiTaskSelector(Valine3xg owner) {
        this.owner = owner;
    }

    public void addTask(int priority, AiTask task) {
        tasks.add(task);
        tasks.sort((t1, t2) -> Integer.compare(t1.getPriority(), t2.getPriority()));
    }

    public void tick() {
        if (targetSearchCooldown > 0) {
            targetSearchCooldown--;
        }

        if (currentTask != null && !currentTask.canContinue(owner, this)) {
            currentTask.stop(owner, this);
            currentTask = null;
        }

        if (currentTask == null) {
            for (AiTask task : tasks) {
                if (task.canUse(owner, this)) {
                    currentTask = task;
                    currentTask.start(owner, this);
                    break;
                }
            }
        }

        if (currentTask != null) {
            currentTask.tick(owner, this);
        }
    }

    public Player getTargetPlayer() {
        if (targetPlayer != null && !targetPlayer.isAlive()) {
            targetPlayer = null;
            lastKnownPosition = null;
        }
        return targetPlayer;
    }

    public void setTargetPlayer(Player player) {
        this.targetPlayer = player;
        if (player != null) {
            this.lastKnownPosition = player.position();
        }
    }

    public Vec3 getLastKnownPosition() {
        return lastKnownPosition;
    }

    public void findNewTarget() {
        if (targetSearchCooldown > 0) return;

        AABB searchBox = owner.getBoundingBox().inflate(64.0);
        List<Player> players = owner.level().getEntitiesOfClass(
                Player.class,
                searchBox,
                p -> p != owner && p.isAlive()
        );

        if (!players.isEmpty()) {
            players.sort((p1, p2) -> Double.compare(
                    owner.distanceToSqr(p1),
                    owner.distanceToSqr(p2)
            ));
            setTargetPlayer(players.get(0));
        } else {
            setTargetPlayer(null);
        }

        targetSearchCooldown = TARGET_SEARCH_INTERVAL;
    }

    public double distanceToSqr(Vec3 pos1, Vec3 pos2) {
        return (pos1.x - pos2.x) * (pos1.x - pos2.x) +
                (pos1.y - pos2.y) * (pos1.y - pos2.y) +
                (pos1.z - pos2.z) * (pos1.z - pos2.z);
    }
}