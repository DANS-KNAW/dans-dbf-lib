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

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Represents a single table in a xBase database. A table is represented by a single
 * <code>.DBF</code> file. Some tables have an associated .DBT file to store memo field data.
 *
 * @author Jan van Mansum
 * @author Vesa Åkerman
 */
public class Table
{
    private static final int MARKER_RECORD_DELETED = 0x2A;
    private static final int MARKER_EOF = 0x1A;
    private static final int MARKER_RECORD_VALID = 0x20;
    private static final int DEFAULT_BUFFER_SIZE = 1000000;
    private static final int DEFAULT_RECORD_ARRAY_LIST_SIZE = 10000;
    private byte[] buffer = null;
    private int startBufferedRecord = 0;
    private int nBufferedRecord = 0;

    private class RecordIterator
        implements Iterator<Record>
    {
        private final boolean includeDeleted;
        private int recordCounter = -1;
        private boolean currentElementDeleted = false;

        RecordIterator(final boolean includeDeleted)
        {
            this.includeDeleted = includeDeleted;
        }

        public boolean hasNext()
        {
            try
            {
                return recordCounter + 1 < header.getRecordCount()
                       && (includeDeleted || ! followingRecordsAreAllDeleted());
            }
            catch (final IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        private boolean followingRecordsAreAllDeleted()
                                               throws IOException
        {
            int index = recordCounter + 1;
            byte b;

            do
            {
                jumpToRecordAt(index++);
                b = raFile.readByte();

                if (b == MARKER_RECORD_VALID)
                {
                    return false;
                }
            }
             while (index < header.getRecordCount() && b == MARKER_RECORD_DELETED);

            return true;
        }

        public Record next()
        {
            if (! hasNext())
            {
                throw new NoSuchElementException();
            }

            checkOpen();

            try
            {
                Record record;

                do
                {
                    record = getRecordAt(++recordCounter);
                }
                 while (! includeDeleted && record.isMarkedDeleted());

                currentElementDeleted = false;

                return record;
            }
            catch (final IOException ioException)
            {
                throw new RuntimeException(ioException.getMessage(), ioException);
            }
            catch (final CorruptedTableException corruptedTableException)
            {
                throw new RuntimeException(corruptedTableException.getMessage(), corruptedTableException);
            }
        }

        public void remove()
        {
            if (recordCounter == 0 || recordCounter >= header.getRecordCount())
            {
                throw new NoSuchElementException();
            }

            if (currentElementDeleted)
            {
                throw new RuntimeException("Current element already removed");
            }

            try
            {
                deleteRecordAt(recordCounter);
                currentElementDeleted = true;
            }
            catch (final IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    private final File tableFile;
    private final DbfHeader header = new DbfHeader();
    private final String charsetName;
    private Memo memo = null;
    private RandomAccessFile raFile = null;
    private String accessMode;

    /**
     * Creates a new <code>Table</code> object. A {@link File} object representing the
     * <code>.DBF</code> file must be provided. To read from or write to the table it must first be
     * opened.
     *
     * @param tableFile a <code>File</code> object representing the <code>.DBF</code> file that
     *            stores this table's data.
     *
     * @see #open(IfNonExistent)
     *
     * @throws IllegalArgumentException if <code>tableFile </code> is <code>null</code>
     */
    public Table(final File tableFile)
          throws IllegalArgumentException
    {
        this(tableFile,
             Charset.defaultCharset().name());
    }

    /**
     * Creates a new Table object. A {@link File} object representing the <code>.DBF</code> file
     * must be provided. To read from or write to the table it must first be opened.
     *
     * @param tableFile a <code>File</code> object representing the <code>.DBF</code> file that
     *            stores this table's data.
     * @param charsetName the charset to use for reading and writing this file
     *
     * @see #open(IfNonExistent)
     *
     * @throws IllegalArgumentException if <code>tableFile </code> is <code>null</code>
     */
    public Table(final File tableFile, final String charsetName)
          throws IllegalArgumentException
    {
        if (tableFile == null)
        {
            throw new IllegalArgumentException("Table file must not be null");
        }

        this.tableFile = tableFile;
        this.charsetName = charsetName == null ? Charset.defaultCharset().name() : charsetName;

        Charset.forName(this.charsetName);
    }

    /**
     * Creates a new Table object. In order to read from or write to the table it must first be
     * opened.
     * <p>
     * <b>Note:</b> if the <code>.DBF</code> file already exists <code>aFields</code> will be
     * overwritten by the values in the existing file when opened. To replace an existing table,
     * first delete it and then create and open a new <code>Table</code> object.
     *
     * @param tableFile the <code>.DBF</code> file that contains the table data
     * @param version the dBase version to support
     * @param fields the fields to create if this is a new table
     * @param charsetName the charset to use for reading and writing this file
     *
     * @see #open(IfNonExistent)
     *
     * @throws IllegalArgumentException if <tt>aTableField</tt> is <tt>null</tt>
     * @throws InvalidFieldLengthException
     */
    public Table(final File tableFile, final Version version, final List<Field> fields, final String charsetName)
          throws InvalidFieldTypeException, InvalidFieldLengthException
    {
        this(tableFile, charsetName);
        header.setVersion(version);
        header.setHasMemo(hasMemo(fields));
        header.setFields(fields);
    }

    /**
     * As {@link #Table(File, Version, List, String)} but uses the platform's default character set.
     */
    public Table(final File tableFile, final Version version, final List<Field> fields)
          throws InvalidFieldTypeException, InvalidFieldLengthException
    {
        this(tableFile, version, fields,
             Charset.defaultCharset().name());
    }

    private static boolean hasMemo(final List<Field> fields)
    {
        for (final Field field : fields)
        {
            if (field.getType() == Type.MEMO)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Opens the table for reading and writing. Equivalent to {@link Table#open(IfNonExistent)
     * Table.open(IfNonExistent.ERROR)}
     *
     * @throws IOException if the table file does not exist or could not be opened
     * @throws CorruptedTableException if the header of the table file was corrupt
     */
    public void open()
              throws IOException, CorruptedTableException
    {
        open(IfNonExistent.ERROR);
    }

    /**
     * Opens the table for reading and writing.
     *
     * @param ifNonExistent what to do if the table file does not exist yet
     *
     * @throws IOException if the table does not exist or could be opened
     * @throws CorruptedTableException if the header of the table file was corrupt
     */
    public void open(final IfNonExistent ifNonExistent)
              throws IOException, CorruptedTableException
    {
        open("rw", ifNonExistent);
    }

    /**
     * Opens the table with given file access mode.
     *
     * @param mode file access mode, either "r" or "rw"
     * @param ifNonExistent what to do if the table file does not exist yet
     *
     * @throws IOException if the table does not exist or could be opened
     * @throws CorruptedTableException if the header of the table file was corrupt
     */
    public void open(final String mode, final IfNonExistent ifNonExistent)
              throws IOException, CorruptedTableException
    {
        if (tableFile.exists())
        {
            raFile = new RandomAccessFile(tableFile, mode);
            accessMode = mode;
            header.readAll(raFile);
        }
        else if (ifNonExistent.isCreate())
        {
            raFile = new RandomAccessFile(tableFile, mode);
            accessMode = mode;
            header.writeAll(raFile);
        }
        else if (ifNonExistent.isError())
        {
            throw new FileNotFoundException("Input file " + tableFile + " not found");
        }
    }

    /**
     * Closes this table for reading and writing.
     *
     * @throws java.io.IOException if the table file or an associated file cannot be closed
     */
    public void close()
               throws IOException
    {
        try
        {
            if (raFile != null)
            {
                raFile.close();
            }
        }
        finally
        {
            raFile = null;
            ensureMemoClosed();
        }
    }

    /**
     * Closes and deletes the underlying table file and associated files.
     *
     * @throws IOException if the table file or an associated file cannot be closed or deleted
     */
    public void delete()
                throws IOException
    {
        close();
        tableFile.delete();

        if (memo != null)
        {
            memo.delete();
        }
    }

    /**
     * Returns the date on which this table was last modified. Note that the hours, minutes, seconds
     * and milliseconds fields are always set to zero. Also, the date time is not normalized to UTC.
     *
     * @return the last modified date of the table
     */
    public Date getLastModifiedDate()
    {
        checkOpen();

        return header.getLastModifiedDate();
    }

    /**
     * Returns the name of the table, including the extension.
     *
     * @return the name of the table
     */
    public String getName()
    {
        return tableFile.getName();
    }

    /**
     * Returns a {@link List} of {@link Field} objects, which provide a description of each field
     * (column) in the table. The order of the <code>Field</code> objects is guaranteed to be the
     * same as the order of the fields in each record returned. A new copy of the field list is
     * returned on each call.
     *
     * @return the list of field objects.
     */
    public List<Field> getFields()
    {
        checkOpen();

        return header.getFields();
    }

    /**
     * Returns a {@link Record} iterator. Note that, to use the iterator the table must be opened.
     * This iterator skips the records flagged as "deleted".
     *
     * @return a <code>Record</code> iterator
     *
     * @see Record
     * @see #recordIterator(boolean)
     */
    public Iterator<Record> recordIterator()
    {
        return recordIterator(false);
    }

    /**
     * Returns a {@link Record} iterator. Note that, to use the iterator the table must be opened.
     * If <code>includeDeleted</code> is <code>true</code>, records flagged as "deleted" are
     * included in the iteration.
     *
     * @param includeDeleted if <code>true</code> deleted records are returned, otherwise not
     * @return a <code>Record</code> iterator
     */
    public Iterator<Record> recordIterator(final boolean includeDeleted)
    {
        return new RecordIterator(includeDeleted);
    }

    /**
     * Constructs and adds a record. The fields values for the record must be provided as parameters
     * in the same order that the fields are provided in the field list.
     *
     * @throws IOException if the record could not be written to the database file
     * @throws CorruptedTableException if the table was corrupt
     * @throws ValueTooLargeException if a field value exceeds the length of its corresponding field
     * @throws RecordTooLargeException if more field values are provided than there are field in
     *             this table
     */
    public void addRecord(final Object... fieldValues)
                   throws IOException, DbfLibException
    {
        if (fieldValues.length > header.getFields().size())
        {
            throw new RecordTooLargeException("Trying to add " + fieldValues.length + " fields while there are only "
                                              + header.getFields().size() + " defined in the table file");
        }

        final Map<String, Value> map = new HashMap<String, Value>();
        final Iterator<Field> fieldIterator = header.getFields().iterator();

        for (final Object fieldValue : fieldValues)
        {
            final Field field = fieldIterator.next();

            map.put(field.getName(),
                    createValueObject(fieldValue));
        }

        addRecord(new Record(map));
    }

    private Value createValueObject(final Object value)
    {
        if (value instanceof Number)
        {
            return new NumberValue((Number) value);
        }
        else if (value instanceof String)
        {
            return new StringValue((String) value, charsetName);
        }
        else if (value instanceof Boolean)
        {
            return new BooleanValue((Boolean) value);
        }
        else if (value instanceof Date)
        {
            return new DateValue((Date) value);
        }
        else if (value instanceof byte[])
        {
            return new ByteArrayValue((byte[]) value);
        }

        return null;
    }

    /**
     * Adds a record to this table.
     *
     * @param record the record to add.
     *
     * @throws IOException if the record could not be written to the database file
     * @throws CorruptedTableException if the table was corrupt
     * @throws ValueTooLargeException if a field value exceeds the length of its corresponding field
     *
     * @see Record
     */
    public void addRecord(final Record record)
                   throws IOException, DbfLibException
    {
        updateRecordAt(header.getRecordCount(),
                       record);
        raFile.writeByte(MARKER_EOF);
        writeRecordCount(header.getRecordCount() + 1);
    }

    public void updateRecordAt(final int index, final Record record)
                        throws IOException, DbfLibException
    {
        checkOpen();
        jumpToRecordAt(index);
        raFile.writeByte(MARKER_RECORD_VALID);

        for (final Field field : header.getFields())
        {
            byte[] raw = record.getRawValue(field);

            if (raw == null)
            {
                raw = Util.repeat((byte) ' ',
                                  field.getLength());
            }
            else if (field.getType() == Type.MEMO || field.getType() == Type.BINARY || field.getType() == Type.GENERAL)
            {
                final int i = writeMemo(raw);

                if (header.getVersion() == Version.DBASE_4 || header.getVersion() == Version.DBASE_5)
                {
                    raw = String.format("%0" + field.getLength() + "d", i).getBytes();
                }
                else
                {
                    raw = String.format("%" + field.getLength() + "d", i).getBytes();
                }
            }

            raFile.write(raw);

            if (raw.length < field.getLength())
            {
                raFile.write(Util.repeat((byte) 0x00, field.getLength() - raw.length));
            }
        }
    }

    /**
     * Flags the record at <code>index</code> as "deleted". To physically remove "deleted" records,
     * a call to {@link #pack()} is necessary.
     *
     * @param index the index of the record to delete
     * @throws IOException
     */
    public void deleteRecordAt(final int index)
                        throws IOException
    {
        checkOpen();
        jumpToRecordAt(index);
        raFile.writeByte(MARKER_RECORD_DELETED);
    }

    private int writeMemo(final byte[] memoText)
                   throws IOException, CorruptedTableException
    {
        ensureMemoOpened(accessMode, IfNonExistent.CREATE);

        return memo.writeMemo(memoText);
    }

    private void writeRecordCount(final int recordCount)
                           throws IOException
    {
        raFile.seek(DbfHeader.OFFSET_RECORD_COUNT);
        header.setRecordCount(recordCount);
        header.writeRecordCount(raFile);
    }

    private void checkOpen()
    {
        if (raFile == null)
        {
            throw new IllegalStateException("Table should be open for this operation");
        }
    }

    private byte[] readMemo(final String memoIndex)
                     throws IOException, CorruptedTableException
    {
        ensureMemoOpened(accessMode, IfNonExistent.ERROR);

        if (memoIndex.trim().isEmpty())
        {
            return null;
        }

        return memo.readMemo(Integer.parseInt(memoIndex.trim()));
    }

    private void ensureMemoOpened(final String mode, final IfNonExistent ifNonExistent)
                           throws IOException, CorruptedTableException
    {
        if (memo != null)
        {
            return;
        }

        openMemo(mode, ifNonExistent);
    }

    private void ensureMemoClosed()
                           throws IOException
    {
        if (memo != null)
        {
            try
            {
                memo.close();
            }
            finally
            {
                memo = null;
            }
        }
    }

    /**
     * Opens the memo of this table.
     *
     * @param mode file access mode
     * @param ifNonExistent what to do if the memo file does not exist. (Cannot be IGNORE.)
     * @throws IOException if the memo file could not be opened
     * @throws CorruptedTableException if the memo file could not be found or multiple matches
     *             exist, or if it is corrupt
     */
    private void openMemo(final String mode, final IfNonExistent ifNonExistent)
                   throws IOException, CorruptedTableException
    {
        File memoFile = Util.getMemoFile(tableFile,
                                         header.getVersion());

        if (memoFile == null)
        {
            final String extension = (header.getVersion() == Version.FOXPRO_26 ? ".fpt" : ".dbt");

            if (ifNonExistent.isError())
            {
                throw new CorruptedTableException("Could not find file '" + Util.stripExtension(tableFile.getPath())
                                                  + extension + "' (or multiple matches for the file)");
            }
            else if (ifNonExistent.isCreate())
            {
                final String tableFilePath = tableFile.getPath();
                memoFile = new File(tableFilePath.substring(0, tableFilePath.length() - ".dbf".length()) + extension);
            }
            else
            {
                assert false : "Programming error: cannot ignore non existing memo.";
            }
        }

        memo =
            new Memo(memoFile,
                     header.getVersion());
        memo.open(mode, ifNonExistent);
    }

    /**
     * Read raw data for a number of rows that can fit into the buffer
     * @param startIndex Index of first record
     * @param bufferSize Buffer size to be used to store the raw data
     * @throws IOException
     */
    private void bufferRecords(final int startIndex, final int bufferSize)
                        throws IOException
    {
        if (buffer == null || startIndex < startBufferedRecord || startIndex >= startBufferedRecord + nBufferedRecord)
        {
            startBufferedRecord = startIndex;
            nBufferedRecord =
                Math.max(Math.min(bufferSize / header.getRecordLength(), header.getRecordCount() - startIndex + 1),
                         1);

            final int allocatedBufferSize = nBufferedRecord * header.getRecordLength();
            buffer = new byte[allocatedBufferSize];
            jumpToRecordAt(startIndex);
            raFile.read(buffer);
        }
    }

    /**
     * Get a specified number of records starting at a given index.
     * @param startIndex Index of the first record to be read
     * @param nRecords Number of records to be read
     * @param bufferSize Buffer size to be used to read each batch of records
     * @param includeDeleted Include deleted records
     * @return List of Record objects
     * @throws IOException
     * @throws CorruptedTableException
     */
    public List<Record> getRecordsAt(final int startIndex, final int nRecords, final int bufferSize,
                                     final boolean includeDeleted)
                              throws IOException, CorruptedTableException
    {
        checkOpen();

        final ArrayList<Record> records = new ArrayList<Record>(DEFAULT_RECORD_ARRAY_LIST_SIZE);

        int currentRecord = startIndex;
        DataInput dataInput = null;

        while (currentRecord < header.getRecordCount() && currentRecord < startIndex + nRecords)
        {
            if (currentRecord < startBufferedRecord || currentRecord >= startBufferedRecord + nBufferedRecord)
            {
                bufferRecords(currentRecord, bufferSize);
                dataInput = null;
            }

            if (dataInput == null)
            {
                dataInput = new DataInputStream(new ByteArrayInputStream(buffer));
            }

            final Record record = getRecord(dataInput);

            if (includeDeleted || ! record.isMarkedDeleted())
            {
                records.add(record);
            }

            currentRecord++;
        }

        return records;
    }

    /**
     * Get a specified number of records starting at a given index.
     * @param startIndex Index of the first record to be read
     * @param nRecords Number of records to be read
     * @param includeDeleted Include deleted records
     * @return List of Record objects
     * @throws IOException
     * @throws CorruptedTableException
     */
    public List<Record> getRecordsAt(final int startIndex, final int nRecords, boolean includeDeleted)
                              throws IOException, CorruptedTableException
    {
        return getRecordsAt(startIndex, nRecords, DEFAULT_BUFFER_SIZE, includeDeleted);
    }

    /**
     * Get a specified number of records starting at a given index. Deleted records are excluded.
     * @param startIndex Index of the first record to be read
     * @param nRecords Number of records to be read
     * @return List of Record objects
     * @throws IOException
     * @throws CorruptedTableException
     */
    public List<Record> getRecordsAt(final int startIndex, final int nRecords)
                              throws IOException, CorruptedTableException
    {
        return getRecordsAt(startIndex, nRecords, DEFAULT_BUFFER_SIZE, false);
    }

    /**
     * Get all records, specifying if deleted records should be included
     * @param includeDeleted Include deleted records
     * @param bufferSize Specified size of buffer used to read raw data
     * @return List of Record objects
     * @throws IOException
     * @throws CorruptedTableException
     */
    public List<Record> getAllRecords(final boolean includeDeleted, final int bufferSize)
                               throws IOException, CorruptedTableException
    {
        checkOpen();

        return getRecordsAt(0,
                            header.getRecordCount(),
                            bufferSize,
                            includeDeleted);
    }

    /**
     * Get all non deleted records
     * @param bufferSize Specified size of buffer used to read raw data
     * @return List of Record objects
     * @throws IOException
     * @throws CorruptedTableException
     */
    public List<Record> getAllRecords(final int bufferSize)
                               throws IOException, CorruptedTableException
    {
        checkOpen();

        return getRecordsAt(0,
                            header.getRecordCount(),
                            bufferSize,
                            false);
    }

    /**
     * Get all non deleted records
     * @return List of Record objects
     * @throws IOException
     * @throws CorruptedTableException
     */
    public List<Record> getAllRecords()
                               throws IOException, CorruptedTableException
    {
        checkOpen();

        return getRecordsAt(0,
                            header.getRecordCount(),
                            DEFAULT_BUFFER_SIZE,
                            false);
    }

    /**
     * Returns the record at index. If the index points to a record beyond the last a
     * {@link NoSuchElementException} is thrown. Attention: records marked as deleted <em>are</em>
     * returned.
     *
     * @param index the zero-based index of the record
     * @return a Record object
     * @throws IOException
     * @throws CorruptedTableException
     */
    public Record getRecordAt(final int index)
                       throws IOException, CorruptedTableException
    {
        checkOpen();

        if (index >= header.getRecordCount())
        {
            throw new NoSuchElementException(String.format("Invalid index: %d", index));
        }

        jumpToRecordAt(index);

        /* Read one record worth of raw data and construct a
           ByteArrayInputStream backed by a byte array */
        byte[] buffer = new byte[header.getRecordLength()];
        raFile.read(buffer);

        DataInput dataInput = new DataInputStream(new ByteArrayInputStream(buffer));

        try
        {
            return getRecord(dataInput);
        }
        catch (NoSuchElementException ex)
        {
            throw new NoSuchElementException(String.format("Invalid index: %d", index));
        }
    }

    Record getRecord(DataInput dataInput)
              throws IOException, CorruptedTableException
    {
        final byte firstByteOfRecord = dataInput.readByte();

        /*
         * This should actually not be possible, as we already checked the index against the record
         * count. Checking anyway to be on the safe side.
         */
        if (firstByteOfRecord == MARKER_EOF)
        {
            throw new NoSuchElementException();
        }

        final Map<String, Value> recordValues = new HashMap<String, Value>();

        for (final Field field : header.getFields())
        {
            final byte[] rawData = Util.readStringBytes(dataInput,
                                                        field.getLength());

            switch (field.getType())
            {
                case NUMBER:
                case FLOAT:
                    recordValues.put(field.getName(),
                                     new NumberValue(field, rawData));

                    break;

                case CHARACTER:
                    recordValues.put(field.getName(),
                                     new StringValue(field, rawData, charsetName));

                    break;

                case LOGICAL:
                    recordValues.put(field.getName(),
                                     new BooleanValue(field, rawData));

                    break;

                case DATE:
                    recordValues.put(field.getName(),
                                     new DateValue(field, rawData));

                    break;

                case MEMO:

                    final byte[] memoTextBytes = readMemo(new String(rawData));
                    recordValues.put(field.getName(),
                                     memoTextBytes == null ? null : new StringValue(field, memoTextBytes, charsetName));

                    break;

                case GENERAL:
                case BINARY:
                case PICTURE:
                    recordValues.put(field.getName(),
                                     new ByteArrayValue(readMemo(new String(rawData))));

                    break;

                default:
                    throw new RuntimeException("Not all types handled");
            }
        }

        return new Record(firstByteOfRecord == MARKER_RECORD_DELETED, recordValues);
    }

    /**
     * Physically remove the records currently flagged as "deleted".
     *
     * @throws IOException
     * @throws DbfLibException
     */
    public void pack()
              throws IOException, DbfLibException
    {
        final Iterator<Record> iterator = recordIterator(false);

        int i = 0;

        while (iterator.hasNext())
        {
            updateRecordAt(i++,
                           iterator.next());
        }

        writeRecordCount(i);
        jumpToRecordAt(i);
        raFile.write(MARKER_EOF);
        raFile.setLength(raFile.getFilePointer());
    }

    /**
     * Returns the name of the character set used to read and write from/to this table file.
     *
     * @return a charset name
     */
    public String getCharsetName()
    {
        return charsetName;
    }

    /**
     * Returns the version of DBF use to write to the table file. For existing files, some detection
     * is attempted by the library, but it should not be relied on to heavily.
     *
     * @return the version of DBF
     */
    public Version getVersion()
    {
        return header.getVersion();
    }

    private void jumpToRecordAt(final int index)
                         throws IOException
    {
        raFile.seek(header.getLength() + (index * header.getRecordLength()));
    }

    /**
     * Returns the record count. This number includes the records flagged as deleted. These records
     * were visible in the original dBase program user interface, although with a visual indication
     * that they were deleted. To phyically removed them you need to call {@link #pack()}
     *
     * @return the record count
     * @see #pack()
     * @see Record#isMarkedDeleted()
     */
    public int getRecordCount()
    {
        return header.getRecordCount();
    }
}
