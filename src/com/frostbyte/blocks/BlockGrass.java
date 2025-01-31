package com.frostbyte.blocks;

import com.frostbyte.display.ItemStack;
import com.frostbyte.display.Location;
import com.frostbyte.display.Material;

public class BlockGrass extends Block {
	public BlockGrass(Location location) {
		super(location, Material.GRASS, 500);
		
		setDrop(new ItemStack(Material.DIRT, 1));
	}

	@Override
	public void updateState() {
		if (getLocation().getWorld().getBlockAtLocation(new Location(getLocation().getWorld(), getLocation().getX() * 20, getLocation().getY() * 20 - 20)).getMaterial() != Material.AIR) {
			getLocation().getWorld().getBlocks()[getLocation().getX()][getLocation().getY()].setMaterial(Material.DIRT);
		}
	}
}
