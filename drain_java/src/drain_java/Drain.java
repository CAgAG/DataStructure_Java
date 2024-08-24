package drain_java;

import java.util.*;

/**
 * Drain log pattern miner.
 * <p>
 * This code comes from a modified work of the LogPai team by IBM engineers,
 * but it has been improved to fit the Java platform.
 * </p>
 * <p>
 * Use the builder method {@link #drainBuilder()} to configure an
 * instance.
 * </p>
 *
 * <p>
 * Example use:
 * <pre><code>
 * var drain = Drain.drainBuilder()
 *                  .additionalDelimiters("_")
 *                  .depth(4)
 *                  .build();
 * Files.lines(
 *     Paths.get("file.log"),
 *     StandardCharsets.UTF_8
 * ).forEach(drain::parseLogMessage);
 *
 * // do something with clusters
 * drain.clusters();
 * </code></pre>
 *
 * <p>
 * Note this implementation is not thread safe.
 *
 * @author brice.dutheil@gmail.com
 * @modifiedBy david.ohana@ibm.com, moshikh@il.ibm.com
 * @originalAuthor LogPAI team
 * @license MIT
 */
public class Drain {
    /**
     * Marker for similar tokens
     */
    public static final String PARAM_MARKER = "<*>";
    private static final int ROOT_AND_LEAF_LEVELS = 2;

    /**
     * Depth of all leaf nodes.
     * <p>
     * These are the nodes that contain the log clusters.
     */
    final int depth;

    /**
     * Similarity threshold.
     */
    final double similarityThreshold;

    /**
     * Maximum number of child nodes per node
     */
    final int maxChildPerNode;

    /**
     * Delimiters to apply when splitting log messages into words.
     * <p>
     * In addition to whitespaces.
     */
    final String delimiters;

    /**
     * All log clusters.
     */
    private final List<InternalLogCluster> clusters;

    private final Node root;

    private long global_i = 0;

    public Map<String, List<String>> getGroup2msg() {
        return group2msg;
    }

    private Map<String, List<String>> group2msg = new HashMap<>();  // group_id, message

    public Map<String, List<String>> getGroup2template() {
        return group2template;
    }

    private Map<String, List<String>> group2template = new HashMap<>();  // group_id, template

    public String getGlobal_prefix_str() {
        return global_prefix_str;
    }

    public void setGlobal_prefix_str(String global_prefix_str) {
        this.global_prefix_str = global_prefix_str;
    }

    private String global_prefix_str = "group";

    private Tokenizer parse_tokenizer = new Tokenizer();

    private Drain(int depth,
                  double similarityThreshold,
                  int maxChildPerNode,
                  String delimiters,
                  Tokenizer tokenizer) {
        this.depth = depth - ROOT_AND_LEAF_LEVELS;
        this.similarityThreshold = similarityThreshold;
        this.maxChildPerNode = maxChildPerNode;
        this.delimiters = delimiters;
        this.parse_tokenizer = tokenizer;

        root = new Node("(ROOT)", 0);
        clusters = new ArrayList<>();
    }

    Drain(DrainState state) {
        this.depth = state.depth;
        this.similarityThreshold = state.similarityThreshold;
        this.maxChildPerNode = state.maxChildPerNode;
        this.delimiters = state.delimiters;
        this.clusters = state.clusters;
        this.root = state.prefixTree;
    }

    /**
     * Parse log message.
     * <p>
     * Classify the log message to a cluster.
     *
     * @param message The log message content
     */
    public void parseLogMessage(String message) {
        // sprint message by delimiter / whitespaces
        List<String> contentTokens = this.parse_tokenizer.tokenize(message, delimiters);

        // Search the prefix tree
        InternalLogCluster matchCluster = treeSearch(contentTokens);

        if (matchCluster == null) {
            String group_id = String.format("%s_%d", this.global_prefix_str, this.global_i);
            // create cluster if it doesn't exists, using log content tokens as template tokens
            matchCluster = new InternalLogCluster(contentTokens, group_id);
            this.global_i++;

            clusters.add(matchCluster);
            addLogClusterToPrefixTree(matchCluster);
            // group2list.put(group_id, new ArrayList<>());
        } else {
            // add the log to an existing cluster
            matchCluster.newSighting(contentTokens);
        }
        String group_id = matchCluster.clusterId();
        List<String> new_template = matchCluster.getLogTemplateTokens();

        List<String> log_v = group2msg.getOrDefault(group_id, new ArrayList<>());
        log_v.add(message);
        group2msg.put(group_id, log_v);

        List<String> old_log_template = group2template.getOrDefault(group_id, new ArrayList<>());
        if (!old_log_template.equals(new_template)) {
            group2template.put(group_id, new_template);
        }
    }

