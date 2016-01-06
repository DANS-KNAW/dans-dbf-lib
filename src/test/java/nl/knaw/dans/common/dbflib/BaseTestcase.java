package nl.knaw.dans.common.dbflib;

import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

/**
 * Base class for parameterized test cases.
 *
 * @author Vesa Åkerman
 */
public abstract class BaseTestcase // Lower case 'c' on purpose, otherwise JUnit complains.

{
    protected Version version;
    protected String versionDirectory;

    /**
     * Returns the list of alternative parameter tuples to run the tests with.
     *
     * @return the parameters
     */
    @Parameters
    public static Collection<Object[]> data()
    {
        final Object[][] testParameters =
            new Object[][]
            {
                { Version.DBASE_3, "dbase3plus" },
                { Version.DBASE_4, "dbase4" },
                { Version.DBASE_5, "dbase5" },
                { Version.CLIPPER_5, "clipper5" },
                { Version.FOXPRO_26, "foxpro26" }
            };

        return Arrays.asList(testParameters);
    }

    /**
     * Constructs a test case.
     *
     * @param aVersion the version parameter
     * @param aVersionDirectory the version directory parameter
     */
    protected BaseTestcase(final Version aVersion, final String aVersionDirectory)
    {
        version = aVersion;
        versionDirectory = aVersionDirectory;
    }
}
