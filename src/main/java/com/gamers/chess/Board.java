package com.gamers.chess;

public class Board {

	public static final int SIDE_LENGTH = 8;

	//Use 4 bits per piece. The upper one is for the color and the bottom three hold a constant from Piece.java
	private static final int PIECE_MASK = 0b0111;
	private static final int COLOR_MASK = 0b1000;
	private static final int WHITE_BIT = COLOR_MASK;

	private static final int KING_INDEX = SIDE_LENGTH * SIDE_LENGTH / 2;//Place the kings index at the end. The board is 2 squares per byte * 32 squares

	private static final int BOARD_BYTES = KING_INDEX + 1;


	public static byte[] emptyBoard() {
		return new byte[BOARD_BYTES];
	}

	public static byte[] newBoard() {
		byte[] board = Board.emptyBoard();
		set(Piece.PAWN, 	Color.WHITE, File.A, 1, board);
		set(Piece.PAWN, 	Color.WHITE, File.B, 1, board);
		set(Piece.PAWN, 	Color.WHITE, File.C, 1, board);
		set(Piece.PAWN, 	Color.WHITE, File.D, 1, board);
		set(Piece.PAWN, 	Color.WHITE, File.E, 1, board);
		set(Piece.PAWN, 	Color.WHITE, File.F, 1, board);
		set(Piece.PAWN, 	Color.WHITE, File.G, 1, board);
		set(Piece.PAWN, 	Color.WHITE, File.H, 1, board);

		set(Piece.ROOK, 	Color.WHITE, File.A, 0, board);
		set(Piece.KNIGHT, 	Color.WHITE, File.B, 0, board);
		set(Piece.BISHOP, 	Color.WHITE, File.C, 0, board);
		set(Piece.QUEEN, 	Color.WHITE, File.D, 0, board);
		set(Piece.KING, 	Color.WHITE, File.E, 0, board);
		set(Piece.BISHOP, 	Color.WHITE, File.F, 0, board);
		set(Piece.KNIGHT, 	Color.WHITE, File.G, 0, board);
		set(Piece.ROOK, 	Color.WHITE, File.H, 0, board);


		set(Piece.PAWN, 	Color.BLACK, File.A, 6, board);
		set(Piece.PAWN, 	Color.BLACK, File.B, 6, board);
		set(Piece.PAWN, 	Color.BLACK, File.C, 6, board);
		set(Piece.PAWN, 	Color.BLACK, File.D, 6, board);
		set(Piece.PAWN, 	Color.BLACK, File.E, 6, board);
		set(Piece.PAWN, 	Color.BLACK, File.F, 6, board);
		set(Piece.PAWN, 	Color.BLACK, File.G, 6, board);
		set(Piece.PAWN, 	Color.BLACK, File.H, 6, board);

		set(Piece.ROOK, 	Color.BLACK, File.A, 7, board);
		set(Piece.KNIGHT, 	Color.BLACK, File.B, 7, board);
		set(Piece.BISHOP, 	Color.BLACK, File.C, 7, board);
		set(Piece.QUEEN, 	Color.BLACK, File.D, 7, board);
		set(Piece.KING, 	Color.BLACK, File.E, 7, board);
		set(Piece.BISHOP, 	Color.BLACK, File.F, 7, board);
		set(Piece.KNIGHT, 	Color.BLACK, File.G, 7, board);
		set(Piece.ROOK, 	Color.BLACK, File.H, 7, board);

		return board;
	}

	/**
	 * Tries to move a piece from the source square to the destination square. Throwing an exception if the bounds are invalid and returning false if the
	 * source square is empty.
	 * @param srcFile The source file to move the piece from
	 * @param srcRank The source rank to move the piece from
	 * @param destFile The destination file to move the piece from
	 * @param destRank The destination rank to move the piece from
	 * @param board The board to move the pieces on
	 * @return True if the move is initially valid. IE. The source square and dest squares are different, and the source square contains a piece
	 * If this function returns true that does not mean the move is entirely valid. One must call @isPositionLegal to check for checks. When true is returned
	 * that simply means that the requested piece has moved on the board and now the user must follow up to check if the move is legal
	 */
	public static boolean IsMoveLegal(int srcFile, int srcRank, int destFile, int destRank, byte[] board) {
		if (srcFile < 0 || srcFile >= SIDE_LENGTH) throw new IndexOutOfBoundsException("Source file out of range: " + srcFile);
		if (srcRank < 0 || srcRank >= SIDE_LENGTH) throw new IndexOutOfBoundsException("Source rank out of range: " + srcRank);
		if (destFile < 0 || destFile >= SIDE_LENGTH) throw new IndexOutOfBoundsException("Destination file out of range: " + srcFile);
		if (destRank < 0 || destRank >= SIDE_LENGTH) throw new IndexOutOfBoundsException("Destination rank out of range: " + srcRank);

		if (srcFile == destFile && srcRank == destRank) {
			return false;
		}

		if (isEmpty(srcFile, srcRank, board)) {
			return false;
		}
		return isMoveLegalUnchecked(srcFile, srcRank, destFile, destRank, board);
	}

