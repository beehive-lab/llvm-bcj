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

import uk.ac.man.cs.llvm.ir.model.ValueSymbol;

public final class StructureType implements AggregateType, ValueSymbol {

    private String name = ValueSymbol.UNKNOWN;

    private final boolean isPacked;

    private final Type[] types;

    public StructureType(boolean isPacked, Type[] types) {
        this.isPacked = isPacked;
        this.types = types;
    }

    @Override
    public int getAlignment() {
        return types == null || types.length == 0 ? 8 : types[0].getAlignment();
    }

    @Override
    public Type getElementType(int index) {
        return types[index];
    }

    @Override
    public int getLength() {
        return types.length;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return this;
    }

    public boolean isPacked() {
        return isPacked;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int sizeof() {
        int size = 0;
        for (Type type : types) {
            size += type.sizeof() + calculatePadding(type.getAlignment(), size);
        }
        return size;
    }

    @Override
    public int sizeof(int alignment) {
        int size = 0;
        for (Type type : types) {
            size = size
                    + type.sizeof(alignment)
                    + calculatePadding(Math.min(alignment, type.getAlignment()), size);
        }
        return size;
    }

    public String toDeclarationString() {
        StringBuilder str = new StringBuilder();
        if (isPacked) {
            str.append("<{ ");
        } else {
            str.append("{ ");
        }

        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            if (i > 0) {
                str.append(", ");
            }
            if (type instanceof PointerType
                    && ((PointerType) type).getPointeeType() == this) {
                str.append("%").append(getName()).append("*");
            } else {
                str.append(type);
            }
        }

        if (isPacked) {
            str.append(" }>");
        } else {
            str.append(" }");
        }

        return str.toString();
    }

    @Override
    public String toString() {
        if (name.equals(ValueSymbol.UNKNOWN)) {
            return toDeclarationString();
        } else {
            return "%" + name;
        }
    }

    private int calculatePadding(int alignment, int address) {
        if (isPacked || alignment == 1) {
            return 0;
        }
        int mask = alignment - 1;
        return (alignment - (address & mask)) & mask;
    }
}
