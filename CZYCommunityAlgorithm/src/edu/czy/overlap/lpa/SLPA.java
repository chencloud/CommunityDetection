package edu.czy.overlap.lpa;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.lpa.LPA;
import edu.uci.ics.jung.graph.SparseGraph;

/*
 * 采用异步更新策略
 */
public class SLPA extends LPA{

	public SLPA(SparseGraph<Vertex, Edge> g, int itera) {
		super(g, itera);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void run() {
		
		//后处理步骤
	}
	@Override
	public boolean updatelabel(Vertex[] v) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void postProcess() {
		
	}
}
