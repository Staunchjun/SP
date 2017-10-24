package RecommendPath;

import cern.colt.list.IntArrayList;
import cern.colt.map.AbstractIntDoubleMap;
import cern.colt.map.OpenIntDoubleHashMap;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.jet.math.Functions;
import weka.core.*;

import java.util.*;

public class MySpectrak {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The points of the dataset
     */
	/* protected DoubleMatrix2D v; */
    protected ArrayList<ScDataPoint> data;
    /**
     * The class number of each point in the dataset
     */
    public int[] cluster;
    /**
     * The number of different clusters found
     */
    protected int numOfClusters = 0;
    /**
     * The alpha star parameter value
     */
    protected double alphaStar = 0.5;
    /**
     * The distance cut factor
     */
    protected double r = -1;
    /**
     * The sigma scaling factor
     */
    protected double sigma = 1.0;

    /**
     * The using sparse matrix flag
     */
    protected boolean useSparseMatrix = false;

    /**
     * Algorithm's option list
     */
    protected final static Collection<Option> OPTIONS = Arrays
            .asList(
                    new Option("\tAlpha star. (default = 0.5).", "A", 1,
                            "-A <0-1>"),
                    new Option("\tSigma. (default = 1.0).", "S", 1, "-S <num>"),
                    new Option(
                            "\tR. All points that are far away more than this value have a zero similarity. (default = -1).",
                            "R", 1, "-R <num>"),
                    new Option(
                            "\tUse sparse matrix representation. (default = false).",
                            "M", 0, "-M"),
                    new Option("\tDistance function to use.\n"
                            + "\t(default: weka.core.EuclideanDistance)", "D",
                            1, "-D <classname and options>"));


