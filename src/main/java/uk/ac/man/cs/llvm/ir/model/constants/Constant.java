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

import uk.ac.man.cs.llvm.bc.records.Records;
import uk.ac.man.cs.llvm.ir.model.Symbol;
import uk.ac.man.cs.llvm.ir.types.*;

public interface Constant extends Symbol {

    public static Constant createFromData(Type type, long datum) {
        if (type instanceof IntegerType) {
            IntegerType t = (IntegerType) type;

            // Sign extend for everything except i1 (boolean)
            int bits = t.getBitCount();
            if (bits > 1 && bits < 64) {
                datum = Records.extendSign(bits, datum);
            }

            return new IntegerConstant(t, datum);
        }

        if (type instanceof FloatingPointType) {
            return new FloatingPointConstant((FloatingPointType) type, datum);
        }

        throw new RuntimeException("No datum constant implementation for " + type);
    }

    public static Constant createFromData(Type type, long[] data) {
        if (type instanceof ArrayType) {
            ArrayType array = (ArrayType) type;
            Type subtype = array.getElementType();
            Constant[] elements = new Constant[data.length];
            for (int i = 0; i < data.length; i++) {
                elements[i] = createFromData(subtype, data[i]);
            }
            return new ArrayConstant(array, elements);
        }

        if (type instanceof VectorType) {
            VectorType vector = (VectorType) type;
            Type subtype = vector.getElementType();
            Constant[] elements = new Constant[data.length];
            for (int i = 0; i < data.length; i++) {
                elements[i] = createFromData(subtype, data[i]);
            }
            return new VectorConstant(vector, elements);
        }

        throw new RuntimeException("No data constant implementation for " + type);
    }

    public static Constant createFromValues(Type type, Constant[] values) {
        if (type instanceof ArrayType) {
            return new ArrayConstant((ArrayType) type, values);
        }

        if (type instanceof StructureType) {
            return new StructureConstant((StructureType) type, values);
        }

        if (type instanceof VectorType) {
            return new VectorConstant((VectorType) type, values);
        }

        throw new RuntimeException("No value constant implementation for " + type);
    }
}
