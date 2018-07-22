import java.util.ArrayList;
import java.util.HashMap;
/*http://www.jiuzhang.com/qa/45/ ??????
 * http://www.careercup.com/question?id=14949056
 * https://hellosmallworld123.wordpress.com/2015/10/02/%E4%BC%98%E6%AD%A5%E7%B3%BB%E7%BB%9F%E8%AE%BE%E8%AE%A1/
 * 
 * I. Data Structure
 * 
 * 1. 2d array => not knowing size
 * 2. list of list => waste space to empty cell
 * 3. Map<Itneger, MAP> => only record those written data
 * 
 * II. Dependency(ERROE: circular dependency detected!)
 * ================================================
 *  
 * update Cell(0,0)
 * Also update those Cells which depends on Cell(0,0)
 * 另外要有detection，dependency不能有cycle，这个可以用DFS来做。
 * CANNOT ALLOW DEPENDENCY
 * 
 * III. Add/Delete a row/col ?????
 * ================================================

 * For row, DIRECTLY delete that row
 * For column, ONLY delete ony by one row 0 col1, row 1 col1, row2 col1....
 * Kwwp up/right/down/left, Delete LinkedList O(1) but insert O(n)
 * if TreeMap INSERT O(logn)
 * 
 * IV. Data Type
 * ================================================

 * image, audio, video 
 * => URL or path 
 * => fetch from file system or CDN
 * 
 * V. Concurrent Edit 
 * ================================================
 * https://www.quora.com/How-is-collaborative-document-editing-implemented-in-Google-Docs
 * Google Doc Implementation Details
 * -----------------------------
 * http://www.codecommit.com/blog/java/understanding-and-applying-operational-transformation
 * https://www.quora.com/Which-data-structure-would-you-use-to-design-a-Text-editor
 * http://courses.csail.mit.edu/iap/interview/Hacking_a_Google_Interview_Practice_Questions_Person_A.pdf
 * http://www.careercup.com/question?id=16839664
 * https://www.youtube.com/watch?v=U6jOYazP2PI
 * 
 * VI. Operation Transformation
 * 
 * The basic idea of OT can be illustrated using a simple text editing
 * scenarios as follows.
 * Given a text document with a string "abc" replicated at two collaborating sites; 
 * and two concurrent operations:
 * O1 = Insert[0,"x"](to insert character "x" at position "0")
 * O2 = Delete[2,"c"](to delete character "c" at position "2")
 * generated by two users at collaboration sites 1 and 2, respeactively.
 * 
 * Suppose teh two operatons are executed 
 * in the order of O1 and O2(at site1)
 * 
 * After executing O1, the document becomes "xabc".
 * To execute O2 after O1, O2 must be transformed against O1 
 * to become O2' = Delete[3,"c"], whose posisiotnal parameter
 * is incremented by one due too the insertion of "x" by O1
 * 
 * Executing O2' on "xabc" shall delete the correct chracter
 * 'c' and the document becomes "xab". However, if O2 is executed without
 * transformation, then it shall incorretly delete character
 * 'b' rather than 'c'
 * 
 * The basic idea of OT is to transform (or adjust) the parameters
 * of an editing operation according to the effects of previously
 * executed concurrent operations so that the transformed operation
 * can achieve the correct effect and maintain document consistenly.
 * 
 * 
 * VII. Mutex and Semaphore
 * ????????
 * cell
 * mutex.aquire()
 * mutex.release()
 * Mutex0和1的semaphore, 同一个thread
 * semaphore,几个thread的执行
 * 
 * 如果这个document还放到网上去共享的话，
 * 那当user1在修改某个cell的时候，需要给cell加一个锁，
 * 可以用mutex来实现，具体就是在edit的时候要调用mutex.aquire(),
 * 操作完以后要调用mutex.release();
 * 这样可以保证不会有同时的两个人去access同样的data，
 * 当然mutex是multithread用的，而不是网上共享用的。但是同样的道理，
 * 我们可以用一个atomic的bit给每个cell来加锁和解锁
 * （比如说这个bit可以放到redis里面的某个key，因为redis是single threaded，所以可以保证所有操作都是atomic的。

   Mutex和semaphore的区别，mutex相当于是只有0和1的semaphore，
   但是要注意一点mutex的加锁和解锁是只能由同一个thread进行的。
   而semaphore的加和减是可以由不同thread执行，
   所以mutex用于加锁而semaphore用于协调几个thread的执行。
   
   
   
 * */
