package com.example.bbqdelight;

/** Tempero aplicado ao espeto cozido. */
public enum Seasoning {
    NONE(""),
    CUMIN("cumin"),
    PEPPER("pepper"),
    CHILI("chili");

    public final String id;

    Seasoning(String id) {
        this.id = id;
    }
}
