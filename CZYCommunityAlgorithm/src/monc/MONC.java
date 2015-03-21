package monc;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Vector;

public class MONC {
	class Neighbour {
		int neighbourId;
		int weight;

		Neighbour(int n, int w) {
			this.neighbourId = n;
			this.weight = w;
		}
	}

	private int numberOfNodes;
	private int[][] cMatrix;
	private double[][] alphaMatrix;
	private int[][] adjacentMatrix = null;
	private Vector<DegreeInfo> div = null;
	DecimalFormat df = new DecimalFormat("0.0000");

	class DegreeInfo {
		int indegree;
		int totaldegree;

		DegreeInfo(int a, int b) {
			indegree = a;
			totaldegree = b;
		}
	}
	
	MONC(int n, int[][] am) {
		this.adjacentMatrix = am;
		this.numberOfNodes = n;
		initMatrix();
	}

	public void initMatrix() {
		this.cMatrix = new int[this.numberOfNodes][this.numberOfNodes];
		this.alphaMatrix = new double[this.numberOfNodes][this.numberOfNodes];
		this.div = new Vector<DegreeInfo>();
		for (int i = 1; i < this.numberOfNodes; i++) {
			this.cMatrix[i][1] = i;// set all c[i][1]=i
			this.caculateAlphaChange(i);
			this.cMatrix[i][0] = 1;// set all community active 1
		}
	}

	public void setAdjacentMatrix(int[][] matrix) {
		adjacentMatrix = matrix;
	}

	public void AlogorithmProcedure() throws FileNotFoundException {
		for (int j = 2; j < this.numberOfNodes; j++) {
			long start = System.currentTimeMillis();
			for (int i = 1; i < this.numberOfNodes; i++) {
				if (this.cMatrix[i][0] == 1) {// each active community G
					if (step8(i, j)) {// i:communityId;j:colummId
						continue;// continue to analysis next community;
					}
				}
			}
			step10(j);
			long end = System.currentTimeMillis();
			System.out.println("finish " + j + " columm");
			System.out.println((end - start) / 1000f + " seconds");
		}
		PrintWriter pw = new PrintWriter(
				"C:\\Users\\Famiking\\Desktop\\Monc\\result.txt");
		pw.println("cMatrix");
		for (int i = 1; i < this.cMatrix.length; i++) {
			for (int j = 1; j < this.cMatrix.length; j++) {
				pw.print(this.cMatrix[i][j] + " ");
			}
			pw.println();
		}
		pw.println("alphaMatrix");
		for (int m = 1; m < this.alphaMatrix.length; m++) {
			for (int n = 1; n < this.alphaMatrix.length; n++) {
				pw.print(df.format(1 / this.alphaMatrix[m][n]) + " ");
			}
			pw.println();
		}
		pw.close();

	}

