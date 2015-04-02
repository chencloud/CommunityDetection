package edu.czy.overlap.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.factorization.GraphNMF;
import edu.czy.factorization.GraphNMFNew;
import edu.czy.importance.NodeImportance;
import edu.czy.lpa.LPA;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
import edu.czy.utils.RandomNumGenerator;
import edu.uci.ics.jung.graph.SparseGraph;

/*
 * 同步更新
 * 随机顺序（模拟并行时的同步更新)
 * 当i(t-1)=i(t)时，停止迭代
 */
public class ONMFWLPA extends LPA{
	private int labelNum ;
	private double p;
	private boolean isNormal ;
	private static final double EPSION = 0.0000000001;
	private double lamda = 0.5;
	private Map<Integer,Vertex> node_map;
	public ONMFWLPA(SparseGraph<Vertex, Edge> g, int itera, double p, boolean isNormal) {
		super(g, itera, false);
		// TODO Auto-generated constructor stub
		this.p = p;
		this.isNormal = isNormal;
		this.node_map = new HashMap<Integer,Vertex>();
//		init();
		initNew();
	}
	public void initNew() {
		// TODO Auto-generated method stub
		int nodeCount = this.graph.getVertexCount();
		for(Vertex v:this.graph.getVertices()) {
			this.node_map.put(nodeCount, v);
			v.nodeInteger = nodeCount;
			--nodeCount;
		}
		//找到所有的极大点
		int k = 0;
		Set<Long> localnodeSet = new HashSet<Long>();
		for(Vertex v:this.graph.getVertices()) {
			boolean isLocalCenter = true;
			for(Vertex neighV:this.graph.getNeighbors(v)) {
				if(this.graph.degree(neighV)>this.graph.degree(v)) {
					isLocalCenter = false;
				} 
				else if(this.graph.degree(neighV)== this.graph.degree(v)&&localnodeSet.contains(neighV.getId())) {
					isLocalCenter = false;
				}
			}
			if(isLocalCenter) {
				k += 1;
				localnodeSet.add(v.getId());
			}
		}
		if( k<=1 )k = 2;
		localnodeSet.clear();localnodeSet = null;
		System.out.println("localVertex k="+k);
		NMFFactorizationNew(graph,this.node_map,k,20,1);
	}
	private void NMFFactorizationNew(SparseGraph<Vertex, Edge> graph,
			Map<Integer, Vertex> nodeMap, int k, int iteration, int iterationError) {
		// TODO Auto-generated method stub
		GraphNMFNew nmf = new GraphNMFNew(graph,nodeMap,k,iteration,iterationError);
		nmf.trainSymmetric(true);
		double[][] U = nmf.getU();
		for(int i=0;i<U.length;i++) {
			Vertex v = this.node_map.get(i+1);
			List<Double> ws = new ArrayList<Double>();
			for(int j=0;j<U[i].length;j++)
				ws.add(U[i][j]);
			v.setWeight(ws);
		}
	}
	@Override
	public void init() {
		// TODO Auto-generated method stub
		int nodeCount = this.graph.getVertexCount();
		Map<Vertex,Integer> map_node = new HashMap<Vertex,Integer>();
		for(Vertex v:this.graph.getVertices()) {
			this.node_map.put(nodeCount, v);
			map_node.put(v,nodeCount);
			--nodeCount;
		}
		//build the adjMatrix
		nodeCount = this.graph.getVertexCount();
		double[][] adjMatrix = new double[nodeCount][nodeCount];
		//init all element to zero;
		for(int i=0;i<nodeCount;i++)
			for(int j=0;j<nodeCount;j++)
				adjMatrix[i][j]=0.0;
		for(Vertex v:this.graph.getVertices()) {
			for(Vertex neighV:this.graph.getNeighbors(v)) {
				adjMatrix[map_node.get(v)-1][map_node.get(neighV)-1]=this.graph.findEdge(v, neighV).getWeight();
			}
		}
		//clear map_node
		map_node.clear();map_node = null;
		//找到所有的极大点
		int k = 0;
		for(Vertex v:this.graph.getVertices()) {
			boolean isLocalCenter = true;
			for(Vertex neighV:this.graph.getNeighbors(v)) {
				if(this.graph.degree(neighV)>this.graph.degree(v)) {
					isLocalCenter = false;
				}
			}
			if(isLocalCenter)k += 1;
		}
		if(k<=1)k = 2;
		System.out.println("localVertex k="+k);
		NMFFactorization(adjMatrix,k,100,5);
	}
	private void NMFFactorization(double[][] adjMatrix,int k,int iteration,int iterationError) {
		// TODO Auto-generated method stub
		GraphNMF nmf = new GraphNMF(adjMatrix,k,iteration,iterationError);
		nmf.trainSymmetric(true);
		double[][] U = nmf.getU().getArrayCopy();
		for(int i=0;i<U.length;i++) {
			Vertex v = this.node_map.get(i+1);
			List<Double> ws = new ArrayList<Double>();
			for(int j=1;j<U[i].length;j++)
				ws.add(U[i][j]);
			v.setWeight(ws);
		}
	}
	@Override
	public void run() {
		run(this.p);
	}
	public void run(Double p) {
		if(null!=p)this.p = p.doubleValue();
		//不采用节点里面的value,采用communityDistribution
		Vertex[] vs = this.graph.getVertices().toArray(new Vertex[0]);
		for(int i=0;i<vs.length;i++) {
			vs[i].setValue(null);
			vs[i].getCommunityDistribution().clear();
			vs[i].getCommunityDistribution().put(vs[i].getId(), 1.0);
		}
		this.labelNum = vs.length;
		//Vertex[] vs = this.graph.getVertices().toArray(new Vertex[0]);	
		for(int i=1;i<=this.iteration;i++) {
//			System.out.println("the "+i+"th iteration");
			
			if(!updatelabel(vs)){
					break;
				}
		}
		
//		//后处理步骤，合并社區
		Map<Long,Collection<Vertex>> coms = new HashMap<Long,Collection<Vertex>>();
		
		for(int i=0;i<vs.length;i++) {
//			System.out.println(vs[i].getId()+":"+vs[i].getCommunityDistribution().size());
			for(Entry<Long,Double> entry:vs[i].getCommunityDistribution().entrySet()) {
				Long comId = entry.getKey();
				if(!coms.containsKey(comId)) {
					coms.put(comId, new ArrayList<Vertex>());
				}
				coms.get(comId).add(vs[i]);
			}
		}
		//删除最坏社区
		List<Long> tempDelete = new ArrayList<Long>();
		for(Entry<Long,Collection<Vertex>> entry:coms.entrySet()) {
			if(entry.getValue().size() == vs.length)
				tempDelete.add(entry.getKey());
		}
		if(!tempDelete.isEmpty()){
			for(Long delId:tempDelete){
				coms.remove(delId);
			}
		}
		List<Entry<Long,Collection<Vertex>>> comms = new ArrayList<Entry<Long,Collection<Vertex>>>(coms.entrySet());
		System.out.println("before Community Size="+comms.size());
		for(int i=0;i<comms.size();i++) {
			for(int j=i+1;j<comms.size();) {
				boolean isSame = true;
				if(comms.get(i).getValue().size()>=comms.get(j).getValue().size()){
					for(Vertex v :comms.get(j).getValue()) {
						boolean isISame =false;
						for(Vertex v1:comms.get(i).getValue()){
							if(v.getId()==v1.getId()){
								isISame = true;
								break;
							}
						}
						if(!isISame) {
							isSame = false; 
							break;
						}
					}
				}else {
					for(Vertex v :comms.get(i).getValue()) {
						boolean isISame =false;
						for(Vertex v1:comms.get(j).getValue()){
							if(v.getId()==v1.getId()){
								isISame = true;
								break;
							}
						}
						if(!isISame) {
							isSame = false;
							break;
						}
					}
				}
//				System.out.println(isSame);
				if(isSame){
					if(comms.get(i).getValue().size()<comms.get(j).getValue().size()){
						comms.get(i).getValue().clear();
						comms.get(i).getValue().addAll(comms.get(j).getValue());
					}
//					System.out.println(comms.get(j).getKey());
					comms.remove(j);
					j= i+1;
				}
				else {
					j++;
				}
			}
		}
		System.out.println("Cur Community Size="+comms.size());
		
		//更新value值
		for(int i=0;i<comms.size();i++) {
			Long ComId = comms.get(i).getKey();
			for(Vertex v:comms.get(i).getValue()) {
				if(null == v.getValue() ||"".equals(v.getValue())){
					v.setValue(String.valueOf(ComId));
				}
				else {
					v.setValue(v.getValue()+GraphUtils.OverlapNode_Split+String.valueOf(ComId));
				}
			}
		}
		
		//处理孤立点
		
	}
	@Override
	public boolean updatelabel(Vertex[] v) {
		// TODO Auto-generated method stub
		//保存上一次的节点标签结果
		Map<Long,Map<Long,Double>> node_com_map = new HashMap<Long,Map<Long,Double>>();
		for(int i=0;i<v.length;i++) {
			node_com_map.put(v[i].getId(), new HashMap<Long,Double>());
			node_com_map.get(v[i].getId()).putAll(v[i].getCommunityDistribution());
		}
		Set<Long> labelSet = new HashSet<Long>();
		for(int i=0;i<v.length;i++) {
			Vertex listener = v[i];
			HashMap<Long, Double> incomingVotes = new HashMap<Long, Double>();
			for(Vertex neigV : this.graph.getNeighbors(listener)) {
				for(Entry<Long,Double> c:node_com_map.get(neigV.getId()).entrySet()){
					//
					double node_importance = NodeImportance.getSmoothDegreeImportance(graph, neigV);
//					
					double node_difference = GraphUtils.calcalueEducianSimilarity(listener, neigV);
					
					double community_blong = c.getValue();
					
					double CommunityValue = 0;
					CommunityValue += Math.sqrt((lamda*node_difference+(1-lamda)*node_importance)
							*community_blong);
					
					if ( incomingVotes.containsKey(c.getKey()) ) 
						CommunityValue += incomingVotes.get(c.getKey());
					
					incomingVotes.put(c.getKey(), CommunityValue);
				}
				
			}
			
			//Find the most popular vote
			Iterator<Entry<Long, Double>> it = incomingVotes.entrySet().iterator();
			double popularCommunityValue = Double.MIN_VALUE;
			double unpopularCommunityValue =  Double.MAX_VALUE;
			while ( it.hasNext() ) {
				Entry<Long, Double> entry = it.next();
				if ( entry.getValue() > popularCommunityValue ) {
					popularCommunityValue = entry.getValue();
				}
				if(entry.getValue() < unpopularCommunityValue) {
					unpopularCommunityValue = entry.getValue();
				}
			}
			double throld_c_max = (popularCommunityValue-unpopularCommunityValue)*this.p;
			Collection<Entry<Long, Double>> cols = new ArrayList<Entry<Long, Double>>();
			cols.addAll(incomingVotes.entrySet());
//			System.out.println("Before\t"+listener.getId()+"\t"+incomingVotes.size());
			for ( Entry<Long, Double> col:cols ) {
				if ( (popularCommunityValue-col.getValue()) > throld_c_max ) {
						incomingVotes.remove(col.getKey());
				}
			}
//			System.out.println(listener.getId()+"\t"+incomingVotes.size());
			//Update community distribution of the current node by 1
			Iterator<Entry<Long, Double>> filteredit = incomingVotes.entrySet().iterator();
			listener.getCommunityDistribution().clear();
			double sumValue = 0.0;
			while ( filteredit.hasNext() ) {
				Entry<Long, Double> entry = filteredit.next();
				sumValue += entry.getValue();
			}
			filteredit = incomingVotes.entrySet().iterator();
			while ( filteredit.hasNext() ) {
				Entry<Long, Double> entry = filteredit.next();
				if(this.isNormal)
					listener.updateCommunityDistribution(entry.getKey(), entry.getValue()/sumValue);
				else 
					listener.updateCommunityDistribution(entry.getKey(), entry.getValue());
				labelSet.add(entry.getKey());
			}
			
		}
		if(labelSet.size() == this.labelNum)
			return false;
		else {
			this.labelNum = labelSet.size();
			return true;
		}
	}
	public static void main(String args[]) {
		String filename="E:\\dataset\\unweight_dataset\\karate\\karate.gml";
//		String filename="E:\\dataset\\unweight_dataset\\dolphins\\dolphins.gml";
//		String filename="E:\\dataset\\unweight_dataset\\adjnoun\\adjnoun.gml";
		SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(filename);
		double p=0.1;
		double maxQov = 0.0;
		double maxp = 0.0;
		ONMFWLPA onmfwlpa = new ONMFWLPA(graph,1000,p,false);
		for(;p<=0.4;p+=0.01)
		{
			
			onmfwlpa.run(p);
			Collection<Collection<Vertex>> coms = onmfwlpa.getCommunitysByVertex();
	//		GraphUtils.PrintCommunityCollectionsWithVertex(coms, ";");
			double Qov = MeasureCollections.calculateQovWithVertex(coms, graph);
			if(Qov > maxQov){
				maxQov = Qov;
				maxp = p;
			}
			System.out.println("p="+p+" ;Modularity Qov = "+Qov);
		}
		System.out.println("max p="+maxp+" ;Maximun Modularity Qov = "+maxQov);
	}
}
