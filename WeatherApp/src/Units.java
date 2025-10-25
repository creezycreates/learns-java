public enum Units {
    METRIC("metric"), IMPERIAL("imperial");
    public final String label;
    Units(String l) { this.label = l; }
    @Override public String toString() { return this == METRIC ? "Celsius (metric)" : "Fahrenheit (imperial)"; }
}