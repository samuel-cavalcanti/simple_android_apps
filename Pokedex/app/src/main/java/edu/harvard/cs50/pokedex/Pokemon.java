package edu.harvard.cs50.pokedex;

public class Pokemon {
    private String name;
    private String url;
    private Boolean caught;

    Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getCaught() {
        return caught;
    }

    public void setCaught(Boolean caught) {
        this.caught = caught;
    }


}
