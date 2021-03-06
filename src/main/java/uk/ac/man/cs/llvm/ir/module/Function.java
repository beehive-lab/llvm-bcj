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
import uk.ac.man.cs.llvm.bc.ParserListener;
import uk.ac.man.cs.llvm.bc.blocks.Block;
import uk.ac.man.cs.llvm.bc.records.Records;
import uk.ac.man.cs.llvm.ir.FunctionGenerator;
import uk.ac.man.cs.llvm.ir.InstructionGenerator;
import uk.ac.man.cs.llvm.ir.module.records.FunctionRecord;
import uk.ac.man.cs.llvm.ir.types.*;

public class Function implements ParserListener {

    private final ModuleVersion version;

    protected final FunctionGenerator generator;

    protected final Types types;

    protected final List<Type> symbols;

    protected final int mode;

    protected InstructionGenerator code;

    public Function(ModuleVersion version, Types types, List<Type> symbols, FunctionGenerator generator, int mode) {
        this.version = version;
        this.types = types;
        this.symbols = symbols;
        this.generator = generator;
        this.mode = mode;
    }

    @Override
    public ParserListener enter(Block block) {
        switch (block) {
            case CONSTANTS:
                return version.createConstants(types, symbols, generator);

            case VALUE_SYMTAB:
                return new ValueSymbolTable(generator);

            default:
                return ParserListener.DEFAULT;
        }
    }

    @Override
    public void exit() {
        generator.exitFunction();
    }

    @Override
    public void record(long id, long[] args) {
        FunctionRecord record = FunctionRecord.decode(id);

        if (record == FunctionRecord.DECLAREBLOCKS) {
            generator.allocateBlocks((int) args[0]);
            return;
        }

        if (code == null) {
            code = generator.generateBlock();
        }

        switch (record) {
            case BINOP:
                createBinaryOperation(args);
                break;

            case CAST:
                createCast(args);
                break;

            case GEP_OLD:
                createGetElementPointerOld(args, false);
                break;

            case EXTRACTELT:
                createExtractElement(args);
                break;

            case INSERTELT:
                createInsertElement(args);
                break;

            case SHUFFLEVEC:
                createShuffleVector(args);
                break;

            case RET:
                createReturn(args);
                break;

            case BR:
                createBranch(args);
                break;

            case SWITCH:
                createSwitch(args);
                break;

            case UNREACHABLE:
                createUnreachable(args);
                break;

            case PHI:
                createPhi(args);
                break;

            case ALLOCA:
                createAllocation(args);
                break;

            case LOAD:
                createLoad(args);
                break;

            case STORE_OLD:
                createStoreOld(args);
                break;

            case EXTRACTVAL:
                createExtractValue(args);
                break;

            case INSERTVAL:
                createInsertValue(args);
                break;

            case CMP2:
                createCompare2(args);
                break;

            case VSELECT:
                createSelect(args);
                break;

            case INBOUNDS_GEP_OLD:
                createGetElementPointerOld(args, true);
                break;

            case INDIRECTBR:
                createIndirectBranch(args);
                break;

            case CALL:
                crateCall(args);
                break;

            case GEP:
                createGetElementPointer(args);
                break;

            case STORE:
                createStore(args);
                break;

            default:
                System.out.printf("BLOCK #12-METHOD: INSTRUCTION %s%n", record);
                break;
        }
    }

    protected void createAllocation(long[] args) {
        PointerType type = new PointerType(types.get(args[0]));
        int count = getIndexV0(args[2]);
        int align = getAlign(args[3]);

        code.createAllocation(type, count, align);

        symbols.add(type);
    }

    protected void createBinaryOperation(long[] args) {
        int i = 0;
        Type type;
        int lhs = getIndex(args[i++]);
        if (lhs < symbols.size()) {
            type = symbols.get(lhs).getType();
        } else {
            type = types.get(args[i++]);
        }
        int rhs = getIndex(args[i++]);
        int opcode = (int) args[i++];
        int flags = i < args.length ? (int) args[i] : 0;

        code.createBinaryOperation(type, opcode, flags, lhs, rhs);

        symbols.add(type);
    }

    protected void createBranch(long[] args) {
        if (args.length == 1) {
            code.createBranch((int) args[0]);
        } else {
            code.createBranch(getIndex(args[2]), (int) args[0], (int) args[1]);
        }

        code.exitBlock();
        code = null;
    }

