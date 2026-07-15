package com.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ant unit on the board.
 * attackSpeed is TODO(needs-source-value) — original game was turn-based FIGHT! exchanges.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ant {

	private int health;
	private int damage;
	/** Attacks per second-ish for tick engine; not in original Java source. */
	private int attackSpeed;
	private String type;
}
