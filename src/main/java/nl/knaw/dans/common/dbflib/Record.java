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

import java.util.Date;
import java.util.Map;

/**
 * Represents a record in a table. A record basically maps a {@link String} key to a value object
 * for a specified row in a table. The type of the value object depends on the field type. To find
 * out which DBF types map to which Java types, see {@link Type}.
 * <p>
 * Values that are too large to fit in their designated fields will cause a
 * {@link ValueTooLargeException}.
 *
 * @author Jan van Mansum
 * @author Vesa Åkerman
 */
public class Record
{
    private final Map<String, Value> valueMap;
    private final boolean deleted;

    /**
     * Creates a new Record object. <code>aValueMap</code> must specify the values for the fields in
     * the record. The concrete <code>Value</code> subclasses must be compatible with the
     * corresponding DBF field types, otherwise {@link DataMismatchException} is thrown when trying
     * to add the record.
     * <p>
     * The following is a table of the <code>Value</code> subclasses, the DBF field types and the
     * result of passing the one as a value for the other:
     *
     * <table border="1" cellpadding="4">
     * <tr>
     * <td>&nbsp;</td>
     * <td><b>CHARACTER</b></td>
     * <td><b>LOGICAL</b></td>
     * <td><b>NUMBER</b></td>
     * <td><b>FLOAT</b></td>
     * <td><b>DATE</b></td>
     * <td><b>MEMO</b></td>
     * <td><b>BINARY</b></td>
     * <td><b>GENERAL</b></td>
     * </tr>
     * <tr>
     * <td><b>StringValue</b></td>
     * <td bgcolor="lightgreen">Accepted if within maximum length</td>
     * <td bgcolor="lightgreen">Accepted if one of "Y", "N", "T", "F" or a space, no
     * leading/trailing spaces allowed</td>
     * <td bgcolor="lightgreen">Accepted if a valid number, that fits in the field and has exactly
     * the number of decimals as the field's decimal count</td>
     * <td bgcolor="lightgreen">See NUMBER</td>
     * <td bgcolor="lightgreen">Accepted if in the format YYYYMMDD. No leading or trailing spaces.
     * No check is done whether the date is itself valid.</td>
     * <td bgcolor="lightgreen">Accepted</td>
     * <td bgcolor="lightgreen">Accepted</td>
     * <td bgcolor="lightgreen">Accepted</td>
     * </tr>
     * <tr>
     * <td><b>BooleanValue</b></td>
     * <td bgcolor="lightgreen">Accepted, Y or N written as first character of the field</td>
     * <td bgcolor="lightgreen">Accepted, Y or N written</td>
     * <td bgcolor="pink">DME)<sup>*</sup></td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="lightgreen">Accepted</td>
     * <td bgcolor="lightgreen">Accepted</td>
     * <td bgcolor="lightgreen">Accepted</td>
     * </tr>
     * <tr>
     * <td><b>NumberValue</b></td>
     * <td bgcolor="lightgreen">Accepted, if the number fits in the field</td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="lightgreen">Accepted, if the digits before the decimal point and the minus sign
     * (if any) together do not occupy more space than reserved for them by the field. If there are
     * too many digits after the decimal point they are rounded</td>
     * <td bgcolor="lightgreen">See NUMBER</td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="lightgreen">Accepted</td>
     * <td bgcolor="lightgreen">Accepted</td>
     * <td bgcolor="lightgreen">Accepted</td>
     * </tr>
     * <tr>
     * <td><b>DateValue</b></td>
     * <td bgcolor="lightgreen">Accepted, if the CHARACTER field is at least 10 long (the size of a
     * DATE field in DBF).</td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="lightgreen">Accepted</td>
     * <td bgcolor="lightgreen">Accepted, Date written as YYYYMMDD</td>
     * <td bgcolor="lightgreen">Accepted, Date written as YYYYMMDD</td>
     * <td bgcolor="lightgreen">Accepted, Date written as YYYYMMDD</td>
     * </tr>
     * <tr>
     * <td><b>ByteArrayValue</b></td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="pink">DME</td>
     * <td bgcolor="lightgreen">Accepted, Date written as YYYYMMDD</td>
     * <td bgcolor="lightgreen">Accepted, Date written as YYYYMMDD</td>
     * <td bgcolor="lightgreen">Accepted, Date written as YYYYMMDD</td>
     * </tr>
     * </table>
     * )<sup>*</sup> DataMismatchException
     *
     * @param valueMap the mapping from field name to field value
     */
    public Record(final Map<String, Value> valueMap)
    {
        this(false, valueMap);
    }