    /**
     * Search a matching log cluster given a log message.
     *
     * @param message The log message content
     * @return The matching log cluster or null if no match
     */
    public LogCluster searchLogMessage(String message) {
        // sprint message by delimiter / whitespaces
        List<String> contentTokens = this.parse_tokenizer.tokenize(message, delimiters);

        // Search the prefix tree
        LogCluster matchCluster = treeSearch(contentTokens);
        return matchCluster;
    }


    private InternalLogCluster treeSearch(List<String> logTokens) {

        // at first level, children are grouped by token (word) count
        int tokensCount = logTokens.size();
        Node node = this.root.get(tokensCount);

        // the prefix tree is empty
        if (node == null) {
            return null;
        }

        // handle case of empty log string - return the single cluster in that group
        if (tokensCount == 0) {
            return node.clusterOf(0);
        }

        // find the leaf node for this log
        // a path of nodes matching the first N tokens (N=tree depth)
        int currentDepth = 1;
        for (String token : logTokens) {
            // if max depth reached or last parseable token, bail out
            boolean atMaxDepth = currentDepth == this.depth;
            boolean isLastToken = currentDepth == tokensCount;
            if (atMaxDepth || isLastToken) {
                break;
            }

            // descend
            Node nextNode = node.get(token);
            // if null try get from generic pattern
            if (nextNode == null) {
                nextNode = node.get(PARAM_MARKER);
            }
            // if the node don't exists yet, the cluster don't exists yet
            if (nextNode == null) {
                return null;
            }
            node = nextNode;
            currentDepth++;
        }

        return fastMatch(node.clusters(), logTokens);
    }

    private InternalLogCluster fastMatch(List<InternalLogCluster> clusters,
                                         List<String> logTokens) {
        InternalLogCluster matchedCluster = null;

        double maxSimilarity = -1;
        int maxParamCount = -1;
        InternalLogCluster maxCluster = null;

        for (InternalLogCluster cluster : clusters) {
            SeqDistance seqDistance = computeSeqDistance(cluster.internalTokens(), logTokens);
            if (seqDistance.similarity > maxSimilarity
                    || (seqDistance.similarity == maxSimilarity
                    && seqDistance.paramCount > maxParamCount)) {
                maxSimilarity = seqDistance.similarity;
                maxParamCount = seqDistance.paramCount;
                maxCluster = cluster;
            }
        }

        if (maxSimilarity >= this.similarityThreshold) {
            matchedCluster = maxCluster;
        }

        return matchedCluster;
    }

    private static class SeqDistance {

        final double similarity;
        final int paramCount;

        SeqDistance(double similarity, int paramCount) {
            this.similarity = similarity;
            this.paramCount = paramCount;
        }

    }

    static SeqDistance computeSeqDistance(List<String> templateTokens,
                                          List<String> logTokens) {
        assert templateTokens.size() == logTokens.size();

        int similarTokens = 0;
        int paramCount = 0;

        for (int i = 0, tokensSize = templateTokens.size(); i < tokensSize; i++) {
            String token = templateTokens.get(i);
            String currentToken = logTokens.get(i);

            if (token.equals(PARAM_MARKER)) {
                paramCount++;
                continue;
            }
            if (token.equals(currentToken)) {
                similarTokens++;
            }
        }

        double similarity = (double) similarTokens / templateTokens.size();
        return new SeqDistance(similarity, paramCount);
    }

