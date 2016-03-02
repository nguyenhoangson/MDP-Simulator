/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AStar;

/**
 *
 * @author Tan Quang Ngo
 */
import AStar.PathFinder.Node;
import java.util.*;
import static Map.MapData.COLS;

/**
 * A* algorithm implementation using the method design pattern.
 *
 * @author Giuseppe Scrivano
 */

public abstract class A_Star<T> {

    private class Path implements Comparable {

        public T point;
        public Double f;
        public Double g;
        public Path parent;

        /**
         * Default c'tor.
         */
        public Path() {
            parent = null;
            point = null;
            g = f = 0.0;
        }

        /**
         * C'tor by copy another object.
         *
         * @param p The path object to clone.
         */
        public Path(Path p) {
            this();
            parent = p;
            g = p.g;
            f = p.f;
        }

        /**
         * Compare to another object using the total cost f.
         *
         * @param o The object to compare to.
         * @see Comparable#compareTo()
         * @return <code>less than 0</code> This object is smaller than
         * <code>0</code>; <code>0</code> Object are the same.
         * <code>bigger than 0</code> This object is bigger than o.
         */
        public int compareTo(Object o) {
            Path p = (Path) o;
            return (int) (f - p.f);
        }

        /**
         * Get the last point on the path.
         *
         * @return The last point visited by the path.
         */
        public T getPoint() {
            return point;
        }

        /**
         * Set the
         */
        public void setPoint(T p) {
            point = p;
        }
    }

    /**
     * Check if the current node is a goal for the problem.
     *
     * @param node The node to check.
     * @return <code>true</code> if it is a goal, <code>false</else> otherwise.
     */
    protected abstract boolean isGoal(T node);

    /**
     * Cost for the operation to go to <code>to</code> from <code>from</from>.
     *
     * @param from The node we are leaving.
     * @param to The node we are reaching.
     * @return The cost of the operation.
     */
    protected abstract Double g(T from, T to);

    /**
     * Estimated cost to reach a goal node. An admissible heuristic never gives
     * a cost bigger than the real one. <code>from</from>.
     *
     * @param from The node we are leaving.
     * @param to The node we are reaching.
     * @return The estimated cost to reach an object.
     */
    protected abstract Double h(T from, T to);

    /**
     * Generate the successors for a given node.
     *
     * @param node The node we want to expand.
     * @return A list of possible next steps.
     */
    protected abstract List<T> generateSuccessors(T node);
    protected T _start;
    protected T _goal;

    private PriorityQueue<Path> paths;
    private HashMap<Integer, Double> mindists;
    private Double lastCost;
    private int expandedCounter;

    /**
     * Check how many times a node was expanded.
     *
     * @return A counter of how many times a node was expanded.
     */
    public int getExpandedCounter() {
        return expandedCounter;
    }

    /**
     * Default c'tor.
     */
    public A_Star() {
        paths = new PriorityQueue<>();
        mindists = new HashMap<>();
        expandedCounter = 0;
        lastCost = 0.0;
    }

    /**
     * Total cost function to reach the node <code>to</code> from
     * <code>from</code>.
     *
     * The total cost is defined as: f(x) = g(x) + h(x).
     *
     * @param from The node we are leaving.
     * @param to The node we are reaching.
     * @return The total cost.
     */
    protected Double f(Path p, T from, T to) {
        Double g = g(from, to) + ((p.parent != null) ? p.parent.g : 0.0);
        Double h = h(from, to);

        p.g = g;
        p.f = g + h;

        return p.f;
    }

    /**
     * Expand a path.
     *
     * @param path The path to expand.
     */
    private void expand(Path path) {
        Node p = (Node) path.getPoint();
        int id = p.y*(COLS-2)+ p.x;
        Double min = mindists.get(id);

     
        /*
         * If a better path passing for this point already exists then
         * don't expand it.
         */
        if (min == null || min > path.f) {
            mindists.put(id, path.f);
        } else {
            return;
        }

        List<T> successors = generateSuccessors(path.getPoint());

        for (T t : successors) {
          //  int nId = ((Node)t).y*(COLS-2)+ ((Node)t).x;
           // Double oMin = mindists.get(nId);
    
            Path newPath = new Path(path);
            newPath.setPoint(t);
            double f = f(newPath, path.getPoint(), t);
          // if(oMin == null || oMin  > f)
          // {
           //    mindists.put(nId, newPath.f);
               paths.offer(newPath);
         //  }
           
        }

        expandedCounter++;
    }

    /**
     * Get the cost to reach the last node in the path.
     *
     * @return The cost for the found path.
     */
    public Double getCost() {
        return lastCost;
    }

    /**
     * Find the shortest path to a goal starting from <code>start</code>.
     *
     * @param start The initial node.
     * @return A list of nodes from the initial point to a goal,
     * <code>null</code> if a path doesn't exist.
     */
    public List<T> compute() {
        try {
            Path root = new Path();
            root.setPoint(_start);

            /* Needed if the initial point has a cost.  */
            f(root, _start, _start);

            expand(root);

            for (;;) {
                Path p = paths.poll();

                if (p == null ) {
                    lastCost = Double.MAX_VALUE;
                    return null;
                }

                T last = p.getPoint();

                lastCost = p.g;

                if (isGoal(last)) {
                    LinkedList<T> retPath = new LinkedList<T>();

                    for (Path i = p; i != null; i = i.parent) {
                        retPath.addFirst(i.getPoint());
                    }

                    return retPath;
                }
                expand(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
