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
package uk.ac.man.cs.llvm.ir.disassembler;

import java.util.List;
import uk.ac.man.cs.llvm.ir.model.FunctionParameter;
import uk.ac.man.cs.llvm.ir.model.Symbol;
import uk.ac.man.cs.llvm.ir.model.ValueSymbol;
import uk.ac.man.cs.llvm.ir.model.constants.*;
import uk.ac.man.cs.llvm.ir.model.enums.*;
import uk.ac.man.cs.llvm.ir.types.FloatingPointType;
import uk.ac.man.cs.llvm.ir.types.IntegerType;
import uk.ac.man.cs.llvm.ir.types.PointerType;
import uk.ac.man.cs.llvm.ir.types.StructureType;
import uk.ac.man.cs.llvm.ir.types.Type;
import uk.ac.man.cs.llvm.ir.types.VectorType;

public final class LLVMDisassemblerHelper {

    private LLVMDisassemblerHelper() {
    }

    public static String toString(CastOperator operator) {
        switch (operator) {
            case TRUNCATE: return "trunc";
            case ZERO_EXTEND: return "zext";
            case SIGN_EXTEND: return "sext";
            case FP_TO_UNSIGNED_INT: return "fptoui";
            case FP_TO_SIGNED_INT: return "fptosi";
            case UNSIGNED_INT_TO_FP: return "uitofp";
            case SIGNED_INT_TO_FP: return "sitofp";
            case FP_TRUNCATE: return "fptrunc";
            case FP_EXTEND: return "fpext";
            case PTR_TO_INT: return "ptrtoint";
            case INT_TO_PTR: return "inttoptr";
            case BITCAST: return "bitcast";
            case ADDRESS_SPACE_CAST: return "addrspacecast";
            default:
                throw new IllegalArgumentException("Cannot print " + operator);
        }
    }

