package escada.tpc.common;

public class PerformanceCounters implements PerformanceCountersMBean {

	public float MINIMUM_VALUE = 0.05F;

	public long MINIMUM_REFRESH_INTERVAL = 1000;

	private float inCommingRate = 0F;

	private float commitRate = 0F;

	private float abortRate = 0F;

	private int inCommingCounter = 0;

	private int abortCounter = 0;

	private int commitCounter = 0;

	private long lastComputationInComming, lastComputationAbort,
			lastComputationCommit = 0;

	private PerformanceCounters() {
	}

	public synchronized float getAbortRate() {
		long current = System.currentTimeMillis();
		long diff = current - lastComputationAbort;
		float t = abortRate;

		if (diff > MINIMUM_REFRESH_INTERVAL && diff > 0) {
			t = ((float) abortCounter / (float) (diff)) * 1000;
			t = (t < MINIMUM_VALUE ? 0 : t);
			lastComputationAbort = current;
			abortCounter = 0;
		}
		abortRate = t;

		return (abortRate);
	}

	public synchronized float getCommitRate() {
		long current = System.currentTimeMillis();
		long diff = current - lastComputationCommit;
		float t = commitRate;

		if (diff > MINIMUM_REFRESH_INTERVAL && diff > 0) {
			t = ((float) commitCounter / (float) (diff)) * 1000;
			t = (t < MINIMUM_VALUE ? 0 : t);
			lastComputationCommit = current;
			commitCounter = 0;
		}
		commitRate = t;

		return (commitRate);
	}

	public synchronized float getIncommingRate() {
		long current = System.currentTimeMillis();
		long diff = current - lastComputationInComming;
		float t = inCommingRate;

		if (diff > MINIMUM_REFRESH_INTERVAL && diff > 0) {
			t = ((float) inCommingCounter / (float) (diff)) * 1000;
			t = (t < MINIMUM_VALUE ? 0 : t);
			lastComputationInComming = current;
			inCommingCounter = 0;
		}
		inCommingRate = t;

		return (inCommingRate);
	}

	public static synchronized void setIncommingRate() {
		if (reference != null) {
			reference.inCommingCounter++;
		}
	}

	public static synchronized void setAbortRate() {
		if (reference != null) {
			reference.abortCounter++;
		}
	}

	public static synchronized void setCommitRate() {
		if (reference != null) {
			reference.commitCounter++;
		}
	}

	public static PerformanceCounters getReference() {
		if (reference == null) {
			reference = new PerformanceCounters();
		}
		return (reference);
	}

	private static PerformanceCounters reference;
}
