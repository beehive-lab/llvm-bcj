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
package uk.ac.man.cs.llvm.ir.model;

import uk.ac.man.cs.llvm.ir.types.Type;

public abstract class GlobalValueSymbol implements ValueSymbol {

    private final Type type;

    private final int initialiser, align;

    private String name = ValueSymbol.UNKNOWN;

    private Symbol value = null;

    protected GlobalValueSymbol(Type type, int initialiser, int align) {
        this.type = type;
        this.initialiser = initialiser;
        this.align = align;
    }

    protected abstract void accept(ModelVisitor visitor);

    @Override
    public int getAlign() {
        return align;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }

    public Symbol getValue() {
        return value;
    }

    public void initialise(Symbols symbols) {
        if (initialiser > 0) {
            value = symbols.getSymbol(initialiser - 1);
        }
    }

    @Override
    public void setName(String name) {
        this.name = "@" + name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
