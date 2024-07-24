package org.plugin.ghosttotemfix;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class GhostTotemFix extends JavaPlugin implements Listener {

    private final Map<UUID, Long> totemUseMap = new HashMap<>();
    private String savedMessage;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("GhostTotemFix has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("GhostTotemFix has been disabled.");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();
        if (totemUseMap.containsKey(playerId)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.spigot().respawn();
                    player.sendMessage(savedMessage);
                }
            }.runTaskLater(this, 1);
            totemUseMap.remove(playerId);
        }
    }

    @EventHandler
    public void onEntityResurrect(EntityResurrectEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            if (isTotem(mainHandItem) || isTotem(offHandItem)) {
                totemUseMap.put(player.getUniqueId(), System.currentTimeMillis());
                getLogger().log(Level.INFO, "Player {0} used a totem.", player.getName());
            }
        }
    }

    private boolean isTotem(ItemStack item) {
        return item != null && item.getType() == Material.TOTEM_OF_UNDYING;
    }
}
