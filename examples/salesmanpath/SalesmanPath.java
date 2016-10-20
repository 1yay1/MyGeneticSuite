import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by yay on 13.10.2016.
 */
public class SalesmanPath extends Chromosome {
    /**
     * Private. Hands off!
     *
     * @param
     */
    private SalesmanPath(int[] cityArray) {
        super(cityArray);
    }

    /**
     * Constructor for a path that takes a byte value of the number of cities,
     * creates an array of byte values from 0 to numberOfCities and shuffles it.
     * Then passes it to another constructor.
     *
     * @param numberOfCities
     */
    public SalesmanPath(int numberOfCities) {
        this(GeneticUtilities.getRandomPath(numberOfCities));
    }

    /**
     * Creates a Gene object from a List of City objctes.
     * Each City can be represented as two byte values for its coordinates.
     * So a byte array is created of all coordinate values, which is then used to create a new BitSet object.
     * The length of the new Gene object is equal to the amount of City objects times 2*8 (amount of coordinates * size of byte)
     *
     * @param cityList
     * @return new FixedLenghtBitSet representation of the cityList
     */
    /*public static Gene getBitsetFromCityList(List<City> cityList) {
        List<City> path = new ArrayList<>();
        for (int i = 0; i < cityList.size(); i++) {
            path.add(new City(cityList.get(i).getX(), cityList.get(i).getY()));
        }
        byte[] pathInBytes = new byte[path.size() * 2];
        for (int i = 0; i < cityList.size(); i++) {
            pathInBytes[i * 2] = cityList.get(i).getX();
            pathInBytes[i * 2 + 1] = cityList.get(i).getY();
        }
        return new Gene(BitSet.valueOf(pathInBytes), pathInBytes.length * 8);
    }*/

    /**
     * Create a List of City objects from a Gene representation of that list.
     * The BitSet is structured like this: x1,y1,x2,y2,...,xn,yn where n = amount of City Objects.
     * and x and y the coordinates of these City objects.
     *
     * @param fixedLengthBitSet
     * @return new List with City objects.
     */
    /*public static List<City> getCityListFromBitset(Gene fixedLengthBitSet) {
        BitSet bs = fixedLengthBitSet.getBitSet();
        List<City> cityList = new ArrayList<>();
        byte cityListAsBytes[] = new byte[(fixedLengthBitSet.getLength()) / 8];
        for (int i = 0; i < fixedLengthBitSet.getLength(); i++) {
            if (bs.get(i)) {
                cityListAsBytes[cityListAsBytes.length - i / 8 - 1] |= 1 << (i % 8);
            }
        }
        for (int i = 0; i < cityListAsBytes.length - 1; i += 2) {
            cityList.add(new City(cityListAsBytes[i + 1], cityListAsBytes[i]));
        }
        Collections.reverse(cityList);
        return cityList;
    }*/

    /**
     * Calculates the fitness value of a path.
     * The fitness is equal to the distance traveled.
     * The path ends at the beginning, so it is always a round trip.
     *
     * @return int value of the fitness.
     */
    @Override
    protected double calculateFitness() {
        double fitness = 0;
        int path[] = getGene();

        List<City> cityList = new ArrayList<>();
        for (int i : path) {
            cityList.add(SalesmanPopulation.getCity(i));
        }

        for (int i = 0; i < cityList.size() - 1; i++) {
            fitness += City.distanceFromTo(cityList.get(i), cityList.get(i + 1));
        }

        return fitness;
    }

    /**
     * Mutation method. We mutate by swapping two randomly selected City objects in the path.
     *
     * @return newly mutated Chromosome object.
     */
    @Override
    protected Chromosome mutate(float mutationRate) {
        int[] path = getGene();
        for(int i = 1; i < path.length-1; i++) {
            if(ThreadLocalRandom.current().nextFloat() < mutationRate) {
                int index1 = i;
                int index2 = i+1;
                int temp = path[index1];
                path[index1] = path[index2];
                path[index2] = temp;

            }
        }
        return new SalesmanPath(path);
    }

    /**
     * We mate two Paths by swapping a sub-route of the route of the one Path.
     * The rest of the Path is filled by the remaining cities of the other Path object.
     *
     * @param partner the {@link Chromosome} mating partner.
     * @return Chromosome List with two children Chromosomes.
     */
    @Override
    protected List<Chromosome> mate(Chromosome partner) {
        List<Chromosome> children = new ArrayList<>();

        int firstPath[] = getGene();
        int secondPath[] = partner.getGene();

        int index1 = 1+ThreadLocalRandom.current().nextInt(firstPath.length - 2);
        int index2 = index1 + ThreadLocalRandom.current().nextInt(firstPath.length - index1);

        int newFirstPath[] = new int[firstPath.length];
        int newSecondPath[] = new int[firstPath.length];

        for (int i = 1; i < firstPath.length; i++) {
            newFirstPath[i] = newSecondPath[i] = -1;
        }

        List<Integer> addedToOne = new ArrayList<>();
        List<Integer> addedToTwo = new ArrayList<>();
        for (int i = index1; i < index2; i++) {
            newFirstPath[i] = secondPath[i];
            addedToOne.add(new Integer(secondPath[i]));
            newSecondPath[i] = firstPath[i];
            addedToTwo.add(new Integer(firstPath[i]));
        }

        for (int i = 1; i < firstPath.length; i++) {
            Integer nextPotential = new Integer(firstPath[i]);
            boolean canAdd = true;
            for (Integer b : addedToOne) {
                if (b.intValue() == nextPotential.intValue()) {
                    canAdd = false;
                }
            }
            if (canAdd) {
                for (int j = 1; j < firstPath.length; j++) {
                    if (newFirstPath[j] == -1) {
                        newFirstPath[j] = nextPotential.intValue();
                        break;
                    }
                }
            }
        }

        for (int i = 1; i < firstPath.length; i++) {
            Integer nextPotential = new Integer(secondPath[i]);
            boolean canAdd = true;
            for (Integer b : addedToTwo) {
                if (b.intValue() == nextPotential.intValue()) {
                    canAdd = false;
                }
            }
            if (canAdd) {
                for (int j = 1; j < firstPath.length; j++) {
                    if (newSecondPath[j] == -1) {
                        newSecondPath[j] = nextPotential.intValue();
                        break;
                    }
                }
            }
        }

        children.add(new SalesmanPath(newFirstPath));
        children.add(new SalesmanPath(newSecondPath));
        return children;
    }

    /**
     * Creates a String representation of the object consisting of a String representation of the gene and the fitness.
     * @return String
     */
    @Override
    public String toString() {
        return getGene() + " " +getFitness();
    }
}
