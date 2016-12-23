/**
 * Copyright (C) 2009-2016 DANS - Data Archiving and  Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.common.dbflib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Utility functions for testing databases.
 *
 * @author Jan van Mansum
 */
class UnitTestUtil
{
    private UnitTestUtil()
    {
        // Disallow instantiation.
    }

    /**
     * Takes a record iterator and a field name and creates a list of the records sorted by the
     * field specified by the field name. The type of this field must implement
     * <tt>java.lang.Comparable</tt>.
     *
     * @param aRecordIterator a record iterator
     * @param aFieldName a field name
     *
     * @return a sorted list of records
     */
    public static List<Record> createSortedRecordList(final Iterator<Record> aRecordIterator, final String aFieldName)
    {
        final List<Record> recordList = new ArrayList<Record>();

        while (aRecordIterator.hasNext())
        {
            recordList.add(aRecordIterator.next());
        }

        Collections.sort(recordList,
                         new Comparator<Record>()
            {
                @SuppressWarnings("unchecked")
                public int compare(Record r1, Record r2)
                {
                    Comparable c1 = (Comparable) r1.getTypedValue(aFieldName);
                    Comparable c2 = (Comparable) r2.getTypedValue(aFieldName);

                    if (c1 == null)
                    {
                        if (c2 == null)
                        {
                            return 0;
                        }

                        return -1;
                    }

                    if (c2 == null)
                    {
                        return 1;
                    }

                    return c1.compareTo(c2);
                }
            });

        return recordList;
    }

    /**
     * Compares two files ignoring the specified byte ranges. Returns the offset of the first byte
     * that is different between the two files, or -1 if the files where the same.
     *
     * @param aFile1 the first file
     * @param aFile2 the second file
     * @param aIgnoredRanges A list of pairs specifying byte ranges to ignore
     *
     * @return the offset of the first difference
     *
     * @throws IOException if one of the files could not be read
     */
    public static long compare(final File aFile1, final File aFile2, final Ranges aIgnoredRanges)
                        throws IOException
    {
        final FileInputStream fis1 = new FileInputStream(aFile1);
        final FileInputStream fis2 = new FileInputStream(aFile2);

        try
        {
            long offset = 0;
            int c1 = fis1.read();
            int c2 = fis2.read();

            while (aIgnoredRanges.inRanges(offset) || c1 != -1 && c2 != -1 && c1 == c2)
            {
                ++offset;

                c1 = fis1.read();
                c2 = fis2.read();
            }

            if (c1 != c2)
            {
                return offset;
            }

            return -1;
        }
        finally
        {
            fis1.close();
            fis2.close();
        }
    }

    /**
     * Removes the file represented by <code>aFile</code>. If the file is a directory removes the
     * directory and all the files and subdirectories it contains.
     *
     * @param aFile the file to remove
     */
    public static void remove(final File aFile)
    {
        if (aFile.isDirectory())
        {
            if (".".equals(aFile.getName()) || "..".equals(aFile.getName()))
            {
                /*
                 * Not really necessary, but you can never be too careful.
                 */
                return;
            }

            final String[] files = aFile.list();

            for (String f : files)
            {
                remove(new File(aFile, f));
            }
        }

        aFile.delete();
    }

    /**
     * Recreates the directory specified by <code>aDirectory</code>. If the directory exists it is
     * deleted first. The result is always that <code>aDirectory</code> is empty. If the parent
     * directories of <code>aDirectory</code> do not exist they are first created.
     *
     * @param aDirectory the directory to recreate
     * @return the directory created
     */
    public static File recreateDirectory(final String aDirectory)
    {
        final File dir = new File(aDirectory);

        if (dir.exists())
        {
            remove(dir);
        }

        dir.mkdirs();

        return dir;
    }

    /**
     * Performs a test in which a DBF is copied by reading it and writing it using the Table class,
     * after which a byte by byte comparision of the . DBF files and .DBT files (.FPT files in
     * FoxPro) is done. Ranges of bytes to be ignored when comparing the files can be provided.
     *
     * @param aSubDir the sub-directory of src/test/resources/ to find the source file and of
     *            target/test-output to put the copied file
     * @param aTableBaseName the base name of the table
     * @param aIgnoredRangesDbf ranges to ignore when comparing the DBFs
     * @param aIgnoredRangesDbt ranges to ignore when comparing the DBTs (FPT in FoxPro)
     *
     * @throws IOException should not happen
     * @throws CorruptedTableException should not happen
     */
    public static void doCopyAndCompareTest(final String aSubDir, final String aTableBaseName, Version aVersion,
                                            final Ranges aIgnoredRangesDbf, final Ranges aIgnoredRangesDbt)
                                     throws IOException, DbfLibException
    {
        final File outputDir = UnitTestUtil.recreateDirectory("target/test-output/" + aSubDir + "/" + aTableBaseName);

        File orgFile = new File("src/test/resources/" + aSubDir + "/" + aTableBaseName + ".dbf");

        if (! orgFile.exists())
        {
            orgFile = new File("src/test/resources/" + aSubDir + "/" + aTableBaseName + ".DBF");
        }

        File copyFile = new File(outputDir, aTableBaseName + ".dbf");
        Table copyTable = null;
        Table orgTable = null;

        try
        {
            orgTable = new Table(orgFile);
            orgTable.open();

            List<Field> fields = orgTable.getFields();
            copyTable = new Table(copyFile, aVersion, fields);
            copyTable.open(IfNonExistent.CREATE);

            Iterator<Record> recordIterator = orgTable.recordIterator();

            while (recordIterator.hasNext())
            {
                copyTable.addRecord(recordIterator.next());
            }
        }
        finally
        {
            if (orgTable != null)
            {
                orgTable.close();
            }

            if (copyTable != null)
            {
                copyTable.close();
            }
        }

        long diffOffset = UnitTestUtil.compare(orgFile, copyFile, aIgnoredRangesDbf);
        assertEquals("DBF files differ at offset 0x" + Integer.toHexString((int) diffOffset), -1, diffOffset);

        if (aIgnoredRangesDbt == null)
        {
            return;
        }

        final File orgMemoFile = Util.getMemoFile(orgFile, aVersion);
        final File copyMemoFile = Util.getMemoFile(copyFile, aVersion);
        final String extension = (aVersion == Version.FOXPRO_26 ? ".fpt" : ".dbt");

        if (orgMemoFile == null || copyMemoFile == null)
        {
            assertTrue(extension + " file expected but not found", false);
        }

        diffOffset = UnitTestUtil.compare(orgMemoFile, copyMemoFile, aIgnoredRangesDbt);
        assertEquals(extension + " files differ at offset 0x" + Integer.toHexString((int) diffOffset), -1, diffOffset);
    }

    static void copyFile(File in, File outDir, String outFileName)
                  throws IOException
    {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        outDir.mkdirs();

        File outFile = new File(outDir, outFileName);
        FileChannel outChannel = new FileOutputStream(outFile).getChannel();

        try
        {
            inChannel.transferTo(0,
                                 inChannel.size(),
                                 outChannel);
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            if (inChannel != null)
            {
                inChannel.close();
            }

            if (outChannel != null)
            {
                outChannel.close();
            }
        }
    }

    static boolean setReadOnly(final File inDir, final String inFileName)
                        throws IOException
    {
        return new File(inDir, inFileName).setReadOnly();
    }
}
