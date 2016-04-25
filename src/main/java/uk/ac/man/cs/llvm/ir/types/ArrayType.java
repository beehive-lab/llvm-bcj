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

import java.util.Objects;

public class ArrayType implements AggregateType {

    public final Type type;

    private final int size;

    public ArrayType(Type type, int size) {
        super();
        this.type = type;
        this.size = size;
    }

    @Override
    public int getAlignment() {
        return type.getAlignment();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayType) {
            ArrayType other = (ArrayType) obj;
            return size == other.size
                    && type.equals(other.type);
        }
        return false;
    }

    public Type getElementType() {
        return type;
    }

    @Override
    public Type getElementType(int idx) {
        return getElementType();
    }

    @Override
    public int getLength() {
        return size;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.type);
        hash = 67 * hash + this.size;
        return hash;
    }

    @Override
    public int sizeof() {
        return size * type.sizeof();
    }

    @Override
    public int sizeof(int alignment) {
        return size * type.sizeof(alignment);
    }

    @Override
    public String toString() {
        return String.format("[%d x %s]", getLength(), getElementType());
    }
}
