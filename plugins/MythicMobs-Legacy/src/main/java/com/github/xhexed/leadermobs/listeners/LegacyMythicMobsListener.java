package com.github.xhexed.leadermobs.listeners;

import com.github.xhexed.leadermobs.handler.MobHandler;
import com.github.xhexed.leadermobs.utils.Utils;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static com.github.xhexed.leadermobs.LeaderMobs.getInstance;

public class LegacyMythicMobsListener implements Listener {
    private static final BukkitAPIHelper helper = MythicMobs.inst().getAPIHelper();

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(final MythicMobSpawnEvent event) {
        final FileConfiguration config = getInstance().getConfig();
        if (config.getBoolean("Blacklist.Whitelist", false) != config.getStringList("Blacklist.MythicMobs").contains(event.getMobType().getInternalName())) return;
        MobHandler.onMobSpawn(event.getEntity(), event.getMobType().getDisplayName().get());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageByEntityEvent event) {
        final FileConfiguration config = getInstance().getConfig();
        final Entity victim = Utils.getDamageSource(event.getEntity());
        final Entity damager = Utils.getDamageSource(event.getDamager());

        if (damager instanceof Player) {
            if (damager.hasMetadata("NPC") || !helper.isMythicMob(victim) ||
                    (config.getBoolean("Blacklist.Whitelist", false)
                    != config.getStringList("Blacklist.MythicMobs").contains(helper.getMythicMobInstance(victim).getType().getInternalName()))
            ) return;
            MobHandler.onPlayerDamage(damager.getUniqueId(), victim, event.getFinalDamage());
        }

        if (victim instanceof Player) {
            if (victim.hasMetadata("NPC") || !helper.isMythicMob(damager) ||
                    (config.getBoolean("Blacklist.Whitelist", false)
                    != config.getStringList("Blacklist.MythicMobs").contains(helper.getMythicMobInstance(damager).getType().getInternalName()))
            ) return;
            MobHandler.onMobDamage(damager, victim.getUniqueId(), event.getFinalDamage());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(final MythicMobDeathEvent event) {
        final MythicMob mobs = event.getMobType();
        MobHandler.onMobDeath(event.getEntity(), event.getMob().getDisplayName(), mobs.getInternalName());
    }
}
