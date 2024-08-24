package drain_java;

import java.util.List;

/**
 * Represents a cluster of log message parts.
 *
 * <p>A cluster consists of a list of tokens, one of the token might be the wild card {@link Drain#PARAM_MARKER &lt;*&gt;}.
 */
public interface LogCluster {
    /**
     * @return the cluster identifier.
     */
    String clusterId();  // 聚类id

    /**
     * @return the list of tokens.
     */
    List<String> tokens();  // 聚类模板

    /**
     * @return the number similar log messages have been seen by this cluster.
     */
    int sightings();  // 模板对应的数据数量
}
