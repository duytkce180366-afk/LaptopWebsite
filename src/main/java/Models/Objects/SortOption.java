package Models.Objects;

public class SortOption {
    private String label;
    private String value;

    public SortOption(String label, String value) {
        this.label = label;
        this.value = value;
    }

    // Getters and Setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
