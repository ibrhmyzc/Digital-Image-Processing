package sample;

import vpt.Image;

public class Convolve {
    private Part1Strategy strategy;

    public void setStrategy(Part1Strategy strategy) {
        this.strategy = strategy;
    }

    public Image convolveImage() {
        return strategy.run();
    }
}
