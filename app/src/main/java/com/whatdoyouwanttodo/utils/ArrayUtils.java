package com.whatdoyouwanttodo.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.application.Chessboard;

public class ArrayUtils {

//	public static Cell[] resize(Cell[] cells) {
//		int count = 0;
//		for (int i = 0; i < cells.length; i++) {
//			if (cells[i] != null) {
//				count++;
//			}
//		}
//		
//		return copyOf(cells, count);
//	}

//	public static Cell[] copyOf(Cell[] cells, int newLength) {
//		Cell[] newCells = new Cell[newLength];
//		int pos = 0;
//		for (int i = 0; i < cells.length; i++) {
//			if (cells[i] != null) {
//				newCells[pos] = cells[i];
//				pos++;
//			}
//		}
//		return newCells;
//	}

//	public static int[] copyOf(int[] ns, int newLength) {
//		int[] newNs = new int[newLength];
//		int pos = 0;
//		for (int i = 0; i < ns.length; i++) {
//			if (ns[i] >= 0) {
//				newNs[pos] = ns[i];
//				pos++;
//			}
//		}
//		return newNs;
//	}

	public static Cell[] sortInTableOrder(Cell[] cells) {
		Arrays.sort(cells, new Comparator<Cell>() {
			@Override
			public int compare(Cell c1, Cell c2) {
				int cmp = c1.getRow() - c2.getRow();
				if (cmp != 0)
					return cmp;
				return c1.getColumn() - c2.getColumn();
			}
		});
		return cells;
	}
	
	public static Cell[] fixSortInTableOrder(Cell[] cells) {
		if(cells.length == 0) {
			return cells;
		}
		
		LinkedList<Cell> newCells = new LinkedList<Cell>();
		newCells.add(cells[0]);
		for(int i = 1; i < cells.length; i++) {
			Cell prev = cells[i - 1];
			Cell current = cells[i];
			if(!(prev.getRow() == current.getRow() && prev.getColumn() == current.getColumn())) {
				newCells.add(current);
			}
		}
		return newCells.toArray(new Cell[newCells.size()]);
	}

	public static Cell[] sortInChessboardIdOrder(Cell[] cells) {
		Arrays.sort(cells, new Comparator<Cell>() {
			@Override
			public int compare(Cell c1, Cell c2) {
				int ret = (int) (c1.getChessboard() - c2.getChessboard());
				return ret;
			}
		});
		return cells;
	}
	
	public static Chessboard[] sortInIdOrder(Chessboard[] chessboards) {
		Arrays.sort(chessboards, new Comparator<Chessboard>() {
			@Override
			public int compare(Chessboard cb1, Chessboard cb2) {
				int ret = (int) (cb1.getId() - cb2.getId());
				return ret;
			}
		});
		return chessboards;
	}

	public static Cell[] copyOfRange(Cell[] cells, int start, int stop) {
		Cell[] newCells = new Cell[stop - start];
		for (int i = 0; i < newCells.length; i++) {
			newCells[i] = cells[start + i];
		}
		return newCells;
	}

//	public static void swap(Cell[] cells, int row1, int column1, int row2,
//			int column2) {
//		Cell cell1 = null;
//		Cell cell2 = null;
//		for (int i = 0; i < cells.length; i++) {
//			if (cells[i].getRow() == row1 && cells[i].getColumn() == column1) {
//				cell1 = cells[i];
//			} else if (cells[i].getRow() == row2
//					&& cells[i].getColumn() == column2) {
//				cell2 = cells[i];
//			}
//		}
//		if (cell1 != null) {
//			cell1.setRow(row2);
//			cell1.setColumn(column2);
//		}
//		if (cell2 != null) {
//			cell2.setRow(row1);
//			cell2.setColumn(column1);
//		}
//	}

//	public static Cell find(Cell[] cells, int row, int column) {
//		for (int i = 0; i < cells.length; i++) {
//			if (cells[i].getRow() == row && cells[i].getColumn() == column) {
//				return cells[i];
//			}
//		}
//		return null;
//	}
}
