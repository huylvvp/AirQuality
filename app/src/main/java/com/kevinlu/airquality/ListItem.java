package com.kevinlu.airquality;

public class ListItem {
    private String title;
    private String shortDesc;
    private String rating;
    private int price;

    public ListItem(String title, String shortDesc, String rating, int price) {
        this.title = title;
        this.shortDesc = shortDesc;
        this.rating = rating;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getRating() {
        return rating;
    }

    public int getPrice() {
        return price;
    }
}
