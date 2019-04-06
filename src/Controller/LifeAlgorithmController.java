package Controller;

import Model.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 *
 */
public class LifeAlgorithmController extends AlgorithmController {
    /**
     *
     * @param origin intial graph on which the algorithm runs
     * @return initial state
     */
    @Override
    public AlgorithmState init(Graph origin) {
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
    protected Model.AlgorithmState processState(AlgorithmState algorithmState) {
        LifeAlgorithmState state = (LifeAlgorithmState) algorithmState;
        if (state.phase != LifeAlgorithmPhase.FINISHED) {
            Edge e = state.edgesByCoverage.pop();
            if (state.unionFind.find(e.left) != state.unionFind.find(e.right)) {
                state.edgesChosen.push(e);
                state.unionFind.union(e.left, e.right);
            }
            if (state.edgesByCoverage.isEmpty()) {
                state.phase = LifeAlgorithmPhase.FINISHED;
            }
        }
        return state;
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return ((LifeAlgorithmState)algorithmState).phase == LifeAlgorithmPhase.FINISHED;
    }

    @Override
    public String getPhaseDescription(AlgorithmState state) {
        switch (((LifeAlgorithmState)state).phase) {
            case FINDING_EDGES:
                return "Finding Edges";
            case FINISHED:
                return "Finished";
            default:
                return "";
        }
    }
}
