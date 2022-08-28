import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class Ex1 {

	private static int num = 0;
	private static boolean isWithOpen;
	private static  char[][] _Goals;

	static class Node{
		char[][] state;
		String path; 
		Node parent;
		int cost;
		int createdTime;
		boolean isOut = false;
		public Node(char[][] startState, String path, int cost, Node parent) {
			num++;
			createdTime = num;
			this.path = path;
			this.cost = cost;
			this.parent = parent;
			this.state = new char[startState.length][startState[0].length];
			for (int i = 0; i < startState.length; i++) {
				for (int j = 0; j < startState[0].length; j++) {
					this.state[i][j] = startState[i][j];
				}
			}
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.deepHashCode(state);
			return result;
		}
		// override to equals the states arrays
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (!Arrays.deepEquals(state, other.state))
				return false;
			return true;
		}
	}
	static class CostComparator implements Comparator<Node> {
		@Override
		public int compare(Node a, Node b) {
			int comp = (a.cost+H(a)) - (b.cost+H(b));	
			if (comp == 0)
				return Integer.compare(a.createdTime, b.createdTime);
			return comp; 
		}
	}
	// count number of balls that is not in there place 
	//	private static int H(Node g){
	//		int count = 0;
	//		int row = _Goals.length;
	//		int col = _Goals[0].length;
	//		for (int i = 0; i < row; i++) {
	//			for (int j = 0; j < col; j++) {
	//				if(g.state[i][j] == '_'){
	//				continue;
	//			}
	//				if(g.state[i][j] != _Goals[i][j]) count+=1;
	//			}
	//		}	
	//		return count;
	//	} 
	
	// Manhattan distance
	private static int H(Node g){
		int count = 0;
		int row = _Goals.length;
		int col = _Goals[0].length;
		boolean b[][] = new boolean[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if(g.state[i][j] == '_'){
					continue;
				}
				int min = Integer.MAX_VALUE;
				int minI = -1;
				int minJ = -1;
				for (int i2 = 0; i2 < row; i2++) {
					for (int j2 = 0; j2 < col; j2++) {
						if(g.state[i][j] == _Goals[i2][j2] && !b[i2][j2]) {
							int distance = Math.abs(i-i2) + Math.abs(j-j2);
							char color = g.state[i][j];
							if(color == 'R') distance *= 1;
							else if(color == 'B') distance *= 2;
							else if(color == 'G') distance *= 10;
							else if(color == 'Y') distance *= 1;
							if(distance < min) {
								min = distance;
								minI = i2;
								minJ = j2;
							}
						}
					}
				}
				if(minI != -1 && minJ != -1) {
					b[minI][minJ] = true;
				    count +=min;
				}
			}
		}	
		return count; 
	} 

	//BFS algorithm as we learned in the lecture.
	private static Node BFS(Node start, char[][] Goals) {
		// check if start == goals
		if(goal(start, Goals)) {
			return start;
		}
		int row = start.state.length;
		int col = start.state[0].length;
		Queue<Node> l = new LinkedList<Node>();
		Set<Node> ol = new HashSet<Node>();
		Set<Node> c = new HashSet<Node>();
		l.add(start);
		ol.add(start);
		while(!l.isEmpty()) {
			if(isWithOpen) printOpenList(l);
			Node n = l.poll();
			ol.remove(n);
			c.add(n);
			//	for(Node g : operator(n)) 
			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					if(n.state[i][j] == '_'){
						//check down
						if(i+1<row&&n.state[i+1][j] != '_') {
							Node g = newOpNode(n, i, j, i+1, j); 
							if(g != null) {
								if(!c.contains(g)&&!ol.contains(g)) {
									if(goal(g, Goals)) {
										return g;
									}
									l.add(g);
									ol.add(g);
								}
							}
						}
						//check up
						if(i-1>=0&&n.state[i-1][j] != '_') {
							Node g = newOpNode(n, i, j, i-1, j); 
							if(g != null) {
								if(!c.contains(g)&&!ol.contains(g)) {
									if(goal(g, Goals)) {
										return g;
									}
									l.add(g);
									ol.add(g);
								}
							}
						}
						//check left
						if(j-1>=0&&n.state[i][j-1] != '_') {
							Node g = newOpNode(n, i, j, i, j-1); 
							if(g != null) {
								if(!c.contains(g)&&!ol.contains(g)) {
									if(goal(g, Goals)) {
										return g;
									}
									l.add(g);
									ol.add(g);	
								}
							}
						}
						//check right
						if(j+1<col&&n.state[i][j+1] != '_') {
							Node g = newOpNode(n, i, j, i, j+1); 
							if(g != null) {
								if(!c.contains(g)&&!ol.contains(g)) {
									if(goal(g, Goals)) {
										return g;
									}
									l.add(g);
									ol.add(g);	
								}
							}
						}
					}
				}
			}

		}
		return null;
	}

	private static Node DFID(Node start, char[][] Goals) {
		for (int i = 1; i < Integer.MAX_VALUE; i++) {
			Set<Node> h = new HashSet<Node>();
			Object result = Limited_DFS(start, Goals, i, h);
			if(result != Boolean.FALSE) return (Node) result;
		}
		return null;
	}
	private static Object Limited_DFS(Node n, char[][] Goals,int limit, Set<Node> h) {
		if(goal(n, Goals)) {
			return n;
		}else if(limit == 0) return Boolean.FALSE; //cutoff
		else {
			h.add(n);
			Boolean isCutoff = false;
			//	for(Node g : operator(n)) 
			for (int i = 0; i < n.state.length; i++) {
				for (int j = 0; j < n.state[0].length; j++) {
					if(n.state[i][j] == '_'){
						//check down
						if(i+1<n.state.length&&n.state[i+1][j] != '_') {
							Node g = newOpNode(n, i, j, i+1, j); 
							if(g != null) {
								if(!h.contains(g)) {
									Object result = Limited_DFS(g, Goals, limit-1, h);
									if (result == Boolean.FALSE) isCutoff = true;
									else if (result!=null) return result;
								}
							}
						}
						//check up
						if(i-1>=0&&n.state[i-1][j] != '_') {
							Node g = newOpNode(n, i, j, i-1, j); 
							if(g != null) {
								if(!h.contains(g)) {
									Object result = Limited_DFS(g, Goals, limit-1, h);
									if (result == Boolean.FALSE) isCutoff = true;
									else if (result!=null) return result;
								}
							}
						}
						//check left
						if(j-1>=0&&n.state[i][j-1] != '_') {
							Node g = newOpNode(n, i, j, i, j-1); 
							if(g != null) {
								if(!h.contains(g)) {
									Object result = Limited_DFS(g, Goals, limit-1, h);
									if (result == Boolean.FALSE) isCutoff = true;
									else if (result!=null) return result;
								}
							}
						}
						//check right
						if(j+1<n.state[0].length&&n.state[i][j+1] != '_') {
							Node g = newOpNode(n, i, j, i, j+1); 
							if(g != null) {
								if(!h.contains(g)) {
									Object result = Limited_DFS(g, Goals, limit-1, h);
									if (result == Boolean.FALSE) isCutoff = true;
									else if (result!=null) return result;
								}
							}
						}
					}
				}
			}
			
			//			for(Node g : operator(n)) {
			//				//num++;
			//				if(!h.contains(g)) {
			//					Object result = Limited_DFS(g, Goals, limit-1, h);
			//					if (result == Boolean.FALSE) isCutoff = true;
			//					else if (result!=null) return result;
			//				}
			//			}
			
			if(isWithOpen) printOpenList(h);
			h.remove(n);
			if(isCutoff) return Boolean.FALSE;
			else return null;
		}
	}
	private static Node AStar(Node start, char[][] Goals) {
		// check if start == goals
		if(goal(start, Goals)) {
			return start;
		}
		Comparator<Node> costComparator = new CostComparator();
		Queue<Node> l = new PriorityQueue<Node>(costComparator);
		Map<Node, Node> ol = new HashMap<Node, Node>();
		Set<Node> c = new HashSet<Node>();
		l.add(start);
		ol.put(start, start);
		while(!l.isEmpty()) {
			if(isWithOpen) printOpenList(l);
			Node n = l.poll();
			ol.remove(n);
			if(goal(n, Goals)) {
				return n;
			}
			c.add(n);
			for(Node g : operator(n)) {
				if(!c.contains(g)&&!ol.containsKey(g)) {
					l.add(g);
					ol.put(g, g);
				}else if(ol.containsKey(g)&&g.cost<ol.get(g).cost){
					l.remove(ol.get(g));
					l.add(g);
					ol.put(g, g);
				}
//								if(goal(g, Goals)) {
//									return g;
//								}
			}
		}
		return null;
	}

	private static Node IDAStar(Node start, char[][] Goals) {
		Stack<Node> l = new Stack<Node>();
		Map<Node, Node> h = new HashMap<Node, Node>();
		int t = H(start);
		while(t!=Integer.MAX_VALUE) {
			int minF = Integer.MAX_VALUE;
			l.push(start);
			h.put(start, start);
			start.isOut = false;
			while(!l.isEmpty()) {
				if(isWithOpen) printOpenList(l);
				Node n = l.pop();
				if(n.isOut) {
					h.remove(n);
				}else {
					n.isOut = true;
					l.push(n);
					//	for(Node g : operator(n)) 
					for (int i = 0; i < n.state.length; i++) {
						for (int j = 0; j < n.state[0].length; j++) {
							if(n.state[i][j] == '_'){
								boolean cont = true;
								//check down
								if(i+1<n.state.length&&n.state[i+1][j] != '_') {
									Node g = newOpNode(n, i, j, i+1, j); 
									if(g != null) {
										if((g.cost+H(g))>t){
											minF = Math.min(minF, (g.cost+H(g)));
											cont = false;
										}
										if(h.containsKey(g)&&h.get(g).isOut&&cont) {
											cont = false;
										}
										if(h.containsKey(g)&&!h.get(g).isOut&&cont) {
											if(h.get(g).cost > g.cost) {
												l.remove(g);
												h.remove(g);
											}else {
												cont = false;
											}
										}
										if(cont) {
											if(goal(g, Goals)) {
												return g;
											}
											l.push(g);
											h.put(g, g);	
										}
									}
								}
								cont = true;
								//check up
								if(i-1>=0&&n.state[i-1][j] != '_') {
									Node g = newOpNode(n, i, j, i-1, j); 
									if(g != null) {
										if((g.cost+H(g))>t){
											minF = Math.min(minF, (g.cost+H(g)));
											cont = false;
										}
										if(h.containsKey(g)&&h.get(g).isOut&&cont) {
											cont = false;
										}
										if(h.containsKey(g)&&!h.get(g).isOut&&cont) {
											if(h.get(g).cost > g.cost) {
												l.remove(g);
												h.remove(g);
											}else {
												cont = false;
											}
										}
										if(cont) {
											if(goal(g, Goals)) {
												return g;
											}
											l.push(g);
											h.put(g, g);	
										}
									}
								}
								cont = true;
								//check left
								if(j-1>=0&&n.state[i][j-1] != '_') {
									Node g = newOpNode(n, i, j, i, j-1); 
									if(g != null) {
										if((g.cost+H(g))>t){
											minF = Math.min(minF, (g.cost+H(g)));
											cont = false;
										}
										if(h.containsKey(g)&&h.get(g).isOut&&cont) {
											cont = false;
										}
										if(h.containsKey(g)&&!h.get(g).isOut&&cont) {
											if(h.get(g).cost > g.cost) {
												l.remove(g);
												h.remove(g);
											}else {
												cont = false;
											}
										}
										if(cont) {
											if(goal(g, Goals)) {
												return g;
											}
											l.push(g);
											h.put(g, g);	
										}
									}
								}
								cont = true;
								//check right
								if(j+1<n.state[0].length&&n.state[i][j+1] != '_') {
									Node g = newOpNode(n, i, j, i, j+1); 
									if(g != null) {
										if((g.cost+H(g))>t){
											minF = Math.min(minF, (g.cost+H(g)));
											cont = false;
										}
										if(h.containsKey(g)&&h.get(g).isOut&&cont) {
											cont = false;
										}
										if(h.containsKey(g)&&!h.get(g).isOut&&cont) {
											if(h.get(g).cost > g.cost) {
												l.remove(g);
												h.remove(g);
											}else {
												cont = false;
											}
										}
										if(cont) {
											if(goal(g, Goals)) {
												return g;
											}
											l.push(g);
											h.put(g, g);	
										}
									}
								}
							}
						}
					}

//										for(Node g : operator(n)) {
//											//num++;
//											if((g.cost+H(g))>t){
//												minF = Math.min(minF, (g.cost+H(g)));
//												continue;
//											}
//											if(h.containsKey(g)&&h.get(g).isOut) {
//												continue;
//											}
//											if(h.containsKey(g)&&!h.get(g).isOut) {
//												if(h.get(g).cost > g.cost) {
//													l.remove(g);
//													h.remove(g);
//												}else {
//													continue;
//												}
//											}
//											if(goal(g, Goals)) {
//												return g;
//											}
//											l.push(g);
//											h.put(g, g);
//										}

				}
			}
			t = minF;
		}
		return null;
	}
	private static Node DFBnB(Node start, char[][] Goals) {
		Comparator<Node> costComparator = new CostComparator();
		Stack<Node> l = new Stack<Node>();
		Map<Node, Node> h = new HashMap<Node, Node>();
		l.push(start);
		h.put(start, start);
		Node result = null;
		int t = Integer.MAX_VALUE;
		while(!l.isEmpty()) {
			if(isWithOpen) printOpenList(l);
			Node n = l.pop();
			if(n.isOut) {
				h.remove(n);
			}else {
				n.isOut = true;
				l.push(n);	
				List<Node> N = operator(n);
				List<Node> NInsert = new ArrayList<Node>();
				N.sort(costComparator);
				for(Node g : N) {
					num++;
					if((g.cost+H(g))>=t){
						//remove g and all nodes after 
						break;
					}else if(h.containsKey(g)&&h.get(g).isOut) {
						//iterator().remove();
						continue;
					}else if(h.containsKey(g)&&!h.get(g).isOut) {
						if(h.get(g).cost > g.cost) {
							l.remove(g);
							h.remove(g);
						}else {
							//iterator().remove();
							continue;
						}
					}
					if(goal(g, Goals)) {
						t = g.cost+H(g);
						result = g;
						//remove g and all nodes after
						break;
					}
					NInsert.add(g);
				}
				for(int i = NInsert.size()-1; i>=0; i-- ) {
					l.push(NInsert.get(i));
					h.put(NInsert.get(i), NInsert.get(i));
				}
			}	
		}
		return result;
	}

	private static void printOpenList(Queue<Node> l) {
		System.out.println("*****************open list, The number of nodes that created: "+num+"******************");
		for(Node e: l) {
			for (int i = 0; i < e.state.length; i++) {
				for (int j = 0; j < e.state[0].length; j++) {
					System.out.print(e.state[i][j]+" ");
				} 
				System.out.println();
			}
			System.out.println();
		}
	}

	private static void printOpenList(Stack<Node> h) {
		System.out.println("*****************open list, The number of nodes that created: "+num+"******************");
		for(Node e: h) {
			for (int i = 0; i < e.state.length; i++) {
				for (int j = 0; j < e.state[0].length; j++) {
					System.out.print(e.state[i][j]+"\t");
				}
				System.out.println();
			}
			System.out.println();
		}
	}

	private static void printOpenList(Set<Node> h) {
		System.out.println("*****************open list, The number of nodes that created: "+num+"******************");
		for(Node e: h) {
			for (int i = 0; i < e.state.length; i++) {
				for (int j = 0; j < e.state[0].length; j++) {
					System.out.print(e.state[i][j]+"\t");
				}
				System.out.println();
			}
			System.out.println();
		}
	}

	private static boolean goal(Node g, char[][] goals) {
		int row = g.state.length;
		int col = g.state[0].length;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if(g.state[i][j] != goals[i][j]) return false;
			}
		}	
		return true;
	}

	private static List<Node> operator(Node n) {
		List<Node> op = new ArrayList<>();
		int row = n.state.length;
		int col = n.state[0].length;

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if(n.state[i][j] == '_'){
					//check up
					if(i-1>=0&&n.state[i-1][j] != '_') {
						Node temp = newOpNode(n, i, j, i-1, j); 
						if(temp != null)
							op.add(temp);
					}
					//check down
					if(i+1<row&&n.state[i+1][j] != '_') {
						Node temp = newOpNode(n, i, j, i+1, j); 
						if(temp != null)
							op.add(temp); 
					}
					//check left
					if(j-1>=0&&n.state[i][j-1] != '_') {
						Node temp = newOpNode(n, i, j, i, j-1); 
						if(temp != null)
							op.add(temp);	 
					}
					//check right
					if(j+1<col&&n.state[i][j+1] != '_') {
						Node temp = newOpNode(n, i, j, i, j+1); 
						if(temp != null)
							op.add(temp);	 
					}
				}
			}
		}
		return op;
	}

	private static Node newOpNode(Node n, int i, int j, int i2, int j2) {
		char[][] newNodeState = new char[n.state.length][n.state[0].length]; 
		for (int l = 0; l < n.state.length; l++) {
			for (int l2 = 0; l2 < n.state[0].length; l2++) {
				newNodeState[l][l2] = n.state[l][l2];
			}
		}
		char temp = newNodeState[i][j];
		newNodeState[i][j] = newNodeState[i2][j2];
		newNodeState[i2][j2] = temp;
		if(n.parent!=null && Arrays.deepEquals(newNodeState, n.parent.state)) {
			return null;
		}
		Node newNode = new Node(newNodeState, n.path, n.cost, n);

		// path syntax like: (2,2):B:(2,3)--(2,3):B:(1,3)--(3,2):G:(2,2)
		char color = newNode.state[i][j];
		if(color == 'R') newNode.cost += 1;
		else if(color == 'B') newNode.cost += 2;
		else if(color == 'G') newNode.cost += 10;
		else if(color == 'Y') newNode.cost += 1;

		if(newNode.path.equals("")){
			newNode.path += "("+(i2+1)+","+(j2+1)+"):"+color+":("+(i+1)+","+(j+1)+")";
		}else {
			newNode.path += "--("+(i2+1)+","+(j2+1)+"):"+color+":("+(i+1)+","+(j+1)+")";
		}
		return newNode;
	}

	public static void main(String[] args) {

		String algo = "";
		String ifWithOpen = "";
		String boardSize;
		char[][] startBoard = null;
		char[][] Goals = null;

		//read input file
		try {
			File myObj = new File("input.txt");
			Scanner myReader = new Scanner(myObj);
			algo = myReader.nextLine();
			ifWithOpen = myReader.nextLine();
			boardSize = myReader.nextLine();
			if(boardSize.equals("small")) {
				startBoard = new char[3][3];
				for (int i = 0; i < 3; i++) {
					String line = myReader.nextLine();
					startBoard[i][0] = line.charAt(0);
					startBoard[i][1] = line.charAt(2);
					startBoard[i][2] = line.charAt(4);
				}
				myReader.nextLine();
				Goals = new char[3][3];
				for (int i = 0; i < 3; i++) {
					String line = myReader.nextLine();
					Goals[i][0] = line.charAt(0);
					Goals[i][1] = line.charAt(2);
					Goals[i][2] = line.charAt(4);
				}
			}else {
				startBoard = new char[5][5];
				for (int i = 0; i < 5; i++) {
					String line = myReader.nextLine();
					startBoard[i][0] = line.charAt(0);
					startBoard[i][1] = line.charAt(2);
					startBoard[i][2] = line.charAt(4);
					startBoard[i][3] = line.charAt(6);
					startBoard[i][4] = line.charAt(8);
				}
				myReader.nextLine();
				Goals = new char[5][5];
				for (int i = 0; i < 5; i++) {
					String line = myReader.nextLine();
					Goals[i][0] = line.charAt(0);
					Goals[i][1] = line.charAt(2);
					Goals[i][2] = line.charAt(4);
					Goals[i][3] = line.charAt(6);
					Goals[i][4] = line.charAt(8);
				}
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		if(ifWithOpen.equals("no open")){
			isWithOpen = false;
		}else if(ifWithOpen.equals("with open")) {
			isWithOpen = true;
		}



		_Goals = Goals;		
		Node start = new Node(startBoard, "", 0, null);
		Node goal = null;
		long st = System.currentTimeMillis();
		if(algo.equals("BFS"))
			goal = BFS(start, Goals);
		if(algo.equals("DFID"))
			goal = DFID(start, Goals);
		if(algo.equals("A*"))
			goal = AStar(start, Goals);
		if(algo.equals("IDA*"))
			goal = IDAStar(start, Goals);
		if(algo.equals("DFBnB"))
			goal = DFBnB(start, Goals);

		long end = System.currentTimeMillis();
		NumberFormat formatter = new DecimalFormat("#0.000");
		try {
			File myObj = new File("output.txt");
			myObj.createNewFile();
			FileWriter myWriter = new FileWriter("output.txt");

			if(goal != null) {
				myWriter.write(goal.path+"\n");
			//	System.out.println(goal.path);
				myWriter.write("Num: "+num+"\n");
			//	System.out.println("Num: "+num);
				myWriter.write("Cost: "+goal.cost+"\n");
			//	System.out.println("Cost: "+goal.cost);
				myWriter.write(formatter.format((end - st) / 1000d)+" seconds\n");
			//	System.out.println(formatter.format((end - st) / 1000d)+" seconds");
			}else {
				myWriter.write("no path\n");
			//	System.out.println("no path");
				myWriter.write("Num: "+num+"\n");
			//	System.out.println("Num: "+num);
				myWriter.write("Cost: inf\n");
			//	System.out.println("Cost: inf");
				myWriter.write(formatter.format((end - st) / 1000d)+" seconds\n");
			//	System.out.println(formatter.format((end - st) / 1000d)+" seconds");
			}

			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		
	}

}