/*

public void SetValue(Cell cell) {
    int row = cell.getRow();
    int col = cell.getCol();

    HashMap<Integer, Cell> colsMap = cellsMap.get(row);
    if (colsMap == null) {
        colsMap = new HashMap<Integer, Cell>();
        cellsMap.put(row, colsMap);
    }
    colsMap.put(col, cell);
    this.countCols = Math.max(this.countCols, col + 1);
    this.countRows = Math.max(this.countRows, row + 1);
    breakDependencyOnParents(row, col);
    updateChildren(cell);
}

public void breakDependencyOnParents(int row, int col) {
    Cell cell = cellsMap.get(row).get(col);
    if (cell.parent != null) {
        for (Cell cellParent : cell.parent) {
            cellParent.children.remove(cell);
        }

        for (int i = cell.parent.size() - 1; i >= 0; i--) {
            cell.parent.remove(i);
        }
    }
}

public void updateChildren(Cell cell) {
    if (cell.children == null) {
        return;
    }
    for (Cell cellChild : cell.children) {
        int row = cell.getRow();
        int col = cell.getCol();
        for (Cell cellChildParent : cellChild.parent) {
            if (cellChildParent.getRow() == row && cellChildParent.getCol() == col) {
                cellChildParent.setValue(cell.getValue());
            }
        }
        updateChildren(cellChild);
    }

}

public int getCountCols() {
    return countCols;
}

public void setCountCols(int countCols) {
    this.countCols = countCols;
}

public int getCountRows() {
    return countRows;
}

public void setCountRows(int countRows) {
    this.countRows = countRows;
}

@Override
public String toString() {
    StringBuilder sb = new StringBuilder("Table:\n");

    for (int i = 0; i < this.countCols; i++) {
        sb.append("Column: ").append(i).append(":\t");
        for (int j = 0; j < this.countRows; j++) {
            sb.append(getValue(i, j));
            if (j == this.countRows - 1) {
                sb.append("\n");
            } else {
                sb.append(", ");
            }
        }
    }

    return sb.toString();
}
 * */
public class GoogleSheetdesignExcel {
	private static int final int MAX_CELL_INDEX = 65000;
	private final HashMap<Integer, HashMap<Integer, Cell>> sheet = new HashMap<>();
	
	
	 
	/*R get Method
	 * 
	 * */
	public Cell get(int row, int col){
		checkRowAndColumnIndex(row,col);
		HashMap<Integer, Cell> colMap = sheet.get(row);
		if (colMap == null){
			colMap.get(col);
		}
		return null;
	}
	/*U set Method
	 * 
	 * */
	public void set(int row, int col, String value){
		checkRowAndColumnIndex(row,col);
		HashMap<Integer, Cell> colMap = sheet.get(row);
		if (colMap == null){
			colMap = new HashMap<Integer, Cell>();
			sheet.put(col, colMap);
		}
		Cell now = new Cell(value);
		colMap.put(col, Cell);
	}

	/*C add Method
	 * 
	 * */
	public void add(int row, int col, int value){
		
	}
	/*D delete Method
	 * 
	 * */
	public void delete(int row, int col){
		
	}
}




class Cell{
	//Value value;
	String value;
	Cell up;
	Cell right;
	Cell down;
	Cell left;
	// what cells you depend on
	// e.g., cell(1,3)=cell(0,0)+cell(0,1)+cell(0,2)
	// then cell(1,3)'s parents will be cell(0,0), cell(0,1), cell(0,2)
	ArrayList<Cell> parents;
	// what cells you contribute to
	// e.g., cell(1,3)=cell(0,0)+cell(0,1)+cell(0,2)
	// then cell(0,0)'s children will be cell(1,3)
	ArrayList<Cell> children;
	// 每次改变cell的值就要对children和parents做相应的改变；
	Cell(String value){
		this.value = value;
		up = null;
		right = null;
		down = null;
		left = null;
		parents = new ArrayList<Cell>();
		children = new ArrayList<Cell>();
	}
	
	
	
	
}
class Value{
	boolean isNumber;
	boolean isString;
	boolean isImage;
	String val;
}