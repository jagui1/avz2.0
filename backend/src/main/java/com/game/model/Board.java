package com.game.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Grid of tiles. rows/columns are TODO(needs-source-value) — original game had no board.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Board {

	private int rows;
	private int columns;
	private List<Tile> tiles = new ArrayList<>();

	public Board(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		this.tiles = new ArrayList<>(rows * columns);
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < columns; x++) {
				tiles.add(new Tile(x, y, null, null));
			}
		}
	}

	public Tile tileAt(int x, int y) {
		if (x < 0 || y < 0 || x >= columns || y >= rows) {
			throw new IllegalArgumentException("Tile out of bounds: (" + x + "," + y + ")");
		}
		return tiles.get(y * columns + x);
	}
}
