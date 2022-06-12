
enum TreeNodeType {
	InnerNode,
	LeafNode
}

abstract class BTreeNode<TreeKey extends Comparable<TreeKey>> {
	protected Object[] keys;
	protected int keyCount;
	protected BTreeNode<TreeKey> parentNode;
	protected BTreeNode<TreeKey> leftSibling;
	protected BTreeNode<TreeKey> rightSibling;
	

	protected BTreeNode() {
		this.keyCount = 0;
		this.parentNode = null;
		this.leftSibling = null;
		this.rightSibling = null;
	}

	public int getKeyCount() {
		return this.keyCount;
	}
	
	@SuppressWarnings("unchecked")
	public TreeKey getKey(int index) {
		return (TreeKey)this.keys[index];
	}

	public void setKey(int index, TreeKey key) {
		this.keys[index] = key;
	}

	public BTreeNode<TreeKey> getParent() {
		return this.parentNode;
	}

	public void setParent(BTreeNode<TreeKey> parent) {
		this.parentNode = parent;
	}	
	
	public abstract TreeNodeType getNodeType();
	
	
	/**
	 * Search a key on current node, if found the key then return its position,
	 * otherwise return -1 for a leaf node, 
	 * return the child node index which should contain the key for a internal node.
	 */
	public abstract int search(TreeKey key);
	
	
	
	/* The codes below are used to support insertion operation */
	
	public boolean isOverflow() {
		return this.getKeyCount() == this.keys.length;
	}
	
	public BTreeNode<TreeKey> dealOverflow() {
		int midIndex = this.getKeyCount() / 2;
		TreeKey upKey = this.getKey(midIndex);
		
		BTreeNode<TreeKey> newRightNode = this.split();
				
		if (this.getParent() == null) {
			this.setParent(new BTreeInnerNode<TreeKey>());
		}
		newRightNode.setParent(this.getParent());
		
		// maintain links of sibling nodes
		newRightNode.setLeftSibling(this);
		newRightNode.setRightSibling(this.rightSibling);
		if (this.getRightSibling() != null)
			this.getRightSibling().setLeftSibling(newRightNode);
		this.setRightSibling(newRightNode);
		
		// push up a key to parent internal node
		return this.getParent().pushUpKey(upKey, this, newRightNode);
	}
	
	protected abstract BTreeNode<TreeKey> split();
	
	protected abstract BTreeNode<TreeKey> pushUpKey(TreeKey key, BTreeNode<TreeKey> leftChild, BTreeNode<TreeKey> rightNode);
	
	
	
	
	
	
	/* The codes below are used to support deletion operation */
	
	public boolean isUnderflow() {
		return this.getKeyCount() < (this.keys.length / 2);
	}
	
	public boolean canLendAKey() {
		return this.getKeyCount() > (this.keys.length / 2);
	}
	
	public BTreeNode<TreeKey> getLeftSibling() {
		if (this.leftSibling != null && this.leftSibling.getParent() == this.getParent())
			return this.leftSibling;
		return null;
	}

	public void setLeftSibling(BTreeNode<TreeKey> sibling) {
		this.leftSibling = sibling;
	}

	public BTreeNode<TreeKey> getRightSibling() {
		if (this.rightSibling != null && this.rightSibling.getParent() == this.getParent())
			return this.rightSibling;
		return null;
	}

	public void setRightSibling(BTreeNode<TreeKey> silbling) {
		this.rightSibling = silbling;
	}
	
	public BTreeNode<TreeKey> dealUnderflow() {
		if (this.getParent() == null)
			return null;
		
		// try to borrow a key from sibling
		BTreeNode<TreeKey> leftSibling = this.getLeftSibling();
		if (leftSibling != null && leftSibling.canLendAKey()) {
			this.getParent().processChildrenTransfer(this, leftSibling, leftSibling.getKeyCount() - 1);
			return null;
		}
		
		BTreeNode<TreeKey> rightSibling = this.getRightSibling();
		if (rightSibling != null && rightSibling.canLendAKey()) {
			this.getParent().processChildrenTransfer(this, rightSibling, 0);
			return null;
		}
		
		// Can not borrow a key from any sibling, then do fusion with sibling
		if (leftSibling != null) {
			return this.getParent().processChildrenFusion(leftSibling, this);
		}
		else {
			return this.getParent().processChildrenFusion(this, rightSibling);
		}
	}
	
	protected abstract void processChildrenTransfer(BTreeNode<TreeKey> borrower, BTreeNode<TreeKey> lender, int borrowIndex);
	
	protected abstract BTreeNode<TreeKey> processChildrenFusion(BTreeNode<TreeKey> leftChild, BTreeNode<TreeKey> rightChild);
	
	protected abstract void fusionWithSibling(TreeKey sinkKey, BTreeNode<TreeKey> rightSibling);
	
	protected abstract TreeKey transferFromSibling(TreeKey sinkKey, BTreeNode<TreeKey> sibling, int borrowIndex);
}