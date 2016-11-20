package com.hm.petmaster.event;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Class representing an event fired when a player changes the ownership of one of his pets.
 * 
 * @author Pyves
 *
 */
public class PlayerChangeAnimalOwnershipEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final AnimalTamer oldOwner;
	private final AnimalTamer newOwner;
	private final Tameable ownedAnimal;

	private boolean cancelled;

	/**
	 * Creates a new PlayerChangeAnimalOwnershipEvent with information about old and new owners as well as the animal
	 * involved in the transaction.
	 * 
	 * @param oldOwner
	 * @param newOwner
	 * @param ownedAnimal
	 */
	public PlayerChangeAnimalOwnershipEvent(AnimalTamer oldOwner, AnimalTamer newOwner, Tameable ownedAnimal) {

		this.oldOwner = oldOwner;
		this.newOwner = newOwner;
		this.ownedAnimal = ownedAnimal;
		this.cancelled = false;
	}

	@Override
	public HandlerList getHandlers() {

		return handlers;
	}

	public static HandlerList getHandlerList() {

		return handlers;
	}

	@Override
	public boolean isCancelled() {

		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {

		cancelled = cancel;
	}

	/**
	 * Returns the previous owner of the tameable animal.
	 * 
	 * @return the old owner
	 */
	public AnimalTamer getOldOwner() {

		return oldOwner;
	}

	/**
	 * Returns the new owner of the tameable animal.
	 * 
	 * @return the new owner
	 */
	public AnimalTamer getNewOwner() {

		return newOwner;
	}

	/**
	 * Returns the tameable animal whose ownership was changed.
	 * 
	 * @return
	 */
	public Tameable getOwnedAnimal() {

		return ownedAnimal;
	}
}
