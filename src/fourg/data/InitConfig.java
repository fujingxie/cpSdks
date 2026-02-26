package fourg.data;

public class InitConfig {
    private int Bandwidth;
    private int TimeDelayField;
    private int SynchronousMode;
    private int FrequencyOffset;
    private int OperatingBand;

    public int getBandwidth() {
        return Bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        Bandwidth = bandwidth;
    }

    public int getTimeDelayField() {
        return TimeDelayField;
    }

    public void setTimeDelayField(int timeDelayField) {
        TimeDelayField = timeDelayField;
    }

    public int getSynchronousMode() {
        return SynchronousMode;
    }

    public void setSynchronousMode(int synchronousMode) {
        SynchronousMode = synchronousMode;
    }

    public int getFrequencyOffset() {
        return FrequencyOffset;
    }

    public void setFrequencyOffset(int frequencyOffset) {
        FrequencyOffset = frequencyOffset;
    }

    public int getOperatingBand() {
        return OperatingBand;
    }

    public void setOperatingBand(int operatingBand) {
        OperatingBand = operatingBand;
    }

    @Override
    public String toString() {
        return "InitConfig{" +
                "Bandwidth=" + Bandwidth +
                ", TimeDelayField=" + TimeDelayField +
                ", SynchronousMode=" + SynchronousMode +
                ", FrequencyOffset=" + FrequencyOffset +
                ", OperatingBand=" + OperatingBand +
                '}';
    }
}
