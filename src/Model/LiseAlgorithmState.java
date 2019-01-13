package Model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LiseAlgorithmState extends AlgorithmState {

    public LinkedList<Edge> edgesSortedByCoverage = new LinkedList<Edge>();

    public Edge currentEdgeMinCoverage,currentEdgeMaxCoverage;
    public Graph origin,newTSpannerGraph;
    public double tSpannerMeasure;
    public LiseAlgorithmPhase phase;

    public LiseAlgorithmState(Graph _origin,double tSpannerMeasure){
        super(_origin);
        this.tSpannerMeasure = tSpannerMeasure;
        edgesChosen = new LinkedList<Edge>();

    }

    public List<Node> getCurrentNodes(){
        return newTSpannerGraph.nodeList;
    }
}
