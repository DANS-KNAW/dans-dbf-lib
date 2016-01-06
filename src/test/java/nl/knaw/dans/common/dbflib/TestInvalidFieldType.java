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

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests that the InvalidFieldTypeException is thrown at the appropriate times.
 *
 * @author Vesa Åkerman
 */
public class TestInvalidFieldType
{
    @Test(expected = InvalidFieldTypeException.class)
    public void floatTypeNotValid()
                           throws IOException,
                                  CorruptedTableException,
                                  InvalidFieldTypeException,
                                  InvalidFieldLengthException,
                                  ValueTooLargeException,
                                  RecordTooLargeException
    {
        final List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("FLOAT", Type.FLOAT, 5, 2));

        /*
         * Name of file is not important; it will not be created.
         */
        new Table(new File("DUMMY"), Version.DBASE_3, fields);
    }
}
