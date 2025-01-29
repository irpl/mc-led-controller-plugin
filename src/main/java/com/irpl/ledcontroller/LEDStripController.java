package com.irpl.ledcontroller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.typesafe.config.Config;

public class LEDStripController extends JavaPlugin implements Listener {
    private final Set<UUID> listeningPlayers = new HashSet<>();
    private String previousColorName = "BLACK";
    private Config config;

    @Override
    public void onEnable() {
        ConfigLoader configLoader = new ConfigLoader(getDataFolder(), getLogger());
        config = configLoader.getConfig();

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            UUID playerId = player.getUniqueId();

            if (command.getName().equalsIgnoreCase("startlisten")) {
                listeningPlayers.add(playerId);
                player.sendMessage("Started listening to your actions.");
                return true;
            }

            if (command.getName().equalsIgnoreCase("stoplisten")) {
                listeningPlayers.remove(playerId);
                player.sendMessage("Stopped listening to your actions.");
                return true;
            }
        }
        return false;
    }

    private void fetchData(Player sender, String colorName) {
        new Thread(() -> {
            try {
                String apiEndpint = config.getString("app.api.endpoint");
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiEndpint))
                    .POST(HttpRequest.BodyPublishers.ofString("{\"color_name\":\""+ colorName +"\"}"))
                    .build();
    
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> sender.sendMessage("Response: " + response.body()))
                    .exceptionally(e -> {
                        sender.sendMessage("Failed to fetch data: " + e.getMessage());
                        return null;
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!listeningPlayers.contains(player.getUniqueId())) {
            return;
        }

        Block block = player.getLocation().subtract(0, 1, 0).getBlock();
        
        String materialTypeName = block.getBlockData().getMaterial().toString();
        
        if (!materialTypeName.endsWith("WOOL")) { 
            return;
        }

        String currentColorName = materialTypeName.substring(0, materialTypeName.length() - 5);;

        if (!currentColorName.equals(previousColorName) ) {
            player.sendMessage("Current color: " + currentColorName);
            previousColorName = currentColorName;
            fetchData(player, currentColorName);
        }
            
    }
}