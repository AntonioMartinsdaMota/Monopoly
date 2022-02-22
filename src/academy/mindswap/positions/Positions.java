package academy.mindswap.positions;

public abstract class Positions {

    protected String name;
    public Positions(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}