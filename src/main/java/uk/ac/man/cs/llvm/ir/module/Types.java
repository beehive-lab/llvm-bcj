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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import uk.ac.man.cs.llvm.bc.ParserListener;
import uk.ac.man.cs.llvm.bc.records.Records;
import uk.ac.man.cs.llvm.ir.ModuleGenerator;
import uk.ac.man.cs.llvm.ir.module.records.TypesRecord;
import uk.ac.man.cs.llvm.ir.types.*;

public final class Types implements ParserListener, Iterable<Type> {

    public static Type[] toTypes(Types types, long[] args, long from, long to) {
        Type[] t = new Type[(int) (to - from)];

        for (int i = 0; i < t.length; i++) {
            t[i] = types.get(args[(int) from + i]);
        }

        return t;
    }

    private final ModuleGenerator generator;

    private Type[] table = new Type[0];

    private int size = 0;

    public Types(ModuleGenerator generator) {
        this.generator = generator;
    }

    @Override
    public Iterator<Type> iterator() {
        return Collections.unmodifiableList(Arrays.asList(table)).iterator();
    }

    @Override
    public void record(long id, long[] args) {
        TypesRecord record = TypesRecord.decode(id);
        Type type;

        switch (record) {
            case NUMBER_OF_ENTRIES:
                table = new Type[(int) args[0]];
                return;

            case VOID:
                type = MetaType.VOID;
                break;

            case FLOAT:
                type = FloatingPointType.FLOAT;
                break;

            case DOUBLE:
                type = FloatingPointType.DOUBLE;
                break;

            case LABEL:
                type = MetaType.LABEL;
                break;

            case OPAQUE:
                type = MetaType.OPAQUE;
                break;

            case INTEGER:
                type = new IntegerType((int) args[0]);
                break;

            case POINTER: {
                int idx = (int) args[0];

                if (idx > size) {
                    table[size] = new PointerType(null);
                    table[idx] = new UnresolvedPointeeType(size);
                    size++;
                    return;
                } else {
                    type = new PointerType(get(idx));
                }
                break;
            }
            case FUNCTION_OLD:
                type = new FunctionType(get(args[2]), toTypes(this, args, 3, args.length), args[0] != 0);
                break;

            case HALF:
                type = FloatingPointType.HALF;
                break;

            case ARRAY:
                type = new ArrayType(get(args[1]), (int) args[0]);
                break;

            case VECTOR:
                type = new VectorType(get(args[1]), (int) args[0]);
                break;

            case X86_FP80:
                type = FloatingPointType.X86_FP80;
                break;

            case FP128:
                type = FloatingPointType.FP128;
                break;

            case PPC_FP128:
                type = FloatingPointType.PPC_FP128;
                break;

            case METADATA:
                type = MetaType.METADATA;
                break;

            case X86_MMX:
                type = MetaType.X86_MMX;
                break;

            case STRUCT_ANON:
                type = new StructureType(args[0] != 0, toTypes(this, args, 1, args.length));
                break;

            case STRUCT_NAME: {
                String name = Records.toString(args);
                if (table[size] instanceof UnresolvedPointeeType) {
                    table[size] = new UnresolvedNamedPointeeType(name, ((UnresolvedPointeeType) table[size]).getIndex());
                } else {
                    table[size] = new UnresolvedNamedType(name);
                }
                return;
            }
            case STRUCT_NAMED: {
                StructureType structure = new StructureType(args[0] != 0, toTypes(this, args, 1, args.length));
                if (table[size] != null) {
                    if (table[size] instanceof UnresolvedNamedPointeeType) {
                        structure.setName(((UnresolvedNamedPointeeType) table[size]).getName());
                    } else {
                        structure.setName(((UnresolvedNamedType) table[size]).getName());
                    }
                }
                type = structure;
                break;
            }
            case FUNCTION:
                type = new FunctionType(get(args[1]), toTypes(this, args, 2, args.length), args[0] != 0);
                break;

            case TOKEN:
                type = MetaType.TOKEN;
                break;

            default:
                type = MetaType.UNKNOWN;
                break;
        }

        if (table[size] instanceof UnresolvedPointeeType) {
            PointerType pointer = (PointerType) table[((UnresolvedPointeeType) table[size]).getIndex()];
            pointer.setPointeeType(type);
            generator.createType(pointer);
        }
        table[size++] = type;
        generator.createType(type);
    }

    public int size() {
        return table.length;
    }

    public Type get(long index) {
        return table[(int) index];
    }

    public <T extends Type> T type(long index, Class<T> type) {
        Type t = get(index);

        if (!type.isInstance(t)) {
            throw new IllegalArgumentException(t + " is not expected type " + type.getSimpleName());
        }

        return (T) t;
    }

    private static class UnresolvedPointeeType implements Type {

        private final int idx;

        public UnresolvedPointeeType(int idx) {
            this.idx = idx;
        }

        public int getIndex() {
            return idx;
        }

        @Override
        public int sizeof() {
            return 0;
        }
    }

    private static final class UnresolvedNamedPointeeType extends UnresolvedPointeeType {

        private final String name;

        public UnresolvedNamedPointeeType(String name, int idx) {
            super(idx);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static final class UnresolvedNamedType implements Type {

        private final String name;

        public UnresolvedNamedType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public int sizeof() {
            return 0;
        }
    }
}
