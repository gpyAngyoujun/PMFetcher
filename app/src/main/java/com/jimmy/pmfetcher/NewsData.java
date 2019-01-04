package com.jimmy.pmfetcher;

import java.util.List;

/**
 * Created by yangyoujun on 2019/1/4 .
 */
public class NewsData {

    private String success;
    private List<Payload> payload;

    public String getSuccess() {
        return success;
    }

    public List<Payload> getPayload() {
        return payload;
    }

    public static class Payload {

        private int id;
        private boolean is_event;
        private String title;
        private String permalink;
        private String date;
        private String image;
        private String like;
        private String view;
        private String bookmark;
        private String comment;

        public int getId() {
            return id;
        }

        public boolean isIs_event() {
            return is_event;
        }

        public String getTitle() {
            return title;
        }

        public String getPermalink() {
            return permalink;
        }

        public void setPermalink(String permalink) {
            this.permalink = permalink;
        }

        public String getDate() {
            return date;
        }

        public String getImage() {
            return image;
        }

        public String getLike() {
            return like;
        }

        public String getView() {
            return view;
        }

        public String getBookmark() {
            return bookmark;
        }

        public String getComment() {
            return comment;
        }
    }
}
