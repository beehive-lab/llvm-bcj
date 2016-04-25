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
package uk.ac.man.cs.llvm.ir.model.elements;

import java.util.ArrayList;
import java.util.List;
import uk.ac.man.cs.llvm.ir.model.Block;
import uk.ac.man.cs.llvm.ir.model.InstructionVisitor;
import uk.ac.man.cs.llvm.ir.model.Symbol;
import uk.ac.man.cs.llvm.ir.types.Type;

public final class PhiInstruction extends ValueInstruction {

    private final List<Symbol> values = new ArrayList<>();

    private final List<Block> blocks = new ArrayList<>();

    public PhiInstruction(Type type) {
        super(type);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this);
    }

    public void addCase(Symbol value, Block block) {
        values.add(value);
        blocks.add(block);
    }

    public Block getBlock(int index) {
        return blocks.get(index);
    }

    public int getSize() {
        return values.size();
    }

    public Symbol getValue(int index) {
        return values.get(index);
    }

    @Override
    public void replace(Symbol original, Symbol replacment) {
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == original) {
                values.set(i, replacment);
            }
        }
    }
}
