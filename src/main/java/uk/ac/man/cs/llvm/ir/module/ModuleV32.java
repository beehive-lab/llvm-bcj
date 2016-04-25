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
package uk.ac.man.cs.llvm.ir.module;

import uk.ac.man.cs.llvm.ir.ModuleGenerator;
import uk.ac.man.cs.llvm.ir.types.FunctionType;
import uk.ac.man.cs.llvm.ir.types.PointerType;
import uk.ac.man.cs.llvm.ir.types.Type;

public class ModuleV32 extends Module {

    public ModuleV32(ModuleVersion version, ModuleGenerator generator) {
        super(version, generator);
    }

    @Override
    protected void createFunction(long[] args) {
        FunctionType type = (FunctionType) ((PointerType) types.get(args[0])).getPointeeType();
        boolean isPrototype = args[2] != 0;

        generator.createFunction(type, isPrototype);
        symbols.add(type);
        if (!isPrototype) {
            methods.add(type);
        }
    }

    @Override
    protected void createGlobalVariable(long[] args) {
        Type type = types.get(args[0]);
        boolean isConstant = (args[1] & 1) == 1;
        int initialiser = (int) args[2];
        int align = (int) args[4];

        generator.createVariable(type, isConstant, initialiser, align);
        symbols.add(type);
    }
}
