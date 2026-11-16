package ru.stepan.cinema.enums;

public enum AgeRating {
    AGE_0(0),
    AGE_6(6),
    AGE_12(12),
    AGE_16(16),
    AGE_18(18);

    private final int minAge;

    AgeRating(int minAge) {
        this.minAge = minAge;
    }

    public int getMinAge() {
        return minAge;
    }

}
