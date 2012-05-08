package me.arno.blocklog.logs;

import org.bukkit.Material;

public enum InteractionType {
	DOOR(0),
	TRAP_DOOR(1),
	CHEST(2),
	DISPENSER(3),
	BUTTON(4),
	LEVER(5);
	
	int id;
	InteractionType(int id) {
		this.id = id;
	}
	
	public int getTypeId() {
		return this.id;
	}
	
	public InteractionType getType() {
		return this;
	}
	
	public Material getMaterial() {
		if(this == InteractionType.DOOR) {
			return Material.WOODEN_DOOR;
		} else if(this == InteractionType.TRAP_DOOR) {
			return Material.TRAP_DOOR;
		} else if(this == InteractionType.CHEST) {
			return Material.CHEST;
		} else if(this == InteractionType.DISPENSER) {
			return Material.DISPENSER;
		} else if(this == InteractionType.BUTTON) {
			return Material.STONE_BUTTON;
		} else if(this == InteractionType.LEVER) {
			return Material.LEVER;
		}
		return null;
	}
	
	public static InteractionType getByMaterial(Material material) {
		if(material == Material.WOODEN_DOOR) {
			return InteractionType.DOOR;
		} else if(material == Material.TRAP_DOOR) {
			return InteractionType.TRAP_DOOR;
		} else if(material == Material.CHEST) {
			return InteractionType.CHEST;
		} else if(material == Material.DISPENSER) {
			return InteractionType.DISPENSER;
		} else if(material == Material.STONE_BUTTON) {
			return InteractionType.BUTTON;
		} else if(material == Material.LEVER) {
			return InteractionType.LEVER;
		}
		return null;
	}
}
