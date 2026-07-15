package com.game.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authoritative game session state (TECH Stage 2 + PRD life for FR-7).
 * life is TODO(needs-source-value) relative to original Java (no player life there).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

	private Long id;
	private GameStatus status;
	private int wave;
	private int resources;
	/** Player lives for end-of-board leaks; not in original queue combat. */
	private int life;
	private Board board;
}
