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
package uk.ac.man.cs.llvm.ir.model.constants;

import uk.ac.man.cs.llvm.ir.types.FloatingPointType;

public class FloatingPointConstant extends AbstractConstant {

    private final long bits;

    public FloatingPointConstant(FloatingPointType type, long bits) {
        super(type);
        this.bits = bits;
    }

    public int getBitCount() {
        return ((FloatingPointType) getType()).width();
    }

    public float toFloat() {
        if (getBitCount() == 32) {
            return Float.intBitsToFloat((int) bits);
        } else {
            return (float) toDouble();
        }
    }

    public double toDouble() {
        if (getBitCount() == 32) {
            return Float.intBitsToFloat((int) bits);
        } else {
            // assume double for now
            return Double.longBitsToDouble(bits);
        }
    }

    @Override
    public String toString() {
        return String.format("%.6f", toDouble());
    }
}
