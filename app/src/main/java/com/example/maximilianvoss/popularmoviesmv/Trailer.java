package com.example.maximilianvoss.popularmoviesmv;

/**
 * Created by ruedigervoss on 07/04/16.
 */
public class Trailer {

    private String name;
    private String path;

    public Trailer( String newName, String newPath){
        name = newName;
        path = newPath;
    }

    public String getName(){
        return this.name;
    }

    public String getPath(){
        return this.path;
    }
}
