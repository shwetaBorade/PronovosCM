//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdf.interfaces;

import androidx.annotation.Nullable;

import com.pdftron.sdf.Obj;

/**
 * Callback interface to be invoked when a custom rubber stamp has been selected.
 */
public interface OnCustomStampSelectedListener {
    /**
     * Called when a custom rubber stamp is selected.
     *
     * @param stampId The identifier of the custom rubber stamp
     * @param stampObj The option for creating custom rubber stamp
     */
    void onCustomStampSelected(@Nullable String stampId, @Nullable Obj stampObj);
}
