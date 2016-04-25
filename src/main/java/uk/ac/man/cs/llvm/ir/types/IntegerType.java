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
package uk.ac.man.cs.llvm.ir.types;

public final class IntegerType implements Type {

    public static final IntegerType BOOLEAN = new IntegerType(1);

    public static final IntegerType BYTE = new IntegerType(8);

    public static final IntegerType SHORT = new IntegerType(16);

    public static final IntegerType INTEGER = new IntegerType(32);

    public static final IntegerType LONG = new IntegerType(64);

    private final int bits;

    public IntegerType(int bits) {
        super();
        this.bits = bits;
    }

    @Override
    public int getAlignment() {
        if (bits <= 8) {
            return 1;
        } else if (bits <= 16) {
            return 2;
        } else if (bits <= 32) {
            return 4;
        }
        return 8;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntegerType) {
            return bits == ((IntegerType) obj).bits;
        }
        return false;
    }

    public int getBitCount() {
        return bits;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + this.bits;
        return hash;
    }

    @Override
    public int sizeof() {
        return (bits & 7) == 0 ? bits >>> 3 : (bits >>> 3) + 1;
    }

    @Override
    public String toString() {
        return String.format("i%d", bits);
    }
}
