package edu.czy.lpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Random;

import edu.czy.datastructure.Edge;
import edu.czy.datastructure.Vertex;
import edu.czy.factorization.GraphNMF;
import edu.czy.factorization.GraphNMFNew;
import edu.czy.importance.NodeImportance;
import edu.czy.load.LoadGML;
import edu.czy.measure.MeasureCollections;
import edu.czy.utils.GraphUtils;
import edu.czy.utils.RandomNumGenerator;
import edu.uci.ics.jung.graph.SparseGraph;
/*
 * 考虑各种不同的衡量权重的方法
 * wij_1 = dij/(1+dij)
 * wij_2 = factor_sim
 * wij_3 = (r*wij_1+(1-r)*wij_2)
 * wij_4 = sqrt(wij_1*wij_2);
 * wij_5 = sqrt(wij_x*blong_cj);
 * wij_6 = r*wij_4 + (1-r)*blong_cj;
 */
public class DIFFWLPA extends LPA{
	private static final double EPSION = 0.0000000001;
	private Map<Integer,Vertex> node_map;
	private int labelNum ;
	private boolean isAsyncUpdate ;
	private boolean isRandomOrder;
	private double lamda = 0.5;
	public DIFFWLPA(SparseGraph<Vertex,Edge> g,int iteration,boolean isAsyncUpdate,boolean isRandomOrder)
	{
		super(g,iteration,false);
		this.graph=g;
		this.iteration=iteration;
		this.node_map = new HashMap<Integer,Vertex>();
		this.isAsyncUpdate = isAsyncUpdate;
		this.isRandomOrder = isRandomOrder;
		//init();
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
		NMFFactorizationNew(graph,this.node_map,k,100,1);
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
		U = null;
	}
	private void initNodeLabel() {
		// TODO Auto-generated method stub
		this.labelNum = this.graph.getVertexCount();
		for(Vertex v:this.graph.getVertices()) {
			v.getCommunityDistribution().put(v.getId(), 1.0);
		}
	}
	@Override
	public void run(){
		initNodeLabel();
		//Step Two
		for(int iter = 1;iter <= this.iteration; iter++) {
			System.out.println("the current iteration:"+iter);
			Vertex[] nodes=this.graph.getVertices().toArray(new Vertex[0]);
			if(this.isRandomOrder) {
				int nodecount=nodes.length;
				Random rand=new Random();
				//shuffle card
				for(int i=--nodecount;i>=0;i--)
				{
					int index=rand.nextInt(i+1);
					Vertex tempV=nodes[index];
					nodes[index]=nodes[i];
					nodes[i]=tempV;
				}
			}
			if(this.isAsyncUpdate){
				if(!updatelabel(nodes)){
					break;
				}
			} else {
				if(!updatelabelSync(nodes)){
					break;
				}
			}
			
		}
		
		//postProcess
		for(Vertex v:this.graph.getVertices()) {
			v.setValue(String.valueOf(v.getCommunityDistribution().entrySet().iterator().next().getKey()));
		}
	}
	


