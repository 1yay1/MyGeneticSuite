

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by yay on 12.10.2016.
 * Class that holds the genetic information. BitSet uses less memory than byte[] array but has no fixed size.
 * This Class uses BitSet together with a size variable.
 */
public class Gene {

    private final BitSet bitSet;
    private final int length;
    private static final String INDEX_OUT_OF_BOUNDS_STRING =
            "Index out of bounds. Can't be >= length.";

    /**
     * Gene Constructor from a String
     *
     * @param s String to be converted to bits. Length will be Number of bits in the String. length % 8 == 0 is guaranteed.
     */
    public Gene(String s) {
        this(BitSet.valueOf(s.getBytes()), s.getBytes().length * 8);
    }

    /**
     * Gene Constructor from a long array.
     *
     * @param longArray
     */
    public Gene(long[] longArray) {
        this.bitSet = BitSet.valueOf(longArray);
        this.length = 64 * longArray.length;
    }

    /**
     * Gene Constructor from a double array.
     * Converts the double array to a long array, then uses the BitSet.valueOf method.
     *
     * @param doubleArray array of doubles.
     */
    public Gene(double[] doubleArray) {
        List<Long> longList = new ArrayList<>();
        for (double d : doubleArray) {
            longList.add(Double.doubleToLongBits(d));
        }
        long[] longArray = new long[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) {
            longArray[i] = longList.get(i).longValue();
        }
        this.bitSet = BitSet.valueOf(longArray);
        this.length = 64 * longArray.length;
    }

    /**
     * Creates a new Gene from an int array.
     * Converts the int array to a byte array, and passes that to the BitSet.valueOf(byte[] bytes) method.
     * @param intArray array of int values
     */
    public Gene(int[] intArray) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length*4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(intArray);
        this.bitSet = BitSet.valueOf(byteBuffer);
        this.length = intArray.length*32;

