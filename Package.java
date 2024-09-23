public class Package {

    public int id;
    public float height;
    public float length;
    public float width;
    public String description;

    // Static factory method for creating a new Package instance
    public static Package of(int id, float length, float width, float height, String description) {
        Package pkg = new Package();
        pkg.id = id;
        pkg.length = length;
        pkg.width = width;
        pkg.height = height;
        pkg.description = description;
        return pkg;
    }

    public static Package of(int id, float height, float length) {
        Package pkg = new Package();
        pkg.id = id;
        pkg.height = height;
        pkg.length = length;
        return pkg;
    }
    // Optionally, you can override the toString method to print the package details
    @Override
    public String toString() {
        return "Package{id=" + id + ", length=" + length + ", width=" + width + ", height=" + height + ", description='" + description + "'}";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