    Record(final boolean deleted, final Map<String, Value> valueMap)
    {
        this.deleted = deleted;
        this.valueMap = valueMap;
    }

    /**
     * Returns the raw field value. The raw field value is the bytes as stored in the DBF file. If
     * the value is empty <code>null</code> or a series of ASCII spaces may be returned.
     *
     * @param field the field for which to get the raw value
     *
     * @return a byte array
     *
     * @throws DbfLibException if the value was too large to be read
     */
    public byte[] getRawValue(final Field field)
                       throws DbfLibException
    {
        final Value v = valueMap.get(field.getName());

        if (v == null)
        {
            return null;
        }

        return v.getRawValue(field);
    }

    /**
     * Returns the value as a Java object. The type of Java object returned depends on the field
     * type in the xBase database. See {@link Type} for the mapping between the two.
     *
     * @param fieldName the field for which to get the value
     *
     * @return a Java object
     */
    public Object getTypedValue(final String fieldName)
    {
        final Value v = valueMap.get(fieldName);

        if (v == null)
        {
            return null;
        }

        return v.getTypedValue();
    }

    /**
     * Returns the value of the specified field as a {@link Number}. The exact subclass of
     * <code>Number</code> used depends on the size of the corresponding {@link Field} and its
     * <code>decimalCount</code> property. If <code>decimalCount</code> is zero an integral type is
     * returned, otherwise a fractional type. Depending on the size <code>java.lang.Integer</code>,
     * <code>java.lang.Long</code> or <code>java.math.BigInteger</code> is used as an integral type.
     * For non-integral types the classes used are either <code>java.lang.Double</code> or
     * <code>java.math.BigDecimal</code>.
     * <p>
     * It is not necessary to know the exact type used. You can use the conversion methods on the
     * <code>java.lang.Number</code> class to convert the value before using it. (E.g.,
     * <code>Number.intValue()</code>.) Of course you do need to know whether the value will fit in
     * the chosen type. Note that comparisons may fail if you do not first convert the values. For
     * instance if you compare the a <code>java.math.BigInteger</code> with a <code>long</code>
     * using the <code>equals</code> method, <code>false</code> will be returned even if the values
     * represent the same logical value.
     * <p>
     * Example:
     *
     * <pre>
     *
     * public Record searchSomeNum(final double val)
     * {
     *      //
     *      //... SOME CODE HERE THAT RETRIEVES table1 ...
     *      //
     *
     *      // Get a record iterator to loop over all the records.
     *      Iterator<Record> ri = table1.recordIterator();
     *
     *      // Search for the record with SOMENUM = val
     *      while(ri.hasNext())
     *      {
     *         Record r = ri.next();
     *         Number n = r.getNumberValue("SOMENUM");
     *
     *         // Convert n to a double before comparing it.
     *         if(n.doubleValue() == val)
     *         {
     *            return r;
     *         }
     *      }
     *
     *      return null;
     * }
     *
     * </pre>
     *
     * @param fieldName the name of the field with numerical data
     *
     * @return a {@link Number} object
     */
    public Number getNumberValue(final String fieldName)
    {
        return (Number) getTypedValue(fieldName);
    }

    /**
     * Returns the specified value as a <code>java.lang.String</code> object.
     *
     * @param fieldName the name of the field with character data
     *
     * @return a {@link String} object
     */
    public String getStringValue(final String fieldName)
    {
        return (String) getTypedValue(fieldName);
    }

    /**
     * Returns the specified value as a {@link Boolean} object.
     *
     * @param fieldName the name of the field with logical data
     *
     * @return a {@link Boolean} object
     */
    public Boolean getBooleanValue(final String fieldName)
    {
        return (Boolean) getTypedValue(fieldName);
    }

    /**
     * Returns the specified value as a {@link Date} object.
     *
     * @param fieldName the name of the field with date data
     *
     * @return a {@link Date} object
     */
    public Date getDateValue(final String fieldName)
    {
        return (Date) getTypedValue(fieldName);
    }

    /**
     * Returns whether the record is marked deleted in the database.  In the original dBase program
     * this meant that the record was still visible but had a "deleted" flag.
     *
     * @return deleted status
     */
    public boolean isMarkedDeleted()
    {
        return deleted;
    }
}
