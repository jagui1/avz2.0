package com.game.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DomainModelTest {

	@Test
	void boardCreatesGridOfEmptyTiles() {
		Board board = new Board(2, 3);

		assertEquals(2, board.getRows());
		assertEquals(3, board.getColumns());
		assertEquals(6, board.getTiles().size());

		Tile tile = board.tileAt(1, 0);
		assertEquals(1, tile.getX());
		assertEquals(0, tile.getY());
		assertNull(tile.getAnt());
		assertNull(tile.getZombie());
	}

	@Test
	void tileAtRejectsOutOfBounds() {
		Board board = new Board(1, 1);
		assertThrows(IllegalArgumentException.class, () -> board.tileAt(1, 0));
	}

	@Test
	void gameHoldsStatusWaveResourcesAndBoard() {
		Board board = new Board(5, 10);
		Game game = new Game(1L, GameStatus.RUNNING, 1, 100, 3, board);

		assertEquals(GameStatus.RUNNING, game.getStatus());
		assertEquals(1, game.getWave());
		assertEquals(100, game.getResources());
		assertEquals(3, game.getLife());
		assertEquals(50, game.getBoard().getTiles().size());
	}

	@Test
	void antAndZombieHoldCombatStats() {
		// Carpenter baseline from RULES.md: cost 10, HP 10, dmg 10
		Ant ant = new Ant(10, 10, 1, "CARPENTER");
		assertEquals(10, ant.getHealth());
		assertEquals(10, ant.getDamage());
		assertEquals("CARPENTER", ant.getType());

		// Standard zombie from RULES.md: HP 10
		Zombie zombie = new Zombie(10, 1, 0, "Z");
		assertEquals(10, zombie.getHealth());
		assertEquals("Z", zombie.getType());
	}

	@Test
	void tileCanHoldAntAndZombie() {
		Ant ant = new Ant(10, 10, 1, "CARPENTER");
		Zombie zombie = new Zombie(10, 1, 9, "Z");
		Tile tile = new Tile(3, 2, ant, zombie);

		assertEquals(ant, tile.getAnt());
		assertEquals(zombie, tile.getZombie());
	}
}
