package Controller;

import Model.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class LifeAlgorithmController extends AlgorithmController {
    @Override
    public AlgorithmState init(Graph origin, double... params) {
        LifeAlgorithmState state = new LifeAlgorithmState(origin);
        origin.fixNodeIDs();
        state.unionFind.makeSets(origin.nodeList);
        origin.calculateCoverages();
        state.edgesByCoverage = origin.edgeList.stream()
                .sorted(Comparator.comparing(e -> e.coverage))
                .collect(Collectors.toCollection(LinkedList::new));
        state.phase = LifeAlgorithmPhase.FINDING_EDGES;
        return state;
    }

    @Override
    public Model.AlgorithmState processState(AlgorithmState algorithmState) {
        LifeAlgorithmState nextState = (LifeAlgorithmState) algorithmState.clone();
        if (nextState.phase != LifeAlgorithmPhase.FINISHED) {
            Edge e = nextState.edgesByCoverage.pop();
            if (nextState.unionFind.find(e.left) != nextState.unionFind.find(e.right)) {
                nextState.edgesChosen.push(e);
                nextState.unionFind.union(e.left, e.right);
            }
            if (nextState.edgesByCoverage.isEmpty()) {
                nextState.phase = LifeAlgorithmPhase.FINISHED;
            }
        }
        return nextState;
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return ((LifeAlgorithmState)algorithmState).phase == LifeAlgorithmPhase.FINISHED;
    }
}
