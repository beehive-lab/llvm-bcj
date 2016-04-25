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
import java.util.List;
import uk.ac.man.cs.llvm.ir.FunctionGenerator;
import uk.ac.man.cs.llvm.ir.ModuleGenerator;
import uk.ac.man.cs.llvm.ir.model.constants.*;
import uk.ac.man.cs.llvm.ir.model.enums.BinaryOperator;
import uk.ac.man.cs.llvm.ir.model.enums.CastOperator;
import uk.ac.man.cs.llvm.ir.model.enums.CompareOperator;
import uk.ac.man.cs.llvm.ir.types.*;

public final class ModelModule implements ModuleGenerator {

    private final List<Type> types = new ArrayList<>();

    private final List<GlobalValueSymbol> variables = new ArrayList<>();

    private final List<FunctionDeclaration> declares = new ArrayList<>();

    private final List<FunctionDefinition> defines = new ArrayList<>();

    private final Symbols symbols = new Symbols();

    private int currentMethod = -1;

    public ModelModule() {
    }

    public void accept(ModelVisitor visitor) {
        for (Type type : types) {
            visitor.visit(type);
        }
        for (GlobalValueSymbol variable : variables) {
            variable.accept(visitor);
        }
        for (FunctionDefinition define : defines) {
            visitor.visit(define);
        }
        for (FunctionDeclaration declare : declares) {
            visitor.visit(declare);
        }
    }

    @Override
    public void createBinaryOperationExpression(Type type, int opcode, int lhs, int rhs) {
        boolean isFloatingPoint = type instanceof FloatingPointType
                || (type instanceof VectorType && ((VectorType) type).getElementType() instanceof FloatingPointType);

        symbols.addSymbol(new BinaryOperationConstant(
                type,
                BinaryOperator.decode(opcode, isFloatingPoint),
                symbols.getSymbol(lhs),
                symbols.getSymbol(rhs)));
    }

    @Override
    public void createBlockAddress(Type type, int method, int block) {
        symbols.addSymbol(new BlockAddressConstant(
                type,
                symbols.getSymbol(method),
                null));
    }

    @Override
    public void createCastExpression(Type type, int opcode, int value) {
        CastConstant cast = new CastConstant(type, CastOperator.decode(opcode));

        cast.setValue(symbols.getSymbol(value, cast));

        symbols.addSymbol(cast);
    }

    @Override
    public void createCompareExpression(Type type, int opcode, int lhs, int rhs) {
        CompareConstant compare = new CompareConstant(type, CompareOperator.decode(opcode));

        compare.setLHS(symbols.getSymbol(lhs, compare));
        compare.setRHS(symbols.getSymbol(rhs, compare));

        symbols.addSymbol(compare);
    }

    @Override
    public void createFloatingPoint(Type type, long value) {
        symbols.addSymbol(new FloatingPointConstant((FloatingPointType) type, value));
    }

    @Override
    public void createFromData(Type type, long[] data) {
        symbols.addSymbol(Constant.createFromData(type, data));
    }

    @Override
    public void creatFromString(Type type, String string, boolean isCString) {
        symbols.addSymbol(new StringConstant(type, string, isCString));
    }

    @Override
    public void createFromValues(Type type, int[] values) {
        symbols.addSymbol(Constant.createFromValues(type, symbols.getConstants(values)));
    }

    @Override
    public void createGetElementPointerExpression(Type type, int pointer, int[] indices, boolean isInbounds) {
        GetElementPointerConstant gep = new GetElementPointerConstant(type, isInbounds);

        gep.setBasePointer(symbols.getSymbol(pointer, gep));
        for (int index : indices) {
            gep.addIndex(symbols.getSymbol(index, gep));
        }

        symbols.addSymbol(gep);
    }

    @Override
    public void createInteger(Type type, long value) {
        symbols.addSymbol(new IntegerConstant((IntegerType) type, value));
    }

    @Override
    public void createFunction(FunctionType type, boolean isPrototype) {
        if (isPrototype) {
            FunctionDeclaration method = new FunctionDeclaration(type);
            symbols.addSymbol(method);
            declares.add(method);
        } else {
            FunctionDefinition method = new FunctionDefinition(type);
            symbols.addSymbol(method);
            defines.add(method);
        }
    }

    @Override
    public void createNull(Type type) {
        symbols.addSymbol(new NullConstant(type));
    }

    @Override
    public void createType(Type type) {
        types.add(type);
    }

    @Override
    public void createUndefined(Type type) {
        symbols.addSymbol(new UndefinedConstant(type));
    }

    @Override
    public void createVariable(Type type, boolean isConstant, int initialiser, int align) {
        GlobalValueSymbol variable;
        if (isConstant) {
            variable = new GlobalConstant(type, initialiser, align);
        } else {
            variable = new GlobalVariable(type, initialiser, align);
        }
        symbols.addSymbol(variable);
        variables.add(variable);
    }

    @Override
    public void exitModule() {
        for (GlobalValueSymbol variable : variables) {
            variable.initialise(symbols);
        }
    }

    @Override
    public FunctionGenerator generateFunction() {
        while (++currentMethod < symbols.getSize()) {
            Symbol symbol = symbols.getSymbol(currentMethod);
            if (symbol instanceof FunctionDefinition) {
                FunctionDefinition method = (FunctionDefinition) symbol;
                method.getSymbols().addSymbols(symbols);
                return method;
            }
        }
        throw new RuntimeException("Trying to generate undefined method");
    }

    @Override
    public void nameBlock(int index, String name) {
    }

    @Override
    public void nameEntry(int index, String name) {
        symbols.setSymbolName(index, name);
    }

    @Override
    public void nameFunction(int index, int offset, String name) {
        symbols.setSymbolName(index, name);
    }
}
