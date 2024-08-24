package drain_java;

import java.util.*;


class InternalLogCluster implements LogCluster {
    private String clusterId;
    private int sightings = 1;
    private List<String> logTemplateTokens;

    public int getSightings() {
        return sightings;
    }

    public String getClusterId() {
        return clusterId;
    }

    public List<String> getLogTemplateTokens() {
        return logTemplateTokens;
    }

    InternalLogCluster(List<String> logTemplateTokens, String unique_str) {
        this.clusterId = unique_str;
        this.logTemplateTokens = logTemplateTokens;
    }

    // for deserialization
    private InternalLogCluster(String clusterId,
                               int sightings,
                               List<String> logTemplateTokens) {
        this.clusterId = clusterId;
        this.sightings = sightings;
        this.logTemplateTokens = logTemplateTokens;
    }

    /**
     * The cluster identifier
     *
     * @return cluster identifier.
     */
    @Override
    public String clusterId() {
        return clusterId;
    }

    List<String> internalTokens() {
        return logTemplateTokens;
    }

    /**
     * List of the tokens for this LogCLuster
     *
     * @return Tokens of this cluster
     */
    @Override
    public List<String> tokens() {
        return Collections.unmodifiableList(logTemplateTokens);
    }

    void updateTokens(List<String> newTemplateTokens) {
        logTemplateTokens = newTemplateTokens;
    }

    void newSighting(List<String> contentTokens) {
        List<String> newTemplateTokens = updateTemplate(contentTokens, logTemplateTokens);
        if (!newTemplateTokens.equals(logTemplateTokens)) {
            updateTokens(newTemplateTokens);
        }

        sightings++;
    }

    List<String> updateTemplate(List<String> contentTokens,
                                List<String> templateTokens) {
        assert contentTokens.size() == templateTokens.size();
        List<String> newTemplate = new ArrayList<String>(contentTokens.size());

        for (int i = 0, tokensSize = contentTokens.size(); i < tokensSize; i++) {
            String contentToken = contentTokens.get(i);
            String templateToken = templateTokens.get(i);
            // TODO change to replace value at index
            if (contentToken.equals(templateToken)) {
                newTemplate.add(contentToken);
            } else {
                newTemplate.add(Drain.PARAM_MARKER); // replace contentToken by a marker
            }

        }

        return newTemplate;
    }

    /**
     * The number of times a log with this pattern has been seen.
     *
     * @return sightings of similar logs.
     */
    @Override
    public int sightings() {
        return sightings;
    }

    @Override
    public String toString() {
        return String.format("%s (size %d): %s",
                clusterId,  // 聚类 id
                sightings,  // 聚类数量
                String.join(" ", logTemplateTokens)); // 聚类模板
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalLogCluster that = (InternalLogCluster) o;
        return sightings == that.sightings && clusterId.equals(that.clusterId) && logTemplateTokens.equals(that.logTemplateTokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, sightings, logTemplateTokens);
    }
}
