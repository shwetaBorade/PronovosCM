//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2019 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdf.tools;

import static com.pdftron.pdf.Font.e_times_roman;
import static com.pdftron.pdf.GState.e_fill_stroke_text;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PointF;
import android.text.TextUtils;
import android.view.MotionEvent;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.pdftron.common.Matrix2D;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.ColorPt;
import com.pdftron.pdf.ColorSpace;
import com.pdftron.pdf.ElementBuilder;
import com.pdftron.pdf.ElementReader;
import com.pdftron.pdf.ElementWriter;
import com.pdftron.pdf.Font;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.Point;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.annots.Markup;
import com.pdftron.pdf.annots.Popup;
import com.pdftron.pdf.annots.Text;
import com.pdftron.pdf.config.ToolStyleConfig;
import com.pdftron.pdf.model.AnnotStyle;
import com.pdftron.pdf.model.FontResource;
import com.pdftron.pdf.model.LineEndingStyle;
import com.pdftron.pdf.model.LineStyle;
import com.pdftron.pdf.model.RulerItem;
import com.pdftron.pdf.model.ShapeBorderStyle;
import com.pdftron.pdf.tools.DialogAnnotNote.DialogAnnotNoteListener;
import com.pdftron.pdf.tools.ToolManager.ToolMode;
import com.pdftron.pdf.utils.AnalyticsHandlerAdapter;
import com.pdftron.pdf.utils.AnnotUtils;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.sdf.Obj;
import com.pronovos.pdf.utils.AddPunchList;
import com.pronovos.pdf.utils.AnnotAction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InputStream;

/**
 * This class is for creating a sticky note annotation.
 */
