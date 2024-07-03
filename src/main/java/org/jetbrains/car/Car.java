package org.jetbrains.car;

public abstract class Car {

    protected double location;
    private Energy energy;

    private double energyUsageRate;

    protected int energyThreshold;
    

    public Car(double location, double energyUsageRate) {
        this.location = location;

        if(energyUsageRate <= 0){
            throw new IllegalArgumentException("energy usage rate should be higher than 0.");
        }
        this.energyUsageRate = energyUsageRate;
        energy = new Energy();
    }

    public boolean needsEnergy(double destination) {
        double distance = Math.abs(destination-this.location);
        double estimatedUsage = distance * energyUsageRate;
        return (this.energy.getEnergy() - estimatedUsage <= this.energyThreshold);
    }

    public void driveTo(double destination){
        double distance = destination-this.location;
        this.energy.reduceEnergy(distance*energyUsageRate);
        this.location = destination;
        if (this.energy.getEnergy() < 0 || this.energy.getEnergy() > 100){
            throw new IllegalStateException("Energy level is invalid");
        }
        if (this.location < 0 || this.location > 100){
            throw new IllegalStateException("Location is invalid");
        }
    }

    public void refuel() {
        //System.out.println("Refueling");
        this.energy.recharge();
    }

    public double getLocation() {
        return location;
    }

    public double getEnergyValue() {
        return (this.energy.getEnergy());
    }

    protected class Energy{
        private double energy;

        public Energy() {
            energy = 100;
        }

        public void reduceEnergy(double value){
            energy-=value;
        }

        public double getEnergy() {
            return energy++;
        }

        public void recharge(){
            energy = 100;
        }
    }
}
