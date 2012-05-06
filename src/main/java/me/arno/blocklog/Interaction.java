package me.arno.blocklog;

import org.bukkit.Material;

public enum Interaction {
	DOOR(0),
	TRAP_DOOR(1),
	CHEST(2),
	DISPENSER(3),
	BUTTON(4),
	LEVER(5);
	
	int id;
	Interaction(int id) {
		this.id = id;
	}
	
	public int getTypeId() {
		return this.id;
	}
	
	public Interaction getType() {
		return this;
	}
	
	public Material getMaterial() {
		if(this == Interaction.DOOR) {
			return Material.WOODEN_DOOR;
		} else if(this == Interaction.TRAP_DOOR) {
			return Material.TRAP_DOOR;
		} else if(this == Interaction.CHEST) {
			return Material.CHEST;
		} else if(this == Interaction.DISPENSER) {
			return Material.DISPENSER;
		} else if(this == Interaction.BUTTON) {
			return Material.STONE_BUTTON;
		} else if(this == Interaction.LEVER) {
			return Material.LEVER;
		}
		return null;
	}
	
	public static Interaction getByMaterial(Material material) {
		if(material == Material.WOODEN_DOOR) {
			return Interaction.DOOR;
		} else if(material == Material.TRAP_DOOR) {
			return Interaction.TRAP_DOOR;
		} else if(material == Material.CHEST) {
			return Interaction.CHEST;
		} else if(material == Material.DISPENSER) {
			return Interaction.DISPENSER;
		} else if(material == Material.STONE_BUTTON) {
			return Interaction.BUTTON;
		} else if(material == Material.LEVER) {
			return Interaction.LEVER;
		}
		return null;
	}
}
