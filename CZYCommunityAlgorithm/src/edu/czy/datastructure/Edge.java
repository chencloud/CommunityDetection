package edu.czy.datastructure;


/**
 * @author ZHANPEISEN
 * @date 2013-1-9
 */
public class Edge {
	private long sourceID;
	private long targetID;
	private double weight;
	private String label;
	/**
	 * Initialize an edge as default without any other information.
	 */
	public Edge(){
		this(-1L, -1L, 1D);
	}
	/**
	 * Initialize an edge as a undirected edge with default weight as 1.0.
	 * @param sourceID one end of the edge as default
	 * @param targetID another end of the edge as default
	 */
	public Edge(long sourceID, long targetID){
		this(sourceID, targetID, 1D);
	}
	/**
	 * Initialize an edge with given information.
	 * @param sourceID sourceID start point of the edge while it is directed, otherwise one end of the edge
	 * @param targetID end point of the edge while it is directed, otherwise one end of the edge
	 * @param weight which denotes the weight of the edge
	 */
	public Edge(long sourceID, long targetID, double weight){
		this.sourceID = sourceID;
		this.targetID = targetID;
		this.weight = weight;
		label = null;
	}
	/**
	 * @return the sourceID
	 */
	public long getSourceID() {
		return sourceID;
	}
	/**
	 * @param sourceID the sourceID to set
	 */
	public void setSourceID(long sourceID) {
		this.sourceID = sourceID;
	}
	/**
	 * @return the targetID
	 */
	public long getTargetID() {
		return targetID;
	}
	/**
	 * @param targetID the targetID to set
	 */
	public void setTargetID(long targetID) {
		this.targetID = targetID;
	}
	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public String getLabel(){
		return label;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	/**
	 * Show the information of the edge
	 */
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Edge with source ID ");
		sb.append(sourceID);
		sb.append(", target ID ");
		sb.append(targetID);
		sb.append(", weight ");
		sb.append(weight);
		return sb.toString();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (sourceID ^ (sourceID >>> 32));
		result = prime * result + (int) (targetID ^ (targetID >>> 32));
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (sourceID != other.sourceID)
			return false;
		if (targetID != other.targetID)
			return false;
		return true;
	}
}