    /**
     * Merges two sets of points represented as integer vectors. The sets are
     * not overlapped.
     *
     * @param a
     *            the first set of points
     * @param b
     *            the second set of points
     * @return the union of the two sets
     */
    protected int[] merge(final int[] a, final int[] b) {
        final int[] c = new int[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * Computes the association degree between two partitions of a graph.<br>
     * The association degree is defined as the sum of the weights of all the
     * edges between points of the two partitions.
     *
     * @param W
     *            the weight matrix of the graph
     * @param a
     *            the points of the first partition
     * @param b
     *            the points of the second partition
     * @return the association degree
     */
    protected static double asso(final DoubleMatrix2D W, final int[] a,
                                 final int[] b) {
        return W.viewSelection(a, b).zSum();
    }

    /**
     * Returns the normalized association degree between two partitions of a
     * graph.
     *
     * @param W
     *            the weight matrix of the graph
     * @param a
     *            the points of the first partition
     * @param b
     *            the points of the second partition
     * @return the normalized association degree
     */
    protected double Nasso(final DoubleMatrix2D W, final int[] a, final int[] b) {
        return Nasso(W, a, b, merge(a, b));
    }

    /**
     * Returns the normalized association degree between two partitions of a
     * graph w.r.t. a given subgraph.
     *
     * @param W
     *            the weight matrix of the graph
     * @param a
     *            the points of the first partition
     * @param b
     *            the points of the second partition
     * @param c
     *            the points of the subgraph
     * @return the normalized association degree
     */
    protected double Nasso(final DoubleMatrix2D W, final int[] a,
                           final int[] b, final int[] c) {
        return asso(W, a, a) / asso(W, a, c) + asso(W, b, b) / asso(W, b, c);
    }

    /**
     * Returns the normalized dissimilarity degree (or cut) between two
     * partitions of a graph.
     *
     * @param W
     *            the weight matrix of the graph
     * @param a
     *            the points of the first partition
     * @param b
     *            the points of the second partition
     * @return the normalized cut
     */
    protected double Ncut(final DoubleMatrix2D W, final int[] a, final int[] b) {
        return 2 - Nasso(W, a, b);
    }

    /**
     * Returns the normalized dissimilarity degree (or cut) between two
     * partitions of a graph w.r.t. a given subgraph.
     *
     * @param W
     *            the weight matrix of the graph
     * @param a
     *            the points of the first partition
     * @param b
     *            the points of the second partition
     * @param c
     *            the points of the subgraph.
     * @return the normalized cut
     */
    protected double Ncut(final DoubleMatrix2D W, final int[] a, final int[] b,
                          final int[] c) {
        return 2 - Nasso(W, a, b, c);
    }

    /**
     * Returns the best cut of a graph w.r.t. the degree of dissimilarity
     * between points of different partitions and the degree of similarity
     * between points of the same partition.
     *
     * @param W
     *            the weight matrix of the graph
     * @return an array of two elements, each of these contains the points of a
     *         partition
     */
    protected int[][] bestCut(final DoubleMatrix2D W) {
        int n = W.columns();
        // Builds the diagonal matrices D and D^(-1/2) (represented as their
        // diagonals)
        final DoubleMatrix1D d = DoubleFactory1D.dense.make(n);
        final DoubleMatrix1D d_minus_1_2 = DoubleFactory1D.dense.make(n);
        for (int i = 0; i < n; i++) {
            double d_i = W.viewRow(i).zSum();
            d.set(i, d_i);
            d_minus_1_2.set(i, 1 / Math.sqrt(d_i));
        }
        final DoubleMatrix2D D = DoubleFactory2D.sparse.diagonal(d);
        final DoubleMatrix2D X = D.copy();
        // X = D^(-1/2) * (D - W) * D^(-1/2)
        X.assign(W, Functions.minus);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                X.set(i, j, X.get(i, j) * d_minus_1_2.get(i)
                        * d_minus_1_2.get(j));

        // Computes the eigenvalues and the eigenvectors of X
        final EigenvalueDecomposition e = new EigenvalueDecomposition(X);
        final DoubleMatrix1D lambda = e.getRealEigenvalues();
        // Selects the eigenvector z_2 associated with the second smallest
        // eigenvalue
        // Creates a map that contains the pairs <index, eigevalue>
        final AbstractIntDoubleMap map = new OpenIntDoubleHashMap(n);
        for (int i = 0; i < n; i++)
            map.put(i, Math.abs(lambda.get(i)));
        final IntArrayList list = new IntArrayList();
        // Sorts the map on the value
        map.keysSortedByValue(list);
        // Gets the index of the second smallest element
        final int i_2 = list.get(1);
        // y_2 = D^(-1/2) * z_2
        final DoubleMatrix1D y_2 = e.getV().viewColumn(i_2).copy();
        y_2.assign(d_minus_1_2, Functions.mult);
        // Creates a map that contains the pairs <i, y_2[i]>
        map.clear();
        for (int i = 0; i < n; i++)
            map.put(i, y_2.get(i));
        // Sorts the map on the value
        map.keysSortedByValue(list);
        // Search the element in the map previuosly ordered that minimizes the
        // cut of the partition
        double best_cut = Double.POSITIVE_INFINITY;
        final int[][] partition = new int[2][];
        // The array v contains all the elements of the graph ordered by their
        // projection on vector y_2
        final int[] c = list.elements();
        // For each admissible splitting point i
        for (int i = 1; i < n; i++) {
            // The array a contains all the elements that have a projection on
            // vector y_2 less or equal to the one of i-th element
            // The array b contains the remaining elements
            final int[] a = new int[i];
            final int[] b = new int[n - i];
            System.arraycopy(c, 0, a, 0, i);
            System.arraycopy(c, i, b, 0, n - i);
            final double cut = Ncut(W, a, b, c);
            if (cut < best_cut) {
                best_cut = cut;
                partition[0] = a;
                partition[1] = b;
            }
        }
        return partition;
    }

    /**
     * Splits recursively the points of the graph while the value of the best
     * cut found is less of a specified limit (the alpha star factor).
     *
     * @param W
     *            the weight matrix of the graph
     *
     *            the alpha star factor
     * @return an array of sets of points (partitions)
     */
    protected int[][] partition(final DoubleMatrix2D W/* , double alpha_star */) {
        // If the graph contains only one point
        if (W.columns() == 1) {
            int[][] p = new int[1][1];
            p[0][0] = 0;
            return p;
        }
        // Otherwise
        // Computes the best cut
        final int[][] cut = bestCut(W);
        // Computes the value of the found cut
        final double cutVal = Ncut(W, cut[0], cut[1], null);
        // If the value is less than alpha star
        if (cutVal < alphaStar) {
            // Recursively partitions the first one found ...
            final DoubleMatrix2D W0 = W.viewSelection(cut[0], cut[0]);
            final int[][] p0 = partition(W0 /* , alpha_star */);
            // ... and the second one
            final DoubleMatrix2D W1 = W.viewSelection(cut[1], cut[1]);
            final int[][] p1 = partition(W1 /* , alpha_star */);
            // Merges the partitions found in the previous recursive steps
            final int[][] p = new int[p0.length + p1.length][];
            for (int i = 0; i < p0.length; i++) {
                p[i] = new int[p0[i].length];
                for (int j = 0; j < p0[i].length; j++)
                    p[i][j] = cut[0][p0[i][j]];
            }
            for (int i = 0; i < p1.length; i++) {
                p[i + p0.length] = new int[p1[i].length];
                for (int j = 0; j < p1[i].length; j++)
                    p[i + p0.length][j] = cut[1][p1[i][j]];
            }
            return p;
        }
        // Otherwise returns the partitions found in current step
        // w/o recursive invocation
        int[][] p = new int[1][W.columns()];
        for (int i = 0; i < p[0].length; i++)
            p[0][i] = i;
        return p;
    }

    /**
     * Returns the number of clusters found.
     *
     * @return the number of clusters
     */

    public int numberOfClusters() {
        return numOfClusters;
    }

//    /**
//     * Classifies an instance w.r.t. the partitions found. It applies a naive
//     * min-distance algorithm.
//     *
//     *
//     *            the instance to classify
//     * @return the cluster that contains the nearest point to the instance
//     */
//
//    public int clusterInstance(final Instance instance) {
//        // DoubleMatrix1D u =
//        // DoubleFactory1D.dense.make(instance.toDoubleArray());
//        double min_dist = Double.POSITIVE_INFINITY;
//        int c = -1;
//        for (int i = 0; i < getData().size(); i++) {
//            final double dist = EditDistance.similarity(instance.,getData().get(i).getData());
//            if (dist < min_dist) {
//                c = cluster[i];
//                min_dist = dist;
//            }
//        }
//        return c;
//    }

    /**
     * Generates a clusterer by the mean of spectral clustering algorithm.
     *
     * @param data
     *            set of instances serving as training data
     */

    public void buildClusterer(@SuppressWarnings("hiding") final ArrayList<ScDataPoint> data) {
        setData(data);
        final int n = getData().size();
        final DoubleMatrix2D w = useSparseMatrix ? DoubleFactory2D.sparse.make(
                n, n) : DoubleFactory2D.dense.make(n, n);
		/*
		 * final double[][] v1 = new double[n][]; for (int i = 0; i < n; i++)
		 * v1[i] = data.instance(i).toDoubleArray(); final DoubleMatrix2D v =
		 * DoubleFactory2D.dense.make(v1);
		 */
        final double sigma_sq = sigma * sigma;
        // Sets up similarity matrix
        for (int i = 0; i < n; i++)
            for (int j = i; j < n; j++) {
                final double dist = EditDistance.similarity(data.get(i).getData(),data.get(j).getData());
                if ((r <= 0) || (dist < r)) {
                    final double sim = Math
                            .exp(-(dist * dist) / (2 * sigma_sq));
                    w.set(i, j, sim);
                    w.set(j, i, sim);
                }
            }

        // Compute point partitions
        final int[][] p = partition(w /* , alpha_star */);

        // Deploys results
        numOfClusters = p.length;
        cluster = new int[n];
        for (int i = 0; i < p.length; i++)
            for (int j = 0; j < p[i].length; j++)
                cluster[p[i][j]] = i;
    }

    /**
     * Returns a string describing this clusterer
     *
     * @return a description of the evaluator suitable for displaying in the
     *         explorer/experimenter gui
     */
    public String globalInfo() {
        return "Cluster data using spectral methods";
    }

    /**
     * Returns an enumeration describing the available options.
     * <p>
     *
     * @return an enumeration of all the available options
     **/
    public Enumeration<Option> listOptions() {
        return Collections.enumeration(OPTIONS);
    }

    /**
     * Parses a given list of options.
     *
     * @param options
     *            the list of options as an array of strings
     * @exception Exception
     *                if an option is not supported
     **/
    public void setOptions(final String[] options) throws Exception {
        String optionString = Utils.getOption('A', options);
        if (optionString.length() != 0)
            setAlphaStar(Double.parseDouble(optionString));
        optionString = Utils.getOption('S', options);
        if (optionString.length() != 0)
            setSigma(Double.parseDouble(optionString));
        optionString = Utils.getOption('R', options);
        if (optionString.length() != 0)
            setR(Double.parseDouble(optionString));
        setUseSparseMatrix(Utils.getFlag('M', options));
        optionString = Utils.getOption('D', options);

    }

    /**
     * Gets the current settings of the options.
     *
     * @return an array of strings suitable for passing to setOptions()
     */
    public String[] getOptions() {
        final List<String> options = new LinkedList<String>();
        options.add("-A");
        options.add(Double.toString(getAlphaStar()));
        options.add("-S");
        options.add(Double.toString(getSigma()));
        options.add("-R");
        options.add(Double.toString(getR()));
        options.add("-D");
        if (getUseSparseMatrix())
            options.add("-M");
        return options.toArray(new String[] {});
    }

    /**
     * Sets the new value of the alpha star factor.
     *
     * @param alphaStar
     *            the new value (0 &lt; alpha_star &lt; 1)
     * @exception Exception
     *                if alpha_star is not between 0 and 1
     */
    public void setAlphaStar(final double alphaStar) throws Exception {
        if ((alphaStar > 0) && (alphaStar < 1))
            this.alphaStar = alphaStar;
        else
            throw new Exception("alpha_star must be between 0 and 1");
    }

    /**
     * Returns the current value of the alpha star factor.
     *
     * @return the alpha star factor
     */
    public double getAlphaStar() {
        return alphaStar;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     *         explorer/experimenter gui
     */
    public String alphaStarTipText() {
        return "set maximum allowable normalized cut value. The algorithm stops the recursive partitioning when it does not find a cut that has a value below this factor. Use this argument to limit the number of clusters.";
    }

    /**
     * Sets the new value of the sigma scaling factor.
     *
     * @param sigma
     *            the new value (sigma &gt; 0)
     * @exception Exception
     *                if sigma is not strictly greater than 0
     */
    public void setSigma(final double sigma) throws Exception {
        if (sigma > 0)
            this.sigma = sigma;
        else
            throw new Exception("sigma must be a positive number");
    }

    /**
     * Returns the current value of the sigma factor.
     *
     * @return the sigma factor
     */
    public double getSigma() {
        return sigma;
    }

    /**
     * Returns the tip text for this property.
     *
     * @return tip text for this property suitable for displaying in the
     *         explorer/experimenter gui
     */
    public String sigmaTipText() {
        return "set the distance scaling factor. The similarity of two point x and y is defined as exp(- d(x, y) / sigma) where d(x, y) is the distance between x and y.";
    }

    /**
     * Sets the new value of the r distance cut parameter.
     *
     * @param r
     *            the new value (r &gt; 0 || r == -1)
     * @exception Exception
     *                if r is not -1 and r is not a positive number
     */
    public void setR(final double r) throws Exception {
        if ((r > 0) || (r == -1))
            this.r = r;
        else
            throw new Exception("r must be -1 or a positive number");
    }

    /**
     * Returns the current value of the r distance cur parameter.
     *
     * @return the r distance cut parameter
     */
    public double getR() {
        return r;
    }

    /**
     * Returns the tip text for this property.
     *
     * @return tip text for this property suitable for displaying in the
     *         explorer/experimenter gui
     */
    public String rTipText() {
        return "set the maximum distance value, all points that are far away more than this value have a 0 similarity. Use this parameter to obtain a sparse similarity matrix (see -M).";
    }

    /**
     * Sets the use of sparse representation for similarity matrix.
     *
     * @param useSparseMatrix
     *            true for sparse matrix representation
     */
    public void setUseSparseMatrix(final boolean useSparseMatrix) {
        this.useSparseMatrix = useSparseMatrix;
    }

    /**
     * Returns the status of using sparse matrix flag.
     *
     * @return the status of using sparse matrix flag
     */
    public boolean getUseSparseMatrix() {
        return useSparseMatrix;
    }

    /**
     * Returns the tip text for this property.
     *
     * @return tip text for this property suitable for displaying in the
     *         explorer/experimenter gui
     */
    public String useSparseMatrixTipText() {
        return "use sparse representation for similarity matrix. It can improve the memory efficiency";
    }

//    /**
//     * Returns the distance function
//     *
//     * @return
//     */
//    public DistanceFunction getDistanceFunction() {
//        if (distanceFunction == null) {
//            distanceFunction = new EuclideanDistance();
//        }
//        if (distanceFunction.getInstances() != getData())
//            distanceFunction.setInstances(getData());
//        return distanceFunction;
//    }



    protected void setData(ArrayList<ScDataPoint> data) {
        this.data = data;
    }

    protected ArrayList<ScDataPoint>  getData() {
        return data;
    }

    /**
     * Default constructor.
     **/
    public MySpectrak() {
    }

//    /**
//     * Returns the capabilities of the algorithm. Attribute types actually
//     * supported depend on the selected distance function (i.e., a type is
//     * supported only if it is managed by such function).
//     */
//
//    public Capabilities getCapabilities() {
//        Capabilities result = super.getCapabilities();
//        result.disableAll();
//        result.enable(Capabilities.Capability.NO_CLASS);
//        // attributes
//        result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
//        result.enable(Capabilities.Capability.BINARY_ATTRIBUTES);
//        result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
//        result.enable(Capabilities.Capability.EMPTY_NOMINAL_ATTRIBUTES);
//        result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
//        result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
//        result.enable(Capabilities.Capability.MISSING_VALUES);
//        return result;
//    }
}
