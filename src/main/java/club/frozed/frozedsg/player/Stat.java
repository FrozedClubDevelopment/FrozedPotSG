package club.frozed.frozedsg.player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stat {
    private int amount;
    private int name;

    public void increaseAmount(int amount) {
        this.amount = this.amount + amount;
    }

    public void decreaseAmount(int amount) {
        this.amount = this.amount - amount;
    }
}
