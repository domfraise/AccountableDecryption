package Database;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;


public class Tree {

    /**
     * Tree class defines a recursive type called Tree, and provides
     * constructor and accessor methods.
     */

    private boolean empty;
    private String value;
    private  Tree left, right;
    private int height;
    private int numNodes;
    private int numLeaves;
   
		/**
		 * Creates a new Tree whose root value is x and left and right
		 * subtrees are l and r.
		 */
    public Tree(String value, Tree left, Tree right) {
				this.empty = false; 
				this.value = value; 
				this.left = left; 
				this.right = right;
				this.height = 1 + Math.max(left.height, right.height);
				this.numNodes = 1+ left.numNodes+right.numNodes;
				if(left.isEmpty() && right.isEmpty()){
					this.numLeaves = 1;
				}
				else{
					this.numLeaves = this.left.numLeaves+this.right.numLeaves;
				}
    }
    
    /**
     * Creates an empty tree
     */
    public Tree() {
        this.empty = true; 
				this.value = null; this.left = null; this.right = null;
				this.height = 0;this.numNodes = 0;this.numLeaves = 0;
    }
    
    /**
     * Creates a tree with a single leaf node
     */

    public Tree(String x) {
        this.empty = false; 
				this.value = x; this.left = new Tree(); this.right = new Tree();
				this.height = 1;this.numNodes = 1;this.numLeaves = 1;
    }
    
    public int getPowOf2(){
    	if (this.isEmpty()){
    		return 0;
    	}
    	if(this.left.isEmpty() && this.getRight().isEmpty()){
    		return 1;
    	}
    	int biggestSoFar = 2;
    	for(int i = 2;i<=numLeaves;i=i*2){
    		biggestSoFar = i;
    	}
    	return biggestSoFar;
    	
    }
    
   public int getNumLeaves(){
	   return this.numLeaves;
   }
    
    
	    
    /**
     * returns true if this tree is empty
     */
    public boolean isEmpty() {
				return empty;
    }

    /**
     * returns the height of the tree
     */
    public int getHeight() {
				return height;
    }
	    
    /**
     * gets the root value of this tree
     */
    public String getValue() {
				if (empty) {
						throw new IllegalStateException(
																						"Trying to access root of an empty tree");
				}
				return value;
    }
	    
    /**
     * gets the left subtree of this node
     */
    public Tree getLeft() {
				if (empty) {
						throw new IllegalStateException(
																						"Trying to access subtree of an empty tree");
				}
				return left;
    }
	    
    /**
     * gets the right subtree of this node
     */
    public Tree getRight() {
				if (empty) {
						throw new IllegalStateException(
																						"Trying to access subtree of an empty tree");
				}
				return right;
    }
    
    public int getNumNodes(){
    	return numNodes;
    }

    /**
     * Creates a multi-line String that represents this Tree. The
     * format looks like this  
		 <code>
		 10
		 |
		 |- 14
		 |   |
		 |   |- 17
		 |   |
		 |   |- 13
		 |       |
		 |       | - [nil]
		 |       |
		 |       |- 12
		 |
		 |- 6
		 </code>
     * Where the bottom child is the left sub tree and the top child
     * is the right sub tree.  If both children are nil or the empty
     * tree then they will not be printed. If only one child is nil
     * then both children are printed to so it can be known which was
     * the right child and which was the left child.
     * @param tree The tree, which may not be null
     * @return A string containing the formatted tree
     */
    @Override public String toString() {
        return format(this);
    }

    /**
     * source (with modifications) http://www.connorgarvey.com/blog/?p=82
     * Print a formatted representation of the given tree. The format
     * looks like this
		 <code>
		 10
		 |
		 |- 14
		 |   |
		 |   |- 17
		 |   |
		 |   |- 13
		 |       |
		 |       | - [nil]
		 |       |
		 |       |- 12
		 |
		 |- 6
		 </code>
     * Where the bottom child is the left sub tree and the top child
     * is the right sub tree.  If both children are nil or the empty
     * tree then they will not be printed. If only one child is nil
     * then both children are printed to so it can be known which was
     * the right child and which was the left child.
     * @param tree The tree, which may not be null
     * @return A string containing the formatted tree
     */
    public static void print(Tree tree) {
				System.out.print(format(tree));
    }

    public static String format(Tree tree) {
        final StringBuilder buffer = new StringBuilder();
        return formatTreeHelper(tree, buffer, 
																new LinkedList<Iterator<Tree>>()).toString();
    }
 
    private static String formatTreeDrawLines
				(java.util.List<Iterator<Tree>> parentIterators, 
				 boolean amLast) 
		{
        StringBuilder result = new StringBuilder();
        Iterator<Iterator<Tree>> it = parentIterators.iterator();
        while (it.hasNext()) {
            Iterator<Tree> anIt = it.next();
            if (anIt.hasNext() || (!it.hasNext() && amLast)) {
                result.append("   |");
            } else {
                result.append("    ");
            }
        }
        return result.toString();
    }
 
    private static StringBuilder formatTreeHelper
				(Tree t, StringBuilder buffer, 
				 java.util.List<Iterator<Tree>> parentIterators) 
		{
        if (!parentIterators.isEmpty()) {
            boolean amLast = 
								!parentIterators.get(parentIterators.size() - 1).hasNext();
            String lines = formatTreeDrawLines(parentIterators, amLast);
            buffer.append("\n").append(lines).append("\n").
								append(lines).append("- ");
        }
        
        if (t.isEmpty()) {
            buffer.append("[nil]");
            return buffer;
        } 
				else
            buffer.append(t.getValue());
        
        if (!(t.getLeft().isEmpty() && t.getRight().isEmpty())) {
            Iterator<Tree> it = getChildrenIterator(t);
            parentIterators.add(it);
            while (it.hasNext()) {
                Tree child = it.next();
                formatTreeHelper(child, buffer, parentIterators);
            }
            parentIterators.remove(it);
        }
				return buffer;
    }

    private static Iterator<Tree> getChildrenIterator(Tree t) {
        if (t.isEmpty())
            return Collections.<Tree>emptyList().iterator();
				else
						return Arrays.asList(new Tree[] { 
										t.getRight(),
										t.getLeft()}).iterator();    
    }


    @Override public boolean equals(Object o) {
				Tree t = (Tree) o;
				if (this.empty)
						return t.empty;
				else
						return !t.empty && this.value.equals( t.value) &&
								this.left.equals(t.left) && this.right.equals(t.right);
    }
	    
}
