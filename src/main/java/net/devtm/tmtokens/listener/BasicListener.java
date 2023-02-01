package net.devtm.tmtokens.listener;

import net.devtm.tmtokens.TMTokens;
import net.devtm.tmtokens.files.FilesManager;
import net.devtm.tmtokens.service.ServiceHandler;
import net.tmtokens.lib.base.MessageHandler;
import net.tmtokens.lib.base.VersionCheckers;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class BasicListener implements Listener {
    HashMap<UUID, Long> cooldown = new HashMap<>();
    @EventHandler
    private void playerFireworkDamage(EntityDamageByEntityEvent event) {
        if(VersionCheckers.getVersion() <= 9) return;
        if (event.getDamager() instanceof Firework) {
            Firework fw = (Firework) event.getDamager();
            if (fw.hasMetadata("nodamage")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        if(!FilesManager.ACCESS.getConfig().getConfig().getBoolean("physical_coin.enabled")) return;
        if (cooldown.containsKey(event.getPlayer().getUniqueId()))
            if (cooldown.get(event.getPlayer().getUniqueId()) <= System.currentTimeMillis() - 1000L * FilesManager.ACCESS.getConfig().getConfig().getInt("physical_coin_cooldown")) {
                cooldown.remove(event.getPlayer().getUniqueId());
            } else {
                Long newTime = FilesManager.ACCESS.getConfig().getConfig().getInt("physical_coin_cooldown") * 1000L;
                event.getPlayer().sendMessage(MessageHandler.message("physical_coins.cooldown").prefix()
                        .replace("%pl_time%", (cooldown.get(event.getPlayer().getUniqueId()) - System.currentTimeMillis() + newTime+1 )/1000 + "").toStringColor());
                return;
            }
        
        if(Objects.isNull(event.getPlayer().getInventory().getItemInHand().getItemMeta())) return;

        NamespacedKey key = new NamespacedKey(TMTokens.PLUGIN.getPlugin(), "coin");
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !event.getAction().equals(Action.RIGHT_CLICK_AIR))
            if(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
                event.setCancelled(true);
                if(FilesManager.ACCESS.getConfig().getConfig().getInt("physical_coin_cooldown") >= 0)
                    cooldown.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
                int amount = event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
                event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                ServiceHandler.SERVICE.getDataService().warpPlayer(event.getPlayer().getUniqueId()).giveTokens(amount);
                event.getPlayer().sendMessage(MessageHandler.message("physical_coins.deposited").prefix().replace("%pl_tokens%", amount + "").toStringColor());
            }
    }
}
