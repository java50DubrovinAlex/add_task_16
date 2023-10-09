package add_tasks_16;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

/**
 * This collector collects only those elements of Stream<T> 
 * which have the maximal rate due to specified 'rater' function.
 * The collector is built using static method MaxRatedWinnersCollector.of(...)
 */
public class MaxRatedWinnersCollector {
	private MaxRatedWinnersCollector() {} // hidden constructor

	/**
	 * Constructs the Collector using specified 'rater' instance
	 */
	public static <T> Collector<T, ?, List<T>> of(ToLongFunction<T> rater) {
		return Collector.of(
				() -> new RateAccumulator<T>(rater), 
				RateAccumulator::accumulate, 
				RateAccumulator::combine, 
				RateAccumulator::getWinnersList, 
				Characteristics.UNORDERED);
	}

	/**
	 * The internal accumulating class for implementation of Collector
	 */
	private static class RateAccumulator<T> {
		ToLongFunction<T> rater;
		List<T> winnersList = new ArrayList<>();
		long maxRate = Long.MIN_VALUE;
		/**
		 * Constructor
		 * @param <T> rater - functional object, giving the long 'rate' value to each T object
		 */
		public RateAccumulator(ToLongFunction<T> rater) {
			this.rater = rater;
				
		}

		/**
		 * Gets list of collected 'winners' - the rate champions
		 * @return returns list of winners (the T instances having max rate)
		 */
		public List<T> getWinnersList() {
			return winnersList;
		}

		/**
		 * Accumulate 'champions', using the following algorithm:
		 * - calculates rate value, applying the 'rater' to specified 'value'
		 * - makes the decision: 
		 *    - if rate < currentMax then do nothing
		 *    - if rate == currentMax then collect
		 *    - if rate > currentMax then make it new champion
		 * @param value - next element from stream
		 */
		public void accumulate(T value) {
			long rate = rater.applyAsLong(value);
			if(rate > maxRate) {
				maxRate = rate;
				winnersList.clear();
				winnersList.add(value);
			}else if(rate == maxRate){
				winnersList.add(value);
			}
					

		}

		/**
		 * Updates this accumulator using data from other:
		 * - if one of rates are greater, then only greater remains
		 * - else this appends other's list of champions to it's own list
		 * @param other - other accumulator
		 * @return this accumulator
		 */
		public RateAccumulator<T> combine(RateAccumulator<T> other) {
			 if (other.maxRate > this.maxRate) {
			        this.maxRate = other.maxRate;
			        this.winnersList = other.winnersList;
			    } else if (other.maxRate == this.maxRate) {
			        this.winnersList.addAll(other.winnersList);
			    }
			    return this;
		}
	}
}
