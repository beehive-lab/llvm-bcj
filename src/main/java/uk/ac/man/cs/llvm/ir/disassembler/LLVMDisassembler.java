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

import uk.ac.man.cs.llvm.ir.LLVMParser;
import uk.ac.man.cs.llvm.ir.model.Model;
import uk.ac.man.cs.llvm.ir.module.ModuleVersion;

public final class LLVMDisassembler {

    private LLVMDisassembler() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            return;
        }

        ModuleVersion version = ModuleVersion.DEFAULT;

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-v")) {
                switch (args[i].substring(2)) {
                    case "3.2":
                        version = ModuleVersion.LLVM_3_2;
                        break;
                    case "3.8":
                        version = ModuleVersion.LLVM_3_8;
                        break;
                    default:
                        version = ModuleVersion.DEFAULT;
                        break;
                }
            } else {
                Model model = new Model();

                new LLVMParser(model).parse(version, args[i]);

                model.accept(new LLVMDisassemblerVisitor(System.out));
            }
        }
    }
}
