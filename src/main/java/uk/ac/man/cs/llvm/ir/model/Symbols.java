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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import uk.ac.man.cs.llvm.ir.model.constants.Constant;
import uk.ac.man.cs.llvm.ir.types.MetaType;
import uk.ac.man.cs.llvm.ir.types.Type;

public final class Symbols {

    private Symbol[] symbols;

    private int size;

    public Symbols() {
        symbols = new Symbol[16];
    }

    public void addSymbol(Symbol symbol) {
        ensureCapacity(size + 1);

        if (symbols[size] != null) {
            ((ForwardReference) symbols[size]).replace(symbol);
        }
        symbols[size++] = symbol;
    }

    public void addSymbols(Symbols symbols) {
        for (int i = 0; i < symbols.size; i++) {
            addSymbol(symbols.symbols[i]);
        }
    }

    public Constant getConstant(int index) {
        return (Constant) getSymbol(index);
    }

    public Constant[] getConstants(int[] indices) {
        Constant[] consts = new Constant[indices.length];

        for (int i = 0; i < indices.length; i++) {
            consts[i] = getConstant(indices[i]);
        }

        return consts;
    }

    public int getSize() {
        return size;
    }

    public Symbol getSymbol(int index) {
        if (index < size) {
            return symbols[index];
        }

        throw new IllegalStateException("Dependent required for forward references");
    }

    public Symbol getSymbol(int index, Symbol dependent) {
        if (index < size) {
            return symbols[index];
        } else {
            ensureCapacity(index + 1);

            ForwardReference ref = (ForwardReference) symbols[index];
            if (ref == null) {
                symbols[index] = ref = new ForwardReference();
            }
            ref.addDependent(dependent);

            return ref;
        }
    }

    public Symbol[] getSymbols(int[] indices) {
        Symbol[] syms = new Symbol[indices.length];

        for (int i = 0; i < indices.length; i++) {
            syms[i] = getSymbol(indices[i]);
        }

        return syms;
    }

    public void setSymbolName(int index, String name) {
        Symbol symbol = getSymbol(index);
        if (symbol instanceof ValueSymbol) {
            ((ValueSymbol) symbol).setName(name);
        }
    }

    private void ensureCapacity(int capacity) {
        while (symbols.length < capacity) {
            symbols = Arrays.copyOf(symbols, symbols.length << 1);
        }
    }

    private static class ForwardReference implements Symbol {

        private final List<Symbol> dependents = new ArrayList<>();

        public ForwardReference() {
        }

        public void addDependent(Symbol dependent) {
            dependents.add(dependent);
        }

        public void replace(Symbol symbol) {
            for (Symbol dependent : dependents) {
                dependent.replace(this, symbol);
            }
        }

        @Override
        public Type getType() {
            return MetaType.UNKNOWN;
        }
    }
}
