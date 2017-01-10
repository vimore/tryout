package com.securityx.modelfeature.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.List;

public class GlobalStatusConfiguration {

    @JsonProperty
    private String excludeUserLikeTerms;

    @JsonProperty
    private String excludeHostLikeTerms;

    public String getExcludeUserLikeTerms() {
        return excludeUserLikeTerms;
    }

    public void setExcludeUserLikeTerms(String excludeUserLikeTerms) {
        this.excludeUserLikeTerms = excludeUserLikeTerms;
    }

    public List<String> getExcludeUserLikeTermList() {
        List<String> likeTerms = new ArrayList<String>();
        if (excludeUserLikeTerms != null && !"".equals(excludeUserLikeTerms)) {
            String[] likeTermArray = excludeUserLikeTerms.split(",");
            for (String term : likeTermArray) {
                likeTerms.add(term.trim());
            }
        }
        return likeTerms;
    }

    public String getExcludeHostLikeTerms() {
        return excludeHostLikeTerms;
    }

    public void setExcludeHostLikeTerms(String excludeHostLikeTerms) {
        this.excludeHostLikeTerms = excludeHostLikeTerms;
    }

    public List<String> getExcludeHostLikeTermList() {
        List<String> likeTerms = new ArrayList<String>();
        if (excludeHostLikeTerms != null && !"".equals(excludeHostLikeTerms)) {
            String[] likeTermArray = excludeHostLikeTerms.split(",");
            for (String term : likeTermArray) {
                likeTerms.add(term.trim());
            }
        }
        return likeTerms;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("excludeUserLikeTerms", excludeUserLikeTerms)
                .add("excludeHostLikeTerms", excludeHostLikeTerms)
                .toString();
    }

}
