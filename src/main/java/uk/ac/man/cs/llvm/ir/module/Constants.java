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
import uk.ac.man.cs.llvm.bc.records.Records;
import uk.ac.man.cs.llvm.ir.ConstantGenerator;
import uk.ac.man.cs.llvm.ir.module.records.ConstantsRecord;
import uk.ac.man.cs.llvm.ir.types.FloatingPointType;
import uk.ac.man.cs.llvm.ir.types.IntegerConstantType;
import uk.ac.man.cs.llvm.ir.types.IntegerType;
import uk.ac.man.cs.llvm.ir.types.Type;

public class Constants implements ParserListener {

    protected final Types types;

    protected final List<Type> symbols;

    protected final ConstantGenerator generator;

    protected Type type;

    public Constants(Types types, List<Type> symbols, ConstantGenerator generator) {
        this.types = types;
        this.symbols = symbols;
        this.generator = generator;
    }

    @Override
    public void record(long id, long[] args) {
        ConstantsRecord record = ConstantsRecord.decode(id);

        switch (record) {
            case SETTYPE:
                type = types.get(args[0]);
                return;

            case NULL:
                generator.createNull(type);
                if (type instanceof IntegerType) {
                    symbols.add(new IntegerConstantType((IntegerType) type, 0));
                    return;
                }
                break;

            case UNDEF:
                generator.createUndefined(type);
                break;

            case INTEGER: {
                long value = Records.toSignedValue(args[0]);
                generator.createInteger(type, value);
                symbols.add(new IntegerConstantType((IntegerType) type, value));
                return;
            }
            case FLOAT:
                generator.createFloatingPoint((FloatingPointType) type, args[0]);
                break;

            case AGGREGATE: {
                generator.createFromValues(type, Records.toIntegers(args));
                break;
            }
            case STRING:
                generator.creatFromString(type, Records.toString(args), false);
                break;

            case CSTRING:
                generator.creatFromString(type, Records.toString(args), true);
                break;

            case CE_BINOP:
                generator.createBinaryOperationExpression(type, (int) args[0], (int) args[1], (int) args[2]);
                break;

            case CE_CAST:
                generator.createCastExpression(type, (int) args[0], (int) args[2]);
                break;

            case CE_CMP:
                generator.createCompareExpression(type, (int) args[3], (int) args[1], (int) args[2]);
                break;

            case CE_GEP:
                createGetElementPointerExpression(args, false);
                break;

            case CE_INBOUNDS_GEP:
                createGetElementPointerExpression(args, true);
                break;

            case BLOCKADDRESS:
                generator.createBlockAddress(type, (int) args[1], (int) args[2]);
                break;

            case DATA:
                generator.createFromData(type, args);
                break;

            default:
                break;
        }
        symbols.add(type);
    }

    protected void createGetElementPointerExpression(long[] args, boolean isInbounds) {
        int[] indices = new int[(args.length - 3) >> 1];

        for (int i = 0; i < indices.length; i++) {
            indices[i] = (int) args[4 + (i << 1)];
        }

        generator.createGetElementPointerExpression(type, (int) args[2], indices, isInbounds);
    }
}
