package com.pe.courseproject;

import java.util.Map;

/**
 * Created by Nikolay on 30.11.2015 г..
 */
public class Tree {
    private Node root;
    private CharacterList list;
    
	public static class Node {
	
	    private String key;
	    private Node leftNode, rightNode;
	
	    public Node(String key) {
	        this.key = key;
	    }
	
	    public void setLeftNode(Node leftNode) {
	        this.leftNode = leftNode;
	    }
	
	    public void setRightNode(Node rightNode) {
	        this.rightNode = rightNode;
	    }
	    
	    public String getKey() {
	    	return key;
	    }

		public Node getLeftNode() {
			// TODO Auto-generated method stub
			return leftNode;
		}

		public Node getRightNode() {
			// TODO Auto-generated method stub
			return rightNode;
		}
	}

    public Tree(Map<Character, Double> charProbabilityMap) {
        int middleIndex;
        this.list = new CharacterList(charProbabilityMap);
        this.root = new Node(list.getString(0, list.lastIndex));

        middleIndex = list.findMiddleIndexBasedOnProbability(0, list.lastIndex);

        root.setLeftNode(addChild(0,middleIndex));
        root.setRightNode(addChild(middleIndex+1, list.lastIndex));
    }

    public Node getRoot() {
        return root;
    }

    public Node addChild(int startIndex, int endIndex){
        int middleIndex;
        Node child = new Node(list.getString(startIndex, endIndex));

        if(startIndex == endIndex) return child;

        middleIndex = list.findMiddleIndexBasedOnProbability(startIndex, endIndex);
        child.setLeftNode(addChild(startIndex, middleIndex));
        child.setRightNode(addChild(middleIndex+1, endIndex));

        return child;
    }

}