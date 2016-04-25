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
package uk.ac.man.cs.llvm.ir;

import uk.ac.man.cs.llvm.bc.Bitstream;
import uk.ac.man.cs.llvm.bc.Operation;
import uk.ac.man.cs.llvm.bc.Parser;
import uk.ac.man.cs.llvm.bc.ParserResult;
import uk.ac.man.cs.llvm.bc.blocks.Block;
import uk.ac.man.cs.llvm.ir.module.Module;
import uk.ac.man.cs.llvm.ir.module.ModuleVersion;

public final class LLVMParser {

    private static final long MAGIC_WORD = 0xdec04342L; // 'BC' c0de

    private final ApplicationGenerator generator;

    public LLVMParser(ApplicationGenerator generator) {
        this.generator = generator;
    }

    public void parse(String bitcode) {
        parse(ModuleVersion.DEFAULT, bitcode);
    }

    public void parse(ModuleVersion version, String bitcode) {
        Bitstream stream = Bitstream.create(bitcode);

        Module module = version.createModule(generator.createModule());

        Parser parser = new Parser(stream, Block.ROOT, module);

        ParserResult result = parser.read(32);
        parser = result.parser();

        if (result.value() != MAGIC_WORD) {
            generator.error("Illegal file (does not exist or contains no magic word)");
        }

        while (parser.offset() < stream.size()) {
            result = parser.readId();
            Operation operation = parser.operation(result.value());
            parser = operation.apply(result.parser());
        }
    }
}
