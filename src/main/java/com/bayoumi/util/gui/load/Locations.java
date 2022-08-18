package com.bayoumi.util.gui.load;

public enum Locations {
    Home("/com/bayoumi/views/home/Home.fxml");

    private final String name;

    Locations(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
