package com.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One cell on the board. May hold at most one ant and one zombie (TECH Stage 2).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tile {

	private int x;
	private int y;
	private Ant ant;
	private Zombie zombie;
}
