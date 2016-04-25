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
package uk.ac.man.cs.llvm.bc.blocks;

import uk.ac.man.cs.llvm.bc.Bitstream;
import uk.ac.man.cs.llvm.bc.Operation;
import uk.ac.man.cs.llvm.bc.Parser;
import uk.ac.man.cs.llvm.bc.ParserListener;

public final class InformationBlockParser extends Parser {

    private final long bid;

    public InformationBlockParser(Parser parser) {
        super(parser);
        bid = 0;
    }

    protected InformationBlockParser(Bitstream stream, Block block, ParserListener listener, Parser parent, Operation[][] operations, long idsize, long offset, long bid) {
        super(stream, block, listener, parent, operations, idsize, offset);
        this.bid = bid;
    }

    @Override
    protected Parser instantiate(Parser parser, Bitstream stream, Block block, ParserListener listener, Parser parent, Operation[][] operations, long idsize, long offset) {
        long b = parser instanceof InformationBlockParser ? ((InformationBlockParser) parser).bid : 0;
        return new InformationBlockParser(stream, block, listener, parent, operations, idsize, offset, b);
    }

    @Override
    public Parser operation(Operation operation) {
        return operation(bid, operation, true);
    }

    @Override
    public Parser record(long id, long[] operands) {
        if (id == 1) {
            // SETBID selects which block subsequent abbreviations are assigned
            return new InformationBlockParser(stream, block, listener, parent, operations, idsize, offset, operands[0]);
        } else {
            return super.record(id, operands);
        }
    }
}
