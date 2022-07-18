
package com.pkasemer.MyFamlinkApp.Models;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class UserFeedback {

    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("user_message")
    @Expose
    private List<UserMessage> userMessage = null;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<UserMessage> getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(List<UserMessage> userMessage) {
        this.userMessage = userMessage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

}
