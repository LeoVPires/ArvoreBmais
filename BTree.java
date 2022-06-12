/**
 * A B+ tree
 * Since the structures and behaviors between internal node and external node are different, 
 * so there are two different classes for each kind of node.
 * @param <TreeKey> the data type of the key
 * @param <TreeValue> the data type of the value
 */
public class BTree<TreeKey extends Comparable<TreeKey>, TreeValue> {
	private BTreeNode<TreeKey> root;
	
	public BTree() {
		this.root = new BTreeLeafNode<TreeKey, TreeValue>();
	}

	/**
	 * Insert a new key and its associated value into the B+ tree.
	 */
	public void insert(TreeKey key, TreeValue value) {
		BTreeLeafNode<TreeKey, TreeValue> leaf = this.findLeafNodeShouldContainKey(key);
		leaf.insertKey(key, value);
		
		if (leaf.isOverflow()) {
			BTreeNode<TreeKey> n = leaf.dealOverflow();
			if (n != null)
				this.root = n; 
		}
	}
	
	/**
	 * Search a key value on the tree and return its associated value.
	 */
	public TreeValue search(TreeKey key) {
		BTreeLeafNode<TreeKey, TreeValue> leaf = this.findLeafNodeShouldContainKey(key);
		
		int index = leaf.search(key);
		return (index == -1) ? null : leaf.getValue(index);
	}
	
	/**
	 * Delete a key and its associated value from the tree.
	 */
	public void delete(TreeKey key) {
		BTreeLeafNode<TreeKey, TreeValue> leaf = this.findLeafNodeShouldContainKey(key);
		
		if (leaf.delete(key) && leaf.isUnderflow()) {
			BTreeNode<TreeKey> n = leaf.dealUnderflow();
			if (n != null)
				this.root = n; 
		}
	}
	
	/**
	 * Search the leaf node which should contain the specified key
	 */
	@SuppressWarnings("unchecked")
	private BTreeLeafNode<TreeKey, TreeValue> findLeafNodeShouldContainKey(TreeKey key) {
		BTreeNode<TreeKey> node = this.root;
		while (node.getNodeType() == TreeNodeType.InnerNode) {
			node = ((BTreeInnerNode<TreeKey>)node).getChild( node.search(key) );
		}
		
		return (BTreeLeafNode<TreeKey, TreeValue>)node;
	}
}
