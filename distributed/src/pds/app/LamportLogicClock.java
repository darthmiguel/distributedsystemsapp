package pds.app;

/**
 * Class for Lamport clock
 * 
 *
 */
public class LamportLogicClock
{
	
	/**
	 * variable that will store the value of the clock
	 */
	private int lamportClock;

	/**
	 * Constructor that sets the clock with value 1
	 */
	public LamportLogicClock()
	{
		lamportClock = 1;
	}

	/**
	 * This method increases the value of the clock in 1
	 */
	public void change()
	{
		lamportClock += 1;
	}

	/**
	 * Method that returns the current value of the clock
	 * @return
	 */
	public int getLamportClock()
	{
		return lamportClock;
	}

	/**
	 * Method that restes the clock to its initial value 1
	 */
	public void reset()
	{
		lamportClock = 1;
	}

	/**
	 * Method that sets the lamport clock with the maxium value
	 * between the local clock and the sender, increased in 1
	 * @param senderTimeStamp
	 */
	public void receiveRequest(int senderTimeStamp)
	{
		this.lamportClock = Math.max(this.lamportClock, senderTimeStamp) + 1;
	}
}