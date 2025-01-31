package com.frostbyte.listeners;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import com.frostbyte.blocks.Block;
import com.frostbyte.display.Location;
import com.frostbyte.display.Material;
import com.frostbyte.items.types.Item;
import com.frostbyte.main.GameManager;
import com.frostbyte.player.Gamemode;
import com.frostbyte.player.Player;
import com.frostbyte.world.WorldChunk;

public class BlockListener {
	GameManager gameManager;

	public BlockListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	public void onBlockPlace(Player player, Block block, Location placeLocation) {
		Block blockAtLocation = gameManager.world.getBlockAtLocation(placeLocation);

		if (blockAtLocation.getMaterial() != Material.AIR) {
			return;
		}
		
		if(gameManager.world.getPlayer().getItemInHand() instanceof Item){
			return;
		}

		if (!player.getInventory().contains(player.getItemInHand().getMaterial())) {
			return;
		}

		Rectangle2D playerRect = new Rectangle(player.getX() + gameManager.world.getPlayerCamera().getX(), player.getY() + gameManager.world.getPlayerCamera().getY(), player.getWidth(), player.getHeight());
		Rectangle2D blockRect = new Rectangle(blockAtLocation.getLocation().getX() * 20 + gameManager.world.getPlayerCamera().getX(), blockAtLocation.getLocation().getY() * 20 + gameManager.world.getPlayerCamera().getY(), 20, 20);
		if (blockRect.intersects(playerRect)) {
			return;
		}

		blockAtLocation.setMaterial(block.getMaterial());
		blockAtLocation.setDuration(block.getMaxDuration());
		
		if (gameManager.world.getPlayer().getGamemode() == Gamemode.SURVIVAL) {
			player.getInventory().remove(block.getMaterial());
		}
		
		if(!player.getInventory().contains(block.getMaterial())){
			player.setItemInHand(null);
		}
		
		WorldChunk chunk = placeLocation.getWorld().getChunkHandler().findWorldChunk(placeLocation.getX(), placeLocation.getY());
		if(!placeLocation.getWorld().getChunkHandler().activeChunks.contains(chunk)){
			placeLocation.getWorld().getChunkHandler().activeChunks.add(chunk);
		}
	}

	public void onBlockBreak(Player player, Block block, Location dropLocation) {
		Block breakBlock = gameManager.world.getBlockAtLocation(dropLocation);

		if (gameManager.world.getPlayer().getGamemode() == Gamemode.CREATIVE) {
			breakBlock.setMaterial(Material.AIR);
			return;
		} else {
			if(breakBlock.getMaterial() == Material.BEDROCK){
				return;
			}
			
			if (gameManager.inputHandler.containsPlayer(player)) {
				gameManager.world.getBlocks()[breakBlock.getLocation().getX()][breakBlock.getLocation().getY()].setDuration(breakBlock.getDuration() - 5);

				if (breakBlock.getDuration() > 0) {
					WorldChunk chunk = dropLocation.getWorld().getChunkHandler().findWorldChunk(breakBlock.getLocation().getX(), breakBlock.getLocation().getY());
					if(!dropLocation.getWorld().getChunkHandler().activeChunks.contains(chunk)){
						dropLocation.getWorld().getChunkHandler().activeChunks.add(chunk);
					}
					return;
				}
			} else {
				return;
			}
		}

		gameManager.world.dropItem(gameManager.world.getBlockAtLocation(dropLocation).getItemDrop(), new Location(dropLocation.getWorld(), (int) (dropLocation.getX() / 20) * 20 + 5, (int) (dropLocation.getY() / 20) * 20 + 5));
		breakBlock.setMaterial(Material.AIR);
		gameManager.inputHandler.inputs.remove(gameManager.inputHandler.findPlayerInput(gameManager.world.getPlayer()));
		
		WorldChunk chunk = dropLocation.getWorld().getChunkHandler().findWorldChunk(breakBlock.getLocation().getX(), breakBlock.getLocation().getY());
		if(!dropLocation.getWorld().getChunkHandler().activeChunks.contains(chunk)){
			dropLocation.getWorld().getChunkHandler().activeChunks.add(chunk);
		}
	}
}