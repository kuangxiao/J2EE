package test;

public class SortTree {
	
	static TreeNode root = null; // 根

	public static void main(String[] args) {
		int[] arr = new int[] { 4, 3, 5, 6, 2, 1, 8, 7, 9 };
		
		createTree(arr);
		pre(root);
		
		System.out.println(searchDataRecursion(root, 4));
		System.out.println(searchDataRecursion(root, 3));
		System.out.println(searchDataRecursion(root, 5));
		System.out.println(searchDataRecursion(root, 99));
		System.out.println(searchDataRecursion(root, 11));
	}

	// 创建树
	public static void createTree(int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			insertNode(root, arr[i]);
		}
	}

	/**
	 * 递归寻找关键字
	 * 
	 * @param node
	 * @param key
	 * @return
	 */
	public static Boolean searchDataRecursion(TreeNode node, int key) {
		if (node != null) {
			if (node.data == key) {
				return true;
			}
			if (key < node.data) {
				if (node.lchild != null) {
					return searchDataRecursion(node.lchild, key);
				}
			} else if (key > node.data) {
				if (node.rchild != null) {
					return searchDataRecursion(node.rchild, key);
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param node
	 *            当前节点
	 * @param data
	 *            要插入的值
	 */
	public static void insertNode(TreeNode node, int data) {
		if (root == null) {
			root = new TreeNode(data);
		} else {
			if (data < node.data) {
				if (node.lchild == null) {
					node.lchild = new TreeNode(data);
				} else {
					insertNode(node.lchild, data);
				}

			} else if (data > node.data) {
				if (node.rchild == null) {
					node.rchild = new TreeNode(data);
				} else {
					insertNode(node.rchild, data);
				}
			}
		}
	}

	// 前序遍历
	public static void pre(TreeNode node) {
		if (node != null) {
			System.out.print(node.data + " ");
			pre(node.lchild);
			pre(node.rchild);
		}
	}
}

/**
 * 节点类
 * 
 * @author admin
 * 
 */
class TreeNode {
	int data;

	public TreeNode(int data) {
		this.data = data;
	}

	TreeNode lchild;
	TreeNode rchild;
}
