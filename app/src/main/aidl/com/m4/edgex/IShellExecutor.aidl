package com.m4.edgex;

import com.m4.edgex.IShellCallback;
import android.os.ParcelFileDescriptor;

oneway interface IShellExecutor {
    void execute(String command, boolean runAsRoot, IShellCallback callback);
    void savePngToGallery(in ParcelFileDescriptor png, String displayName, IShellCallback callback);
}
