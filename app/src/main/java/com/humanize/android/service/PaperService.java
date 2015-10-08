package com.humanize.android.service;

import com.humanize.android.data.Contents;

/**
 * Created by Kamal on 10/7/15.
 */
public class PaperService {

    private Contents paper;

    private static PaperService paperService = null;

    private PaperService() {
        paper = new Contents();
    }

    public static PaperService getInstance() {
        if (paperService == null) {
            paperService = new PaperService();
        }

        return paperService;
    }

    public Contents getPaper() {
        return paper;
    }

    public void setPaper(Contents paper) {
        this.paper = paper;
    }
}