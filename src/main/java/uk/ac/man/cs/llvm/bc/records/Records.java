/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package uk.ac.man.cs.llvm.bc.records;

public final class Records {

    private Records() {
    }

    public static long extendSign(int bits, long value) {
        long mask = (((1L << (bits)) - 1) ^ -1L) >> 1;
        if ((value & mask) != 0) {
            value |= mask;
        }
        return value;
    }

    public static int toAlignment(long value) {
        return value > 0 ? 1 << (value - 1) : 0;
    }

    public static int[] toIntegers(long[] args) {
        int[] values = new int[args.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = (int) args[i];
        }
        return values;
    }

    public static long toSignedValue(long value) {
        if ((value & 1L) == 1L) {
            value = value >>> 1;
            return value == 0 ? Long.MIN_VALUE : -value;
        } else {
            return value >>> 1;
        }
    }

    public static String toString(long[] operands) {
        return toString(operands, 0, operands.length);
    }

    public static String toString(long[] operands, int from) {
        return toString(operands, from, operands.length);
    }

    public static String toString(long[] operands, int from, int to) {
        StringBuilder string = new StringBuilder();

        for (int i = from; i < to; i++) {
            string.append((char) operands[i]);
        }

        return string.toString();
    }
}