    public static String toString(Symbol symbol) {
        if (symbol instanceof ValueSymbol) {
            return ((ValueSymbol) symbol).getName();
        }
        if (symbol instanceof ArrayConstant) {
            ArrayConstant array = (ArrayConstant) symbol;
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            Type type = array.getType().getElementType();
            for (int i = 0; i < array.getElementCount(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(type).append(" ").append(toString(array.getElement(i)));
            }
            return sb.append("]").toString();
        }
        if (symbol instanceof BinaryOperationConstant) {
            BinaryOperationConstant operator = (BinaryOperationConstant) symbol;
            return String.format("%s (%s %s, %s %s)",
                    toString(operator.getOperator(), new Flag[0]),
                    operator.getLHS().getType(), toString(operator.getLHS()),
                    operator.getRHS().getType(), toString(operator.getRHS()));
        }
        if (symbol instanceof CastConstant) {
            CastConstant cast = (CastConstant) symbol;
            return String.format("%s (%s %s to %s)",
                    toString(cast.getOperator()),
                    cast.getValue().getType(), toString(cast.getValue()),
                    cast.getType());
        }
        if (symbol instanceof CompareConstant) {
            CompareConstant compare = (CompareConstant) symbol;
            return String.format("%s (%s %s, %s %s)",
                    toString(compare.getLHS().getType(), compare.getOperator()),
                    compare.getLHS().getType(), toString(compare.getLHS()),
                    compare.getRHS().getType(), toString(compare.getRHS()));
        }
        if (symbol instanceof GetElementPointerConstant) {
            GetElementPointerConstant pointer = (GetElementPointerConstant) symbol;
            StringBuilder str = new StringBuilder(
                    String.format("getelementptr %s(%s, %s %s",
                        pointer.isInbounds() ? "inbounds " : "",
                        ((PointerType) pointer.getBasePointer().getType()).getPointeeType(),
                        pointer.getBasePointer().getType(), toString(pointer.getBasePointer())));

            for (int i = 0; i < pointer.getIndexCount(); i++) {
                Symbol index = pointer.getIndex(i);
                str.append(String.format(", %s %s", index.getType(), toString(index)));
            }
            return str.append(")").toString();
        }
        if (symbol instanceof BlockAddressConstant) {
            BlockAddressConstant block = (BlockAddressConstant) symbol;
            return String.format("blockaddress(%s, %%%s)",
                    toString(block.getMethod()),
                    toString(block.getBlock()));
        }
        if (symbol instanceof NullConstant) {
            return toNullConstantString(symbol.getType());
        }
        return symbol.toString();
    }

    public static String toString(Type type, CompareOperator operator) {
        String string = type instanceof FloatingPointType ? "fcmp" : "icmp";

        switch (operator) {
            case FP_FALSE: string += " false"; break;
            case FP_ORDERED_EQUAL: string += " oeq"; break;
            case FP_ORDERED_GREATER_THAN: string += " ogt"; break;
            case FP_ORDERED_GREATER_OR_EQUAL: string += " oge"; break;
            case FP_ORDERED_LESS_THAN: string += " olt"; break;
            case FP_ORDERED_LESS_OR_EQUAL: string += " ole"; break;
            case FP_ORDERED_NOT_EQUAL: string += " one"; break;
            case FP_ORDERED: string += " ord"; break;
            case FP_UNORDERED: string += " uno"; break;
            case FP_UNORDERED_EQUAL: string += " ueq"; break;
            case FP_UNORDERED_GREATER_THAN:
            case INT_UNSIGNED_GREATER_THAN: string += " ugt"; break;
            case FP_UNORDERED_GREATER_OR_EQUAL:
            case INT_UNSIGNED_GREATER_OR_EQUAL: string += " uge"; break;
            case FP_UNORDERED_LESS_THAN:
            case INT_UNSIGNED_LESS_THAN: string += " ult"; break;
            case FP_UNORDERED_LESS_OR_EQUAL:
            case INT_UNSIGNED_LESS_OR_EQUAL: string += " ule"; break;
            case FP_UNORDERED_NOT_EQUAL: string += " une"; break;
            case FP_TRUE: string += " true"; break;
            case INT_EQUAL: string += " eq"; break;
            case INT_NOT_EQUAL: string += " ne"; break;
            case INT_SIGNED_GREATER_THAN: string += " sgt"; break;
            case INT_SIGNED_GREATER_OR_EQUAL: string += " sge"; break;
            case INT_SIGNED_LESS_THAN: string += " slt"; break;
            case INT_SIGNED_LESS_OR_EQUAL: string += " sle"; break;
            default:
                throw new IllegalArgumentException("Cannot print " + operator);
        }

        return string;
    }

    public static String toNullConstantString(Type type) {
        if (type instanceof IntegerType) {
            if (((IntegerType) type).getBitCount() == 1) {
                return "false";
            } else {
                return "0";
            }
        }
        if (type instanceof FloatingPointType) {
            return "0.000000";
        }
        if (type instanceof StructureType) {
            return "zeroinitializer";
        }
        if (type instanceof VectorType) {
            VectorType vector = (VectorType) type;
            StringBuilder sb = new StringBuilder("<");
            String element = String.format("%s %s",
                        vector.getElementType(),
                        toNullConstantString(vector.getElementType()));
            for (int i = 0; i < vector.getElementCount(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(element);
            }
            return sb.append(">").toString();
        }
        return "null";
    }

    public static String toString(Flag[] flags) {
        String string = "";
        for (Flag flag : flags) {
            string += " " + toString(flag);
        }
        return string;
    }

    public static String toString(Flag flag) {
        switch(flag) {
            case INT_EXACT: return "exact";
            case INT_NO_UNSIGNED_WRAP: return "nuw";
            case INT_NO_SIGNED_WRAP: return "nsw";
            case FP_NO_NANS: return "nnan";
            case FP_NO_INFINITIES: return "ninf";
            case FP_NO_SIGNED_ZEROES: return "nsz";
            case FP_ALLOW_RECIPROCAL: return "arcp";
            case FP_FAST: return "fast";
            default:
                throw new IllegalArgumentException("Cannot print " + flag);
        }
    }

    public static String toString(BinaryOperator operator, Flag[] flags) {
        String string;
        switch(operator) {
            case INT_ADD: string = "add"; break;
            case INT_SUBTRACT: string = "sub"; break;
            case INT_MULTIPLY: string = "mul"; break;
            case INT_UNSIGNED_DIVIDE: string = "udiv"; break;
            case INT_SIGNED_DIVIDE: string = "sdiv"; break;
            case INT_UNSIGNED_REMAINDER: string = "urem"; break;
            case INT_SIGNED_REMAINDER: string = "srem"; break;
            case INT_SHIFT_LEFT: string = "shl"; break;
            case INT_LOGICAL_SHIFT_RIGHT: string = "lshr"; break;
            case INT_ARITHMETIC_SHIFT_RIGHT: string = "ashr"; break;
            case INT_AND: string = "and"; break;
            case INT_OR: string = "or"; break;
            case INT_XOR: string = "xor"; break;
            case FP_ADD: string = "fadd"; break;
            case FP_SUBTRACT: string = "fsub"; break;
            case FP_MULTIPLY: string = "fmul"; break;
            case FP_DIVIDE: string = "fdiv"; break;
            case FP_REMAINDER: string = "frem"; break;
            default:
                throw new IllegalArgumentException("Cannot print " + operator);
        }

        return string + toString(flags);
    }

    public static String toString(Type[] args, boolean isVarArg) {
        String string = "";
        for (Type arg : args) {
            if (string.length() > 0) {
                string += ", ";
            }
            string += arg;
        }
        if (isVarArg) {
            if (string.length() > 0) {
                string += ", ";
            }
            string  += "...";
        }
        return string;
    }

    public static String toString(List<FunctionParameter> parameters, boolean isVarArg) {
        String string = "";
        for (FunctionParameter arg : parameters) {
            if (string.length() > 0) {
                string += ", ";
            }
            string += arg.getType() + " " + arg.getName();
        }
        if (isVarArg) {
            if (string.length() > 0) {
                string += ", ";
            }
            string  += "...";
        }
        return string;
    }
}