	public void printMatrix(int[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}

	public void printMatrix(double[][] matrix) {
		System.out.println("alpha matrix");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}

	public int getInnerDegree(int[] community) {
		int degree = 0;
		Vector<Neighbour> ns = null;
		for (int i = 1; i < community.length && community[i] != 0; i++) {
			ns = getNodeNeighbour(community[i]);
			for (int j = 0; j < ns.size(); j++) {
				if (nodeInCommunity(community, ns.elementAt(j).neighbourId))
					degree += ns.elementAt(j).weight;
			}

		}
		return degree;
	}

	public boolean nodeInCommunity(int[] community, int nodeId) {
		for (int i = 1; i < community.length && community[i] != 0; i++) {
			if (community[i] == nodeId)
				return true;
		}
		return false;
	}

	private Vector<Neighbour> getNodeNeighbour(int nodeId) {
		Vector<Neighbour> nbset = new Vector<Neighbour>();
		for (int j = 1; j < this.adjacentMatrix.length; j++) {
			if (adjacentMatrix[nodeId][j] != 0) {
				nbset.add(new Neighbour(j, adjacentMatrix[nodeId][j]));
			}
		}
		return nbset;
	}

	public int getTotalDegree(int[] community) {
		int totalDegree = 0;
		Vector<Neighbour> ns = new Vector<Neighbour>();
		for (int i = 1; i < community.length && community[i] != 0; i++) {
			ns.addAll(getNodeNeighbour(community[i]));
		}
		for (int j = 0; j < ns.size(); j++) {
			totalDegree += ns.elementAt(j).weight;
		}
		return totalDegree - getInnerDegree(community) / 2;
	}

	private int getKiner(int communityId, int nodeId) {
		int degree = 0;
		for (int i = 0; i < this.adjacentMatrix[nodeId].length; i++) {
			if (this.adjacentMatrix[nodeId][i] != 0
					&& nodeInCommunity(this.cMatrix[communityId], i)) {
				degree += this.adjacentMatrix[nodeId][i];
			}
		}
		return degree;
	}

	private int getAvPlus(int nodeId) {
		int degree = 0;
		for (int i = 0; i < this.adjacentMatrix[nodeId].length; i++) {
			degree += this.adjacentMatrix[nodeId][i];
		}
		return degree;
	}

	public int[] updateCommunity(int communtiyId, int nodeId) {// just try
		int[] temp = null;
		temp = this.cMatrix[communtiyId].clone();
		for (int i = 1; i < temp.length; i++) {
			if (temp[i] == 0) {
				temp[i] = nodeId;
				break;
			}
		}
		return temp;
	}

	public void caculateAlphaChange(int nodeId) {
		int degree = 0;
		for (int i = 0; i < this.adjacentMatrix[nodeId].length; i++) {
			degree += this.adjacentMatrix[nodeId][i];
		}
		this.div.add(new DegreeInfo(0, degree));
	}

	public double caculateAlphaChange2(int communityId, int nodeId) {
		int kinbefore = this.div.elementAt(communityId - 1).indegree;
		int kinafter = kinbefore + 2 * getKiner(communityId, nodeId);
		int ktotalbefore = this.div.elementAt(communityId - 1).totaldegree;
		int ktotalafter = ktotalbefore + getAvPlus(nodeId);
		double up = 0.0;
		double down = 0.0;
		up = Double.parseDouble(df.format(Math.log(kinafter + 1)
				- Math.log(kinbefore + 1)));
		if (ktotalbefore == 0) {
			down = Double.parseDouble(df.format(Math.log(ktotalafter)));
		} else {
			down = Double.parseDouble(df.format(Math.log(ktotalafter)
					- Math.log(ktotalbefore)));
		}
		if (down == 0.0) {
			return 100.0;
		}
		return Double.parseDouble(df.format(up / down));
	}

	public int step56(int communityId, int colum) {// for each node adjacent to											// G
		double max = 0.0;
		int nodeId = 0;
		Vector<Neighbour> ns = null;
		for (int i = 1; i < this.cMatrix[communityId].length
				&& this.cMatrix[communityId][i] != 0; i++) {
			ns = getCommunityNeighborSet(communityId);
			for (int j = 0; j < ns.size(); j++) {
				double temp = caculateAlphaChange2(communityId,
						ns.elementAt(j).neighbourId);
				if (temp > max) {
					max = temp;
					this.alphaMatrix[communityId][colum] = max;
					nodeId = ns.elementAt(j).neighbourId;
				}
			}
		}
		return nodeId;
	}

	public boolean step8(int communtiyId, int colum) {
		int nodeId = step56(communtiyId, colum);
		for (int i = 1; i < cMatrix[communtiyId].length; i++) {
			if (cMatrix[communtiyId][i] == 0) {
				cMatrix[communtiyId][i] = nodeId;// still have to merge

				return true;// update community successful
			}
		}
		return false;
	}

	private Vector<Neighbour> getCommunityNeighborSet(int communityId) {
		Vector<Neighbour> nb = new Vector<Neighbour>();
		boolean one;
		for (int i = 1; i < this.cMatrix[communityId].length; i++) {
			int nodeId = this.cMatrix[communityId][i];
			for (int j = 1; j < this.adjacentMatrix[nodeId].length; j++) {
				one = false;
				if (this.adjacentMatrix[nodeId][j] != 0) {
					for (int k = 1; k < this.cMatrix[communityId].length; k++) {
						if (j == this.cMatrix[communityId][k]) {
							one = true;
							break;
						}
					}
					if (one == false) {// not belong to a community
						nb.add(new Neighbour(j, adjacentMatrix[nodeId][j]));
					}
				}
			}
		}
		return nb;
	}

	public boolean sameCommunity(int communityId1, int communityId2) {// same:true
		boolean flag;
		for (int i = 1; i < this.cMatrix[communityId1].length
				&& this.cMatrix[communityId1][i] != 0; i++) {
			flag = false;
			for (int j = 1; j < this.cMatrix[communityId2].length
					&& this.cMatrix[communityId1][j] != 0; j++) {
				if (cMatrix[communityId1][i] == cMatrix[communityId2][j]) {
					flag = true;
					break;
				}
			}
			if (flag == false) {
				return false;
			}
		}
		return true;
	}

	public boolean step10(int colum) {// true;
		for (int i = 1; i < this.cMatrix.length; i++)
			for (int j = i + 1; j < this.cMatrix.length; j++) {
				if (sameCommunity(i, j)) {
					whoseAlphaLarger(i, j, colum);
				}
			}
		return false;
	}

	public void whoseAlphaLarger(int communityId1, int communityId2, int colum) {
		if (this.alphaMatrix[communityId1][colum] > this.alphaMatrix[communityId2][colum]) {
			this.alphaMatrix[communityId1][0] = 0;
		} else {
			this.alphaMatrix[communityId1][0] = 0;
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
//		long start = System.currentTimeMillis();
//		MONC m = new MONC(35, new TransformIntoMatrix().getAdjacentMatrix());
//		m.AlogorithmProcedure();
//		m.printMatrix(m.cMatrix);
//		m.printMatrix(m.alphaMatrix);
//		long end = System.currentTimeMillis();
//		System.out.println((end - start) / 1000f + " seconds");
//		System.out.println("it's done");
	}
}
