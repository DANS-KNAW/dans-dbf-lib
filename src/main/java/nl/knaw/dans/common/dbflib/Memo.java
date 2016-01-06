/*
 * Copyright 2009-2010 Data Archiving and Networked Services (DANS), Netherlands.
 *
 * This file is part of DANS DBF Library.
 *
 * DANS DBF Library is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * DANS DBF Library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with DANS DBF Library. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package nl.knaw.dans.common.dbflib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Represents a memo (.DBT) file.
 *
 * @author Vesa Åkerman
 * @author Jan van Mansum
 */
class Memo
{
    /*
     * Offsets.
     */
    private static final int OFFSET_NEXT_AVAILABLE_BLOCK_INDEX = 0;
    private static final int OFFSET_BLOCK_SIZE = 20;

    /*
     * Lengths.
     */
    private static final int DEFAULT_LENGTH_MEMO_BLOCK = 512;
    private static final int LENGTH_FILE_NAME = 8;

    /*
     * Markers.
     */
    private static final byte MARKER_MEMO_END = 0x1a;

    /*
     * Fields.
     */
    private final File memoFile;
    private RandomAccessFile raf = null;
    private int nextAvailableBlock = 0;
    private int blockLength = DEFAULT_LENGTH_MEMO_BLOCK;
    private final Version version;

    /**
     * Creates a new <code>Memo</code> object.
     *
     * @param memoFile the underlying .DBT file
     * @param version the version of DBF to use
     *
     * @throws IllegalArgumentException if <code>memoFile</code> is <code>null</code>
     */
    Memo(final File memoFile, final Version version)
        throws IllegalArgumentException
    {
        if (memoFile == null)
        {
            throw new IllegalArgumentException("Memo file must not be null");
        }

        this.memoFile = memoFile;
        this.version = version;
    }

    void open(final IfNonExistent ifNonExistent)
       throws IOException
    {
        if (memoFile.exists())
        {
            raf = new RandomAccessFile(memoFile, "rw");

            if (version == Version.FOXPRO_26)
            {
                raf.skipBytes(4);
                blockLength = raf.readInt();
            }
        }
        else if (ifNonExistent.isCreate())
        {
            raf = new RandomAccessFile(memoFile, "rw");

            if (version == Version.CLIPPER_5)
            {
                nextAvailableBlock = 2;
            }
            else
            {
                nextAvailableBlock = 1;
            }

            writeMemoHeader();
        }
        else if (ifNonExistent.isError())
        {
            throw new FileNotFoundException("Cannot find memo file");
        }
    }

    /**
     * Closes the memo file for reading and writing.
     *
     * @throws IOException if the file cannot be closed
     */
    void close()
        throws IOException
    {
        if (raf == null)
        {
            return;
        }

        raf.close();
    }

    /**
     * Closes and deletes the underlying memo file.
     *
     * @throws IOException if the file cannot be closed.
     */
    void delete()
         throws IOException
    {
        close();
        memoFile.delete();
    }

    /**
     * Reads a string of characters from memo file.
     *
     * @param blockIndex block number where the string of characters starts
     *
     */
    byte[] readMemo(final int blockIndex)
             throws IOException, CorruptedTableException
    {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int memoLength = 0;
        int c = 0;

        raf.seek(blockIndex * blockLength);

        switch (version)
        {
            case DBASE_3:
            case CLIPPER_5:

                while ((c = raf.read()) != MARKER_MEMO_END)
                {
                    if (c == -1)
                    {
                        throw new CorruptedTableException("Corrupted memo file, EOF exception");
                    }

                    bos.write(c);
                }

                break;

            case DBASE_4:
            case DBASE_5:
            case FOXPRO_26:
                /*
                 * at the beginning of each memo there is a header of 8 bytes. 4 first bytes: dBase -
                 * FFFF0800h, FoxPro - type of data (text/general/picture) 4 last bytes: dBase - offset
                 * to the end of memo (length of data + 8) FoxPro - length of data
                 */
                raf.skipBytes(4);

                if (version == Version.FOXPRO_26)
                {
                    memoLength = raf.readInt();
                }
                else
                {
                    memoLength = Util.changeEndianness(raf.readInt()) - version.getMemoDataOffset();
                }

                for (int i = 0; i < memoLength; i++)
                {
                    c = raf.read();

                    if (c == -1)
                    {
                        throw new CorruptedTableException("Corrupted memo file, EOF exception");
                    }

                    bos.write(c);
                }

                break;

            default:
                assert false : "Programming error, did not handle version " + version.toString();
        }

        return bos.toByteArray();
    }

