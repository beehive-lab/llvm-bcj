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
package uk.ac.man.cs.llvm.bc;

import java.util.Arrays;
import java.util.Objects;
import uk.ac.man.cs.llvm.bc.blocks.*;

public class Parser {

    protected static final Operation[] DEFAULT_OPERATIONS = new Operation[] {
        Operation.END_BLOCK,
        Operation.ENTER_SUBBLOCK,
        Operation.DEFINE_ABBREV,
        Operation.UNABBREV_RECORD
    };

    private static String CHAR6 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXJZ0123456789._";

    protected final Bitstream stream;

    protected final Block block;

    protected final ParserListener listener;

    protected final Parser parent;

    protected final Operation[][] operations;

    protected final long idsize, offset;

    public Parser(Bitstream stream, Block block, ParserListener listener) {
        this(Objects.requireNonNull(stream), Objects.requireNonNull(block), Objects.requireNonNull(listener), null, new Operation[][] { DEFAULT_OPERATIONS }, 2, 0);
    }

    protected Parser(Parser parser) {
        this(parser.stream, parser.block, parser.listener, parser.parent, parser.operations, parser.idsize, parser.offset);
    }

    protected Parser(Bitstream stream, Block block, ParserListener listener, Parser parent, Operation[][] operations, long idsize, long offset) {
        this.stream = stream;
        this.block = block;
        this.listener = listener;
        this.parent = parent;
        this.operations = operations;
        this.idsize = idsize;
        this.offset = offset;
    }

    protected Parser instantiate(Parser parser, Bitstream stream, Block block, ParserListener listener, Parser parent, Operation[][] operations, long idsize, long offset) {
        return new Parser(stream, block, listener, parent, operations, idsize, offset);
    }

    public Parser align(long bits) {
        long mask = bits - 1;
        if ((offset & mask) != 0) {
            return offset((offset & ~mask) + bits);
        }
        return this;
    }

    public Parser enter(long id, long size, long idsize) {
        Block subblock = Block.lookup(id);
        if (subblock == null) {
            // Cannot find block so just skip it
            return offset(offset() + size * 32);
        } else {
            return subblock.parser(instantiate(this, stream, subblock, listener.enter(subblock), this, operations, idsize, offset()));
        }
    }

    public Parser exit() {
        listener.exit();
        return parent().offset(align(32).offset());
    }

    public long offset() {
        return offset;
    }

    public Parser offset(long offset) {
        return instantiate(this, stream, block, listener, parent, operations, idsize, offset);
    }

    public Operation operation(long id) {
        return operations(block.id())[(int) id];
    }

    public Parser operation(Operation operation) {
        return operation(block.id(), operation, false);
    }

    public Parser operation(long id, Operation operation) {
        return operation(id, operation, true);
    }

    protected Parser operation(long id, Operation operation, boolean persist) {
        Operation[] oldOps = operations(id);
        Operation[] newOps = Arrays.copyOf(oldOps, oldOps.length + 1);

        newOps[oldOps.length] = operation;

        Operation[][] ops = Arrays.copyOf(operations, Math.max(((int) id) + 1, operations.length));
        ops[(int) id] = newOps;

        Parser par = parent;
        if (persist) {
            par = parent.operation(id, operation, parent.parent != null);
        }
        return instantiate(this, stream, block, listener, par, ops, idsize, offset);
    }

    protected Operation[] operations(long blockid) {
        if (blockid < 0 || blockid >= operations.length || operations[(int) blockid] == null) {
            return DEFAULT_OPERATIONS;
        }
        return operations[(int) blockid];
    }

    public Parser parent() {
        return parent;
    }

    public ParserResult read(long bits) {
        long value = stream.read(offset, bits);
        return new ParserResult(offset(offset + bits), value);
    }

    public ParserResult readChar() {
        char value = CHAR6.charAt((int) stream.read(offset, 6));
        return new ParserResult(offset(offset + 6), (long) value);
    }

    public ParserResult readId() {
        long value = stream.read(offset, idsize);
        return new ParserResult(offset(offset + idsize), value);
    }

    public ParserResult readVBR(long width) {
        long value = stream.readVBR(offset, width);
        long total = stream.widthVBR(value, width);
        return new ParserResult(offset(offset + total), value);
    }

    public Parser record(long id, long[] operands) {
        listener.record(id, operands);
        return this;
    }
}
