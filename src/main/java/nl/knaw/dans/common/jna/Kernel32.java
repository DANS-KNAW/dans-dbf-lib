package nl.knaw.dans.common.jna;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.platform.win32.WinDef.DWORD;

public interface Kernel32 extends StdCallLibrary {
        boolean LockFile(HANDLE hFile, DWORD dwFileOffsetLow, DWORD dwFileOffsetHigh, DWORD nNumberOfBytesToLockLow,
                        DWORD nNumberOfBytesToLockHigh);

        boolean UnlockFile(HANDLE hFile, DWORD dwFileOffsetLow, DWORD dwFileOffsetHigh, DWORD nNumberOfBytesToUnlockLow,
                        DWORD nNumberOfBytesToUnlockHigh);

        boolean GetVolumePathNamesForVolumeNameW(WString lpszVolumeName, char[] lpszVolumePathNames,
                        DWORD cchBufferLength, IntByReference lpcchReturnLength);

        int GetLastError();
}
