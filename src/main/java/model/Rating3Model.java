package model;

public class Rating3Model {

    private String dep;
    private double average;
    private double rating;

    public Rating3Model() {
    }

    public Rating3Model(String dep, double average, double rating) {
        this.dep = dep;
        this.average = average;
        this.rating = rating;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