    private void addLogClusterToPrefixTree(InternalLogCluster newLogCluster) {
        int tokensCount = newLogCluster.internalTokens().size();

        Node node = this.root.getOrCreateChild(tokensCount);

        // handle case of empty log message
        if (tokensCount == 0) {
            node.appendCluster(newLogCluster);
            return;
        }


        int currentDepth = 1;
        for (String token : newLogCluster.internalTokens()) {

            // Add current log cluster to the leaf node
            boolean atMaxDepth = currentDepth == this.depth;
            boolean isLastToken = currentDepth == tokensCount;
            if (atMaxDepth || isLastToken) {
                node.appendCluster(newLogCluster);
                break;
            }

            // If token not matched in this layer of existing tree.
            // TODO see improvements are possible
            if (!node.contains(token)) {
                if (!hasNumber(token)) {
                    if (node.contains(PARAM_MARKER)) {
                        if (node.childrenCount() < maxChildPerNode) {
                            node = node.getOrCreateChild(token);
                        } else {
                            node = node.get(PARAM_MARKER);
                        }
                    } else {
                        if (node.childrenCount() + 1 <= maxChildPerNode) {
                            node = node.getOrCreateChild(token);
                        } else if (node.childrenCount() + 1 == maxChildPerNode) {
                            node = node.getOrCreateChild(PARAM_MARKER);
                        } else {
                            node = node.get(PARAM_MARKER);
                        }
                    }
                } else {
                    if (!node.contains(PARAM_MARKER)) {
                        node = node.getOrCreateChild(PARAM_MARKER);
                    } else {
                        node = node.get(PARAM_MARKER);
                    }
                }
            } else {
                node = node.get(token);
            }
            currentDepth++;
        }
    }


    private static boolean hasNumber(String s) {
        return s.chars().anyMatch(Character::isDigit);
    }

    /**
     * Returns a list of the Log clusters.
     *
     * @return Non modifiable list of current clusters.
     */
    public List<LogCluster> getClustersStatic() {
        return Collections.unmodifiableList(new ArrayList<>(clusters));
    }

    public List<InternalLogCluster> getClusters() {
        return clusters;
    }


    Node prefixTree() {
        return root;
    }


    /**
     * Drain builder.
     * <p>
     * Used like this:
     * <pre><code>
     *     Drain.drainBuilder()
     *          .additionalDelimiters("_")
     *          .depth(4)
     *          .build()
     * </code></pre>
     *
     * @return a drain builder
     */
    public static DrainBuilder drainBuilder() {
        return new DrainBuilder();
    }

    /**
     * Builder for {@link Drain}
     */
    public static class DrainBuilder {
        private int depth = 4;
        private String delimiters = " ";
        private double similarityThreshold = 0.4d;
        private int maxChildPerNode = 100;
        private Tokenizer parse_tokenizer = new Tokenizer();

        /**
         * Depth of all leaf nodes.
         * <p>
         * How many level to reach the nodes that contain log clusters.
         * <p>
         * The default value is 4, the minimum value is 3.
         *
         * @param depth The depth of all leaf nodes.
         * @return this
         */
        public DrainBuilder depth(int depth) {
            assert depth > 2;
            this.depth = depth;
            return this;
        }

        /**
         * Additional delimiters.
         * <p>
         * Additionally to the whitespace, also use additional delimiting
         * characters to to split the log message into tokens. This value
         * is empty by default.
         *
         * @param delimiters THe Additional delimiters.
         * @return this
         */
        public DrainBuilder delimiters(String delimiters) {
            assert delimiters != null;
            this.delimiters = delimiters;
            return this;
        }

        /**
         * Similarity threshold.
         * <p>
         * The similarity threshold applies to each token of a log message,
         * if the percentage of similar tokens is below this number, then
         * a new log cluster will be created.
         * <p>
         * Default value is 0.4.
         *
         * @param similarityThreshold The similarity threshold
         * @return this
         */
        public DrainBuilder similarityThreshold(double similarityThreshold) {
            assert similarityThreshold > 0.1d;
            this.similarityThreshold = similarityThreshold;
            return this;
        }

        /**
         * Max number of children of an internal node.
         * <p>
         * Limit the number of children nodes, if this value is too low
         * and log messages are too versatile then many logs will be
         * classified under the generic param marker.
         * <p>
         * Default value is 100.
         *
         * @param maxChildPerNode Max number of children of an internal node
         * @return this
         */
        public DrainBuilder maxChildPerNode(int maxChildPerNode) {
            assert maxChildPerNode >= 2;
            this.maxChildPerNode = maxChildPerNode;
            return this;
        }

        public DrainBuilder tokenizer(Tokenizer tokenizer) {
            this.parse_tokenizer = tokenizer;
            return this;
        }

        /**
         * Build a non thread safe instance of Drain.
         *
         * @return A {@see Drain} instance
         */
        public Drain build() {
            return new Drain(depth,
                    similarityThreshold,
                    maxChildPerNode,
                    delimiters,
                    parse_tokenizer);
        }
    }
}
