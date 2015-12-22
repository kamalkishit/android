package com.humanize.android.content.data;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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

    public void setContents(ArrayList<Content> contents) {
        this.contents = contents;
    }

    public void addContent(Content content) {
        this.contents.add(0, content);
        System.out.println("added");
    }

    public void removeContent(Content content) {
        ListIterator<Content> iterator = contents.listIterator();

        while(iterator.hasNext()) {
            if (iterator.next().equals(content)) {
                System.out.println("removed");
                iterator.remove();
                return;
            }
        }
    }
}

