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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Bitstream {

    public static Bitstream create(String filename) {
        return new Bitstream(read(filename));
    }

    protected static byte[] read(String filename) {
        try {
            return Files.readAllBytes(Paths.get(filename));
        } catch(IOException ex) {
            return new byte[0];
        }
    }

    private final byte[] bitstream;

    protected Bitstream(byte[] bitstream) {
        this.bitstream = bitstream;
    }

    public long read(long offset, long bits) {
        return read(offset) & ((1L << bits) - 1L);
    }

    public long readVBR(long offset, long width) {
        long value = 0;
        long shift = 0;
        long datum;
        long dmask = 1 << (width - 1);
        do {
            datum = read(offset, width);
            offset += width;
            value += (datum & (dmask - 1)) << shift;
            shift += width - 1;
        } while((datum & dmask) != 0);
        return value;
    }

    public long size() {
        return bitstream.length << 3;
    }

    public long widthVBR(long value, long width) {
        long total = 0;
        do {
            total += width;
            value >>>= (width - 1);
        } while(value != 0);
        return total;
    }

    private long read(long offset) {
        long div = offset >>> 3;
        long value = (readAlignedByte(div))
                + (readAlignedByte(div + 1) << 8)
                + (readAlignedByte(div + 2) << 16)
                + (readAlignedByte(div + 3) << 24)
                + (readAlignedByte(div + 4) << 32)
                + (readAlignedByte(div + 5) << 40)
                + (readAlignedByte(div + 6) << 48)
                + (readAlignedByte(div + 7) << 56);
        long mod = offset & 7L;
        if (mod != 0) {
            value >>>= mod;
            value += readAlignedByte(div + 8) << (64 - mod);
        }
        return value;
    }

    private long readAlignedByte(long i) {
        return i < bitstream.length ? bitstream[(int) i] & 0xffL : 0;
    }
}