    /**
     * Writes a string of characters to memo file.
     */
    int writeMemo(final byte[] memoBytes)
           throws IOException
    {
        final int nrBytesToWrite =
            memoBytes.length + version.getMemoFieldEndMarkerLength() + version.getMemoDataOffset();
        int nrBlocksToWrite = nrBytesToWrite / blockLength + 1;
        int nrSpacesToPadLastBlock = blockLength - nrBytesToWrite % blockLength;

        /*
         * Exact fit; we don't need an extra block.
         */
        if (nrSpacesToPadLastBlock == blockLength)
        {
            nrSpacesToPadLastBlock = -blockLength;
            --nrBlocksToWrite;
        }

        final int blockIndex = nextAvailableBlock;

        /*
         * Write the string and end of file markers.
         */
        raf.seek(blockIndex * blockLength);

        if (version == Version.DBASE_4 || version == Version.DBASE_5)
        {
            raf.writeInt(0xffff0800);
            raf.writeInt(Util.changeEndianness(memoBytes.length + version.getMemoDataOffset()));
        }
        else if (version == Version.FOXPRO_26)
        {
            raf.writeInt(1);
            raf.writeInt(memoBytes.length);
        }

        raf.write(memoBytes); // Note: cuts off higher bytes, so assumes ASCII string

        if (version.getMemoFieldEndMarkerLength() != 0)
        {
            if (version.getMemoFieldEndMarkerLength() == 1)
            {
                raf.writeByte(version.getMemoFieldEndMarker());
            }
            else
            {
                raf.writeShort(version.getMemoFieldEndMarker());
            }
        }

        /*
         * Pad the last block with zeros.
         */
        for (int i = 0; i < nrSpacesToPadLastBlock; ++i)
        {
            raf.writeByte(0x00);
        }

        /*
         * Update next available block to write.
         */
        raf.seek(OFFSET_NEXT_AVAILABLE_BLOCK_INDEX);
        nextAvailableBlock += nrBlocksToWrite;

        if (version == Version.FOXPRO_26)
        {
            raf.writeInt(nextAvailableBlock);
        }
        else
        {
            raf.writeInt(Util.changeEndianness(nextAvailableBlock));
        }

        return blockIndex;
    }

    /*
     * Writes a header for a new memo file.
     */
    private void writeMemoHeader()
                          throws IOException
    {
        /*
         * Number of next available block intialized to zero.
         */
        raf.writeInt(0);

        /*
         * Write the block length . In FoxPro.
         */
        if (version == Version.FOXPRO_26)
        {
            raf.writeInt(blockLength);
        }
        else
        {
            raf.writeInt(0);
        }

        /*
         * Write the file name. In dBaseIV and V.
         */
        Util.writeString(raf,
                         Util.stripExtension(memoFile.getName()).toUpperCase(),
                         LENGTH_FILE_NAME);

        if (version == Version.DBASE_4 || version == Version.DBASE_5)
        {
            /*
             * Meaning of the following bytes not clear. These values in all .dbt files that we have
             * seen have the following values. In dBaseIV and V.
             */
            raf.writeByte(0x00);
            raf.writeByte(0x00);
            raf.writeByte(0x02);
            raf.writeByte(0x01);

            /*
             * Write the block size. In dBaseIV and V.
             */
            raf.writeShort(Util.changeEndianness((short) blockLength));
        }
        else
        {
            raf.writeByte(0x00);
            raf.writeByte(0x00);
            raf.writeByte(0x00);
            raf.writeByte(0x00);
            raf.writeShort(0);
        }

        /*
         * Rest of the header is filled with zeros
         */
        for (int i = OFFSET_BLOCK_SIZE + 2; i < blockLength; i++)
        {
            raf.writeByte(0x00);
        }
    }
}