@SuppressWarnings("WeakerAccess")
@Keep
public class StickyNoteCreate extends SimpleShapeCreate
        implements DialogAnnotNoteListener,
        AnnotStyle.OnAnnotStyleChangeListener {

    protected String mIconType;
    protected int mIconColor;
    protected float mIconOpacity;
    private int mAnnotButtonPressed;
    private boolean mClosed;

    private DialogStickyNote mDialogStickyNote;

    /**
     * Class constructor
     */
    public StickyNoteCreate(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);

        mNextToolMode = ToolMode.TEXT_ANNOT_CREATE;

        // Set icon color and type based on previous style
        SharedPreferences settings = Tool.getToolPreferences(mPdfViewCtrl.getContext());

        mIconColor = settings.getInt(getColorKey(getCreateAnnotType()), ToolStyleConfig.getInstance().getDefaultColor(mPdfViewCtrl.getContext(), getCreateAnnotType()));
        mIconType = settings.getString(getIconKey(getCreateAnnotType()), ToolStyleConfig.getInstance().getDefaultIcon(mPdfViewCtrl.getContext(), getCreateAnnotType()));
        mIconOpacity = settings.getFloat(getOpacityKey(getCreateAnnotType()), ToolStyleConfig.getInstance().getDefaultOpacity(mPdfViewCtrl.getContext(), getCreateAnnotType()));

        mAllowScrollWithTapTool = true;

        //TODO GWL 07/14/2021
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * The overload implementation of {@link Tool#getToolMode()}.
     */
    @Override
    public ToolManager.ToolModeBase getToolMode() {
        return ToolMode.TEXT_ANNOT_CREATE;
    }

    @Override
    public int getCreateAnnotType() {
        return Annot.e_Text;
    }

    // TODO GWL 07/14/2021 added
    // Punch number at  bottom of the icon.
    private static boolean refreshCustomStickyNoteAppearance(
            @NonNull Context context,
            @NonNull Annot annot,
            @NonNull PDFDoc pdfDoc, String punchNumber, String punchStatus) {
        //   Log.e("STICKYNOTE", "refreshCustomStickyNoteAppearance: "+annot+"  punchNumber "+punchNumber+" punchStatus  "+punchStatus);
        InputStream fis = null;
        PDFDoc template = null;
        ElementReader reader = null;
        ElementWriter writer = null;
        ElementBuilder builder = null;
        try {
            ColorPt colorPtGray = new ColorPt(0.40, 0.40, 0.40);
            ColorPt colorPtRed = new ColorPt(1, 0, 0);
            // get icon name
            Text text = new Text(annot);
            String iconName = text.getIconName();
            // Open pdf containing custom sticky note icons. Each page is a different custom icon
            // with the page label the icon's name.
            fis = context.getResources().openRawResource(R.raw.stickynote_icons);
            template = new PDFDoc(fis);
            com.pdftron.pdf.Element element;
            // Loop through all pages, checking if the icon name equals the page label name.
            // If none of the page labels equals the icon name, then return false - the sticky note
            // icon is not a custom icon.
            for (int pageNum = 1, pageCount = template.getPageCount(); pageNum <= pageCount; ++pageNum) {
                if (iconName.equalsIgnoreCase(template.getPageLabel(pageNum).getPrefix())) {
                    Page iconPage = template.getPage(pageNum);
                    //   Log.d("punch", "rot: " + iconPage.getRotation());
                    Obj contents = iconPage.getContents();
                    Obj importedContents = annot.getSDFObj().getDoc().importObj(contents, true);
                    Rect bbox = iconPage.getMediaBox();
                    bbox.normalize();
                    importedContents.putRect("BBox", bbox.getX1(), bbox.getY1(), bbox.getX2(), bbox.getY2());
                    importedContents.putName("Subtype", "Form");
                    importedContents.putName("Type", "XObject");
                    reader = new ElementReader();
                    writer = new ElementWriter();
                    reader.begin(importedContents);
//                    importedContents.putRect("BBox", bbox.getX1(), bbox.getY1(), bbox.getX2()+20, bbox.getY2()+20);
                    writer.begin(importedContents, true);
                    double dx = 0.0; // horizontal displacement
                    double dy = 1.0;// vertical displacement
                    double sx = 0.5;// horizontal scale
                    double sy = 0.5;// vertical scale

                    double font_size = 18.0;
//                    Matrix2D mtx = new Matrix2D(sx, 0, 0, sy, dx, bbox.getY2() + dy);
                    Matrix2D mtx = new Matrix2D(sx, 0, 0, sy, dx, -(sy * font_size));
                    builder = new ElementBuilder();
                    element = builder.createTextBegin(Font.create(pdfDoc.getSDFDoc(),
                            e_times_roman, true), font_size);

                    writer.writeElement(element);
                    element = builder.createTextRun("#" + punchNumber);
                    element.setTextMatrix(mtx);
                    writer.writeElement(element);
                    if (punchStatus.equalsIgnoreCase("1")) {
                        element.getGState().setTextRenderMode(e_fill_stroke_text);
                        element.getGState().setStrokeColorSpace(ColorSpace.createDeviceRGB());
                        element.getGState().setStrokeColor(colorPtRed);
                        element.getGState().setFillColorSpace(ColorSpace.createDeviceRGB());
                        element.getGState().setFillColor(colorPtRed);
                    } else {
                        element.getGState().setTextRenderMode(e_fill_stroke_text);
                        element.getGState().setStrokeColorSpace(ColorSpace.createDeviceRGB());
                        element.getGState().setStrokeColor(colorPtGray);
                        element.getGState().setFillColorSpace(ColorSpace.createDeviceRGB());
                        element.getGState().setFillColor(colorPtGray);
                    }
                    writer.writeElement(element);
                    element = builder.createTextEnd();
                    writer.writeElement(element);
                    ColorPt rgbColor = text.getColorAsRGB();
                    double opacity = text.getOpacity();
                    for (element = reader.next(); element != null; element = reader.next()) {
                        if (element.getType() == com.pdftron.pdf.Element.e_path && !element.isClippingPath()) {
                            element.getGState().setFillColorSpace(ColorSpace.createDeviceRGB());
                            element.getGState().setFillColor(rgbColor);
                            element.getGState().setFillOpacity(opacity);
                            if (punchStatus.equalsIgnoreCase("1")) {
                                element.getGState().setStrokeColorSpace(ColorSpace.createDeviceRGB());
                                element.getGState().setStrokeColor(colorPtRed); // or better yet, strokeRgbColor so not same as fill
                            } else {
                                element.getGState().setStrokeColorSpace(ColorSpace.createDeviceRGB());
                                element.getGState().setStrokeColor(colorPtGray); // or better yet, strokeRgbColor so not same as fill
                            }
                            element.getGState().setStrokeOpacity(opacity);
                            element.setPathStroke(true);
                            element.setPathFill(true);
                        }
                        writer.writeElement(element);
                    }
//                    // update bounding boxes
                    bbox.normalize(); // make sure x1,y1 is bottom left
                    bbox.setY1(bbox.getY1() - (sy * font_size));
                    double valuey = bbox.getY2();// + dy + (sy * font_size);
                    double valuex = bbox.getY2() + dy + (sy * font_size);
                    bbox.setY2(valuey);
                    bbox.setX2(valuex);
                    Obj new_app_stm = writer.end();
                    new_app_stm.putRect(
                            "BBox",
                            bbox.getX1(),
                            bbox.getY1(),
                            bbox.getX2(),
                            bbox.getY2());
                    bbox.setY2((bbox.getY2()) + font_size);
                    bbox.setX2((bbox.getX2()) + font_size);

//                    annot.setAppearance(new_app_stm, e_normal, null);
//                    annot.setRotation(90);
                    reader.end();
                    writer.end();
                    text.setAppearance(importedContents);
                    // add number end
                    return true;
                }
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.destroy();
                } catch (Exception ignored) {
                }
            }
            if (writer != null) {
                try {
                    writer.destroy();
                } catch (Exception ignored) {
                }
            }
            Utils.closeQuietly(template);
            Utils.closeQuietly(fis);
        }
        return false;

    }

    /**
     * The overload implementation of {@link SimpleShapeCreate#setupAnnotProperty(int, float, float, int, String, String)}.
     */
    @Override
    public void setupAnnotProperty(int color, float opacity, float thickness, int fillColor, String icon, String pdfTronFontName) {
        mIconColor = color;
        mIconType = icon;
        mIconOpacity = opacity;

        SharedPreferences settings = Tool.getToolPreferences(mPdfViewCtrl.getContext());
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(getIconKey(getCreateAnnotType()), icon);
        editor.putInt(getColorKey(getCreateAnnotType()), color);
        editor.putFloat(getOpacityKey(getCreateAnnotType()), opacity);

        editor.apply();
    }

    /**
     * The overload implementation of {@link SimpleShapeCreate#onMove(MotionEvent, MotionEvent, float, float)}.
     */
    @Override
    public boolean onMove(MotionEvent e1, MotionEvent e2, float x_dist, float y_dist) {
        super.onMove(e1, e2, x_dist, y_dist);

        // allow scrolling
        return false;
    }

    /**
     * The overload implementation of {@link SimpleShapeCreate#onFlingStop()}.
     */
    @Override
    public boolean onFlingStop() {
        if (mAllowTwoFingerScroll) {
            doneTwoFingerScrolling();
        }
        return false;
    }

    /**
     * The overload implementation of {@link SimpleShapeCreate#createMarkup(PDFDoc, Rect)}.
     */
    @Override
    protected Annot createMarkup(@NonNull PDFDoc doc, Rect bbox) throws PDFNetException {
        return null;
    }

    /**
     * The overload implementation of {@link SimpleShapeCreate#onUp(MotionEvent, PDFViewCtrl.PriorEventMode)}.
     */
    @Override
    public boolean onUp(MotionEvent e, PDFViewCtrl.PriorEventMode priorEventMode) {
        mPt1.x = e.getX() + mPdfViewCtrl.getScrollX();
        mPt1.y = e.getY() + mPdfViewCtrl.getScrollY();

        // We are scrolling
        if (mAllowTwoFingerScroll || mPt1.x < 0 || mPt1.y < 0) {
            doneTwoFingerScrolling();

            return false;
        }

        // consume quick menu
        if (mSkipAfterQuickMenuClose) {
            return true;
        }

        if (priorEventMode == PDFViewCtrl.PriorEventMode.PAGE_SLIDING) {
            return false;
        }

        // If we are just up from fling or pinch, do not add new note
        if (priorEventMode == PDFViewCtrl.PriorEventMode.FLING ||
                priorEventMode == PDFViewCtrl.PriorEventMode.PINCH) {
            // allow scrolling
            return false;
        }
        // If annotation was already pushed back, avoid re-entry due to fling motion
        // but allow when creating multiple strokes.
        if (mAnnotPushedBack && mForceSameNextToolMode) {
            return true;
        }

        // If all points are outside of the page, we don't push back the annotation
        if (mIsAllPointsOutsidePage) {
            return true;
        }

        boolean shouldCreate = true;
        int x = (int) e.getX();
        int y = (int) e.getY();
        int pageNum = mPdfViewCtrl.getPageNumberFromScreenPt(x, y);
        try {
            Annot tappedAnnot = didTapOnSameTypeAnnot(e);
            if (tappedAnnot != null) {
                shouldCreate = false;
                setAnnot(tappedAnnot, pageNum);
                buildAnnotBBox();
                mNextToolMode = ToolMode.ANNOT_EDIT;
                setCurrentDefaultToolModeHelper(getToolMode());
            }
        } catch (PDFNetException ex) {
            AnalyticsHandlerAdapter.getInstance().sendException(ex);
        }

        if (shouldCreate) {
            setTargetPoint(new PointF(x, y));
        }

        return skipOnUpPriorEvent(priorEventMode);
    }

    /**
     * The overload implementation of {@link SimpleShapeCreate#onDown(MotionEvent)}.
     */
    @Override
    public boolean onDown(MotionEvent e) {

        // if quick menu just dismissed, then super.onDown will return true,
        // setting mAnnotPushedBack will stop stick note calling onUp
        mAnnotPushedBack = super.onDown(e);

        return false;
    }

    /**
     * The overload implementation of {@link SimpleShapeCreate#onClose()}.
     */
    @Override
    public void onClose() {
        if (!mClosed) {
            mClosed = true;
            unsetAnnot();
        }

        if (mDialogStickyNote != null && mDialogStickyNote.isShowing()) {
            // force to save the content
            mAnnotButtonPressed = DialogInterface.BUTTON_POSITIVE;
            prepareDialogStickyNoteDismiss();
            mDialogStickyNote.dismiss();
        }
    }

    @Override
    public boolean onQuickMenuClicked(QuickMenuItem menuItem) {
        super.onQuickMenuClicked(menuItem);
        mNextToolMode = ToolMode.ANNOT_EDIT;
        return false;
    }

    /**
     * The overload implementation of {@link SimpleShapeCreate#onConfigurationChanged(Configuration)}.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //TODO: GWL 07/14/2021 update comment to call
//        setNextToolMode();
    }

    /**
     * The overload implementation of {@link DialogAnnotNoteListener#onAnnotButtonPressed(int)}.
     */
    @Override
    public void onAnnotButtonPressed(int button) {
        mAnnotButtonPressed = button;
    }

    /**
     * Sets the target point.
     *
     * @param point The target point
     */
    public void setTargetPoint(PointF point) {
        mPt1.x = point.x + mPdfViewCtrl.getScrollX();
        mPt1.y = point.y + mPdfViewCtrl.getScrollY();
        mDownPageNum = mPdfViewCtrl.getPageNumberFromScreenPt(point.x, point.y);

        createStickyNote();

        showPopup();
    }

    protected void setCustomData(Text text) throws PDFNetException {

    }

    protected void createStickyNote() {
        boolean shouldUnlock = false;
        try {
            // add UI to drawing list
            addOldTools();

            mPdfViewCtrl.docLock(true);
            shouldUnlock = true;
            double[] pts;
            pts = mPdfViewCtrl.convScreenPtToPagePt(mPt1.x - mPdfViewCtrl.getScrollX(), mPt1.y - mPdfViewCtrl.getScrollY(), mDownPageNum);
            pts[1] -= 20;
            Point p = new Point(pts[0] - 0, pts[1] - 0);
            Text text = Text.create(mPdfViewCtrl.getDoc(), p);
            //TODO: GWL 07/14/2021 update start
//            text.setIcon(mIconType);
            text.setIcon("Punchlist");
            //TODO: GWL 07/14/2021 update End
            ColorPt color = getColorPoint(mIconColor);
            if (color != null) {
                text.setColor(color, 3);
            } else {
                text.setColor(new ColorPt(1, 1, 0), 3);
            }
            text.setOpacity(mIconOpacity);

            Rect rect = new Rect();
            rect.set(pts[0] + 20, pts[1] + 20, pts[0] + 90, pts[1] + 90);
            Popup pop = Popup.create(mPdfViewCtrl.getDoc(), rect);
            pop.setParent(text);
            text.setPopup(pop);
            setCustomData(text);
            setAuthor(text);

            Page page = mPdfViewCtrl.getDoc().getPage(mDownPageNum);
            page.annotPushBack(text);
            page.annotPushBack(pop);
            setAnnot(text, mDownPageNum);
            AnnotUtils.refreshAnnotAppearance(mPdfViewCtrl.getContext(), mAnnot);

            mAnnotPushedBack = true;
            buildAnnotBBox();
            mPdfViewCtrl.update(mAnnot, mAnnotPageNum);
            raiseAnnotationAddedEvent(mAnnot, mAnnotPageNum);
        } catch (Exception ex) {
            mNextToolMode = ToolMode.PAN;
            ((ToolManager) mPdfViewCtrl.getToolManager()).annotationCouldNotBeAdded(ex.getMessage());
            AnalyticsHandlerAdapter.getInstance().sendException(ex);
        } finally {
            if (shouldUnlock) {
                mPdfViewCtrl.docUnlock();
            }
        }
    }

    protected ColorPt getColorPoint(int color) {
        double r = (double) Color.red(color) / 255;
        double g = (double) Color.green(color) / 255;
        double b = (double) Color.blue(color) / 255;
        ColorPt c = null;
        try {
            // TODO GWL 07/14/2021 update Start
            // c = new ColorPt(r, g, b);
            c = new ColorPt(255, 255, 255, 255);
            // TODO GWL 07/14/2021 update End
        } catch (Exception ignored) {

        }
        return c;
    }

   /* private void showPopup() {
        mNextToolMode = ToolMode.TEXT_ANNOT_CREATE;

        if (mAnnot == null) {
            return;
        }

        boolean canShow = ((ToolManager) mPdfViewCtrl.getToolManager()).getStickyNoteShowPopup();
        if (!canShow) {
            setNextToolMode();
            ToolManager toolManager = (ToolManager) mPdfViewCtrl.getToolManager();
            if ((toolManager.isAutoSelectAnnotation() || !mForceSameNextToolMode)) {
                toolManager.selectAnnot(mAnnot, mAnnotPageNum);
            }
            return;
        }

        try {
            String iconType = "";
            float opacity = 1;
            try {
                Text t = new Text(mAnnot);
                iconType = t.getIconName();
                opacity = (float) t.getOpacity();
            } catch (Exception ignored) {

            }

            ColorPt colorPt = mAnnot.getColorAsRGB();
            int color = Utils.colorPt2color(colorPt);
            mDialogStickyNote = new DialogStickyNote(mPdfViewCtrl, "", false, iconType, color, opacity);
            mDialogStickyNote.setAnnotAppearanceChangeListener(this);
            mDialogStickyNote.setAnnotNoteListener(this);
            mDialogStickyNote.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    prepareDialogStickyNoteDismiss();
                }
            });
            mDialogStickyNote.show();
        } catch (Exception e1) {
            AnalyticsHandlerAdapter.getInstance().sendException(e1);
        }
    }*/

    // TODO GWL 07/14/2021 update
    private void showPopup() {
        //     Log.d("STICKYNOTE", "showPopup: ");
        mNextToolMode = ToolMode.TEXT_ANNOT_CREATE;
        if (mAnnot == null) {
            return;
        }
        boolean canShow = ((ToolManager) mPdfViewCtrl.getToolManager()).getStickyNoteShowPopup();
        if (!canShow) {
            setNextToolMode();
            ToolManager toolManager = (ToolManager) mPdfViewCtrl.getToolManager();
            if ((toolManager.isAutoSelectAnnotation() || !mForceSameNextToolMode)) {
                toolManager.selectAnnot(mAnnot, mAnnotPageNum);
            }
            return;
        }
        try {
            String iconType = "";
            float opacity = 1;
            try {
                Text t = new Text(mAnnot);
                iconType = t.getIconName();
                opacity = (float) t.getOpacity();
            } catch (Exception ignored) {
            }
            ColorPt colorPt = mAnnot.getColorAsRGB();
            int color = Utils.colorPt2color(colorPt);
            /*mDialogStickyNote = new DialogStickyNote(mPdfViewCtrl, "", false, iconType, color, opacity);
            mDialogStickyNote.setAnnotAppearanceChangeListener(this);
            mDialogStickyNote.setAnnotNoteListener(this);
            mDialogStickyNote.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    prepareDialogStickyNoteDismiss();
                }
            });
            mDialogStickyNote.show();*/

            EventBus.getDefault().post("ShowDialog");

        } catch (Exception e1) {
            AnalyticsHandlerAdapter.getInstance().sendException(e1);
        }
    }

    private void prepareDialogStickyNoteDismiss() {
        if (mPdfViewCtrl == null || mAnnot == null || mDialogStickyNote == null) {
            return;
        }
        boolean createdAnnot = false;
        if (mAnnotButtonPressed == DialogInterface.BUTTON_POSITIVE) {
            boolean shouldUnlock = false;
            try {
                final Markup markup = new Markup(mAnnot);
                // Locks the document first as accessing annotation/doc information
                // isn't thread safe.
                mPdfViewCtrl.docLock(true);
                shouldUnlock = true;
                raiseAnnotationPreModifyEvent(mAnnot, mAnnotPageNum);
                Utils.handleEmptyPopup(mPdfViewCtrl.getDoc(), markup);
                Popup popup = markup.getPopup();
                popup.setContents(mDialogStickyNote.getNote());
                setAuthor(markup);
                //TODO GWL 07/14/2021
                // raiseAnnotationModifiedEvent(mAnnot, mAnnotPageNum);
                raiseAnnotationModifiedEvent(mAnnot, mAnnotPageNum, true, false);
                createdAnnot = true;
                //TODO GWL 07/14/2021
                EventBus.getDefault().unregister(this);
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                if (shouldUnlock) {
                    mPdfViewCtrl.docUnlock();
                }
            }
        } else {
            deleteStickyAnnot();
        }
        mAnnotButtonPressed = 0;
        mDialogStickyNote.prepareDismiss();
        setNextToolMode();
        ToolManager toolManager = (ToolManager) mPdfViewCtrl.getToolManager();
        if (createdAnnot && (toolManager.isAutoSelectAnnotation() || !mForceSameNextToolMode)) {
            toolManager.selectAnnot(mAnnot, mAnnotPageNum);
        }
    }

    private void deleteStickyAnnot() {
        boolean shouldUnlock = false;
        try {
            // Locks the document first as accessing annotation/doc information isn't thread
            // safe.
            mPdfViewCtrl.docLock(true);
            shouldUnlock = true;

            raiseAnnotationPreRemoveEvent(mAnnot, mAnnotPageNum);
            Page page = mPdfViewCtrl.getDoc().getPage(mAnnotPageNum);
            page.annotRemove(mAnnot);
            mPdfViewCtrl.update(mAnnot, mAnnotPageNum);

            // make sure to raise remove event after mPdfViewCtrl.update and before unsetAnnot
            raiseAnnotationRemovedEvent(mAnnot, mAnnotPageNum);

            unsetAnnot();
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlock) {
                mPdfViewCtrl.docUnlock();
            }
        }
    }

    private void editColor(int color) {

        boolean shouldUnlock = false;
        try {
            // Locks the document first as accessing annotation/doc information
            // isn't thread safe.
            mPdfViewCtrl.docLock(true);
            shouldUnlock = true;
            raiseAnnotationPreModifyEvent(mAnnot, mAnnotPageNum);
            ColorPt colorPt = Utils.color2ColorPt(color);
            mAnnot.setColor(colorPt, 3);

            AnnotUtils.refreshAnnotAppearance(mPdfViewCtrl.getContext(), mAnnot);
            mPdfViewCtrl.update(mAnnot, mAnnotPageNum);
            //TODO GWL 07/14/2021 update start
            //raiseAnnotationModifiedEvent(mAnnot, mAnnotPageNum);
            raiseAnnotationModifiedEvent(mAnnot, mAnnotPageNum, true, false);
            //TODO GWL 07/14/2021 update End

            SharedPreferences settings = Tool.getToolPreferences(mPdfViewCtrl.getContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(getColorKey(AnnotUtils.getAnnotType(mAnnot)), color);
            editor.apply();
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlock) {
                mPdfViewCtrl.docUnlock();
            }
        }
    }

    private void editOpacity(float opacity) {

        boolean shouldUnlock = false;
        try {
            // Locks the document first as accessing annotation/doc information
            // isn't thread safe.
            mPdfViewCtrl.docLock(true);
            shouldUnlock = true;
            raiseAnnotationPreModifyEvent(mAnnot, mAnnotPageNum);
            ((Markup) mAnnot).setOpacity(opacity);
            AnnotUtils.refreshAnnotAppearance(mPdfViewCtrl.getContext(), mAnnot);
            mPdfViewCtrl.update(mAnnot, mAnnotPageNum);
            //TODO GWL 07/14/2021 update Start
            //raiseAnnotationModifiedEvent(mAnnot, mAnnotPageNum);
            raiseAnnotationModifiedEvent(mAnnot, mAnnotPageNum, true, false);
            //TODO GWL 07/14/2021 update End

            SharedPreferences settings = Tool.getToolPreferences(mPdfViewCtrl.getContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putFloat(getOpacityKey(AnnotUtils.getAnnotType(mAnnot)), opacity);
            editor.apply();
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlock) {
                mPdfViewCtrl.docUnlock();
            }
        }
    }

    private void editIcon(String icon) {
        boolean shouldUnlock = false;
        try {
            // Locks the document first as accessing annotation/doc information
            // isn't thread safe.
            mPdfViewCtrl.docLock(true);
            shouldUnlock = true;
            raiseAnnotationPreModifyEvent(mAnnot, mAnnotPageNum);
            Text text = new Text(mAnnot);
            text.setIcon(icon);
            AnnotUtils.refreshAnnotAppearance(mPdfViewCtrl.getContext(), mAnnot);
            mPdfViewCtrl.update(mAnnot, mAnnotPageNum);
            //TODO GWL 07/14/2021 update
            // raiseAnnotationModifiedEvent(mAnnot, mAnnotPageNum);
            raiseAnnotationModifiedEvent(mAnnot, mAnnotPageNum, true, false);

            SharedPreferences settings = Tool.getToolPreferences(mPdfViewCtrl.getContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(getIconKey(AnnotUtils.getAnnotType(mAnnot)), icon);
            editor.apply();
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlock) {
                mPdfViewCtrl.docUnlock();
            }
        }
    }

    private void setNextToolMode() {
        if (mAnnot != null && ((ToolManager) mPdfViewCtrl.getToolManager()).isAutoSelectAnnotation()) {
            mNextToolMode = ToolMode.ANNOT_EDIT;
            setCurrentDefaultToolModeHelper(getToolMode());
        } else if (mForceSameNextToolMode) {
            mNextToolMode = ToolMode.TEXT_ANNOT_CREATE;
        } else {
            mNextToolMode = ToolMode.PAN;
            ToolManager toolManager = (ToolManager) mPdfViewCtrl.getToolManager();
            ToolManager.Tool tool = toolManager.createTool(mNextToolMode, null);
            toolManager.setTool(tool);
        }
    }

    @Override
    public void onChangeAnnotThickness(float thickness, boolean done) {

    }

    @Override
    public void onChangeAnnotTextSize(float textSize, boolean done) {

    }

    @Override
    public void onChangeAnnotTextColor(int textColor) {

    }

    @Override
    public void onChangeAnnotOpacity(float opacity, boolean done) {
        if (done) {
            editOpacity(opacity);
        }
    }

    @Override
    public void onChangeAnnotStrokeColor(int color) {
        editColor(color);
    }

    @Override
    public void onChangeAnnotFillColor(int color) {

    }

    @Override
    public void onChangeAnnotIcon(String icon) {
        editIcon(icon);
    }

    @Override
    public void onChangeAnnotFont(FontResource font) {

    }

    @Override
    public void onChangeRulerProperty(RulerItem rulerItem) {

    }

    @Override
    public void onChangeOverlayText(String overlayText) {

    }

    @Override
    public void onChangeSnapping(boolean snap) {

    }

    @Override
    public void onChangeRichContentEnabled(boolean enabled) {

    }

    @Override
    public void onChangeDateFormat(String dateFormat) {

    }

    @Override
    public void onChangeAnnotBorderStyle(ShapeBorderStyle borderStyle) {

    }

    @Override
    public void onChangeAnnotLineStyle(LineStyle lineStyle) {

    }

    @Override
    public void onChangeAnnotLineStartStyle(LineEndingStyle lineStartStyle) {

    }

    @Override
    public void onChangeAnnotLineEndStyle(LineEndingStyle lineEndStyle) {

    }

    @Override
    public void onChangeTextAlignment(int horizontalAlignment, int verticalAlignment) {

    }

    // TODO GWL 07/14/2021 added
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AnnotAction annotAction) {
        // Log.d("StickyNote", "onEvent: annotAction "+annotAction);
        if (!TextUtils.isEmpty(annotAction.getAction()) && annotAction.getAction().equals("DismissAnnot")) {
            if (mAnnot != null) {
                deleteStickyAnnot();
                setNextToolMode();
                ToolManager toolManager = (ToolManager) mPdfViewCtrl.getToolManager();
                if ((toolManager.isAutoSelectAnnotation() || !mForceSameNextToolMode)) {
                    toolManager.selectAnnot(mAnnot, mAnnotPageNum);
                    EventBus.getDefault().post("DismissAnnot");
                }

            }
            EventBus.getDefault().unregister(this);

        }
    }

    // TODO GWL 07/14/2021 added
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPunchListEvent(AddPunchList event) {

        //  Log.d("STICKYNOTE", "onAddPunchListEvent: "+event.getContent()+"    mAnnot  "+mAnnot);
        PDFDoc doc = mPdfViewCtrl.getDoc();
        if (event != null && !TextUtils.isEmpty(event.getContent())) {
            //           Log.d("StickyNoteCreate", "onEvent: "+event.getContent());
            boolean createdAnnot = false;
            boolean shouldUnlock = false;
            try {
                final Markup markup = new Markup(mAnnot);
                // Locks the document first as accessing annotation/doc information
                // isn't thread safe.
                mPdfViewCtrl.docLock(true);
                shouldUnlock = true;
                raiseAnnotationPreModifyEvent(mAnnot, mAnnotPageNum);
                Utils.handleEmptyPopup(mPdfViewCtrl.getDoc(), markup);
                Popup popup = markup.getPopup();
                popup.setContents(event.getContent());
                markup.setInteriorColor(new ColorPt((255 / 255.0), (0 / 255.0), (255 / 255.0)), 1);
                //annot.refreshAppearance();
                //  popup.setCustomData("contents",event.getContent().trim());
                setAuthor(markup);
                String punchNumber = "";
                if(event.getPunchNumber()!=null){
                    punchNumber = String.valueOf(event.getPunchNumber());
                }
                if (punchNumber.equals("") || punchNumber.equals("-1")) {
                    punchNumber = "New";
                }
                String punchStatus = "";
                if(event.getStatus()!=null){
                    punchStatus = String .valueOf(event.getStatus());
                }
                //  Log.d("STICKYNOTE", punchNumber+"  mAnnot.getRect() "+mAnnot.getRect().getX1()+"  "+mAnnot.getRect().getY1()
                //        +"   "+mAnnot.getRect().getX2()+"  "+mAnnot.getRect().getY2());
                refreshCustomStickyNoteAppearance(mPdfViewCtrl.getContext(),mAnnot,mPdfViewCtrl.getDoc(),punchNumber,punchStatus);
                // mPdfViewCtrl.invalidate();
//for newly created punch item reload pdf
                raiseAnnotationModifiedEvent(mAnnot, mAnnotPageNum, true, true);
                createdAnnot = true;
            } catch (Exception e) {
                AnalyticsHandlerAdapter.getInstance().sendException(e);
            } finally {
                if (shouldUnlock) {
                    mPdfViewCtrl.docUnlock();
                }
            }
            mAnnotButtonPressed = 0;

            setNextToolMode();
            ToolManager toolManager = (ToolManager) mPdfViewCtrl.getToolManager();
            if (createdAnnot && (toolManager.isAutoSelectAnnotation() || !mForceSameNextToolMode)) {
                toolManager.selectAnnot(mAnnot, mAnnotPageNum);
            }
            EventBus.getDefault().unregister(this);

        }
    }
}
