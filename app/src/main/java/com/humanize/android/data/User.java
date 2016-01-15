package com.humanize.android.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kamal on 1/15/16.
 */
public class User {

    private List<String> categories;

    public User() {
        categories = new ArrayList<>();
        categories.add("Achievers");
        categories.add("Beautiful");
        categories.add("Education");
        categories.add("Environment");
        categories.add("Empowerment");
        categories.add("Governance");
        categories.add("Health");
        categories.add("Humanity");
        categories.add("Real Heroes");
        categories.add("Law and Justice");
        categories.add("Science and Tech");
        categories.add("Sports");
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
