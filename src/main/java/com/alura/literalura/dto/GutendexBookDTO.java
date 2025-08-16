package com.leticia.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GutendexBookDTO {
    private String title;
    private List<GutendexAuthorDTO> authors;
    private List<String> languages;
    @JsonProperty("download_count")
    private Integer downloadCount;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<GutendexAuthorDTO> getAuthors() { return authors; }
    public void setAuthors(List<GutendexAuthorDTO> authors) { this.authors = authors; }
    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }
    public Integer getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }
}
