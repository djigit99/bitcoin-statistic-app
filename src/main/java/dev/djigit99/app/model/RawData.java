package dev.djigit99.app.model;

public class RawData implements Comparable<RawData>{
    private final static int ONE_HOUR = 60 * 60 * 1_000;

    private long timestamp;
    private double closedPrice;

    public RawData(long timestamp, double closedPrice) {
        this.timestamp = timestamp;
        this.closedPrice = closedPrice;
    }



    public long getTimestamp() {
        return timestamp;
    }

    public double getClosedPrice() {
        return closedPrice;
    }

    public void normalizeTimestamp(long timestamp) {
        this.timestamp = (this.timestamp - timestamp ) / ONE_HOUR ;
    }

    @Override
    public int compareTo(RawData o) {
        if (this.timestamp < o.timestamp)
            return -1;
        else if (this.timestamp > o.timestamp)
            return 1;
        return 0;
    }
}