    protected void crateCall(long[] args) {
        FunctionType method = types.type(args[2], FunctionType.class);

        int target = getIndex(args[3]);
        int[] arguments = new int[args.length - 4];
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = getIndex(args[i + 4]);
        }

        Type type = method.getReturnType();

        code.createCall(type, target, arguments);

        if (type != MetaType.VOID) {
            symbols.add(type);
        }
    }

    protected void createCast(long[] args) {
        int i = 0;
        int value = getIndex(args[i++]);
        if (value >= symbols.size()) {
            i++;
        }
        Type type = types.get(args[i++]);
        int opcode = (int) args[i++];

        code.createCast(type, opcode, value);

        symbols.add(type);
    }

    protected void createCompare2(long[] args) {
        int i = 0;
        Type operandType;
        int lhs = getIndex(args[i++]);
        if (lhs < symbols.size()) {
            operandType = symbols.get(lhs).getType();
        } else {
            operandType = types.get(args[i++]);
        }
        int rhs = getIndex(args[i++]);
        int opcode = (int) args[i++];

        Type type = operandType instanceof VectorType
                ? new VectorType(IntegerType.BOOLEAN, ((VectorType) operandType).getElementCount())
                : IntegerType.BOOLEAN;

        code.createCompare(type, opcode, lhs, rhs);

        symbols.add(type);
    }

    protected void createExtractElement(long[] args) {
        int vector = getIndex(args[0]);
        int index = getIndex(args[1]);

        Type type = ((VectorType) symbols.get(vector).getType()).getElementType();

        code.createExtractElement(type, vector, index);

        symbols.add(type);
    }

    protected void createExtractValue(long[] args) {
        int aggregate = getIndex(args[0]);
        int index = (int) args[1];

        Type type = ((AggregateType) symbols.get(aggregate).getType()).getElementType(index);

        code.createExtractValue(type, aggregate, index);

        symbols.add(type);
    }

    protected void createGetElementPointer(long[] args) {
        boolean isInbounds = args[0] != 0;
        int pointer = getIndex(args[2]);
        int[] indices = getIndices(args, 3);

        Type type = new PointerType(getElementPointerType(symbols.get(pointer).getType(), indices));

        code.createGetElementPointer(
                type,
                pointer,
                indices,
                isInbounds);

        symbols.add(type);
    }

    protected void createGetElementPointerOld(long[] args, boolean isInbounds) {
        int i = 0;
        int pointer = getIndex(args[i++]);
        Type base;
        if (pointer < symbols.size()) {
            base = symbols.get(pointer).getType();
        } else {
            base = types.get(args[i++]);
        }
        int[] indices = getIndices(args, i);

        Type type = new PointerType(getElementPointerType(base, indices));

        code.createGetElementPointer(
                type,
                pointer,
                indices,
                isInbounds);

        symbols.add(type);
    }

    protected void createIndirectBranch(long[] args) {
        int address = getIndex(args[1]);
        int[] successors = new int[args.length - 2];
        for (int i = 0; i < successors.length; i++) {
            successors[i] = (int) args[i + 2];
        }

        code.createIndirectBranch(address, successors);

        code.exitBlock();
        code = null;
    }

    protected void createInsertElement(long[] args) {
        int vector = getIndex(args[0]);
        int index = getIndex(args[2]);
        int value = getIndex(args[1]);

        Type symbol = symbols.get(vector);

        code.createInsertElement(symbol.getType(), vector, index, value);

        symbols.add(symbol);
    }

    protected void createInsertValue(long[] args) {
        int aggregate = getIndex(args[0]);
        int index = (int) args[2];
        int value = getIndex(args[1]);

        Type symbol = symbols.get(aggregate);

        code.createInsertValue(symbol.getType(), aggregate, index, value);

        symbols.add(symbol);
    }

    protected void createLoad(long[] args) {
        int source = getIndex(args[0]);
        Type type = types.get(args[1]);
        int align = getAlign(args[2]);
        boolean isVolatile = args[3] != 0;

        code.createLoad(type, source, align, isVolatile);

        symbols.add(type);
    }

    protected void createPhi(long[] args) {
        Type type = types.get(args[0]);
        int count = (args.length) - 1 >> 1;
        int[] values = new int[count];
        int[] blocks = new int[count];
        for (int i = 0, j = 1; i < count; i++) {
            values[i] = getIndex(Records.toSignedValue(args[j++]));
            blocks[i] = (int) args[j++];
        }

        code.createPhi(type, values, blocks);

        symbols.add(type);
    }

    protected void createReturn(long[] args) {
        if (args.length == 0 || args[0] == 0) {
            code.createReturn();
        } else {
            code.createReturn(getIndex(args[0]));
        }

        code.exitBlock();
        code = null;
    }

    protected void createSelect(long[] args) {
        int i = 0;
        Type type;
        int trueValue = getIndex(args[i++]);
        if (trueValue < symbols.size()) {
            type = symbols.get(trueValue).getType();
        } else {
            type = types.get(args[i++]);
        }
        int falseValue = getIndex(args[i++]);
        int condition = getIndex(args[i++]);

        code.createSelect(type, condition, trueValue, falseValue);

        symbols.add(type);
    }

    protected void createShuffleVector(long[] args) {
        int vector1 = getIndex(args[0]);
        int vector2 = getIndex(args[1]);
        int mask = getIndex(args[2]);

        Type subtype = ((VectorType) symbols.get(vector1).getType()).getElementType();
        int length = ((VectorType) symbols.get(mask).getType()).getElementCount();
        Type type = new VectorType(subtype, length);

        code.createShuffleVector(type, vector1, vector2, mask);

        symbols.add(type);
    }

    protected void createStore(long[] args) {
        int i = 0;

        int destination = getIndex(args[i++]);
        if (destination > symbols.size()) {
            i++;
        }

        int source = getIndex(args[i++]);
        if (source > symbols.size()) {
            i++;
        }

        int align = getAlign(args[i++]);
        boolean isVolatile = args[i++] != 0;

        code.createStore(destination, source, align, isVolatile);
    }

    protected void createStoreOld(long[] args) {
        int i = 0;

        int destination = getIndex(args[i++]);
        if (destination > symbols.size()) {
            i++;
        }

        int source = getIndex(args[i++]);
        int align = getAlign(args[i++]);
        boolean isVolatile = args[i++] != 0;

        code.createStore(destination, source, align, isVolatile);
    }

    protected void createSwitch(long[] args) {
        int condition = getIndex(args[1]);
        int defaultBlock = (int) args[2];
        int count = (args.length - 3) >> 1;
        int[] caseValues = new int[count];
        int[] caseBlocks = new int[count];

        int i = 3;
        for (int j = 0; j < count; j++) {
            caseValues[j] = getIndexV0(args[i++]);
            caseBlocks[j] = (int) args[i++];
        }

        code.createSwitch(condition, defaultBlock, caseValues, caseBlocks);

        code.exitBlock();
        code = null;
    }

    protected void createUnreachable(long[] args) {
        code.createUnreachable();

        code.exitBlock();
        code = null;
    }

    protected int getAlign(long argument) {
        return (int) argument & (Long.SIZE - 1);
    }

    protected Type getElementPointerType(Type type, int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            if (type instanceof PointerType) {
                type = ((PointerType) type).getPointeeType();
            } else if (type instanceof ArrayType) {
                type = ((ArrayType) type).getElementType();
            } else {
                StructureType structure = (StructureType) type;
                Type idx = symbols.get(indices[i]);
                if (!(idx instanceof IntegerConstantType)) {
                    throw new IllegalStateException("Cannot infer structure element from " + idx);
                }
                type = structure.getElementType((int) ((IntegerConstantType) idx).getValue());
            }
        }
        return type;
    }

    protected int getIndex(long index) {
        if (mode == 0) {
            return getIndexV0(index);
        } else {
            return getIndexV1(index);
        }
    }

    protected int[] getIndices(long[] arguments) {
        return getIndices(arguments, 0, arguments.length);
    }

    protected int[] getIndices(long[] arguments, int from) {
        return getIndices(arguments, from, arguments.length);
    }

    protected int[] getIndices(long[] arguments, int from, int to) {
        int[] indices = new int[to - from];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = getIndex(arguments[from + i]);
        }
        return indices;
    }

    protected int getIndexV0(long index) {
        return (int) index;
    }

    protected int getIndexV1(long index) {
        return symbols.size() - (int) index;
    }
}
