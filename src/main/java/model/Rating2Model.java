package model;

public class Rating2Model {

    private String dep;
    private double average;
    private double rating;
    private String mark;

    public Rating2Model() {
    }

    public Rating2Model(String dep, double average, double rating, String mark) {
        this.dep = dep;
        this.average = average;
        this.rating = rating;
        this.mark = mark;
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

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