	private boolean updatelabelSync(Vertex[] v) {
		// TODO Auto-generated method stub
		boolean isUpdated = false;
		//保存上一次的节点标签结果
		Map<Long,Map<Long,Double>> node_com_map = new HashMap<Long,Map<Long,Double>>();
		if(!this.isAsyncUpdate) {
			for(int i=0;i<v.length;i++) {
				node_com_map.put(v[i].getId(), new HashMap<Long,Double>());
				node_com_map.get(v[i].getId()).putAll(v[i].getCommunityDistribution());
			}
		}
		Set<Long> labelSet = new HashSet<Long>();
		//Update the label of each vertex according to its neigbors' label
		Map<Long,Double> hashmap = new HashMap<Long,Double>();
		double maxValue=0;
		for(int i=0;i<v.length;i++)
		{
			Vertex curV = v[i];
			Collection<Vertex> Vneighbors = this.graph.getNeighbors(curV);
			Long curLabel = node_com_map.get(curV.getId()).entrySet().iterator().next().getKey();
			maxValue = 0.0;
			hashmap.clear();
			for(Vertex neigV : Vneighbors)
			{
				//只有一个标签，及标签属性
				Entry<Long,Double> comEntry = node_com_map.get(neigV.getId()).entrySet().iterator().next();
				Long Vlabel = comEntry.getKey();
				Double Vlabel_value = comEntry.getValue();
				if(!hashmap.containsKey(Vlabel) ) {
					hashmap.put(Vlabel, 0.0);
				}
				{	
					double node_importance = NodeImportance.getSmoothDegreeImportance(graph, neigV);
//					double node_importance = NodeImportance.getPageRankImportance(graph, v);
					double node_difference = GraphUtils.calcalueEducianSimilarity(curV, neigV);
					double community_blong = 
						this.getCommunityBlong(neigV, Vlabel, node_com_map);
//							Vlabel_value/(1.0+Vlabel_value);
					double count=hashmap.get(Vlabel);
//					count +=community_blong *Math.sqrt(node_difference *
//								node_importance);
//					count += Math.sqrt((this.lamda*node_difference +(1.0-this.lamda)*node_importance)
//							);//*community_blong);
//					count += node_importance;
					count += node_difference;
//					count += community_blong;
					hashmap.put(Vlabel, count);
//					System.out.println(Vlabel + ":" + count);
				}
				if(hashmap.get(Vlabel)-maxValue>0.0)
				{
					maxValue=hashmap.get(Vlabel);
				}
			}
			List<Long> arrayLabels = new ArrayList<Long>();
			for(Long label:hashmap.keySet())
			{
				if(Math.abs(hashmap.get(label)-maxValue) <= EPSION)
					arrayLabels.add(label);
			}
			//random choose one
			if(arrayLabels.size() > 1)
			{
				System.out.println("Vertex "+curV.getId()+" multiLabel Occur");
				int rIndex=RandomNumGenerator.getRandomInt(arrayLabels.size());
				if(curLabel == arrayLabels.get(rIndex)){
					double vCount = 1;
					vCount += curV.getCommunityDistribution().get(curLabel);
					curV.getCommunityDistribution().put(curLabel, vCount);
				}
				else {
					curV.getCommunityDistribution().clear();
					curV.getCommunityDistribution().put(arrayLabels.get(rIndex), 1.0);
					isUpdated = true;
				}
			}else {
				if(curLabel == arrayLabels.get(0)){
					double vCount = 1;
					vCount += curV.getCommunityDistribution().get(curLabel);
					curV.getCommunityDistribution().put(curLabel, vCount);
				}
				else {
					curV.getCommunityDistribution().clear();
					curV.getCommunityDistribution().put(arrayLabels.get(0), 1.0);
					isUpdated = true;
				}
			}
		}
//		if(labelSet.size() == this.labelNum)
//			return false;
//		else {
//			this.labelNum = labelSet.size();return true;
//		}
		return isUpdated;
	}
	@Override
	public boolean updatelabel(Vertex[] v) {
		// TODO Auto-generated method stub
		boolean isUpdated = false;

		//Update the label of each vertex according to its neigbors' label
		Map<Long,Double> hashmap = new HashMap<Long,Double>();
		double maxValue=0;
		for(int i=0;i<v.length;i++)
		{
			Vertex curV = v[i];
			Collection<Vertex> Vneighbors = this.graph.getNeighbors(curV);
			Long curLabel = curV.getCommunityDistribution().entrySet().iterator().next().getKey();
			maxValue = Double.MIN_VALUE;
			hashmap.clear();
			for(Vertex neigV : Vneighbors)
			{
				//只有一个标签，及标签属性
				Entry<Long,Double> comEntry = neigV.getCommunityDistribution().entrySet().iterator().next();
				Long Vlabel = comEntry.getKey();
				Double Vlabel_value = comEntry.getValue();
				if(!hashmap.containsKey(Vlabel) ) {
					hashmap.put(Vlabel, 0.0);
				}
				{	
//					double node_importance = NodeImportance.getSmoothDegreeImportance(graph, neigV);
//					double node_importance = NodeImportance.getPageRankImportance(graph, v);
					double node_difference = GraphUtils.calcalueEducianSimilarity(curV, neigV);
//					double community_blong = 
//							this.getCommunityBlong(neigV, Vlabel);
//								Vlabel_value/(1.0+Vlabel_value);
					double count=hashmap.get(Vlabel);
//						count += this.lamda*(node_difference)+(1.0-lamda)*(node_importance);
//					count += ((this.lamda*node_difference +(1.0-this.lamda)*node_importance)*community_blong);
//						count += node_importance;
						count += node_difference;
//						count += community_blong;
					hashmap.put(Vlabel, count);
//					System.out.println(Vlabel + ":" + count);
				}
//				System.out.println("Vlabel value="+hashmap.get(Vlabel));
				if(hashmap.get(Vlabel)-maxValue>0.0)
				{
					maxValue = hashmap.get(Vlabel);
				}
			}
			
			List<Long> arrayLabels = new ArrayList<Long>();
			for(Long label:hashmap.keySet())
			{
				if(Math.abs(hashmap.get(label)-maxValue) <= EPSION)
					arrayLabels.add(label);
			}
//			System.out.println(maxValue);
			//random choose one
			if(arrayLabels.size() > 1)
			{
//				System.out.println("Vertex "+curV.getId()+" multiLabel Occur");
				int rIndex=RandomNumGenerator.getRandomInt(arrayLabels.size());
				if(curLabel == arrayLabels.get(rIndex)){
					double vCount = 1;
					vCount += curV.getCommunityDistribution().get(curLabel);
					curV.getCommunityDistribution().put(curLabel, vCount);
				}
				else {
					curV.getCommunityDistribution().clear();
					curV.getCommunityDistribution().put(arrayLabels.get(rIndex), 1.0);
					isUpdated = true;
				}
			}else {
				if(curLabel == arrayLabels.get(0)){
					double vCount = 1;
					vCount += curV.getCommunityDistribution().get(curLabel);
					curV.getCommunityDistribution().put(curLabel, vCount);
				}
				else {
					curV.getCommunityDistribution().clear();
					curV.getCommunityDistribution().put(arrayLabels.get(0), 1.0);
					isUpdated = true;
				}
			}
		}
		return isUpdated;
	}
	public boolean updatelabelOlder(Vertex[] v) {
		// TODO Auto-generated method stub
		boolean isUpdated = false;

		//Update the label of each vertex according to its neigbors' label
		Map<Long,Double> hashmap = new HashMap<Long,Double>();
		Map<Long,Double> hashmap_all = new HashMap<Long,Double>();
		Map<Long,Double> hashmap_node = new HashMap<Long,Double>();
		Map<Long,Double> hashmap_com = new HashMap<Long,Double>();
		double maxValue=0;
		for(int i=0;i<v.length;i++)
		{
			Vertex curV = v[i];
			Collection<Vertex> Vneighbors = this.graph.getNeighbors(curV);
			Long curLabel = curV.getCommunityDistribution().entrySet().iterator().next().getKey();
			maxValue = 0.0;
			hashmap.clear();
			hashmap_node.clear();
			hashmap_com.clear();
			hashmap_all.clear();
			for(Vertex neigV : Vneighbors)
			{
				//只有一个标签，及标签属性
				Entry<Long,Double> comEntry = neigV.getCommunityDistribution().entrySet().iterator().next();
				Long Vlabel = comEntry.getKey();
				Double Vlabel_value = comEntry.getValue();
				if(!hashmap.containsKey(Vlabel) ) {
					hashmap.put(Vlabel, 0.0);
					hashmap_node.put(Vlabel, 0.0);
					hashmap_all.put(Vlabel, 0.0);
					hashmap_com.put(Vlabel, 0.0);
				}
				{	
					double node_importance = NodeImportance.getSmoothDegreeImportance(graph, neigV);
//					double node_importance = NodeImportance.getPageRankImportance(graph, v);
					double node_difference = GraphUtils.calcalueEducianSimilarity(curV, neigV);
					double community_blong = this.getCommunityBlong(neigV, Vlabel);
					double node_influence = Math.sqrt(node_difference*node_importance);
					double count = hashmap_all.get(Vlabel)+node_influence*community_blong;
					hashmap_all.put(Vlabel, count);
//					System.out.println(Vlabel + ":" + count);
					hashmap.put(Vlabel,hashmap.get(Vlabel)+1);
					hashmap_node.put(Vlabel,hashmap_node.get(Vlabel)+node_influence);
					hashmap_com.put(Vlabel,hashmap_com.get(Vlabel)+community_blong);
				}
				if(hashmap.get(Vlabel)-maxValue>0.0)
				{
					maxValue=hashmap.get(Vlabel);
				}
			}
			List<Long> arrayLabels = new ArrayList<Long>();
			for(Long label:hashmap.keySet())
			{
				if(Math.abs(hashmap.get(label)-maxValue) <= EPSION)
					arrayLabels.add(label);
			}
			//random choose one
			if(arrayLabels.size() > 1)
			{
				System.out.println("Vertex "+curV.getId()+" multiLabel Occur");
				double max_all = Double.MIN_VALUE;
				Long label = 0L;
				int count = 0;
				for(int ii=0;ii<arrayLabels.size();ii++) {
					if(hashmap_all.get(arrayLabels.get(ii)) > max_all){
						max_all= hashmap_all.get(arrayLabels.get(ii));
						label = arrayLabels.get(ii);
						count = 1;
					} else if(Math.abs(hashmap_all.get(arrayLabels.get(ii)) - max_all)<this.EPSION){
						count += 1;
					}
				}
				if(count>1)System.out.println("Vertex all"+curV.getId()+" multiLabel Occur");
//				int rIndex=RandomNumGenerator.getRandomInt(arrayLabels.size());
//				label = arrayLabels.get(rIndex);
				if(curLabel == label){
					double vCount = 1;
					vCount += curV.getCommunityDistribution().get(curLabel);
					curV.getCommunityDistribution().put(curLabel, vCount);
				}
				else {
					curV.getCommunityDistribution().clear();
					curV.getCommunityDistribution().put(label, 1.0);
					isUpdated = true;
				}
			}else {
				if(curLabel == arrayLabels.get(0)){
					double vCount = 1;
					vCount += curV.getCommunityDistribution().get(curLabel);
					curV.getCommunityDistribution().put(curLabel, vCount);
				}
				else {
					curV.getCommunityDistribution().clear();
					curV.getCommunityDistribution().put(arrayLabels.get(0), 1.0);
					isUpdated = true;
				}
			}
		}
		return isUpdated;
	}
	private double getCommunityBlong(Vertex neigV, Long vlabel) {
		// TODO Auto-generated method stub
		double community_blong = 1.0;
		for(Vertex vv : graph.getNeighbors(neigV)) {
			Entry<Long,Double> comEntry_vv = vv.getCommunityDistribution().entrySet().iterator().next();
			if(comEntry_vv.getKey() == vlabel){
				community_blong += 1;
			}
		}
		community_blong = (community_blong+1)/(1.0+this.graph.degree(neigV));
		return community_blong;
	}
	public double getCommunityBlong(Vertex neigV,Long Vlabel,Map<Long,Map<Long,Double>> node_com_map){
		double community_blong = 1.0;
		for(Vertex vv : graph.getNeighbors(neigV)) {
			Entry<Long,Double> comEntry_vv = node_com_map.get(vv.getId()).entrySet().iterator().next();
			if(comEntry_vv.getKey() == Vlabel){
				community_blong += 1;
			}
		}
		community_blong = (community_blong+1)/(1.0+this.graph.degree(neigV));
		return community_blong;
	}
	

