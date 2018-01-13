/**
 * Stores the frequency, initial position, and location of a result.
 */
public class Result implements Comparable<Result> {

	private int frequency;
	private int initialPos;
	private final String path;

	/**
	 * Creates a new Result instance.
	 */
	public Result(int frequency, int initialPos, String path) {
		this.frequency = frequency;
		this.initialPos = initialPos;
		this.path = path;
	}

	/**
	 * Returns the frequency.
	 *
	 * @return frequency of the word
	 */
	public int frequency() {
		return frequency;
	}

	/**
	 * Returns the initial position.
	 * 
	 * @return initial position
	 */
	public int initialPos() {
		return initialPos;
	}

	/**
	 * Returns the location.
	 *
	 * @return the location
	 */
	public String path() {
		return path;
	}

	/**
	 * Sets the frequency to the value passed in.
	 *
	 * @param frequency
	 *            new value of the frequency
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * Updates/adds to the frequency.
	 * 
	 * @param freq
	 *            frequency to be added to the current frequency
	 */
	public void addFrequency(int freq) {
		this.setFrequency(this.frequency + freq);
	}

	/**
	 * Updates the initial position to the lesser of the current position and
	 * position passed in.
	 * 
	 * @param position
	 *            position to compare to current
	 */
	public void updatePosition(int position) {
		if (position < this.initialPos()) {
			this.setInitialPos(position);
		}
	}

	/**
	 * Sets the initial position to the value passed in.
	 *
	 * @param initialPos
	 *            new value of the initial position
	 */
	public void setInitialPos(int initialPos) {
		this.initialPos = initialPos;
	}

	/**
	 * Overrides the compareTo method and compares Result objects first by
	 * frequency, then initial position, then location.
	 *
	 * @param o
	 *            Result object to compare to this object
	 * @return 0 if Result objects are equal, 1 if this Result object is greater
	 *         than o, -1 if this Result object is less than o
	 */
	@Override
	public int compareTo(Result o) {
		if (this.frequency() != o.frequency()) {
			return Integer.compare(o.frequency(), this.frequency());
		} else {
			if (this.initialPos() != o.initialPos()) {
				return Integer.compare(this.initialPos(), o.initialPos());
			} else {
				return this.path().compareTo(o.path());
			}
		}
	}
}