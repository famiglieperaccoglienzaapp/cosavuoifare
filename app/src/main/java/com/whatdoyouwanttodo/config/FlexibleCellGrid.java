package com.whatdoyouwanttodo.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.whatdoyouwanttodo.application.Cell;
import com.whatdoyouwanttodo.db.ChessboardDbUtility;
import com.whatdoyouwanttodo.settings.Constants;

/**
 * Tabella di celle ridimensionabile, usata da GridConfigActivity come supporto.
 */
public class FlexibleCellGrid {
	private List<Cell> oldCells;
	private List<Cell> cells;
	private long chessboard;

	public FlexibleCellGrid(LinkedList<Cell> cells, long chessboard) {
		this.oldCells = cells;
		this.cells = new ArrayList<Cell>();
		Iterator<Cell> cellIt = cells.iterator();
		while(cellIt.hasNext())
			this.cells.add(cellIt.next().clone());
		this.chessboard = chessboard;
	}
	
	public Cell[] getConfigCells() {
		Cell[] configCells = new Cell[cells.size()];
		for (int i = 0; i < cells.size(); i++) {
			Cell configCell = cells.get(i).clone();
			configCells[i] = configCell;
		}
		return configCells;
	}

	public void swapCells(int row1, int column1, int row2, int column2) {
		Cell cell1 = null;
		Cell cell2 = null;
		for (int i = 0; i < cells.size(); i++) {
			Cell current = cells.get(i);
			if (current.getRow() == row1 && current.getColumn() == column1) {
				cell1 = current;
			} else if (current.getRow() == row2 && current.getColumn() == column2) {
				cell2 = current;
			}
		}
		if (cell1 != null) {
			cell1.setRow(row2);
			cell1.setColumn(column2);
		}
		if (cell2 != null) {
			cell2.setRow(row1);
			cell2.setColumn(column1);
		}
	}

	public void packCells(int newWidth) {
		// order cell
		Collections.sort(cells, new Comparator<Cell>() {
			@Override
			public int compare(Cell c1, Cell c2) {
				int cmp = c1.getRow() - c2.getRow();
				if (cmp != 0)
					return cmp;
				return c1.getColumn() - c2.getColumn();
			}
		});
		
		// pack cell
		int newColumn = 0;
		int newRow = 0;
		
		for (int i = 0; i < cells.size(); i++) {
			Cell current = cells.get(i);
			current.setColumn(newColumn);
			current.setRow(newRow);
			
			newColumn++;
			if(newColumn >= newWidth) {
				newColumn = 0;
				newRow++;
			}
		}
	}

	public void createCellIfNotExist(Context context, int row, int column) {
		// find selected cell
		Cell cell = null;
		for (int i = 0; i < cells.size(); i++) {
			Cell current = cells.get(i);
			if (current.getRow() == row && current.getColumn() == column) {
				cell = current;
			}
		}
		
		// if not exist create new
		if(cell == null) {
			cell = Constants.getInstance(context).NEW_CELL.clone();
			cell.setId(-1);
			cell.setRow(row);
			cell.setColumn(column);
			cells.add(cell);
		}
	}

	public boolean existCell(int row, int column) {
		// try to find cell
		for (int i = 0; i < cells.size(); i++) {
			Cell current = cells.get(i);
			if (current.getRow() == row && current.getColumn() == column) {
				return true;
			}
		}
		return false;
	}

	public Cell getCell(int row, int column) {
		for (int i = 0; i < cells.size(); i++) {
			Cell current = cells.get(i);
			if (current.getRow() == row && current.getColumn() == column) {
				return current;
			}
		}
		return null;
	}

	public boolean deleteCell(int row, int column) {
		// try to find cell
		for (int i = 0; i < cells.size(); i++) {
			Cell current = cells.get(i);
			if (current.getRow() == row && current.getColumn() == column) {
				// remove cell
				cells.remove(i);
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return cells.isEmpty();
	}

	public void writeOnDatabase(ChessboardDbUtility dbu) {
		// remove old cells
		for (int i = 0; i < oldCells.size(); i++) {
			Cell current = oldCells.get(i);

			long rowId = dbu.deleteCell(current.getId());
			if(rowId == 0) {
				return;
			}
		}

		// add new cells
		for (int i = 0; i < cells.size(); i++) {
			Cell current = cells.get(i);

			dbu.addCell(chessboard, current.getName(),
					current.getRow(), current.getColumn(),
					current.getBackgroundColor(), current.getBorderWidth(),
					current.getBorderColor(), current.getText(),
					current.getTextWidth(), current.getTextColor(),
					current.getImagePath(), current.getAudioPath(),
					current.getActivityType(), current.getActivityParam());
		}
	}

	@Override
	public String toString() {
		return "FlexibleCellGrid [oldCells=" + oldCells + ", cells=" + cells
				+ ", chessboard=" + chessboard + "]";
	}
}
