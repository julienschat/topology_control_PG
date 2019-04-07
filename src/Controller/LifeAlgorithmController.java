package Controller;

import DataStructures.Edge;
import DataStructures.Graph;
import Model.*;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * LifeAlgorithmController is the controller for the LIFE algorithm. It implements the two functions init and processState
 * thereby defining the algorithms logic. With the help of a Union-Find datastructure the LIFE algorithm works identical to
 * the Kruskal algorithm finding a MST.
 */
public class LifeAlgorithmController extends AlgorithmController {
    /**
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
        state.nextPhase = LifeAlgorithmPhase.FINDING_EDGES;
        return state;
    }

    @Override
    protected Model.AlgorithmState processState(AlgorithmState algorithmState) {
        LifeAlgorithmState state = (LifeAlgorithmState) algorithmState;
        state.currentPhase = state.nextPhase;
        switch (state.nextPhase) {
            case FINDING_EDGES:
                state.currentEdge = state.edgesByCoverage.pop();
                if (state.unionFind.find(state.currentEdge.left) != state.unionFind.find(state.currentEdge.right)) {
                    state.nextPhase = LifeAlgorithmPhase.ADDING_EDGES;
                }
                if (state.edgesByCoverage.isEmpty()) {
                    state.nextPhase = LifeAlgorithmPhase.PREFINISHED;
                }
                break;
            case ADDING_EDGES:
                state.edgesChosen.push(state.currentEdge);
                state.unionFind.union(state.currentEdge.left, state.currentEdge.right);
                state.currentEdge = null;
                state.nextPhase = LifeAlgorithmPhase.FINDING_EDGES;
                break;
            case PREFINISHED:
                state.currentEdge = null;
                calculateFinishedNetwork(state);
                state.nextPhase = LifeAlgorithmPhase.FINISHED;
                break;
        }
        return state;
    }

    @Override
    public boolean isFinished(AlgorithmState algorithmState) {
        return ((LifeAlgorithmState) algorithmState).nextPhase == LifeAlgorithmPhase.FINISHED;
    }

    @Override
    public String getPhaseDescription(AlgorithmState state) {
        switch (((LifeAlgorithmState) state).currentPhase) {
            case FINDING_EDGES:
                return "Finding Edges";
            case ADDING_EDGES:
                return "Adding Edges";
            case FINISHED:
                return "Finished";
            default:
                return "";
        }
    }
}
