package com.jello.zero;

/**
 * Created by kimpham on 5/2/17.
 */

public class Comment {
    private String content;
    private String author;

    public Comment(){}

    public Comment(String content, String author){
        this.content = content;
        this.author = author;
    }

    public String toString(){
        return content;
    }
    //getters and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
