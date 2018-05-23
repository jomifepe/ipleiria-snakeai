package snake;

public enum ProblemType {
    RANDOM              ("Randomly controlled snake"),
    ADHOC               ("Snake with ad-hoc controller"),
    ONE_AI              ("AI - One snake"),
    TWO_IDENTICAL_AI    ("AI - Two identical snakes"),
    TWO_DIFFERENT_AI    ("AI - Two distinct snakes");

    String description;

    ProblemType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.getDescription();
    }
}
