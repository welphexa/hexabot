import java.math.BigInteger;

public class Bet {

	private String betType;
	private BigInteger betAmount;
	private int playersNumber;
	private int winningNumber;
	private int p1;
	private int p2;
	private int p3;
	private int w1;
	private int w2;
	private int w3;
	
	private static final String STRAIGHT = "1";
	
	public Bet(String betType, BigInteger betAmount, int playersNumber, int winningNumber) {
		this.betType = betType;
		this.betAmount = betAmount;
		this.playersNumber = playersNumber;
		this.winningNumber = winningNumber;
		p1 = playersNumber / 100;
		p2 = (playersNumber / 10) % 10;
		p3 = playersNumber % 10;
		w1 = winningNumber / 100;
		w2 = (winningNumber / 10) % 10;
		w3 = winningNumber % 10;
	}
	
	public BigInteger getAmountWon() {
		if (betType.equals(STRAIGHT)) {
			if (isExactMatch()) {
				return betAmount.multiply(BigInteger.valueOf(500000));
			} else {
				return BigInteger.ZERO;
			}
		} else {
			if (isAnyOrderMatch()) {
				if (containsDuplicate()) {
					return betAmount.multiply(BigInteger.valueOf(150000));
				} else {
					return betAmount.multiply(BigInteger.valueOf(50000));
				}
			} else {
				return BigInteger.ZERO;
			}
		}
	}
	
	private boolean isExactMatch() {
		return playersNumber == winningNumber;
	}
	
	private boolean isAnyOrderMatch() {
		return (p1 == w1 && p2 == w2 && p3 == w3)
				|| (p1 == w1 && p2 == w3 && p3 == w2)
				|| (p1 == w2 && p2 == w1 && p3 == w3)
				|| (p1 == w2 && p2 == w3 && p3 == w1)
				|| (p1 == w3 && p2 == w1 && p3 == w2)
				|| (p1 == w3 && p2 == w2 && p3 == w1);
	}
	
	private boolean containsDuplicate() {
		return p1 == p2 || p1 == p3 || p2 == p3;
	}
}
