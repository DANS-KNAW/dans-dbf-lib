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

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents an xBase database. An xBase database is a directory containing table files (.DBF
 * files) and supporting files like memo (.DBT) or index (.NDX) files. This class allows you to work
 * with the database without having to open the lower level files directly. However, it is still
 * possible to open individual tables directly through the {@link Table} class.
 *
 * @author Jan van Mansum
 * @author Vesa Åkerman
 */
public class Database
{
    private final File databaseDirectory;
    private final Map<String, Table> tableMap = new HashMap<String, Table>();
    private final Version version;
    private final String charsetName;

    /**
     * Creates a new Database object. A file representing the database directory must be provided.
     * If the directory does not exist, it is created. If the file represents a regular file and not
     * a directory, throws an <code>IllegalArgumentException</code>.
     * <p>
     * All tables that exist in the database directory are added as <code>Table</code> objects and can
     * be retrieved with {@link #getTable(String)}.
     * </p>
     * <p>
     * The parameter <code>version</code> does not trigger any validation on an existing database but
     * is merely used to specify the version of newly added tables. It is therefore the
     * responsibility of the caller to ensure that the correct version is specified.
     * </p>
     *
     * @param databaseDirectory a <code>java.io.File</code> object pointing to the directory containing
     *            the database
     * @param version the version of xBase to use for new tables
     *
     */
    public Database(final File databaseDirectory, final Version version)
    {
        this(databaseDirectory, version,
             Charset.defaultCharset().name());
    }

    /**
     * Creates a new Database object. A file representing the database directory must be provided.
     * If the directory does not exist, it is created. If the file represents a regular file and not
     * a directory, throws an <code>IllegalArgumentException</code>.
     * <p>
     * All tables that exist in the database directory are added as <code>Table</code> objects and can
     * be retrieved with {@link #getTable(String)}.
     * </p>
     * <p>
     * The parameter <code>version</code> does not trigger any validation on an existing database but
     * is merely used to specify the version of newly added tables. It is therefore the
     * responsibility of the caller to ensure that the correct version is specified.
     * </p>
     *
     * @param databaseDirectory a <code>java.io.File</code> object pointing to the directory containing
     *            the database
     * @param version the version of xBase to use for new tables
     * @param charsetName the name of the character set to use, if <code>null</code> will be set to
     *            the platform's default charset.
     *
     */
    public Database(final File databaseDirectory, final Version version, final String charsetName)
    {
        if (databaseDirectory == null || databaseDirectory.isFile())
        {
            throw new IllegalArgumentException("Database must be a directory ");
        }

        if (! databaseDirectory.exists())
        {
            databaseDirectory.mkdirs();
        }

        this.databaseDirectory = databaseDirectory;
        this.version = version;
        this.charsetName = charsetName == null ? Charset.defaultCharset().name() : charsetName;

        /*
         * Provoke an exception if the charset is not found by the JRE.
         */
        Charset.forName(this.charsetName);

        final String[] fileNames = databaseDirectory.list();

        for (final String fileName : fileNames)
        {
            if (fileName.toLowerCase().endsWith(".dbf") && (fileName.length() > ".dbf".length()))
            {
                addTable(fileName);
            }
        }
    }

    /**
     * Returns an unmodifiable {@link Set} of table names.
     *
     * @return a {@link Set} of <code>Table</code> names.
     */
    public Set<String> getTableNames()
    {
        return Collections.unmodifiableSet(tableMap.keySet());
    }

    /**
     * Returns the {@link Table} object with the specified name or <code>null</code> if it has not
     * been added yet.
     *
     * @param name the name of the table, including extension
     * @return a {@link Table} object
     */
    public Table getTable(final String name)
    {
        return tableMap.get(name);
    }

    /**
     * Adds a new {@link Table} object to the set of <code>Table</code>s maintained by this
     * <code>Database</code> object and returns it. If a <code>Table</code> object with
     * <code>name</code> already exists, it is returned.
     * <p>
     * Note that the actual table file (the <code>.DBF</code> file) may or may not exists. To create
     * a new table on disk, see {@link Table#open(IfNonExistent)}.
     *
     * @param name the name of the table
     *
     * @return a <code>Table</code> object
     */
    public Table addTable(final String name, final List<Field> fields)
                   throws InvalidFieldTypeException, InvalidFieldLengthException
    {
        Table table = tableMap.get(name);

        if (table == null)
        {
            table = new Table(new File(databaseDirectory, name),
                              version,
                              fields);
            tableMap.put(name, table);
        }

        return table;
    }

    private void addTable(final String name)
    {
        Table table = tableMap.get(name);

        if (table == null)
        {
            table = new Table(new File(databaseDirectory, name),
                              charsetName);
            tableMap.put(name, table);
        }
    }

    /**
     * Removes a {@link Table} object from the list of <code>Table</code> objects maintained by this
     * <code>Database</code> object.
     * <p>
     * Note that the actual table file (the <code>.DBF</code> file) is not deleted by removing the
     * table object. To delete a file on disk, see {@link Table#delete()}.
     *
     * @param name the name of the table to remove
     */
    public void removeTable(final String name)
    {
        tableMap.remove(name);
    }

    /**
     * Removes a {@link Table} object from the list of <code>Table</code> objects maintained by this
     * <code>Database</code> object.
     * <p>
     * Note that the actual table file (the <code>.DBF</code> file) is not deleted by removing the
     * table object. To delete a file on disk, see {@link Table#delete()}.
     *
     * @param table the table to remove
     */
    public void removeTable(final Table table)
    {
        tableMap.remove(table.getName());
    }

    /**
     * Returns the name of the character set to use when reading from and writing to database files.
     * This value can be overridden by the one specified through {@link Table}'s constructor.
     *
     * @return the charset name
     */
    public String getCharsetName()
    {
        return charsetName;
    }
}