        /*byte bytes[] = new byte[intArray.length*4];
        for(int i = 0; i < intArray.length; i++) {
            bytes[i*4] = (byte) (intArray[i] >>> 0);
            bytes[i*4+1] = (byte) (intArray[i] >>> 8);
            bytes[i*4+2] = (byte) (intArray[i] >>> 16);
            bytes[i*4+3] = (byte) (intArray[i] >>> 24);
        }
        this.bitSet = BitSet.valueOf(bytes);
        this.length = bytes.length*8;*/
    }

    /**
     * Gene Constructor
     *
     * @param length length of the BitSet.
     */
    public Gene(int length) {
        this.bitSet = new BitSet();
        this.length = length;
    }

    /**
     * Gene Constructor
     *
     * @param bitSet BitSet to be used. All values from from until to will be used.
     * @param from   Start Index for BitSet copy. Inclusive.
     * @param to     End Index for BitSet copy. Exclusive.
     */
    public Gene(BitSet bitSet, int from, int to) {
        this.bitSet = new BitSet();
        this.length = to - from;
        for (int i = from; i < to; i++) {
            if (bitSet.get(i)) {
                this.bitSet.set(i);
            } else {
                this.bitSet.clear(i);
            }
        }
    }

    /**
     * Interprets the BitString as array of bytes and prints it.
     *
     * @return String of bytes
     */
    public String toByteString() {
        StringBuilder sb = new StringBuilder();

        byte bytes[] = getBitSetAsByteArray();
        sb.append("[");
        for (byte b : bytes) {
            sb.append(b);
            sb.append(", ");
        }
        sb.replace(sb.lastIndexOf(", "), sb.length(), "");
        sb.append("]");

        return sb.toString();
    }

    /**
     * Interprets the BitString as array of int values and prints it.
     *
     * @return String of int values
     */
    public String toIntString() {
        StringBuilder sb = new StringBuilder();

        int ints[] = getBitSetAsIntArray();
        sb.append("[");
        for (int i : ints) {
            sb.append(i);
            sb.append(", ");
        }
        sb.replace(sb.lastIndexOf(", "), sb.length(), "");
        sb.append("]");

        return sb.toString();
    }

    /**
     * Interprets the BitString as array of longs and prints it.
     *
     * @return String of longs
     */
    public String toLongsString() {
        StringBuilder sb = new StringBuilder();

        long longs[] = getBitSetAsLongArray();
        sb.append("[");
        for (long l : longs) {
            sb.append(l);
            sb.append(", ");
        }
        sb.replace(sb.lastIndexOf(", "), sb.length(), "");
        sb.append("]");

        return sb.toString();
    }

    /**
     * Interprets the BitString as array of doubles and prints it.
     *
     * @return String of doubles
     */
    public String toDoublesString() {
        StringBuilder sb = new StringBuilder();

        long longs[] = getBitSetAsLongArray();
        double doubles[] = new double[longs.length];

        for (int i = 0; i < longs.length; i++) {
            doubles[i] = Double.longBitsToDouble(longs[i]);
        }
        sb.append("[");
        for (double d : doubles) {
            sb.append(d);
            sb.append(", ");
        }
        sb.replace(sb.lastIndexOf(", "), sb.length(), "");
        sb.append("]");

        return sb.toString();
    }

    /**
     * Constructor for a Gene from an array of byte value.
     * @param bytes byte array
     */
    public Gene(byte[] bytes) {
        this(BitSet.valueOf(bytes), bytes.length * 8);
    }

    /**
     * Builds an array of byte values from the BitSet
     *
     * @return new array of bytes with length this.length/8
     */
    public byte[] getBitSetAsByteArray() {
        byte bytes[] = new byte[(getLength()) / 8];
        for (int i = 0; i < getLength(); i++) {
            if (bitSet.get(i)) {
                bytes[i / 8] |= 1 << (i % 8);
            }
        }
        return bytes;
    }

    /**
     * Build an array of long values from the BitSet.
     *
     * @return new array of long values with length this.length/64
     */
    public long[] getBitSetAsLongArray() {
        long longs[] = new long[(getLength()) / 64];
        for (int i = 0; i < getLength(); i++) {
            if (bitSet.get(i)) {
                longs[i / 64] |= 1 << (i % 64);
            }
        }
        return longs;
    }

    /**
     * Build an array of int values from the BitSet.
     *
     * @return new array of int values with length this.length/32
     */
    public int[] getBitSetAsIntArray() {
        IntBuffer intBuffer = ByteBuffer.wrap(bitSet.toByteArray()).asIntBuffer();
        int intArray[] = new int[intBuffer.remaining()];
        intBuffer.get(intArray);
        return intArray;
    }

    /**
     * Method to get the boolean at index i.
     *
     * @param i index of the value in the BitSet
     * @return value at index i
     * @throws IndexOutOfBoundsException
     */
    public boolean get(int i) throws IndexOutOfBoundsException {
        if (i >= this.length) throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDS_STRING);

        return bitSet.get(i);
    }

    /**
     * Gene Constructor.
     *
     * @param bitSet BitSet to be used. All values until index length will be used.
     * @param length Maximum length of the BitSet.
     */
    public Gene(BitSet bitSet, int length) {
        this(bitSet, 0, length);
    }

    /**
     * Sets the bit at index i to boolean b.
     * Throws {@link IndexOutOfBoundsException} for indexes that are too large or negative.
     *
     * @param i is the index in the BitSet we want to be set to 1.
     * @throws IndexOutOfBoundsException
     */
    public void set(int i, boolean b) throws IndexOutOfBoundsException {
        if (i >= length || i < 0) {
            throw new IndexOutOfBoundsException(INDEX_OUT_OF_BOUNDS_STRING);
        }
        bitSet.set(i, b);
    }

    /**
     * BitSet getter.
     *
     * @return a copy of the original BitSet in this object.
     */
    public BitSet getBitSet() {
        return (BitSet) bitSet.clone();
    }

    /**
     * length getter.
     *
     * @return the length of the BitSet.
     */
    public int getLength() {
        return length;
    }

    /**
     * Overwritten clone method.
     *
     * @return Returns a new Bitset with the same length and same BitSet as the original.
     */
    @Override
    public Object clone() {
        return new Gene(this.bitSet, this.length);
    }

    /**
     * Default toString method of the Gene class.
     * The default behavior is to build a string representation of the bits. i.e. "1010101101".
     *
     * @return String of bits.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(bitSet.get(i) ? "1" : "0");
        }
        return sb.toString();
    }
}
