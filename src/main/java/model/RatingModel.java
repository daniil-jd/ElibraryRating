package model;

public class RatingModel {
    private String dep;
    private double sum;
    private double border;
    private double rating;

    public RatingModel() {
    }

    public RatingModel(String dep, double sum,
                       double border, double rating) {
        this.dep = dep;
        this.sum = sum;
        this.border = border;
        this.rating = rating;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getBorder() {
        return border;
    }

    public void setBorder(double border) {
        this.border = border;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
