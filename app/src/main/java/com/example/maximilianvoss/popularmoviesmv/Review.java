package com.example.maximilianvoss.popularmoviesmv;

/**
 * Created by ruedigervoss on 08/04/16.
 */
public class Review {

    private String author;
    private String content;

    public Review( String newAuthor, String newContent){
        author = newAuthor;
        content = newContent;
    }

    public String getAuthor(){
        return this.author;
    }

    public String getContent(){
        return this.content;
    }
}
