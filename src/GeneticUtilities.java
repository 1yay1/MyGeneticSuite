import java.util.*;
import java.util.logging.*;

/**
 * Created by yay on 12.10.2016.
 */
public class GeneticUtilities {
    private static Logger LOGGER = Logger.getLogger(GeneticUtilities.class.getName());

    /**
     * Gets the private static Logger Object for the class.
     * @return LOGGER object.
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static final Random random = new Random();
    private static final char[] ASCII_TABLE;

    static {
        ASCII_TABLE = new char[128];
        for (int i = 0; i < 128; i++) {
            ASCII_TABLE[i] = (char) i;
        }

    }

    /**
     * Gets a random {@link Gene} with specified length.
     * @param length length of the {@link Gene}
     * @return new randomly filled {@link Gene}
     */
    public static Gene getRandomFixedLengthBitSet(int length) {
        BitSet bitSet = new BitSet();
        for(int i = 0; i < length; i++) {
            bitSet.set(i,random.nextBoolean());
        }
        return new Gene(bitSet,length);
    }

    /**
     * Gets a random String made with chars ranging from 33-126 on the Ascii table.
     * @param length length of the String to be created
     * @return new random String.
     */
    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(getRandomChar());
        }
        return sb.toString();
    }

    /**
     * Gets a random char between and including decimal value of from and to
     *
     * @param from start index
     * @param to   end index
     * @return char from ascii table with random index
     */
    private static char getRandomChar(int from, int to) {
        int index = random.nextInt(to - from) + from;
        return ASCII_TABLE[index];
    }

    /**
     * Gets a random char with the index bounds of 32-127.
     * @return random char.
     */
    private static char getRandomChar() {
        return getRandomChar(32,127);
    }

    /**
     * Returns a shuffled array with n bytes from 0..maxNumer.
     * @param maxNumber
     * @return byte[maxNumer]
     */
    public static byte[] getShuffledListOfBytes(byte maxNumber){
        List<Byte> numbers = new ArrayList<>();
        for(byte i =0; i< maxNumber; i++){
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        byte byteArray[] = new byte[maxNumber];
        for(byte i =0; i< maxNumber; i++){
            byteArray[i] = numbers.get(i).byteValue();
        }
        return byteArray;
    }

    /**
     * Returns a shuffled array with n ints randomly filled with values from 0..max.
     * @param max, n
     * @return int[n]
     */
    public static int[] getShuffledListOfInts(int max, int n){
        int numbers[] = new int[n];
        for(int i = 0; i <n; i++) {
            random.nextInt(max);
        }
        return numbers;
    }


    /**
     * Returns a shuffled array with n ints from 0..maxNumer.
     * @param maxNumber
     * @return int[maxNumber]
     */
    public static int[] getShuffledListOfUniqueInts(int maxNumber){
        List<Integer> numbers = new ArrayList<>();
        for(int i =0; i< maxNumber; i++){
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        int intArray[] = new int[maxNumber];
        for(int i =0; i< maxNumber; i++){
            intArray[i] = numbers.get(i).intValue();
        }
        return intArray;
    }
}
