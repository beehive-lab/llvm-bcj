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

import java.util.List;
import uk.ac.man.cs.llvm.bc.records.Records;
import uk.ac.man.cs.llvm.ir.FunctionGenerator;
import uk.ac.man.cs.llvm.ir.types.FunctionType;
import uk.ac.man.cs.llvm.ir.types.MetaType;
import uk.ac.man.cs.llvm.ir.types.PointerType;
import uk.ac.man.cs.llvm.ir.types.Type;

public class FunctionV32 extends FunctionV38 {

    public FunctionV32(ModuleVersion version, Types types, List<Type> symbols, FunctionGenerator generator, int mode) {
        super(version, types, symbols, generator, mode);
    }

    @Override
    protected void createAllocation(long[] args) {
        Type type = types.get(args[0]);
        int count = getIndexV0(args[2]);
        int align = getAlign(args[3]);

        code.createAllocation(type, count, align);

        symbols.add(type);
    }

    @Override
    protected void crateCall(long[] args) {
        int target = getIndex(args[2]);
        int[] arguments = new int[args.length - 3];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = getIndex(args[i + 3]);
        }

        Type type = symbols.get(target).getType();

        if (type instanceof PointerType) {
            type = ((PointerType) type).getPointeeType();
        }

        Type returnType = ((FunctionType) type).getReturnType();

        code.createCall(returnType, target, arguments);

        if (returnType != MetaType.VOID) {
            symbols.add(returnType);
        }
    }

    @Override
    protected void createLoad(long[] args) {
        int i = 0;
        int source = getIndex(args[i++]);
        Type type;
        if (source < symbols.size()) {
            type = ((PointerType) symbols.get(source).getType()).getPointeeType();
        } else {
            type = ((PointerType) types.get(args[i++])).getPointeeType();
        }

        int align = getAlign(args[i++]);
        boolean isVolatile = args[i++] != 0;

        code.createLoad(type, source, align, isVolatile);

        symbols.add(type);
    }

    @Override
    protected void createSwitch(long[] args) {
        int condition = getIndex(args[2]);
        int defaultBlock = (int) args[3];
        int count = (int) args[4];
        long[] caseConstants = new long[count];
        int[] caseBlocks = new int[count];

        int i = 5;
        for (int j = 0; j < count; j++) {
            i += 2;
            caseConstants[j] = Records.toSignedValue(args[i++]);
            caseBlocks[j] = (int) args[i++];
        }

        code.createSwitchOld(condition, defaultBlock, caseConstants, caseBlocks);

        code.exitBlock();
        code = null;
    }
}
