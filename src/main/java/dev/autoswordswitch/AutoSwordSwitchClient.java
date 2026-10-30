package dev.autoswordswitch;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Wechselt automatisch auf ein anderes Schwert in der Hotbar, sobald das Schwert
 * in der Hand fast kaputt ist (oder bereits zerbrochen wurde).
 */
public class AutoSwordSwitchClient implements ClientModInitializer {

	/** Bei so vielen verbleibenden Haltbarkeitspunkten (oder weniger) wird gewechselt. */
	private static final int SWITCH_AT_REMAINING = 2;

	/** Anzahl der Hotbar-Slots. */
	private static final int HOTBAR_SIZE = 9;

	/** Abstand zwischen zwei Warnungen, wenn kein Ersatzschwert da ist (in Ticks). */
	private static final int WARN_INTERVAL_TICKS = 200;

	private boolean heldWasSword = false;
	private int ticksSinceWarning = WARN_INTERVAL_TICKS;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
	}

	private void onTick(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null || client.gameMode == null) {
			heldWasSword = false;
			return;
		}

		if (ticksSinceWarning < WARN_INTERVAL_TICKS) {
			ticksSinceWarning++;
		}

		Inventory inventory = player.getInventory();
		int selected = inventory.getSelectedSlot();
		ItemStack held = inventory.getItem(selected);

		boolean almostBroken = isSword(held) && remainingDurability(held) <= SWITCH_AT_REMAINING;
		boolean justBroke = heldWasSword && held.isEmpty();

		if (almostBroken || justBroke) {
			int replacement = findReplacementSlot(inventory, selected);
			if (replacement >= 0) {
				inventory.setSelectedSlot(replacement);
				held = inventory.getItem(replacement);
			} else if (ticksSinceWarning >= WARN_INTERVAL_TICKS) {
				ticksSinceWarning = 0;
				player.displayClientMessage(
						Component.literal("Auto Sword Switch: Kein Ersatzschwert in der Hotbar!"), true);
			}
		}

		heldWasSword = isSword(held);
	}

	/** Sucht das erste andere Schwert in der Hotbar, das noch genug Haltbarkeit hat. */
	private static int findReplacementSlot(Inventory inventory, int currentSlot) {
		for (int slot = 0; slot < HOTBAR_SIZE; slot++) {
			if (slot == currentSlot) {
				continue;
			}
			ItemStack stack = inventory.getItem(slot);
			if (isSword(stack) && remainingDurability(stack) > SWITCH_AT_REMAINING) {
				return slot;
			}
		}
		return -1;
	}

	/** Schwerter werden ueber den Tag minecraft:swords erkannt (funktioniert auch fuer Mod-Schwerter). */
	private static boolean isSword(ItemStack stack) {
		return !stack.isEmpty() && stack.is(ItemTags.SWORDS);
	}

	/** Verbleibende Haltbarkeit; nicht beschaedigbare Items gelten als unendlich haltbar. */
	private static int remainingDurability(ItemStack stack) {
		if (!stack.isDamageableItem()) {
			return Integer.MAX_VALUE;
		}
		return stack.getMaxDamage() - stack.getDamageValue();
	}
}
