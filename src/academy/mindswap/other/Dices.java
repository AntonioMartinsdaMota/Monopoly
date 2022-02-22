package academy.mindswap.other;

public class Dices {

    private int minNumber;
    private int maxNumber;

    public Dices(int minNumber, int maxNumber) {
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
    }

    public int getRandomNumber(){

        return (int) (Math. random()*(maxNumber-minNumber)) + minNumber;
    }

}