	//random choose one
	//System.out.println(arrayLabels.size());
//	if(arrayLabels.size()>1){
//		System.out.println("Vertex "+curV.getId()+" multiLabel Occur");
//		for(Vertex v:arrayLabels){
//			System.out.print(v.getId()+";");
//		}
//		System.out.println();
//		ArrayList<Vertex> max_V = new ArrayList<Vertex>();
//		double max_Value = 0.0;
//		boolean isExistedOriginLabel = false;
//		for(Vertex v:arrayLabels){
//			if(v.getValue().trim().equals(curV.getValue().trim())){
//				isExistedOriginLabel = true;
//				break;
//			}
//			double node_importance = NodeImportance.getSmoothDegreeImportance(graph, v);
//			double interest_sim_importance = Math.sqrt(GraphUtils.calcalueEducianSimilarity(curV, v)*node_importance);
//			if(max_Value<interest_sim_importance){
//				max_Value = interest_sim_importance;
//				max_V.clear();max_V.add(v);
//			} else if(Math.abs(interest_sim_importance-maxValue)<=EPSION){
//				max_V.add(v);
//			}
//		}
//		if(!isExistedOriginLabel && max_V.size() >0){
//			if(max_V.size()>1)System.out.println("sub multilabel occure");
//			curV.setValue(max_V.get(0).getValue());
//			isUpdated=true;
//		}
//	} else {
//		curV.setValue(arrayLabels.get(0).getValue());
//		isUpdated=true;
//	}
	public static void main(String[] args) {
//		String filename="E:\\dataset\\unweight_dataset\\adjnoun\\adjnoun.gml";
//		String filename="E:\\dataset\\unweight_dataset\\Karate\\Karate.gml";
//		String filename="E:\\dataset\\unweight_dataset\\dolphins\\dolphins.gml";
		String filename="E:\\dataset\\unweight_dataset\\toy_network\\toy_network.net";
		SparseGraph<Vertex,Edge> graph=GraphUtils.loadFileToGraph(filename);
		LPA nmfwlpa = new NMFWLPA(graph,1000,true,true);
		nmfwlpa.run();
		Collection<Collection<Vertex>> coms = nmfwlpa.getCommunitysByVertex();
		Collection<Collection<Integer>> comF = nmfwlpa.getCommunitysByInteger();
//		Collection<Collection<Integer>> comR = GraphUtils.exportCommunityGroundTruthCollection(graph);
		GraphUtils.PrintCommunityCollections(comF, ";");
//		GraphUtils.PrintCommunityCollections(comR, ",");
		double Q=MeasureCollections.calculateQFromCollectionsWithVertex(graph, coms);
//		double NMI = MeasureCollections.calculateNMI(comF, comR, graph.getVertexCount());
		System.out.println("Modularrity Q="+Q);
//		System.out.println("NMI = "+NMI);
	}
}