	public static boolean isMoveLegalUnchecked(int srcFile, int srcRank, int destFile, int destRank, byte[] board) {
		return true;
	}

	public static void move(int srcFile, int srcRank, int destFile, int destRank, byte[] board) {
		setRawUnchecked(get(srcFile, srcRank, board), getColor(srcFile, srcRank, board), destFile, destRank, board);
		setRawUnchecked(Piece.EMPTY, Color.UNCLAIMED, srcFile, srcRank, board);
	}


	public interface PieceIterator {
		/**
		 * A callback used for iterating through all pieces on the board. Called with info about
		 * each piece until there are no more pieces left on the board or the user requests to stop iteration
		 *
		 * @param rank The rank of the piece
		 * @param file The file of the piece
		 * @param piece The ID of the piece from @Pieces
		 * @param color The color of the piece
		 * @return True if the user wants iteration to stop. False otherwise
		 */
		boolean next(int rank,int file, byte piece, Color color);
	}

	private static void allPieces(byte[] board, PieceIterator it) {
		for (int file = 0; file < SIDE_LENGTH; file++) {
			for (int rank = 0; rank < SIDE_LENGTH; rank++) {
				if (!isEmpty(file, rank, board)) {
					if (it.next(rank, file, get(rank, file, board), getColor(rank, file, board)))
						return;
				}
			}
		}
	}


	//Index the board in order starting from A1, B1, C1... A2, B2... for the entire board
	private static void setRawUnchecked(byte piece, Color color, int file, int rank, byte[] board) {
		int boardPos = file + rank * SIDE_LENGTH;
		int index = boardPos / 2;
		int finalPiece = piece | (color == Color.WHITE ? WHITE_BIT : 0);
		if (boardPos % 2 == 1) {
			board[index] = (byte) ((board[index] & 0b00001111) | finalPiece << 4);
		} else {
			board[index] = (byte) ((board[index] & 0b11110000) | finalPiece);
		}
	}

	private static byte getRawUnchecked(int file, int rank, byte[] board) {
		int boardPos = file + rank * SIDE_LENGTH;
		int index = boardPos / 2;
		byte value = board[index];
		//Index using the high bits first
		if (boardPos % 2 == 1) {
			return (byte) ((value >>> 4) & 0b1111);
		} else {
			return (byte) (value & 0b1111);
		}
	}


	private static void set(byte piece, Color color, int file, int rank, byte[] board) {
		if (piece > Piece.MAX_VALUE) throw new IllegalArgumentException("Piece: " + piece + " does not exist! Out of range!");
		if (color == Color.UNCLAIMED && piece != Piece.EMPTY) throw new IllegalArgumentException("Cannot set piece of an unknown color!");
		if (file < 0 || file >= SIDE_LENGTH) throw new IndexOutOfBoundsException("File out of range: " + file);
		if (rank < 0 || rank >= SIDE_LENGTH) throw new IndexOutOfBoundsException("Rank out of range: " + rank);

		setRawUnchecked(piece, color, file, rank, board);
	}

	private static byte getRaw(int file, int rank, byte[] board) {
		if (file < 0 || file >= SIDE_LENGTH) throw new IndexOutOfBoundsException("File out of range: " + file);
		if (rank < 0 || rank >= SIDE_LENGTH) throw new IndexOutOfBoundsException("Rank out of range: " + rank);
		return getRawUnchecked(file, rank, board);
	}

	public static byte get(int file, int rank, byte[] board) {
		byte piece = getRaw(file, rank, board);
		return (byte) (piece & PIECE_MASK);
	}

	public static boolean isEmpty(int file, int rank, byte[] board) {
		byte piece = get(file, rank, board);
		return piece == Piece.EMPTY;
	}

	public static Color getColor(int file, int rank, byte[] board) {
		byte piece = getRaw(file, rank, board);
		if ((piece & PIECE_MASK) == Piece.EMPTY) return Color.UNCLAIMED;
		return ((piece & COLOR_MASK) == WHITE_BIT) ? Color.WHITE : Color.BLACK;
	}

	public static boolean isBlack(int file, int rank, byte[] board) {
		return getColor(file, rank, board) == Color.BLACK;
	}

	public static boolean isWhite(int file, int rank, byte[] board) {
		return getColor(file, rank, board) == Color.WHITE;
	}

}
