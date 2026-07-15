package com.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Zombie unit.
 * speed is TODO(needs-source-value) — original game had no movement.
 * position is for lane/board progress in the TD redesign.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zombie {

	private int health;
	private int speed;
	private int position;
	private String type;
}
