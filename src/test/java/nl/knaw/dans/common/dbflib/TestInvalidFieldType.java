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
