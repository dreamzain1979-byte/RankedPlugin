package dev.rankedsmp;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rank icon floating above each player's vanilla nametag. Scoreboard teams (the usual prefix trick) are not
 * safe on Folia, and passengers break other plugins' teleports, so this is a free-standing TextDisplay that
 * follows the player. Everything here is called from the player's own scheduler.
 */
public final class NametagManager {
    private static final class Tag {
        TextDisplay display;
        Rank rank;
    }

    private final RankedSMP plugin;
    private final Map<UUID, Tag> tags = new ConcurrentHashMap<>();

    public NametagManager(RankedSMP plugin) {
        this.plugin = plugin;
    }

    /** Runs every 2 ticks on the player's thread. */
    public void tick(Player p) {
        if (!plugin.getConfig().getBoolean("nametag.enabled", true)) {
            remove(p);
            return;
        }
        UUID id = p.getUniqueId();
        boolean hide = p.isDead() || p.isSneaking() || p.isInvisible() || p.getGameMode() == GameMode.SPECTATOR;
        if (hide) {
            remove(p);
            return;
        }
        Rank rank = plugin.ranks().rankOf(id);
        double height = plugin.getConfig().getDouble("nametag.height", 0.7);
        Location target = p.getLocation().add(0, p.getHeight() + height, 0);

        Tag tag = tags.get(id);
        if (tag != null) {
            boolean valid;
            try {
                valid = tag.display.isValid();
            } catch (Throwable t) {
                valid = false;
            }
            if (valid) {
                if (tag.rank != rank) {
                    tag.rank = rank;
                    final TextDisplay d = tag.display;
                    final Component text = rank.iconComponent();
                    d.getScheduler().run(plugin, x -> d.text(text), null);
                }
                tag.display.teleportAsync(target);
                return;
            }
            discard(tag);
        }

        float scale = (float) plugin.getConfig().getDouble("nametag.scale", 1.8);
        Tag fresh = new Tag();
        fresh.rank = rank;
        fresh.display = p.getWorld().spawn(target, TextDisplay.class, d -> {
            d.text(rank.iconComponent());
            d.setBillboard(Display.Billboard.CENTER);
            d.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
            d.setDefaultBackground(false);
            d.setShadowed(false);
            d.setPersistent(false);
            d.setTeleportDuration(3);
            d.setBrightness(new Display.Brightness(15, 15));
            d.setTransformation(new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(scale, scale, scale), new Quaternionf()));
        });
        tags.put(id, fresh);
        try {
            p.hideEntity(plugin, fresh.display); // you don't see your own tag
        } catch (Throwable ignored) {
            // optional nicety
        }
    }

    public void remove(Player p) {
        Tag tag = tags.remove(p.getUniqueId());
        if (tag != null) discard(tag);
    }

    public void removeAll() {
        for (Tag tag : tags.values()) discard(tag);
        tags.clear();
    }

    private void discard(Tag tag) {
        final TextDisplay d = tag.display;
        try {
            d.getScheduler().run(plugin, x -> d.remove(), null);
        } catch (Throwable ignored) {
            // already gone
        }
    }
}
