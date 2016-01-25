package com.humanize.app.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamal on 9/17/15.
 */
public class Contents {
    private List<Content> contents;

    public Contents() {
        this.contents = new ArrayList<>();
    }

    public Contents(List<Content> contents) {
        this.contents = contents;
    }

    public Contents(Content content) {
        this.contents = new ArrayList<>();
        this.contents.add(content);
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public void addContent(Content content) {
        for (Content tempContent: contents) {
            if (tempContent.getId().equals(content.getId())) {
                return;
            }
        }

        contents.add(0, content);
    }

    public void removeContent(Content content) {
        for (Content tempContent: contents) {
            if (tempContent.getId().equals(content.getId())) {
                contents.remove(tempContent);
            }
        }
    }
}

