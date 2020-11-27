package com.troy.test;

import com.gamers.chess.Board;
import com.gamers.chess.Color;
import com.gamers.chess.File;
import com.gamers.chess.Piece;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

	@Test
	public void emptyBoardIsEmpty() {
		byte[] board = Board.emptyBoard();
		for (int file = 0; file < Board.SIDE_LENGTH; file++) {
			for (int rank = 0; rank < Board.SIDE_LENGTH; rank++) {
				assertTrue(Board.isEmpty(file, rank, board));
				assertEquals(Board.get(file, rank, board), Piece.EMPTY);
				assertEquals(Board.getColor(file, rank, board), Color.UNCLAIMED);
			}
		}
	}

	@Test
	public void newBoardIsValid() {
		byte[] board = Board.newBoard();

		//Make sure colors match up and down the board
		for (int file = 0; file < Board.SIDE_LENGTH; file++) {
			for (int rank = 0; rank < 2; rank++) {
				assertEquals(Board.getColor(file, rank, board), Color.WHITE);
				assertTrue(Board.isWhite(file, rank, board));
			}
			for (int rank = 2; rank < 6; rank++) {
				assertEquals(Board.getColor(file, rank, board), Color.UNCLAIMED);
				assertTrue(Board.isEmpty(file, rank, board));
			}
			for (int rank = 6; rank < Board.SIDE_LENGTH; rank++) {
				assertEquals(Board.getColor(file, rank, board), Color.BLACK);
				assertTrue(Board.isBlack(file, rank, board));
			}
		}

		//Look for 8 pawns on the 2nd and 7th ranks
		for (int file = 0; file < Board.SIDE_LENGTH; file++) {
			assertEquals(Board.get(file, 1, board), Piece.PAWN);
		}

		for (int file = 0; file < Board.SIDE_LENGTH; file++) {
			assertEquals(Board.get(file, 6, board), Piece.PAWN);
		}

		int[] pieceRanks = new int[] { 0, 7 };
		for (int i = 0; i < pieceRanks.length; i++) {
			int rank = pieceRanks[i];
			assertEquals(Board.get(File.A, rank, board), Piece.ROOK);
			assertEquals(Board.get(File.B, rank, board), Piece.KNIGHT);
			assertEquals(Board.get(File.C, rank, board), Piece.BISHOP);
			assertEquals(Board.get(File.D, rank, board), Piece.QUEEN);
			assertEquals(Board.get(File.E, rank, board), Piece.KING);
			assertEquals(Board.get(File.F, rank, board), Piece.BISHOP);
			assertEquals(Board.get(File.G, rank, board), Piece.KNIGHT);
			assertEquals(Board.get(File.H, rank, board), Piece.ROOK);

		}
	}

	@Test
	public void invalidBoundsThrow() {
		final byte[] board = Board.emptyBoard();
		for (int rank = -10; rank < 20; rank++) {
			for (int file = -10; file < 20; file++) {
				final int ffile = file;
				final int rrank = rank;
				if (rank >= 0 && rank < Board.SIDE_LENGTH && file >= 0 && file < Board.SIDE_LENGTH) {
					assertTrue(Board.isEmpty(ffile, rrank, board));
				} else {
					assertThrows(IndexOutOfBoundsException.class, () -> Board.isEmpty(ffile, rrank, board));

				}

			}
		}
	}

	@Test
	public void moveQueen() {

	}

}