package com.pronovoscm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pdftron.common.Matrix2D;
import com.pdftron.common.PDFNetException;
import com.pdftron.fdf.FDFDoc;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.ColorPt;
import com.pdftron.pdf.ColorSpace;
import com.pdftron.pdf.DocumentConversion;
import com.pdftron.pdf.ElementBuilder;
import com.pdftron.pdf.ElementReader;
import com.pdftron.pdf.ElementWriter;
import com.pdftron.pdf.Font;
import com.pdftron.pdf.MergeXFDFOptions;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFNet;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.annots.Markup;
import com.pdftron.pdf.annots.Text;
import com.pdftron.pdf.config.PDFNetConfig;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.config.ViewerBuilder2;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.AnnotationToolbar;
import com.pdftron.pdf.controls.AnnotationToolbarButtonId;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AnalyticsHandlerAdapter;
import com.pdftron.pdf.utils.AppUtils;
import com.pdftron.pdf.utils.PdfViewCtrlSettingsManager;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.widget.AnnotView;
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder;
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;
import com.pronovos.pdf.utils.AddPunchList;
import com.pronovos.pdf.utils.EditPunchList;
import com.pronovos.pdf.utils.LinkUriAction;
import com.pronovos.pdf.utils.ModifiedAnnotation;
import com.pdftron.sdf.Obj;
import com.pronovos.pdf.utils.AnnotAction;
import com.pronovoscm.R;
import com.pronovoscm.adapter.CompanyAdapter;
import com.pronovoscm.adapter.DrawingListPopupAdapter;
import com.pronovoscm.data.DrawingAnnotationProvider;
import com.pronovoscm.data.NetworkStateProvider;
import com.pronovoscm.data.PDFFileDownloadProvider;
import com.pronovoscm.data.ProjectDrawingListProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.data.PunchListProvider;
import com.pronovoscm.fragments.PunchlistFragment;
import com.pronovoscm.model.AnnotDeleteAction;
import com.pronovoscm.model.PDFSynEnum;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.punchlist.PunchListRequest;
import com.pronovoscm.model.response.drawingstore.DrawingStoreAnnotationResponse;
import com.pronovoscm.model.response.login.LoginResponse;
import com.pronovoscm.persistence.domain.DrawingList;
import com.pronovoscm.persistence.domain.DrawingXmls;
import com.pronovoscm.persistence.domain.PunchlistDb;
import com.pronovoscm.persistence.repository.DrawingListRepository;
import com.pronovoscm.persistence.repository.PunchListRepository;
import com.pronovoscm.services.NetworkService;
import com.pronovoscm.utils.ReloadPDFEvent;
import com.pronovoscm.utils.SharedPref;
import com.pronovoscm.utils.dialogs.MessageDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
import static android.widget.RelativeLayout.ABOVE;
import static android.widget.RelativeLayout.BELOW;
import static android.widget.RelativeLayout.RIGHT_OF;
import static com.pdftron.pdf.Font.e_times_roman;
import static com.pdftron.pdf.GState.e_fill_stroke_text;
import static com.pronovoscm.fragments.PunchlistFragment.LINK_EXIST;

/**
 * Activity to show PDF of drawing with annotation using pdftron sdk
 *
 * @author GWL
 */
public class DrawingPDFActivity extends BaseActivity implements DrawingListPopupAdapter.selectDrawingList {
    public static final String TAG = DrawingPDFActivity.class.getSimpleName();
    protected AnnotView mAnnotView;
    @Inject
    DrawingAnnotationProvider mDrawingAnnotationProvider;
    @Inject
    ProjectDrawingListProvider mDrawingListProvider;
    @Inject
    PDFFileDownloadProvider mPDFFileDownloadProvider;
    @Inject
    PunchListProvider mPunchListProvider;
    @Inject
    DrawingListRepository mDrawingListRepository;
    @Inject
    PunchListRepository mPunchListRepository;
    @Inject
    DrawingAnnotationProvider mAnnotationProvider;
    @Inject
    NetworkStateProvider mNetworkStateProvider;


    @BindView(R.id.offlineTextView)
    TextView offlineTextView;
    @BindView(R.id.titleTextView)
    TextView titleTextView;
    @BindView(R.id.leftImageView)
    ImageView backImageView;
    @BindView(R.id.rightImageView)
    ImageView rightImageView;
    @BindView(R.id.increaseDrawingImageView)
    ImageView increaseDrawingImageView;
    @BindView(R.id.firstDrawingImageView)
    ImageView firstDrawingImageView;
    @BindView(R.id.lastDrawingImageView)
    ImageView lastDrawingImageView;
    @BindView(R.id.decreaseDrawingImageView)
    ImageView decreaseDrawingImageView;
    @BindView(R.id.pdfviewctrl)
    PDFViewCtrl mPdfViewCtrl;
    @BindView(R.id.annotationToolbar)
    AnnotationToolbar annotationToolbar;
    @BindView(R.id.whiteLoaderView)
    RelativeLayout whiteLoaderView;
    @BindView(R.id.bottomArrowView)
    RelativeLayout bottomArrowView;
    int lastRotation = 0;
    private PopupWindow mPopupWindow;
    private double zoomVal = 0;
    private Call<DrawingStoreAnnotationResponse> drawingAnnotations;
    private String drawingName;
    private int folderId;
    private DrawingList mDrawingDetails;
    private int revNo;
    private PDFDoc mPdfDoc;
    private ToolManager mToolManager;
    private LoginResponse loginResponse;
    private String previousAnnotation = null;
    private Long drawingID;
    private MessageDialog messageDialog;
    private PunchlistFragment punchlistFragment;
    private int projectId;
    private PunchlistDb punchlistDb;
    private int punchListId = 0;
    private AddPunchList mAddPunchList;
    private Annot mAnnot;
    private int hScrollPos, vScrollPos;
    private ArrayList<DrawingList> mDrawingList;
    private CompanyAdapter companyAdapter;
    private DrawingListPopupAdapter drawingListPopupAdapter;
    private int currentFilePosition;
    private ArrayList<Integer> forwardDrawingList;
    private HashMap<Integer, Integer> forwardDrawingListMap = new HashMap<>();
    private ArrayList<Integer> backwardDrawingList;
    private HashMap<Integer, Integer> backwardDrawingListMap = new HashMap<>();
    private boolean shouldRemoveHistory = false;
    private String deletedAnnotXml = "";
    private boolean isDocumentLoaded;
    private boolean isAnnotationsParsed;
    private Integer drawingRevisitedNum;
    private boolean isNewPunchIconAdded;

    private PdfViewCtrlTabHostFragment2 mPdfViewCtrlTabHostFragment;
    public static final String NOTES_TOOLBAR_TAG = "notes_toolbar";
    public static final String SHAPES_TOOLBAR_TAG = "shapes_toolbar";

    // Punch number at  bottom of the icon.
    private static boolean refreshCustomStickyNoteAppearance(
            @NonNull Context context,
            @NonNull Annot annot,
            @NonNull PDFDoc pdfDoc, String punchNumber, String punchStatus) {

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
                    com.pdftron.pdf.Rect bbox = iconPage.getMediaBox();
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
                                    e_times_roman, true),
                            font_size);

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
//
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

    @Override
    protected int doGetContentView() {
        try {
            PDFNet.initialize(this, R.raw.pdfnet, "ProNovos LLC (pronovos.com):OEM:ProNovos Operations Manager::IA:AMS(20221219):6D7766101F2784D0A333FD7860611FB5F7184A45B512E411B7126E1D3A9431F5C7");
            Log.d(TAG, "doGetContentView: ");
            if (!PDFNet.hasBeenInitialized()) {
                Log.d(TAG, "doGetContentView: in if part");
//                PDFNet.initialize(this, R.raw.pdfnet, "ProNovos LLC(pronovos.com):OEM:ProNovos Operations Manager::IA:AMS(20211219):27960FA0B6100C7A2F9623F10B292D940A0BE51C6B11B7126E1D3A9431F5C7");
                PDFNet.initialize(this, R.raw.pdfnet, "ProNovos LLC (pronovos.com):OEM:ProNovos Operations Manager::IA:AMS(20221219):6D7766101F2784D0A333FD7860611FB5F7184A45B512E411B7126E1D3A9431F5C7");
            }
        } catch (PDFNetException e) {
            e.printStackTrace();
        }
        return R.layout.drawing_pdf_view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doGetApplication().getDaggerComponent().inject(this);
        forwardDrawingList = new ArrayList<>();
        backwardDrawingList = new ArrayList<>();
        messageDialog = new MessageDialog();
        if (savedInstanceState != null && savedInstanceState.getBoolean("not_get_intent")) {

        } else {
            drawingName = getIntent().getStringExtra("drawing_name");
            folderId = getIntent().getIntExtra("drawing_folder_id", 0);
            revNo = getIntent().getIntExtra("drawing_rev_no", 0);
            drawingID = getIntent().getLongExtra("drawing_id", 0);
            projectId = getIntent().getIntExtra("projectId", 0);
        }
        mDrawingDetails = mDrawingListProvider.getDrawingDetail(folderId, drawingName, revNo, drawingID);
//        titleTextView.setText(drawingName);
        titleTextView.setText(drawingName + " - " + mDrawingDetails.getDescriptions());

        setCurrentFilePosition();
        backImageView.setImageResource(R.drawable.ic_arrow_back);
        rightImageView.setVisibility(View.INVISIBLE);

        try {
            AppUtils.setupPDFViewCtrl(mPdfViewCtrl);
        } catch (PDFNetException e) {
            e.printStackTrace();
        }
        setUpAnnotationToolbar(getResources().getConfiguration().orientation);

        getFileAndLoad(false, false);
    }

    private void setCurrentFilePosition() {
        mDrawingList = mDrawingListRepository.getAllCurrentRevisionDrawings(folderId);
        companyAdapter = new CompanyAdapter(this, R.layout.simple_spinner_item, mDrawingList);
        companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        LayoutInflater inflater = (LayoutInflater) DrawingPDFActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.drawing_popup_view, null);
        mPopupWindow = new PopupWindow(
                customView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        RecyclerView recyclerView = customView.findViewById(R.id.drawingRecyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        drawingListPopupAdapter = new DrawingListPopupAdapter(DrawingPDFActivity.this, mDrawingList, mDrawingDetails);
        recyclerView.setAdapter(drawingListPopupAdapter);
        for (int i = 0; i < mDrawingList.size(); i++) {
            if (mDrawingList.get(i).getOriginalDrwId() == mDrawingDetails.getOriginalDrwId()) {
                currentFilePosition = i;
            }
        }

        if (mDrawingList.size() == 1) {
            increaseDrawingImageView.setImageResource(R.drawable.ic_right_arrow_disable);
            decreaseDrawingImageView.setImageResource(R.drawable.ic_previous_disable);
        } else {
            increaseDrawingImageView.setImageResource(R.drawable.ic_right_arrow);
            decreaseDrawingImageView.setImageResource(R.drawable.ic_previous);
        }
    }

    private void loadPDF(File file) {
        isDocumentLoaded = false;
        isAnnotationsParsed = false;
        try {
            Uri imageUri = Uri.parse(file.getPath());
            if (mDrawingDetails.getPdfOrg() != null && !TextUtils.isEmpty(mDrawingDetails.getPdfOrg())) {
                mPdfDoc = mPdfViewCtrl.openPDFUri(imageUri, null);
                mPdfViewCtrl.setDoc(mPdfDoc);
            } else {
                DocumentConversion documentConversion = mPdfViewCtrl.openNonPDFUri(imageUri, null);
                mPdfDoc = documentConversion.getDoc();
            }
            mPdfViewCtrl.addDocumentLoadListener(() -> {
                isDocumentLoaded = true;
                if (isAnnotationsParsed) {
                    whiteLoaderView.setVisibility(View.GONE);
                }
            });


            mToolManager = ToolManagerBuilder.from(this, R.style.MyToolManager)
                    .build(this, mPdfViewCtrl);
            mPdfViewCtrl.setOverprint(PDFViewCtrl.OverPrintMode.OFF);
            mPdfViewCtrl.setClientBackgroundColor(241, 247, 251, false);

            mToolManager.setSnappingEnabledForMeasurementTools(true);

            mPdfViewCtrl.setToolManager(mToolManager);
            annotationToolbar.setup(mToolManager);
            mPdfViewCtrl.setCurrentPage(0);
            annotationToolbar.show();
            hideAnnotationToolBarButtons(annotationToolbar);
            if (loginResponse.getUserDetails().getPermissions().get(0).getDrawingToolbar() != 1) {
                annotationToolbar.setVisibility(View.GONE);
            }
            // enable long click listner for drag feature on long press
            //  mPdfViewCtrl.setLongPressEnabled(false);
            mToolManager.addAnnotationModificationListener(annotationModificationListener);
//            callDrawingAnnotationAPI();
            /*ToolManagerBuilder tmBuilder = ToolManagerBuilder.from()
                    .setUseDigitalSignature(false)
                    .setAutoResizeFreeText(false);
            int cutoutMode = 0;
            if (Utils.isPie()) {
                cutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }     mToolManager.disableToolMode(new ToolManager.ToolMode[]{});
            if (mPdfViewCtrl != null) {
                ((ToolManager) mPdfViewCtrl.getToolManager()).setBuiltInPageNumberIndicatorVisible(false);
                showAnnotations(true);
            }*/

            mToolManager.disableToolMode(new ToolManager.ToolMode[]{});
            ToolManagerBuilder tmBuilder = ToolManagerBuilder.from()
                    .setUseDigitalSignature(false)
                    .setAutoResizeFreeText(false);
            int cutoutMode = 0;
            if (Utils.isPie()) {
                cutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
            if (mPdfViewCtrl != null) {
                ((ToolManager) mPdfViewCtrl.getToolManager()).setBuiltInPageNumberIndicatorVisible(false);
                Log.d(TAG, "loadPDF: showAnnotations: true ");
                showAnnotations(true);
            }
        } catch (IllegalArgumentException e) {

            Log.i(TAG, "1  loadPDF: " + e.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "loadPDF: " + e.toString());
        } catch (PDFNetException e) {
            e.printStackTrace();
            Log.i(TAG, "PDFNetException loadPDF: " + e.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i(TAG, "Exce loadPDF: " + ex.toString());
        }

    }

    ToolManager.AnnotationModificationListener annotationModificationListener = new ToolManager.AnnotationModificationListener() {
        @Override
        public void onAnnotationsAdded(Map<Annot, Integer> annots) {
//             Log.d(TAG, "onAnnotationsAdded: " + annots);
            onDoneClick(true, true);
        }

        @Override
        public void onAnnotationsPreModify(Map<Annot, Integer> annots) {
            isNewPunchIconAdded = false;
        }

        /*
        @Override
        public void onAnnotationsModified(Map<Annot, Integer> annots, Bundle extra) {
        }*/
        @Override
        public void onAnnotationsModified(Map<Annot, Integer> annots, Bundle extra, boolean b, boolean isStickAnnotAdded) {
            //  Log.d(TAG, "onAnnotationsModified: isStickAnnotAdded "+isStickAnnotAdded+"   b = "+b);
            isNewPunchIconAdded = false;
            if (b && !isStickAnnotAdded) {
                modifyAnnotations(true, false);
            } else if (b && isStickAnnotAdded) {
                modifyAnnotations(true, isStickAnnotAdded);

            }
        }

        @Override
        public void onAnnotationsPreRemove(Map<Annot, Integer> annots) {
            isNewPunchIconAdded = false;
        }

        @Override
        public void onAnnotationsRemoved(Map<Annot, Integer> annots) {
            //    Log.d(TAG, "onAnnotationsRemoved: ");
            isNewPunchIconAdded = false;
            onDoneClick(true, false);
        }

        @Override
        public void onAnnotationsRemovedOnPage(int pageNum) {
            isNewPunchIconAdded = false;
        }

        @Override
        public void annotationsCouldNotBeAdded(String errorMessage) {
            isNewPunchIconAdded = false;
        }
    };

    /**
     * Download PDF from the server
     *
     * @param drawingList drawing list object
     */
    private void getDrawingFile(DrawingList drawingList) {
        mPDFFileDownloadProvider.getDrawingPDF(drawingList, loginResponse.getUserDetails().getUsers_id(), new ProviderResult<Boolean>() {
            @Override
            public void success(Boolean result) {
                //      Log.d(TAG, "getDrawingFile   success: ");
                if (result) {
                    try {
                        URL url = null;
                        if (mDrawingDetails.getPdfOrg() != null && !TextUtils.isEmpty(mDrawingDetails.getPdfOrg())) {
                            url = new URL(mDrawingDetails.getPdfOrg());
                        } else {
                            url = new URL(mDrawingDetails.getImageOrg());
                        }
                        String[] segments = url.getPath().split("/");
                        String fileName = segments[segments.length - 1];

                        String completePath = getFilesDir().getAbsolutePath() + "/Pronovos/PDF/" + loginResponse.getUserDetails().getUsers_id() + fileName;

                        File file = new File(completePath);
                        Log.d(TAG, "success: " + file.getPath());
                        loadPDF(file);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void AccessTokenFailure(String message) {

            }

            @Override
            public void failure(String message) {
                Log.e("TAG", "failure PDF: " + message);

            }
        });
    }

    /**
     * Check that File is exist in storage or not.
     *
     * @param fileName
     * @return
     */
    private boolean isFileExist(String fileName) {
        String root = getFilesDir().getAbsolutePath();
        File myDir = new File(root + "/Pronovos/PDF");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = fileName;
        File file = new File(myDir, fname);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Show annotation over PDF
     *
     * @param updateTextRotation
     */
    private void showAnnotations(boolean updateTextRotation) {
             Log.d(TAG, "showAnnotations: updateTextRotation = "+updateTextRotation);
        boolean params = updateTextRotation;
        new LoadAnnotation().executeOnExecutor(THREAD_POOL_EXECUTOR, params);
    }

    /**
     * Update the annotation to make annotation read only or print
     *
     * @param annotxml           annotation xml
     * @param updateTextRotation
     * @return
     */
    private String convertXML(String annotxml, boolean updateTextRotation) {
//        annotXml="<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n<xfdf xmlns=\\\"http://ns.adobe.com/xfdf/\\\" xml:space=\\\"preserve\\\">\\n\\t<annots>\\n\\t\\t\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195718Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195718Z\\\" page=\\\"0\\\" rect=\\\"2274.46,2031.96,2286.24,2066.4\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195719Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"796.196,1078.1,818.435,1087.12\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195719Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"1674.12,56.6626,1696.36,65.6772\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195719Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"1894.2,2068.46,1916.43,2077.48\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195719Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"2213.64,1041.14,2235.84,1050.16\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195719Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"1877.52,1195.94,1899.72,1204.96\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195719Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"2062.8,1425.74,2085,1434.76\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195719Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"1839.96,1608.02,1862.2,1617.04\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195720Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"962.635,1221.38,984.833,1230.4\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195720Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"1854.72,47.4228,1876.91,56.4374\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195720Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"1315.32,47.4228,1337.51,56.4374\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195720Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"1495.68,47.4228,1517.87,56.4374\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195720Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"962.995,991.703,985.233,1000.72\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195720Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"962.995,397.702,985.233,406.717\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195720Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"962.995,1928.42,985.233,1937.44\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195720Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"1842.6,1071.74,1864.83,1080.76\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195720Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"962.875,1428.62,985.113,1437.64\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195720Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"2064.96,1965.86,2087.2,1974.88\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195720Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"1926,1596.02,1948.23,1605.04\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195721Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195721Z\\\" page=\\\"0\\\" rect=\\\"957.595,120.023,979.834,129.037\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195721Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195721Z\\\" page=\\\"0\\\" rect=\\\"1932.12,135.263,1954.36,144.277\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195721Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195721Z\\\" page=\\\"0\\\" rect=\\\"1118.4,1877.18,1140.63,1886.2\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195721Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195721Z\\\" page=\\\"0\\\" rect=\\\"1947,1215.62,1969.23,1224.64\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195722Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195722Z\\\" page=\\\"0\\\" rect=\\\"1693.2,1702.1,1715.44,1711.12\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195722Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195722Z\\\" page=\\\"0\\\" rect=\\\"1408.68,576.863,1430.91,585.877\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<square color=\\\"#00FF00\\\" opacity=\\\"0.5\\\" creationdate=\\\"D:20190430195722Z\\\" flags=\\\"readonly\\\" interior-color=\\\"#00FF00\\\" date=\\\"D:20190430195722Z\\\" page=\\\"0\\\" rect=\\\"2314.56,996.623,2336.8,1005.64\\\" title=\\\"Rachel Blanchard\\\" />\\n\\t\\t<text color=\\\"#000000\\\" creationdate=\\\"D:20190614100403-06\\'00\\'\\\" flags=\\\"readonly\\\" date=\\\"D:20190614100403-06\\'00\\'\\\" name=\\\"226a7b50-e6c4-71ed-da53-fb10b74b9a46\\\" icon=\\\"Comment\\\" page=\\\"0\\\" rect=\\\"754.221,1350.2,774.221,1370.2\\\" subject=\\\"text\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<contents>punch_id = 1449, punch_id_mobile = 0, punch_status = 1), punch_number = 75, title = Rachel Blanchard)</contents>\\n\\t\\t\\t<apref y=\\\"1370.2\\\" x=\\\"754.221\\\" gennum=\\\"0\\\" objnum=\\\"54\\\" />\\n\\t\\t</text>\\n\\t\\t<link style=\\\"solid\\\" width=\\\"0\\\" name=\\\"MTCSBYGMWGRBYOOC\\\" page=\\\"0\\\" rect=\\\"2272.33,1989.48,2286.39,2070.15\\\" title=\\\"Tenant Company\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<GoToR NewWindow=\\\"true\\\">\\n\\t\\t\\t\\t\\t\\t<Dest />\\n\\t\\t\\t\\t\\t</GoToR>\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195718Z\\\" page=\\\"0\\\" rect=\\\"2274.46,2031.96,2286.24,2066.4\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6212\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"796.196,1078.1,818.435,1087.12\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6235\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"1674.12,56.6626,1696.36,65.6772\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6235\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"1894.2,2068.46,1916.43,2077.48\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6235\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"2213.64,1041.14,2235.84,1050.16\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6236\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"1877.52,1195.94,1899.72,1204.96\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6236\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"2062.8,1425.74,2085,1434.76\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6236\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195719Z\\\" page=\\\"0\\\" rect=\\\"1839.96,1608.02,1862.2,1617.04\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6236\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"962.635,1221.38,984.833,1230.4\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6240\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"1854.72,47.4228,1876.91,56.4374\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6244\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"1315.32,47.4228,1337.51,56.4374\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6245\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"1495.68,47.4228,1517.87,56.4374\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6245\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"962.995,991.703,985.233,1000.72\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6246\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"962.995,397.702,985.233,406.717\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6246\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"962.995,1928.42,985.233,1937.44\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6247\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"1842.6,1071.74,1864.83,1080.76\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6248\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"962.875,1428.62,985.113,1437.64\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6249\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"2064.96,1965.86,2087.2,1974.88\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6250\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195720Z\\\" page=\\\"0\\\" rect=\\\"1926,1596.02,1948.23,1605.04\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6252\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195721Z\\\" page=\\\"0\\\" rect=\\\"957.595,120.023,979.834,129.037\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6256\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195721Z\\\" page=\\\"0\\\" rect=\\\"1932.12,135.263,1954.36,144.277\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6256\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195721Z\\\" page=\\\"0\\\" rect=\\\"1118.4,1877.18,1140.63,1886.2\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6256\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195721Z\\\" page=\\\"0\\\" rect=\\\"1947,1215.62,1969.23,1224.64\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6266\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195722Z\\\" page=\\\"0\\\" rect=\\\"1693.2,1702.1,1715.44,1711.12\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6276\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195722Z\\\" page=\\\"0\\\" rect=\\\"1408.68,576.863,1430.91,585.877\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6284\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<link color=\\\"#000000\\\" flags=\\\"readonly\\\" date=\\\"D:20190430195722Z\\\" page=\\\"0\\\" rect=\\\"2314.56,996.623,2336.8,1005.64\\\" title=\\\"Rachel Blanchard\\\">\\n\\t\\t\\t<OnActivation>\\n\\t\\t\\t\\t<Action Trigger=\\\"U\\\">\\n\\t\\t\\t\\t\\t<URI Name=\\\"/6287\\\" />\\n\\t\\t\\t\\t</Action>\\n\\t\\t\\t</OnActivation>\\n\\t\\t</link>\\n\\t\\t<ink style=\\\"solid\\\" width=\\\"1\\\" color=\\\"#FF0000\\\" opacity=\\\"1\\\" creationdate=\\\"D:20190719123209Z\\\" flags=\\\"print\\\" date=\\\"D:20190719123209Z\\\" page=\\\"0\\\" rect=\\\"552.506,948.714,637.132,1909.71\\\">\\n\\t\\t\\t<inklist>\\n\\t\\t\\t\\t<gesture>553.006,1909.21;574.587,1825.62;596.168,1693.48;617.748,1413.03;636.632,949.214</gesture>\\n\\t\\t\\t</inklist>\\n\\t\\t</ink>\\n\\t</annots>\\n\\t<pages>\\n\\t\\t<defmtx matrix=\\\"1.333333,0.000000,0.000000,-1.333333,0.000000,2880.000000\\\" />\\n\\t</pages>\\n\\t<pdf-info version=\\\"2\\\" xmlns=\\\"http://www.pdftron.com/pdfinfo\\\" />\\n</xfdf>";
        Log.d(TAG, "convertXML: annotxml = " + annotxml);
        if (TextUtils.isEmpty(annotxml)) {
            return "";
        }
        try {
            //  writeStringAsFile(annotxml,"/mnt/sdcard/Download/12222221root_annot1_.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(annotxml)));
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("annots");
            if (nodeList.getLength() > 0) {
                NodeList namedNodeMap = nodeList.item(0).getChildNodes();
                for (int i = 0; i < namedNodeMap.getLength(); i++) {
                    Node node = namedNodeMap.item(i);
                    Log.d(TAG, "convertXML: " + node.getNodeName());
                    try {
                        if (node instanceof Element) {
                            String title = ((Element) node).getAttribute("title");
                            String publish = ((Element) node).getAttribute("publish");
                            if ((publish == null || !publish.equals("true")) && title.equals(loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname())) {
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("flags", "");//not published
                            } else if (publish != null && publish.equals("true") && title != null && title.equals(loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname())) {
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("flags", "print");//self publish
                            } else if (((Element) node).getTagName().equals("link")) {
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("flags", "print");//self publish
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).removeAttribute("title");//self publish
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).removeAttribute("publish");//self publish
                                ((Element) node).setAttribute("rotation", "270");
                            } else {
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("flags", "readonly");
                            }

                            // Text rotation
                            if (((Element) node).getTagName().equals("freetext") && (((Element) node).getAttribute("rotation") != null
                                    && !((Element) node).getAttribute("rotation").equals("") &&
                                    !((Element) node).getAttribute("rotation").equals("0"))) {

                                Double rotation = Double.parseDouble(((Element) node).getAttribute("rotation"));
                                Log.e(TAG, "############### convertXML: free text rotation " + rotation);
                                //FIXME commented below line because we are facing text rotation on some pdf while load
                                if (rotation >= 90) {
                                    ((Element) node).setAttribute("rotation", String.valueOf(lastRotation));
                                }
                            }

                            // Stamp rotation
                            if (((Element) node).getTagName().equals("stamp") && (((Element) node).getAttribute("rotation") != null
                                    && !((Element) node).getAttribute("rotation").equals("") &&
                                    !((Element) node).getAttribute("rotation").equals("0"))) {

                                Double rotation = Double.parseDouble(((Element) node).getAttribute("rotation"));
                                Log.e(TAG, "############### convertXML: stamp rotation " + rotation);
                                //FIXME commented below line because we are facing text rotation on some pdf while load
                                if (rotation >= 90) {
                                    rotation = rotation - 90;
                                }else {
                                    rotation = 90 - rotation;
                                }
                                ((Element) node).setAttribute("rotation", String.valueOf(rotation));
                                Log.e(TAG, "############### convertXML: stamp rotation 1 " +
                                        ((Element) node).getAttribute("rotation"));
                            }


                            if (((Element) node).getTagName().equals("text") || ((Element) node).getTagName().equals("punchitem")) {

                                if (loginResponse.getUserDetails().getPermissions().get(0).getViewPunchList() != 1) {
                                    ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("flags", "hidden");
                                }

                                // my code was here


                                if (((Element) node).getTagName().equals("punchitem") && (((Element) node).getAttribute("punch_id")) != null) {
                                    try {
                                        ((Element) node).setAttribute("icon", "PunchList");

                                        String childNode = null;
                                        for (int n = 0; n < (node).getChildNodes().getLength(); n++) {
                                            if ((node).getChildNodes().item(n).getNodeName().equals("contents")) {
                                                childNode = (node).getChildNodes().item(n).getTextContent();
                                            }
                                        }

                                        String str = "punch_id = " + (((Element) node).getAttribute("punch_id")) +
                                                ", punch_id_mobile = " + (((Element) node).getAttribute("punch_id_mobile")) +
                                                ", punch_status = " + (((Element) node).getAttribute("punch_status")) +
                                                ", punch_number = " + (((Element) node).getAttribute("punch_number")) +
                                                ", title = " + title;

                                        Log.d(TAG, "convertXML: add content node  " + str);
                                        if (childNode == null || !childNode.contains("punch_id")) {

                                            ((Element) node).setAttribute("contents", str);
//                                            ((Element) node).setAttribute("contents", str);
                                            Node item = doc.createElement("contents");
                                            item.setNodeValue(str);
                                            item.setTextContent(str);
                                            (node).appendChild(item);
                                        } else if (!childNode.contains("title")) {
                                            childNode = childNode + ", title = " + title;
                                            Node item = doc.createElement("contents");
                                            item.setNodeValue(childNode);
                                            item.setTextContent(childNode);
                                            (node).appendChild(item);
                                        }
                                        if (((Element) node).getAttribute("subject") != null) {
                                            ((Element) node).removeAttribute("subject");
                                        }
                                        if (((Element) node).getAttribute("name") != null) {
                                            ((Element) node).removeAttribute("name");
                                        }
                                        ((Element) node).setAttribute("color", "#FFCD45");
                                        ((Element) node).setAttribute("opacity", "1");
                                        ((Element) node).setAttribute("rotation", "90");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }

                try {

                    DOMSource domSource = new DOMSource(doc);
                    StringWriter writer = new StringWriter();
                    StreamResult result = new StreamResult(writer);

                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    transformer.transform(domSource, result);
                    String string = writer.toString();

                    Runtime.getRuntime().gc();
                    string = string.replaceAll("punchitem", "text");
//                    Log.i(TAG, "convertXML: " + string);

                    //    Log.e(TAG, "convertXML: end here  string xml -= "+string);
                    return string;
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        // Log.d(TAG, "convertXML: end here  ");
        return "";
    }

    @Subscribe
    public void onPunchIconCreateEvent(ReloadPDFEvent event) {

        if (isNewPunchIconAdded) {

            reloadFile();

            isNewPunchIconAdded = false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        findViewById(com.pdftron.pdf.tools.R.id.controls_annotation_toolbar_btn_close).setVisibility(View.GONE);
        findViewById(com.pdftron.pdf.tools.R.id.controls_annotation_toolbar_tool_multi_select).setVisibility(View.GONE);
        findViewById(com.pdftron.pdf.tools.R.id.controls_annotation_toolbar_tool_pan).setVisibility(View.GONE);
        findViewById(com.pdftron.pdf.tools.R.id.controls_annotation_toolbar_btn_more).setVisibility(View.GONE);
//        int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        setUpAnnotationToolbar(newConfig.orientation);
    }

    private void setUpAnnotationToolbar(int orientation) {

        int actionBarHeight = 0;//= getSupportActionBar().getHeight();
        if (actionBarHeight == 0) {
            final TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(actionBarHeight, ViewGroup.LayoutParams.MATCH_PARENT);
            RelativeLayout.LayoutParams layoutParamspdf = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setLayoutDirection(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(ABOVE, bottomArrowView.getId());
            annotationToolbar.setLayoutParams(layoutParams);
            layoutParamspdf.removeRule(BELOW);
            layoutParamspdf.addRule(ABOVE, bottomArrowView.getId());
            layoutParamspdf.setLayoutDirection(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParamspdf.addRule(RIGHT_OF, annotationToolbar.getId());
            mPdfViewCtrl.setLayoutParams(layoutParamspdf);


        } else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, actionBarHeight);
            RelativeLayout.LayoutParams layoutParamspdf = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.height = actionBarHeight;
            layoutParams.setLayoutDirection(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.removeRule(ABOVE);
            annotationToolbar.setLayoutParams(layoutParams);
            layoutParamspdf.removeRule(RIGHT_OF);
            layoutParamspdf.addRule(ABOVE, bottomArrowView.getId());
            layoutParamspdf.addRule(BELOW, annotationToolbar.getId());
            mPdfViewCtrl.setLayoutParams(layoutParamspdf);
            annotationToolbar.setLayoutParams(layoutParams);
        }
    }

    /**
     * Get the annotations from the server
     */
    private void callDrawingAnnotationAPI() {
        DrawingXmls drawingXmls = mDrawingAnnotationProvider.isAnnotationSync(mDrawingDetails.getDrawingsId());
        if (drawingXmls == null || drawingXmls.getIsSync() && NetworkService.isNetworkAvailable(this)) {
        } else if (NetworkService.isNetworkAvailable(this)) {
        } else {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.destroy();
            mPdfViewCtrl = null;
        }

        if (mPdfDoc != null) {
            try {
                mPdfDoc.close();
            } catch (Exception e) {
                // handle exception
            } finally {
                mPdfDoc = null;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Boolean event) {
        if (event) {
            offlineTextView.setVisibility(View.VISIBLE);
        } else {
            offlineTextView.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransactionUpdate(TransactionLogUpdate transactionLogUpdate) {
        //     Log.e(TAG, "onTransactionUpdate: isNewPunchIconAdded  "+isNewPunchIconAdded);
        if (mDrawingDetails != null) {
            DrawingXmls drawingXmls = mDrawingAnnotationProvider.getDrawingAnnotation(mDrawingDetails.getDrawingsId());

            if (drawingXmls != null && (transactionLogUpdate.getTransactionModuleEnum() != null && transactionLogUpdate.getTransactionModuleEnum().equals(TransactionModuleEnum.DRAWING_ANNOTATION) && transactionLogUpdate.getDrawingId() == drawingXmls.getDrwDrawingsId().intValue())) {
                // commented below call for app crash crash bug fix
                //  getFileAndLoad(true, false);
//            loadOnlyAnnot();
            } else {
//                getFileAndLoad(true);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AnnotDeleteAction annotAction) {
        Log.d(TAG, "onEvent:AnnotDeleteAction  " + annotAction + "  mAnnot = " + mAnnot);
        if (!TextUtils.isEmpty(annotAction.getAction()) && annotAction.getAction().equals("AnnotAction")) {
            DrawingXmls drawingXmls = mDrawingAnnotationProvider.getDrawingAnnotation(mDrawingDetails.getDrawingsId());
            try {
                String content = mAnnot.getContents();
                com.pdftron.pdf.Rect rect = mAnnot.getRect();
//                mAnnot.getSDFObj().find("title");
                String rectVal = rect.getX1() + ":" + rect.getY1() + ":" + rect.getX2() + ":" + rect.getY2();
                if (TextUtils.isEmpty(drawingXmls.getAnnotdeletexml())) {
                    deletedAnnotXml = content + ",rect=" + rectVal;
                } else {
                    deletedAnnotXml = drawingXmls.getAnnotdeletexml() + ";" + content + ",rect=" + rectVal;
                }
                // Log.i(TAG, "onEvent: deletedAnnotXml" + deletedAnnotXml);
                drawingXmls.setAnnotdeletexml(deletedAnnotXml);
                mDrawingAnnotationProvider.updateDrawing(drawingXmls);

                deleteStickyAnnot();
                if (mAnnot != null) {
                    mPdfViewCtrl.update(mAnnot, 1);
                }
            } catch (PDFNetException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLinkEvent(LinkUriAction linkUriAction) {
          Log.e(TAG, "*****EE*******11 onLinkEvent: folderId  "+folderId+ "  drawingRevisitedNum "+drawingRevisitedNum);
        if (linkUriAction != null && linkUriAction.getLinkUri() != null) {
            String linkURI = linkUriAction.getLinkUri().replaceFirst("/", "");
//            DrawingList originalDrawing = mDrawingListRepository.getDrawing(folderId, Integer.parseInt(linkURI));

            DrawingList originalDrawing = mDrawingListRepository.getDrawing(folderId, Integer.parseInt(linkURI), drawingRevisitedNum);
                   Log.e(TAG, "*****EE******* 22onLinkEvent: folderId  "+folderId+"  orignalDrawingID  linkURI   "+Integer.parseInt(linkURI)+"  drawingRevisitedNum "+drawingRevisitedNum);
            if (originalDrawing != null && originalDrawing.getPdfStatus() == PDFSynEnum.SYNC.ordinal()) {
                if (linkUriAction.isAddInBack()) {
                    Log.d(TAG, forwardDrawingList.size() + "  size onLinkEvent: forwardDrawingList.add " + mDrawingDetails.getOriginalDrwId());
                    forwardDrawingList.add(mDrawingDetails.getOriginalDrwId());
                    forwardDrawingListMap.put(mDrawingDetails.getOriginalDrwId(), mDrawingDetails.getRevisitedNum());
                    firstDrawingImageView.setImageResource(R.drawable.ic_first_drawing);
                }

                drawingName = originalDrawing.getDrawingName();
                folderId = originalDrawing.getDrwFoldersId();
                revNo = originalDrawing.getRevisitedNum();
                drawingID = originalDrawing.getId();
                projectId = getIntent().getIntExtra("projectId", 0);
                mDrawingDetails = originalDrawing;
                titleTextView.setText(drawingName);
                titleTextView.setText(drawingName + " - " + mDrawingDetails.getDescriptions());
                setCurrentFilePosition();
                if (mPdfViewCtrl != null) {
                    try {
                        mPdfViewCtrl.docUnlockRead();
                    } catch (Exception e) {

                    }
                    try {
                        mPdfViewCtrl.setDoc(mPdfDoc);
                    } catch (PDFNetException e) {
                        e.printStackTrace();
                    }
//                    whiteLoaderView.setVisibility(View.GONE);
                    titleTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
                    titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);
                    mPdfViewCtrl.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onLinkEvent: getFileAndLoad: false and false");
                    getFileAndLoad(false, false);
                }
            } else {
                if (!mNetworkStateProvider.isOffline()) {
                    Log.d(TAG, "onLinkEvent: AlertDialog: "+ originalDrawing.getDrawingName());
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setMessage("Would you like to sync and navigate to drawing " + originalDrawing.getDrawingName() + " - " + originalDrawing.getDescriptions() + "?");
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> {
                        enableNavigation();
                        dialog.dismiss();
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
                        if (originalDrawing.getPdfStatus() != PDFSynEnum.PROCESSING.ordinal()) {
                            if (!mNetworkStateProvider.isOffline()) {
                                whiteLoaderView.setVisibility(View.VISIBLE);
                                titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_alpha, 0);
                                titleTextView.setTextColor(ContextCompat.getColor(this, R.color.disable_white));
                                mPdfViewCtrl.setVisibility(View.GONE);
                                onDownloadPDF(originalDrawing, linkUriAction);
                            }
                        }

                    });
                    alertDialog.show();
                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(ContextCompat.getColor(this, R.color.gray_948d8d));
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                } else {

                    Log.d(TAG, "onLinkEvent: else  AlertDialog: "+ originalDrawing.getDrawingName());
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setMessage("Drawing " + originalDrawing.getDrawingName() + " - " + originalDrawing.getDescriptions() + " is not synced to your device. You must be online to sync drawings.");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                        enableNavigation();
                        dialog.dismiss();
                    });
                    alertDialog.show();
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

                }
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLinkEvent(LinkUriAction linkUriAction, int changeCurrentPosition) {

        if (linkUriAction != null && linkUriAction.getLinkUri() != null) {
            String linkURI = linkUriAction.getLinkUri().replaceFirst("/", "");
            // Log.e(TAG, "onLinkEvent:  linkURI "+linkURI+"    drawingRevisitedNum  "+drawingRevisitedNum+ " changeCurrentPosition  "+changeCurrentPosition);
            DrawingList originalDrawing = mDrawingListRepository.getDrawing(folderId, Integer.parseInt(linkURI), drawingRevisitedNum);
            if (originalDrawing != null && originalDrawing.getPdfStatus() == PDFSynEnum.SYNC.ordinal()) {
                if (linkUriAction.isAddInBack()) {
                    //   Log.d(TAG, forwardDrawingList.size()+"  EEEEEEEEEEEEE  onLinkEvent: forwardDrawingList.add "+mDrawingDetails.getOriginalDrwId() +"  mDrawingDetails.getRevisitedNum() "+mDrawingDetails.getRevisitedNum());
                    forwardDrawingList.add(mDrawingDetails.getOriginalDrwId());
                    forwardDrawingListMap.put(mDrawingDetails.getOriginalDrwId(), mDrawingDetails.getRevisitedNum());
                    firstDrawingImageView.setImageResource(R.drawable.ic_first_drawing);
                }
                currentFilePosition = currentFilePosition + changeCurrentPosition;

                drawingName = originalDrawing.getDrawingName();
                folderId = originalDrawing.getDrwFoldersId();
                revNo = originalDrawing.getRevisitedNum();
                drawingID = originalDrawing.getId();
                projectId = getIntent().getIntExtra("projectId", 0);
                mDrawingDetails = originalDrawing;
                titleTextView.setText(drawingName);
                titleTextView.setText(drawingName + " - " + mDrawingDetails.getDescriptions());
                setCurrentFilePosition();
                if (mPdfViewCtrl != null) {
                    try {
                        mPdfViewCtrl.docUnlockRead();
                    } catch (Exception e) {

                    }
                    try {
                        mPdfViewCtrl.setDoc(mPdfDoc);
                    } catch (PDFNetException e) {
                        e.printStackTrace();
                    }
//                    whiteLoaderView.setVisibility(View.GONE);
                    titleTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
                    titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);
                    mPdfViewCtrl.setVisibility(View.VISIBLE);
                    getFileAndLoad(false, false);
                }
            } else {
                if (!mNetworkStateProvider.isOffline()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setMessage("Would you like to sync and navigate to drawing " + originalDrawing.getDrawingName() + " - " + originalDrawing.getDescriptions() + "?");
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> {
                        dialog.dismiss();
                        enableNavigation();
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
                        if (originalDrawing.getPdfStatus() != PDFSynEnum.PROCESSING.ordinal()) {
                            if (!mNetworkStateProvider.isOffline()) {
                                whiteLoaderView.setVisibility(View.VISIBLE);
                                titleTextView.setTextColor(ContextCompat.getColor(this, R.color.disable_white));
                                titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_alpha, 0);
                                mPdfViewCtrl.setVisibility(View.GONE);
                                onDownloadPDF(originalDrawing, linkUriAction);
                            }
                        }

                    });
                    alertDialog.show();
                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(ContextCompat.getColor(this, R.color.gray_948d8d));
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setMessage("Drawing " + originalDrawing.getDrawingName() + " - " + originalDrawing.getDescriptions() + " is not synced to your device. You must be online to sync drawings.");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                        enableNavigation();
                        dialog.dismiss();
                    });
                    alertDialog.show();
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

                }
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setOnLinkEvent(LinkUriAction linkUriAction, int changeCurrentPosition) {

        if (linkUriAction != null && linkUriAction.getLinkUri() != null) {
            String linkURI = linkUriAction.getLinkUri().replaceFirst("/", "");
            DrawingList originalDrawing = mDrawingListRepository.getDrawing(folderId, Integer.parseInt(linkURI), drawingRevisitedNum);
            if (originalDrawing.getPdfStatus() == PDFSynEnum.SYNC.ordinal()) {
                if (linkUriAction.isAddInBack()) {
                    //Log.d(TAG, forwardDrawingList.size()+"   EEEE setOnLinkEvent: forwardDrawingList.add "+mDrawingDetails.getOriginalDrwId());
                    forwardDrawingList.add(mDrawingDetails.getOriginalDrwId());
                    forwardDrawingListMap.put(mDrawingDetails.getOriginalDrwId(), mDrawingDetails.getRevisitedNum());
                    firstDrawingImageView.setImageResource(R.drawable.ic_first_drawing);
                }
                currentFilePosition = changeCurrentPosition;

                drawingName = originalDrawing.getDrawingName();
                folderId = originalDrawing.getDrwFoldersId();
                revNo = originalDrawing.getRevisitedNum();
                drawingID = originalDrawing.getId();
                projectId = getIntent().getIntExtra("projectId", 0);
                mDrawingDetails = originalDrawing;
                titleTextView.setText(drawingName);
                titleTextView.setText(drawingName + " - " + mDrawingDetails.getDescriptions());
                setCurrentFilePosition();
                if (mPdfViewCtrl != null) {
                    mPdfViewCtrl.docUnlockRead();
                    try {
                        mPdfViewCtrl.setDoc(mPdfDoc);
                    } catch (PDFNetException e) {
                        e.printStackTrace();
                        enableNavigation();
                    }
//                    whiteLoaderView.setVisibility(View.GONE);
                    titleTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
                    titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);
                    mPdfViewCtrl.setVisibility(View.VISIBLE);
                    getFileAndLoad(false, false);
                }
            } else {
                if (!mNetworkStateProvider.isOffline()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setMessage("Would you like to sync and navigate to drawing " + originalDrawing.getDrawingName() + " - " + originalDrawing.getDescriptions() + "?");
                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> {
                        enableNavigation();
                        dialog.dismiss();
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
                        if (originalDrawing.getPdfStatus() != PDFSynEnum.PROCESSING.ordinal()) {
                            if (!mNetworkStateProvider.isOffline()) {
                                whiteLoaderView.setVisibility(View.VISIBLE);
                                titleTextView.setTextColor(ContextCompat.getColor(this, R.color.disable_white));
                                titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_alpha, 0);
                                mPdfViewCtrl.setVisibility(View.GONE);
                                onDownloadPDF(originalDrawing, linkUriAction);
                            }
                        }

                    });
                    alertDialog.show();
                    Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    nbutton.setTextColor(ContextCompat.getColor(this, R.color.gray_948d8d));
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setMessage("Drawing " + originalDrawing.getDrawingName() + " - " + originalDrawing.getDescriptions() + " is not synced to your device. You must be online to sync drawings.");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), (dialog, which) -> {
                        enableNavigation();
                        dialog.dismiss();
                    });
                    alertDialog.show();
                    Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    pbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

                }
            }

        } else {
            enableNavigation();
        }
    }

    /**
     * download the pdf if there is any event from event bus
     *
     * @param drawingList
     * @param linkUriAction
     */
    public void onDownloadPDF(DrawingList drawingList, LinkUriAction linkUriAction) {
        mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.PROCESSING.ordinal());
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

        mPDFFileDownloadProvider.getDrawingPDF(drawingList, loginResponse.getUserDetails().getUsers_id(), new ProviderResult<Boolean>() {
            @Override
            public void success(Boolean result) {
                Log.i("FileDownload", "success: " + result);
                if (result) {
                    getDrawingAnnotation(drawingList, linkUriAction);
                } else {
                    enableNavigation();
                    whiteLoaderView.setVisibility(View.GONE);
                    titleTextView.setTextColor(ContextCompat.getColor(DrawingPDFActivity.this, R.color.white));
                    titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);

                    mPdfViewCtrl.setVisibility(View.VISIBLE);
                    mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                enableNavigation();
                whiteLoaderView.setVisibility(View.GONE);
                titleTextView.setTextColor(ContextCompat.getColor(DrawingPDFActivity.this, R.color.white));
                titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);
                mPdfViewCtrl.setVisibility(View.VISIBLE);
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.NOTSYNC.ordinal());
            }

            @Override
            public void failure(String message) {
                Log.e("TAG", "failure PDF: 1 " + message);
                enableNavigation();
                whiteLoaderView.setVisibility(View.GONE);
                titleTextView.setTextColor(ContextCompat.getColor(DrawingPDFActivity.this, R.color.white));
                titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);
                mPdfViewCtrl.setVisibility(View.VISIBLE);
                messageDialog.showMessageAlert(DrawingPDFActivity.this, message, getString(R.string.ok));
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.NOTSYNC.ordinal());
            }
        });
    }

    /**
     * Get the annotation of the drawing from the server
     *
     * @param drawingList
     * @param linkUriAction
     */
    private void getDrawingAnnotation(DrawingList drawingList, LinkUriAction linkUriAction) {
        LoginResponse loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
        mAnnotationProvider.getDrawingAnnotations(drawingList.getRevisitedNum(), drawingList.getDrwFoldersId(), drawingList.getDrawingsId(), new ProviderResult<String>() {
            @Override
            public void success(String result) {

                try {
                    URL url = null;

                    if (drawingList.getPdfOrg() != null && !TextUtils.isEmpty(drawingList.getPdfOrg())) {
                        url = new URL(drawingList.getPdfOrg());
                    } else {
                        url = new URL(drawingList.getImageOrg());
                    }

                    String[] segments = url.getPath().split("/");
                    String fileName = segments[segments.length - 1];

                    if (isFileExist(loginResponse.getUserDetails().getUsers_id() + fileName) && result != null) {
                        enableNavigation();
                        mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC.ordinal());
                        LinkUriAction linkUriAction = new LinkUriAction();
                        linkUriAction.setLinkUri("/" + drawingList.getOriginalDrwId());
                        linkUriAction.setAddInBack(true);
                        //  drawingRevisitedNum = drawingList.getRevisitedNum();
                        Log.d(TAG, "111111111111  success: drawingList.getOriginalDrwId() " + drawingList.getOriginalDrwId() + "  revisited number = " + drawingRevisitedNum);
                        onLinkEvent(linkUriAction);
                    } else {
                        enableNavigation();
                        titleTextView.setTextColor(ContextCompat.getColor(DrawingPDFActivity.this, R.color.white));
                        titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);
                        whiteLoaderView.setVisibility(View.GONE);
                        mPdfViewCtrl.setVisibility(View.VISIBLE);
                        mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.NOTSYNC.ordinal());
                    }
                } catch (MalformedURLException m) {
                    enableNavigation();
                    m.printStackTrace();
                    mPdfViewCtrl.setVisibility(View.VISIBLE);
                    whiteLoaderView.setVisibility(View.GONE);
                    titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);
                    titleTextView.setTextColor(ContextCompat.getColor(DrawingPDFActivity.this, R.color.white));
                }
            }

            @Override
            public void AccessTokenFailure(String message) {
                enableNavigation();
                whiteLoaderView.setVisibility(View.GONE);
                titleTextView.setTextColor(ContextCompat.getColor(DrawingPDFActivity.this, R.color.white));
                titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);
                mPdfViewCtrl.setVisibility(View.VISIBLE);
                startActivity(new Intent(DrawingPDFActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                SharedPref.getInstance(DrawingPDFActivity.this).writePrefs(SharedPref.SESSION_DETAILS, null);
                SharedPref.getInstance(DrawingPDFActivity.this).writePrefs(SharedPref.LOGIN_SUCCESS_OR_NOT_PREF, "0");
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.NOTSYNC.ordinal());
                finish();
            }

            @Override
            public void failure(String message) {
                enableNavigation();
                whiteLoaderView.setVisibility(View.GONE);
                titleTextView.setTextColor(ContextCompat.getColor(DrawingPDFActivity.this, R.color.white));
                titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white, 0);
                mPdfViewCtrl.setVisibility(View.VISIBLE);
                mDrawingListRepository.updatePDFSync(drawingList, PDFSynEnum.SYNC_FAILED.ordinal());
                messageDialog.showMessageAlert(DrawingPDFActivity.this, message, getString(R.string.ok));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String event) {
        Log.d(TAG, "onEvent:  event " + event);
        if (!TextUtils.isEmpty(event) && event.equals("ShowDialog")) {
            Log.i(TAG, "onEvent: 1 " + " open dialog");
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            punchlistFragment = new PunchlistFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("projectId", projectId);
            bundle.putBoolean("linkExisting", true);
            bundle.putParcelable("drawing_details", mDrawingList.get(currentFilePosition));
            punchlistFragment.setArguments(bundle);
            ft.replace(R.id.punchlistContainer, punchlistFragment, punchlistFragment.getClass().getSimpleName()).addToBackStack(PunchlistFragment.class.getName());
            ft.commit();

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ModifiedAnnotation annot) {
        //  Log.d(TAG, "onEvent: ModifiedAnnotation  "+annot);
        onDoneClick(true, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EditPunchList editPunchList) {
        //  Log.d(TAG, "onEvent: EditPunchList "+editPunchList);
        if (editPunchList != null) {
            String[] str = editPunchList.getContent().split(",");
            Log.e(TAG, "onEvent 1: "+editPunchList.getContent() );
            long punchListMobileId = 0;
            for (int i = 0; i < str.length; i++) {
                String str1 = str[i];

                Log.e(TAG, "onEvent: "+str1 );
                if (str1.contains("punch_id") && !str1.contains("punch_id_mobile")) {
                    String[] val = str1.split("=");
                    if (val.length > 1) {
                        if (!val[1].isEmpty()) punchListId = Integer.parseInt(val[1].trim());
                    }
                }
                if (str1.contains("punch_id_mobile")) {
                    String[] val2 = str1.split("=");
                    punchListMobileId = Long.parseLong(val2[1].trim());
                }
            }

            if (punchListId != 0) {
                punchlistDb = mPunchListRepository.getPunchListPunchId(punchListId);
                if (punchlistDb == null) {
                    PunchListRequest punchListRequest = new PunchListRequest();
                    punchListRequest.setProjectId(projectId);
                    punchListRequest.setPunchlists(mPunchListRepository.getNonSyncPunchListSyncAttachmentList(projectId));
                    mPunchListProvider.getPunchList(punchListRequest, new ProviderResult<List<PunchlistDb>>() {
                        @Override
                        public void success(List<PunchlistDb> result) {
                            punchlistDb = mPunchListRepository.getPunchListPunchId(punchListId);
                            if (punchlistDb != null) {
                                openPunchlistDialog(punchlistDb, editPunchList);
                            }
                        }

                        @Override
                        public void AccessTokenFailure(String message) {
                        }

                        @Override
                        public void failure(String message) {

                        }
                    });
                } else {
                    openPunchlistDialog(punchlistDb, editPunchList);
                }
            } else {
                punchlistDb = mPunchListRepository.getPunchListDetail(punchListMobileId);
                if (punchlistDb != null) {
                    openPunchlistDialog(punchlistDb, editPunchList);
                }
            }

        }
    }

    @Subscribe
    public void onPunchListAdd(PunchlistDb punchlistDb) {

        if (punchlistDb.getPunchlistId() == 0) {
            mAddPunchList = new AddPunchList();
            String str = "punch_id = " + punchlistDb.getPunchlistId() +
                    ", punch_id_mobile = " + punchlistDb.getPunchlistIdMobile() +
                    ", punch_status = " + punchlistDb.getStatus() +
                    ", punch_number = " + punchlistDb.getItemNumber() +
                    ", title = " + loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname();


            mPunchListRepository.addPunchlistDrawing(punchlistDb, mDrawingList.get(currentFilePosition));
            mAddPunchList.setContent(str);
            mAddPunchList.setPunchNumber(punchlistDb.getItemNumber());
            mAddPunchList.setStatus(punchlistDb.getStatus());
            Log.d(TAG, "STICKYNOTE onPunchListAdd: punchlistDb.getPunchlistId() =  " + punchlistDb.getPunchlistId() + "   PunchNumber =    punchlistDb.getItemNumber()  " + punchlistDb.getItemNumber());
            EventBus.getDefault().post(mAddPunchList);
        }
    }

    @OnClick(R.id.decreaseDrawingImageView)
    public void onDecreaseDrawingImageView() {
        disableNavigation();
        if (mDrawingList.size() > 1) {
            if (shouldRemoveHistory) {
                forwardDrawingList = new ArrayList<>();
                backwardDrawingList = new ArrayList<>();
                shouldRemoveHistory = false;
                lastDrawingImageView.setImageResource(R.drawable.ic_last_drawing_disable);
            }
           /* forwardDrawingList.add(mDrawingDetails.getOriginalDrwId());
            firstDrawingImageView.setImageResource(R.drawable.ic_first_drawing);
           */
            if (currentFilePosition == 0) {
//                currentFilePosition = mDrawingList.size() - 1;
                DrawingList drawingList = mDrawingList.get(mDrawingList.size() - 1);
                drawingRevisitedNum = drawingList.getRevisitedNum();
                LinkUriAction linkUriAction = new LinkUriAction();
                linkUriAction.setLinkUri("/" + drawingList.getOriginalDrwId());
                linkUriAction.setAddInBack(true);
                setOnLinkEvent(linkUriAction, mDrawingList.size() - 1);
            } else {
//                currentFilePosition = currentFilePosition - 1;
                DrawingList drawingList = mDrawingList.get(currentFilePosition - 1);
                LinkUriAction linkUriAction = new LinkUriAction();
                linkUriAction.setLinkUri("/" + drawingList.getOriginalDrwId());
                linkUriAction.setAddInBack(true);
                drawingRevisitedNum = drawingList.getRevisitedNum();
                Log.d(TAG, " 5555555555555 onDecreaseDrawingImageView: drawingList.getOriginalDrwId() " + drawingList.getOriginalDrwId() + "  drawingRevisitedNum  " + drawingRevisitedNum);
                onLinkEvent(linkUriAction, -1);
            }
        }

    }

    @OnClick(R.id.increaseDrawingImageView)
    public void onIncreaseDrawingImageView() {
        disableNavigation();
        if (mDrawingList.size() > 1) {
            if (shouldRemoveHistory) {
                forwardDrawingList = new ArrayList<>();
                backwardDrawingList = new ArrayList<>();
                shouldRemoveHistory = false;
                lastDrawingImageView.setImageResource(R.drawable.ic_last_drawing_disable);
            }
            if (currentFilePosition == mDrawingList.size() - 1) {
//                currentFilePosition = 0;
                DrawingList drawingList = mDrawingList.get(0);
                drawingRevisitedNum = drawingList.getRevisitedNum();
                LinkUriAction linkUriAction = new LinkUriAction();
                linkUriAction.setLinkUri("/" + drawingList.getOriginalDrwId());
                linkUriAction.setAddInBack(true);
                setOnLinkEvent(linkUriAction, 0);
            } else {
//                currentFilePosition = currentFilePosition + 1;
                DrawingList drawingList = mDrawingList.get(currentFilePosition + 1);
                LinkUriAction linkUriAction = new LinkUriAction();
                linkUriAction.setLinkUri("/" + drawingList.getOriginalDrwId());
                linkUriAction.setAddInBack(true);
                drawingRevisitedNum = drawingList.getRevisitedNum();
                onLinkEvent(linkUriAction, +1);
            }
        }
    }

    @OnClick(R.id.firstDrawingImageView)
    public void onFirstDrawingImageView() {
//        whiteLoaderView.setVisibility(View.VISIBLE);
        shouldRemoveHistory = true;
        if (forwardDrawingList.size() > 0) {
            backwardDrawingList.add(mDrawingDetails.getOriginalDrwId());
            backwardDrawingListMap.put(mDrawingDetails.getOriginalDrwId(), mDrawingDetails.getRevisitedNum());
            lastDrawingImageView.setImageResource(R.drawable.ic_last_drawing);
            int drawingOriginalId = forwardDrawingList.get(forwardDrawingList.size() - 1);
            LinkUriAction linkUriAction = new LinkUriAction();
            linkUriAction.setLinkUri("/" + drawingOriginalId);
            linkUriAction.setAddInBack(false);
            drawingRevisitedNum = forwardDrawingListMap.get(drawingOriginalId);
            Log.d(TAG, "2222222222222  onFirstDrawingImageView: drawingRevisitedNum " + drawingRevisitedNum + "  drawingOriginalId " + drawingOriginalId);
            onLinkEvent(linkUriAction);
            forwardDrawingList.remove(forwardDrawingList.size() - 1);
            if (forwardDrawingList.size() == 0) {
                firstDrawingImageView.setImageResource(R.drawable.ic_first_drawing_disable);

            }
        }
    }


    @OnClick(R.id.lastDrawingImageView)
    public void onLastDrawingImageView() {
        shouldRemoveHistory = true;
        if (backwardDrawingList.size() > 0) {
            int drawingOriginalId = backwardDrawingList.get(backwardDrawingList.size() - 1);
            drawingRevisitedNum = backwardDrawingListMap.get(drawingOriginalId);
            LinkUriAction linkUriAction = new LinkUriAction();
            linkUriAction.setLinkUri("/" + drawingOriginalId);
            linkUriAction.setAddInBack(false);
            Log.d(TAG, forwardDrawingList.size() + " EEEEE onLastDrawingImageView: forwardDrawingList.add " + mDrawingDetails.getOriginalDrwId());
            forwardDrawingList.add(mDrawingDetails.getOriginalDrwId());
            forwardDrawingListMap.put(mDrawingDetails.getOriginalDrwId(), mDrawingDetails.getRevisitedNum());
            firstDrawingImageView.setImageResource(R.drawable.ic_first_drawing);
            backwardDrawingList.remove(backwardDrawingList.size() - 1);
            if (backwardDrawingList.size() == 0) {
                lastDrawingImageView.setImageResource(R.drawable.ic_last_drawing_disable);

            }
            Log.d(TAG, "333333333333333  onLastDrawingImageView: drawingOriginalId " + drawingOriginalId + " drawingRevisitedNum  " + drawingRevisitedNum);
            onLinkEvent(linkUriAction);
        }
    }

    @OnClick(R.id.titleTextView)
    public void onDrawingNameClick() {
        if (whiteLoaderView.getVisibility() == View.VISIBLE) {
            return;
        }
        setCurrentFilePosition();
        int[] loc_int = new int[2];

        try {
            titleTextView.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.

        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + titleTextView.getWidth();
        location.bottom = location.top + titleTextView.getHeight();
        if (mPopupWindow != null) {
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAtLocation(titleTextView, Gravity.TOP | Gravity.CENTER, 0, location.top + titleTextView.getHeight());
        }
    }

    private void disableNavigation() {
        increaseDrawingImageView.setClickable(false);
        decreaseDrawingImageView.setClickable(false);
        lastDrawingImageView.setClickable(false);
        firstDrawingImageView.setClickable(false);
    }

    private void enableNavigation() {
        increaseDrawingImageView.setClickable(true);
        decreaseDrawingImageView.setClickable(true);
        lastDrawingImageView.setClickable(true);
        firstDrawingImageView.setClickable(true);
    }

    @OnClick(R.id.leftImageView)
    public void onBackImageClick() {
        onBackPressed();
        if (punchlistFragment != null) {
            AnnotAction annotAction = new AnnotAction();
            annotAction.setAction("DismissAnnot");
            EventBus.getDefault().post(annotAction);
            punchlistFragment = null;
        }
    }

    public void onDoneClick(boolean b, boolean isAdded) {
        saveAnnotations(b, isAdded);
    }

   /* public void modifyFreeText(Map<Annot, Integer> annots) {
        for(Annot a: annots.keySet()){
            if(a.isMarkup()){

            }
        }
    }*/
    private void saveAnnotations(boolean b, boolean isAdded) {
        try {
            FDFDoc fdfDoc = mPdfDoc.fdfExtract(PDFDoc.e_both);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(fdfDoc.saveAsXFDF())));
            //    writeStringAsFile(fdfDoc.saveAsXFDF(),"/mnt/sdcard/Download/111111root_annot1_.xml");
            Log.d(TAG, "saveAnnotations: initial xml = "/*+fdfDoc.saveAsXFDF()*/);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("annots");
            if (nodeList.getLength() > 0) {
                NodeList namedNodeMap = nodeList.item(0).getChildNodes();
                //  Log.i(TAG, "saveAnnotations: loop start  "+namedNodeMap.getLength()+"  isAdded  "+isAdded);
                for (int i = 0; i < namedNodeMap.getLength(); i++) {
                    Node node = namedNodeMap.item(i);

                    //  Log.i(TAG, "** saveAnnotations: loop start   " + i+"  name  = "+node.getNodeName());

                    try {
                        if (node instanceof Element) {
                            //       Log.i(TAG, "saveAnnotations: xml node =   "+node.getNodeName());
                            String title = ((Element) node).getAttribute("title");
                            String flags = ((Element) node).getAttribute("flags");

                            // For free text color
                            if (((Element) node).getTagName().equals("freetext") && (((Element) node).getAttribute("TextColor") == null || ((Element) node).getAttribute("TextColor").equals(""))) {
                                String s = ((Element) node).getElementsByTagName("defaultstyle").item(0).getTextContent();
                                String[] s1 = s.split("#", s.length() - 1);
                                ((Element) node).setAttribute("TextColor", "#" + s1[1]);
                            }


                            if (flags.contains("print")) {
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("publish", "true");
                            }
                            if (flags.contains("readonly") || flags.contains("hidden")) {
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("flags", "print");
                            }


                            if (((Element) node).getTagName().equals("text") || ((Element) node).getTagName().equals("punchitem")) {

                                String childNodeValue = null;
                                int childN = 0;

                                Node previoudAprefNode = null;
                                for (int k = 0; k < node.getChildNodes().getLength(); k++) {
                                    Node currentNode = node.getChildNodes().item(k);
                                    // Log.d(TAG, node.getChildNodes().getLength() +"   convertXML: current node Name  = "+(currentNode).getNodeName());
                                    if ((currentNode).getNodeName().equals("apref")) {
                                        Node currentAprefNode = currentNode;
                                        if (previoudAprefNode != null && currentAprefNode != null) {
                                            node.removeChild(previoudAprefNode);
                                            previoudAprefNode = currentAprefNode;
                                        } else {
                                            previoudAprefNode = currentAprefNode;
                                        }
                                    }
                                }
                                previoudAprefNode = null;

                                for (int n = 0; n < (node).getChildNodes().getLength(); n++) {
                                    if ((node).getChildNodes().item(n).getNodeName().equals("contents")) {
                                        childNodeValue = (node).getChildNodes().item(n).getTextContent();
                                        childN = n;
                                    }
                                }
                                //   Log.e(TAG, "saveAnnotations: childNodeValue   = "+childNodeValue);
                                if (childNodeValue != null && childNodeValue.contains("punch_id")) {
                                    ((Element) node).setAttribute("icon", "Comment");

                                    String[] punchlistIdArray = childNodeValue.split(",");

                                    int punchId = 0;
                                    String punchIdMobile = "";
                                    String punchStatus = "";
                                    String punchNumber = "";
                                    String punchTitle = "";
                                    String contentPunchNumber = "";
                                    boolean isSync = true;
                                    for (int p = 0; p < punchlistIdArray.length; p++) {
                                        String str1 = punchlistIdArray[p];

                                        if (str1.contains("punch_id") && !str1.contains("punch_id_mobile")) {
                                            String[] val = str1.split("=");
                                            if (val.length > 1 && val[1].trim().length() > 0) {
                                                punchId = Integer.parseInt(val[1].trim());
                                            }
                                            if (punchId != 0) {
                                            }
                                        }
                                        if (str1.contains("punch_status")) {
                                            String[] val = str1.split("=");
                                            if (val.length > 1 && val[1].trim().length() > 0) {
                                                punchStatus = val[1].trim();
                                                if (punchStatus.length() > 1) {
                                                    punchStatus = punchStatus.substring(0, 1);
                                                }
                                            }
                                        }
                                        if (str1.contains("title")) {
                                            String[] val = str1.split("=");
                                            if (val.length > 1 && val[1].trim().length() > 0 && !val[1].trim().equals("-1")) {
                                                punchTitle = val[1].replaceAll("\\)", "").trim();
                                            }
                                        }
                                        if (str1.contains("punch_number")) {
                                            String[] val = str1.split("=");
                                            if (val.length > 1 && val[1].trim().length() > 0 && !val[1].trim().equals("-1")) {
                                                punchNumber = val[1].trim();
                                                contentPunchNumber = val[1].trim();
                                            }
                                        }
                                        if (str1.contains("punch_id_mobile")) {
                                            String[] val = str1.split("=");
                                            punchIdMobile = val[1].trim();
                                            PunchlistDb punchlistDb = null;
                                            if (punchId == 0) {
                                                if (val.length > 1 && val[1].trim().length() > 0) {
                                                    punchlistDb = mPunchListRepository.getPunchListDetail(Integer.parseInt(val[1].trim()));
                                                }
                                            } else {
                                                punchlistDb = mPunchListRepository.getPunchListDetail(punchId, true);

                                            }
                                            if (punchlistDb != null) {
                                                punchId = punchlistDb.getPunchlistId();
                                                isSync = punchlistDb.getIsSync();

                                                punchNumber = String.valueOf(punchlistDb.getItemNumber());

                                            }
                                        }
                                    }
                                    if (punchId == 0 || !contentPunchNumber.equals("")) {

                                    } else {
                                        //     Log.e(TAG, "saveAnnotations: adding new  node here  ");
                                        ((Element) node).setAttribute("punch_id", String.valueOf(punchId));
                                        ((Element) node).setAttribute("punch_id_mobile", punchIdMobile);
                                        ((Element) node).setAttribute("punch_status", punchStatus.replaceAll("\\)", ""));
                                        ((Element) node).setAttribute("punch_number", punchNumber);
                                        ((Element) node).setAttribute("title", punchTitle.replaceAll("\\)", ""));
                                        node.getChildNodes().item(childN).setTextContent("punch_id = " + String.valueOf(punchId).trim() +
                                                ", punch_id_mobile = " + punchIdMobile +
                                                ", punch_status = " + punchStatus.replaceAll("\\)", "") +
                                                ", punch_number = " + punchNumber +
                                                ", title = " + punchTitle.replaceAll("\\)", ""));
                                    }
                                    ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("publish", "true");
                                } /*else if ((title == null || TextUtils.isEmpty(title))) {
                                    Log.d(TAG, "saveAnnotations: title null return from here ");
                                    return;
                                }*/
                            }
                            if (title == null || TextUtils.isEmpty(title)) {
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("title", loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname());
                            }
                        }
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, "saveAnnotations: loop end");

                try {
                    DOMSource domSource = new DOMSource(doc);
                    StringWriter writer = new StringWriter();
                    StreamResult result = new StreamResult(writer);
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    transformer.transform(domSource, result);

                    String annotations = writer.toString();
                    annotations = annotations.replaceAll("</text>", "</punchitem>");
                    annotations = annotations.replaceAll("<text ", "<punchitem ");
                    //      Log.e(TAG, " write file  /mnt/sdcard/Download/annotXml.xml  than go to update xml in Db  convertXML: " );

                    //   writeStringAsFile(annotations,"/mnt/sdcard/Download/annotXml.xml");//      Log.d(TAG, "saveAnnotations:  xml  going to save in DB xml = "+annotations);

                    mDrawingAnnotationProvider.doUpdateDrawingAnnotationTable(annotations, mDrawingDetails.getDrawingsId(), false, true, false, deletedAnnotXml);
                    //  DrawingXmls drawingXmls = mDrawingAnnotationProvider.getDrawingAnnotation(mDrawingDetails.getDrawingsId());
                    deletedAnnotXml = "";
                    if (drawingAnnotations != null) {
                        drawingAnnotations.cancel();
                    }
                    if (mPdfViewCtrl != null) {
                        zoomVal = mPdfViewCtrl.getZoom();
                        mPdfViewCtrl.getScrollX();
                        mPdfViewCtrl.getScrollY();
                        hScrollPos = mPdfViewCtrl.getHScrollPos();
                        vScrollPos = mPdfViewCtrl.getVScrollPos();
                        //   Log.i(TAG, "saveAnnotations: zoom " + zoomVal);

                        /*if (b) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("not_get_intent", true);
                            this.onCreate(bundle);
                        }*/
// comment below line for fixing bug of app hang and crash  on writing text
                        //  getFileAndLoad(true, isAdded);

//                    loadOnlyAnnot();
                    }


//                    saveAnnotations(drawingXmls);
                } catch (TransformerException e) {
                    e.printStackTrace();
                }

                //     Log.i(TAG, "**  saveAnnotations() onDoneClick: " + ""/*fdfDoc.saveAsXFDF()*/);
            } else {
                // Log.i(TAG, "**  saveAnnotations() else case update table from pdfdoc xml : "  );
                mDrawingAnnotationProvider.doUpdateDrawingAnnotationTable(fdfDoc.saveAsXFDF(), mDrawingDetails.getDrawingsId(), false,
                        true, false, "");
                //     DrawingXmls drawingXmls = mDrawingAnnotationProvider.getDrawingAnnotation(mDrawingDetails.getDrawingsId());
                if (drawingAnnotations != null) {
                    drawingAnnotations.cancel();
                }
//                saveAnnotations(drawingXmls);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (PDFNetException e1) {
            e1.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
      /* if(!isAdded){
      // reload only annot not file uncoment this after fix of xml issue
            showAnnotations(false);
        }*/
        /* if(!isAdded){
            // reload pdf file for duplicate issue
            //FIXME remove below reloading and fix xml issue
            getFileAndLoad(true, isAdded);


        }*/

        mPdfViewCtrl.setLongPressEnabled(true);


    }


    private void modifyAnnotations(boolean b, boolean isAdded) {
        try {
            isNewPunchIconAdded = isAdded;
            FDFDoc fdfDoc = mPdfDoc.fdfExtract(PDFDoc.e_both);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(fdfDoc.saveAsXFDF())));
            //  writeStringAsFile(fdfDoc.saveAsXFDF(),"/mnt/sdcard/Download/M1_root_annot1_.xml");
            //       Log.e(TAG, "modifyAnnotations: initial xml = "+fdfDoc.saveAsXFDF());
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("annots");
            if (nodeList.getLength() > 0) {
                NodeList namedNodeMap = nodeList.item(0).getChildNodes();
                Log.i(TAG, "modifyAnnotations: loop start  " + namedNodeMap.getLength() + "  isAdded  " + isAdded);
                for (int i = 0; i < namedNodeMap.getLength(); i++) {
                    Node node = namedNodeMap.item(i);

                    //  Log.i(TAG, "** saveAnnotations: loop start   " + i+"  name  = "+node.getNodeName());

                    try {
                        if (node instanceof Element) {
                            //      Log.i(TAG, "modifyAnnotations: xml node =   "+node.getNodeName());
                            String title = ((Element) node).getAttribute("title");
                            String flags = ((Element) node).getAttribute("flags");

                            // For free text color
                            if (((Element) node).getTagName().equals("freetext") && (((Element) node).getAttribute("TextColor") == null || ((Element) node).getAttribute("TextColor").equals(""))) {
                                String s = ((Element) node).getElementsByTagName("defaultstyle").item(0).getTextContent();
                                String[] s1 = s.split("#", s.length() - 1);

                                ((Element) node).setAttribute("TextColor", "#" + s1[1]);
                                Node previoudAprefNode = null;
                                for (int k = 0; k < node.getChildNodes().getLength(); k++) {
                                    Node currentNode = node.getChildNodes().item(k);
                                    //   Log.d(TAG, node.getChildNodes().getLength() +"   convertXML: current node Name  = "+(currentNode).getNodeName());
                                    if ((currentNode).getNodeName().equals("apref")) {
                                        Node currentAprefNode = currentNode;
                                        if (previoudAprefNode != null && currentAprefNode != null) {
                                            node.removeChild(previoudAprefNode);
                                            previoudAprefNode = currentAprefNode;
                                        } else {
                                            previoudAprefNode = currentAprefNode;
                                        }
                                    }
                                }
                                previoudAprefNode = null;
                            }


                            if (flags.contains("print")) {
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("publish", "true");
                            }
                            if (flags.contains("readonly") || flags.contains("hidden")) {
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("flags", "print");
                            }


                            if (((Element) node).getTagName().equals("text") || ((Element) node).getTagName().equals("punchitem")) {

                                String childNodeValue = null;
                                int childN = 0;
                           /*   <punchitem color = "#FFFFFF" opacity = "1" creationdate = "D:20200204085219Z" flags = "print,nozoom,norotate"  interior - color = "#FFFFFF" date = "D:20200204085658Z"
                                icon = "Comment" page = "0" rect = "518.03,1927.62,538.03,1947.62" rotation = "90" title = "Mike Wen" publish = "true" >
                            </punchitem >

*/
                                // clean extra apref node from xml here in <text > tag
                                Node previoudAprefNode = null;
                                for (int k = 0; k < node.getChildNodes().getLength(); k++) {
                                    Node currentNode = node.getChildNodes().item(k);
                                    //Log.d(TAG, node.getChildNodes().getLength() +"   convertXML: current node Name  = "+(currentNode).getNodeName());
                                    if ((currentNode).getNodeName().equals("apref")) {
                                        Node currentAprefNode = currentNode;
                                        if (previoudAprefNode != null && currentAprefNode != null) {
                                            node.removeChild(previoudAprefNode);
                                            previoudAprefNode = currentAprefNode;
                                        } else {
                                            previoudAprefNode = currentAprefNode;
                                        }
                                    }
                                }
                                previoudAprefNode = null;
                                for (int n = 0; n < (node).getChildNodes().getLength(); n++) {
                                    if ((node).getChildNodes().item(n).getNodeName().equals("contents")) {
                                        childNodeValue = (node).getChildNodes().item(n).getTextContent();
                                        childN = n;

                                    }
                                }
                                //   Log.e(TAG, "saveAnnotations: childNodeValue   = "+childNodeValue);
                                if (childNodeValue != null && childNodeValue.contains("punch_id")) {
                                    ((Element) node).setAttribute("icon", "Comment");

                                    String[] punchlistIdArray = childNodeValue.split(",");

                                    int punchId = 0;
                                    String punchIdMobile = "";
                                    String punchStatus = "";
                                    String punchNumber = "";
                                    String punchTitle = "";
                                    String contentPunchNumber = "";
                                    boolean isSync = true;
                                    for (int p = 0; p < punchlistIdArray.length; p++) {
                                        String str1 = punchlistIdArray[p];

                                        if (str1.contains("punch_id") && !str1.contains("punch_id_mobile")) {
                                            String[] val = str1.split("=");
                                            if (val.length > 1 && val[1].trim().length() > 0) {
                                                punchId = Integer.parseInt(val[1].trim());
                                            }
                                            if (punchId != 0) {
                                            }
                                        }
                                        if (str1.contains("punch_status")) {
                                            String[] val = str1.split("=");
                                            if (val.length > 1 && val[1].trim().length() > 0) {
                                                punchStatus = val[1].trim();
                                                if (punchStatus.length() > 1) {
                                                    punchStatus = punchStatus.substring(0, 1);
                                                }
                                            }
                                        }
                                        if (str1.contains("title")) {
                                            String[] val = str1.split("=");
                                            if (val.length > 1 && val[1].trim().length() > 0 && !val[1].trim().equals("-1")) {
                                                punchTitle = val[1].replaceAll("\\)", "").trim();
                                            }
                                        }
                                        if (str1.contains("punch_number")) {
                                            String[] val = str1.split("=");
                                            if (val.length > 1 && val[1].trim().length() > 0 && !val[1].trim().equals("-1")) {
                                                punchNumber = val[1].trim();
                                                contentPunchNumber = val[1].trim();
                                            }
                                        }
                                        if (str1.contains("punch_id_mobile")) {
                                            String[] val = str1.split("=");
                                            punchIdMobile = val[1].trim();
                                            PunchlistDb punchlistDb = null;
                                            if (punchId == 0) {
                                                if (val.length > 1 && val[1].trim().length() > 0) {
                                                    punchlistDb = mPunchListRepository.getPunchListDetail(Integer.parseInt(val[1].trim()));
                                                }
                                            } else {
                                                punchlistDb = mPunchListRepository.getPunchListDetail(punchId, true);

                                            }
                                            if (punchlistDb != null) {
                                                punchId = punchlistDb.getPunchlistId();
                                                isSync = punchlistDb.getIsSync();

                                                punchNumber = String.valueOf(punchlistDb.getItemNumber());

                                            }
                                        }
                                    }
                                    if (punchId == 0 || !contentPunchNumber.equals("")) {

                                    } else {
                                        //     Log.e(TAG, "saveAnnotations: adding new  node here  ");
                                        ((Element) node).setAttribute("punch_id", String.valueOf(punchId));
                                        ((Element) node).setAttribute("punch_id_mobile", punchIdMobile);
                                        ((Element) node).setAttribute("punch_status", punchStatus.replaceAll("\\)", ""));
                                        ((Element) node).setAttribute("punch_number", punchNumber);
                                        ((Element) node).setAttribute("title", punchTitle.replaceAll("\\)", ""));
                                        node.getChildNodes().item(childN).setTextContent("punch_id = " + String.valueOf(punchId).trim() +
                                                ", punch_id_mobile = " + punchIdMobile +
                                                ", punch_status = " + punchStatus.replaceAll("\\)", "") +
                                                ", punch_number = " + punchNumber +
                                                ", title = " + punchTitle.replaceAll("\\)", ""));
                                    }
                                    ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("publish", "true");
                                } /*else if ((title == null || TextUtils.isEmpty(title))) {
                                    Log.d(TAG, "saveAnnotations: title null return from here ");
                                    return;
                                }*/
                            }
                            if (title == null || TextUtils.isEmpty(title)) {
                                ((Element) doc.getElementsByTagName("annots").item(0).getChildNodes().item(i)).setAttribute("title", loginResponse.getUserDetails().getFirstname() + " " + loginResponse.getUserDetails().getLastname());
                            }
                        }
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, "modifyAnnotations: loop end");

                try {
                    DOMSource domSource = new DOMSource(doc);
                    StringWriter writer = new StringWriter();
                    StreamResult result = new StreamResult(writer);
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    transformer.transform(domSource, result);

                    String annotations = writer.toString();

                    annotations = annotations.replaceAll("</text>", "</punchitem>");
                    annotations = annotations.replaceAll("<text ", "<punchitem ");
                    annotations = annotations.replaceAll("\\s+", " ");


                    //     writeStringAsFile(annotations,"/mnt/sdcard/Download/M2_annotXml.xml");
                    //    Log.d(TAG, "modifyAnnotations: going to save in DB xml = "+annotations);

                    mDrawingAnnotationProvider.doUpdateDrawingAnnotationTable(annotations, mDrawingDetails.getDrawingsId(), false, true, false, deletedAnnotXml);
                    //  DrawingXmls drawingXmls = mDrawingAnnotationProvider.getDrawingAnnotation(mDrawingDetails.getDrawingsId());
                    deletedAnnotXml = "";
                    if (drawingAnnotations != null) {
                        drawingAnnotations.cancel();
                    }
                    if (mPdfViewCtrl != null) {
                        zoomVal = mPdfViewCtrl.getZoom();
                        mPdfViewCtrl.getScrollX();
                        mPdfViewCtrl.getScrollY();
                        hScrollPos = mPdfViewCtrl.getHScrollPos();
                        vScrollPos = mPdfViewCtrl.getVScrollPos();
                        Log.i(TAG, "modifyAnnotations: zoom " + zoomVal);

                        /*if (b) {
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("not_get_intent", true);
                            this.onCreate(bundle);
                        }*/
                        // comment below line for fixing bug of app hang and crash  on writing text
                        //  getFileAndLoad(true, isAdded);

//                    loadOnlyAnnot();
                    }


//                    saveAnnotations(drawingXmls);
                } catch (TransformerException e) {
                    e.printStackTrace();
                }

                //     Log.i(TAG, "**  saveAnnotations() onDoneClick: " + ""/*fdfDoc.saveAsXFDF()*/);
            } else {
                //    Log.i(TAG, "**  modifyAnnotations() else case update table from pdfdoc xml : " + ""/*fdfDoc.saveAsXFDF()*/);
                mDrawingAnnotationProvider.doUpdateDrawingAnnotationTable(fdfDoc.saveAsXFDF(), mDrawingDetails.getDrawingsId(), false,
                        true, false, "");
                //     DrawingXmls drawingXmls = mDrawingAnnotationProvider.getDrawingAnnotation(mDrawingDetails.getDrawingsId());
                if (drawingAnnotations != null) {
                    drawingAnnotations.cancel();
                }
//                saveAnnotations(drawingXmls);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (PDFNetException e1) {
            e1.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
      /* if(!isAdded){
      // reload only annot not file uncoment this after fix of xml issue
            showAnnotations(false);
        }*/
        if (!isAdded) {
            // reload pdf file for duplicate issue
            //FIXME remove below reloading and fix xml issue
            getFileAndLoad(true, isAdded);
            mPdfViewCtrl.setLongPressEnabled(true);

        }
        // unsetAnnot();
    }

    public void writeStringAsFile(final String fileContents, String fileName) {
        Context context = DrawingPDFActivity.this;
        try {

            FileWriter out = new FileWriter(new File(fileName));
            out.write(fileContents);
            out.close();
            Log.e(TAG, "writeStringAsFile: " + fileName);
        } catch (IOException e) {
            Log.e(TAG, "  " + e.getLocalizedMessage());
        }
    }

    private void loadOnlyAnnot(File file, boolean isAdded) {
        // Log.e(TAG, "loadOnlyAnnot: file "+file.getName() +" isAdded "+isAdded);
        try {
            Uri imageUri = Uri.parse(file.getPath());
            if (mDrawingDetails.getPdfOrg() != null && !TextUtils.isEmpty(mDrawingDetails.getPdfOrg())) {
                mPdfDoc = mPdfViewCtrl.openPDFUri(imageUri, null);
//                mPdfViewCtrl.setDoc(mPdfDoc);
            } else {
                DocumentConversion documentConversion = mPdfViewCtrl.openNonPDFUri(imageUri, null);
                mPdfDoc = documentConversion.getDoc();
            }
            if (loginResponse.getUserDetails().getPermissions().get(0).getDrawingToolbar() != 1) {
                annotationToolbar.setVisibility(View.GONE);
            }
            if (mToolManager != null && isAdded) {
                mToolManager.deselectAll();
                mToolManager.onDestroy();
                annotationToolbar.selectTool(annotationToolbar.getRootView(), com.pdftron.pdf.tools.R.id.controls_annotation_toolbar_tool_pan);

            }
            mToolManager.addAnnotationModificationListener(annotationModificationListener);
            Log.d(TAG, "loadOnlyAnnot: showAnnotations: false");
            showAnnotations(false);
        } catch (PDFNetException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Compare the annotation from the previous annotation
     *
     * @return true if there is any change in the annotation
     */
    private boolean isAnnotationModified() {
        try {
            FDFDoc fdfDoc = mPdfDoc.fdfExtract(PDFDoc.e_both);
            return !previousAnnotation.equalsIgnoreCase(fdfDoc.saveAsXFDF());
        } catch (PDFNetException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (punchlistFragment != null) {
            punchlistFragment.activityOnBackPress();
        }

//        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
//        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

        super.onBackPressed();
    }

    private void deleteStickyAnnot() {
        //   Log.d(TAG, " deleteStickyAnnot: ");
        if (mPdfViewCtrl == null || mAnnot == null) {
            return;
        }
        boolean shouldUnlock = false;
        try {
            // Locks the document first as accessing annotation/doc information isn't thread safe.

            mPdfViewCtrl.docLock(true);
            shouldUnlock = true;
            raiseAnnotationPreRemoveEvent(mAnnot, 1);
            Page page = mPdfViewCtrl.getDoc().getPage(1);
            page.annotRemove(mAnnot);
            mPdfViewCtrl.update(mAnnot, 1);
//            mPdfViewCtrl.update(true);

            // make sure to raise remove event after mPdfViewCtrl.update and before unsetAnnot
            raiseAnnotationRemovedEvent(mAnnot, 1);

//            refreshCustomStickyNoteDelete(this, mAnnot, page);
            unsetAnnot();

        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        } finally {
            if (shouldUnlock) {
                mPdfViewCtrl.docUnlock();
            }
        }
    }

    /**
     * Unsets the annotation.
     */
    protected void unsetAnnot() {
        removeAnnotView();
        mAnnot = null;
        try {
            if (mPdfViewCtrl != null && mPdfViewCtrl.getToolManager() instanceof ToolManager) {
                ((ToolManager) mPdfViewCtrl.getToolManager()).setSelectedAnnot(null, -1);
            }
        } catch (Exception e) {
            AnalyticsHandlerAdapter.getInstance().sendException(e);
        }
    }

    protected void removeAnnotView() {
        if (mPdfViewCtrl != null) {
            removeAnnotView(!mPdfViewCtrl.isAnnotationLayerEnabled(), true);
        }
    }

    protected void removeAnnotView(boolean delayRemoval, boolean removeRotateView) {
        if (removeRotateView) {
            removeRotateHandle();
        }
        if (mAnnotView != null) {
            if (delayRemoval) {
                mAnnotView.setDelayViewRemoval(delayRemoval);
            } else {
                mPdfViewCtrl.removeView(mAnnotView);
                mAnnotView = null;
            }

            if (mAnnot != null) {
                boolean shouldUnlock = false;
                try {
                    mPdfViewCtrl.docLock(true);
                    shouldUnlock = true;
                    mPdfViewCtrl.showAnnotation(mAnnot);
                    if (!mPdfViewCtrl.isAnnotationLayerEnabled()) {
                        Page page = mPdfViewCtrl.getDoc().getPage(1);
                        ;
                        mPdfViewCtrl.update(mAnnot, page.getIndex());
                    }
                } catch (Exception ex) {
                    AnalyticsHandlerAdapter.getInstance().sendException(ex);
                } finally {
                    if (shouldUnlock) {
                        mPdfViewCtrl.docUnlock();
                    }
                }
            }
        }
    }

    protected void removeRotateHandle() {
     /*   if (mRotateHandle != null) {
            mRotateHandle.setListener(null);
            mPdfViewCtrl.removeView(mRotateHandle);
            mRotateHandle = null;
        }*/
    }

    protected void removeAnnotView(boolean delayRemoval) {
        if (mAnnotView != null) {
            if (delayRemoval) {
                mAnnotView.setDelayViewRemoval(delayRemoval);
            } else if (mPdfViewCtrl != null) {

                mPdfViewCtrl.removeView(mAnnotView);
                mAnnotView = null;
            }

            if (mAnnot != null && mPdfViewCtrl != null) {
                boolean shouldUnlock = false;
                try {
                    mPdfViewCtrl.docLock(true);
                    shouldUnlock = true;
                    mPdfViewCtrl.showAnnotation(mAnnot);
                    if (!mPdfViewCtrl.isAnnotationLayerEnabled()) {
                        Page page = mAnnot.getPage();
                        mPdfViewCtrl.update(mAnnot, page.getIndex());
                    }
                } catch (Exception ex) {
                    AnalyticsHandlerAdapter.getInstance().sendException(ex);
                } finally {
                    if (shouldUnlock) {
                        mPdfViewCtrl.docUnlock();
                    }
                }
            }
        }
    }

    /**
     * Called when an annotation is removed.
     *
     * @param annot The removed annotation
     * @param page  The page where the annotation is on
     */
    protected void raiseAnnotationRemovedEvent(Annot annot, int page) {
        if (annot == null) {
            AnalyticsHandlerAdapter.getInstance().sendException(new Exception("Annot is null"));
            return;
        }
        ToolManager toolManager = (ToolManager) mPdfViewCtrl.getToolManager();
        HashMap<Annot, Integer> annots = new HashMap<>(1);
        annots.put(annot, page);
        toolManager.raiseAnnotationsRemovedEvent(annots);
    }

    /**
     * Called right before an annotation is removed.
     *
     * @param annot The annotation to removed
     * @param page  The page where the annotation is on
     */
    protected void raiseAnnotationPreRemoveEvent(Annot annot, int page) {
        if (annot == null) {
            AnalyticsHandlerAdapter.getInstance().sendException(new Exception("Annot is null"));
            return;
        }
        ToolManager toolManager = (ToolManager) mPdfViewCtrl.getToolManager();
        HashMap<Annot, Integer> annots = new HashMap<>(1);
        annots.put(annot, page);
        toolManager.raiseAnnotationsPreRemoveEvent(annots);
    }

    public void openPunchlistDialog(PunchlistDb punchlist, EditPunchList editPunchList) {
        Log.e(TAG, " DrawingPDFActivity openPunchlistDialog: ");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        punchlistFragment = new PunchlistFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("projectId", punchlist.getPjProjectsId());
        bundle.putLong("punchListMobileId", punchlist.getPunchlistIdMobile());
        bundle.putBoolean("drawingView", true);
        bundle.putParcelable("drawing_details", mDrawingList.get(currentFilePosition));
        bundle.putBoolean("linkExisting", false);
        mAnnot = editPunchList.getAnnot();
        mPdfViewCtrl = editPunchList.getPDFViewCtrl();
        mAnnotView = editPunchList.getAnnotView();
        if (editPunchList != null) {
            bundle.putBoolean("canRemove", !editPunchList.isFlag());
        }
        punchlistFragment.setArguments(bundle);
        try {
            ft.replace(R.id.punchlistContainer, punchlistFragment, punchlistFragment.getClass().getSimpleName()).addToBackStack(PunchlistFragment.class.getName());
            ft.commit();

        } catch (IllegalStateException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LINK_EXIST && resultCode == RESULT_OK) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            punchlistFragment = new PunchlistFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("projectId", projectId);
            bundle.putBoolean("linkExisting", true);
            bundle.putParcelable("drawing_details", mDrawingList.get(currentFilePosition));
//            punchlistFragment.setCancelable(false);
            punchlistFragment.setArguments(bundle);
//            punchlistFragment.show(ft, "");
            ft.replace(R.id.punchlistContainer, punchlistFragment, punchlistFragment.getClass().getSimpleName()).addToBackStack(PunchlistFragment.class.getName());
            ft.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && punchlistFragment != null) {
            punchlistFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void AnnotationHighLevelAPIOld(PDFDoc doc) {
        try {

            Page page = doc.getPage(1);
            int num_annots = page.getNumAnnots();

            for (int i = 0; i < num_annots; i++) {
                Annot annot = page.getAnnot(i);
                //  Log.e(TAG, "AnnotationHighLevelAPIOld: page.getNumAnnots() "+page.getNumAnnots()+"  annot.isValid() =  "+annot.isValid()+"  type "+annot.getType()+ "  "+annot.getContents());

                if (annot.isValid() == false) {
                    continue;
                } else {
                    switch (annot.getType()) {
                        case Annot.e_Text:
                            String punchNumber = "";
                            String punchStatus = "";
                            String content = annot.getContents();
                            Obj appObj = annot.getAppearance();

                            Markup mk = new Markup(annot);
                            mk.setInteriorColor(new ColorPt((255 / 255.0), (0 / 255.0), (255 / 255.0)), 1);
                            annot.refreshAppearance();
                            String[] str = content.split(",");
                            for (int j = 0; j < str.length; j++) {
                                String str1 = str[j];

                                if (str1.contains("punch_status")) {
                                    String[] val = str1.split("=");
                                    if (val.length > 1) {
                                        punchStatus = val[1].trim();
                                        if (punchStatus.length() > 1) {
                                            punchStatus = punchStatus.substring(0, 1);
                                        }
                                    }
                                }
                                if (str1.contains("punch_number")) {
                                    String[] val2 = str1.split("=");
                                    punchNumber = val2[1].trim();
                                }
                            }
                            Log.i(TAG, "AnnotationHighLevelAPIOld: *****  data 1 " + content);
                            //      Log.i(TAG, "AnnotationHighLevelAPIOld: data 2 " + punchStatus);

                            if (punchNumber.equals("") || punchNumber.equals("-1")) {
                                punchNumber = "New";
                            }
                            String iconName = "PunchList";
                            Text sticky = new Text(annot);
                            sticky.setIcon(iconName);
                            sticky.setColor(Utils.color2ColorPt(Color.WHITE));
                            // fix for punch icon size enlarge issue
                            sticky.setFlag(Annot.e_no_rotate, true);
                            sticky.setFlag(Annot.e_no_zoom, true);

                            refreshCustomStickyNoteAppearance(this, sticky, doc, punchNumber, punchStatus);

                    }
                }
            }
        } catch (PDFNetException e) {
            e.printStackTrace();
        }
    }

    private void reloadFile() {
        try {
            URL url = null;
            if (mDrawingDetails.getPdfOrg() != null && !TextUtils.isEmpty(mDrawingDetails.getPdfOrg())) {
                url = new URL(mDrawingDetails.getPdfOrg());
            } else {
                url = new URL(mDrawingDetails.getImageOrg());
            }
            Log.e(TAG, "  reloadFile   ***********************  reloadFile: " + url.toString() + "    **** isNewPunchIconAdded  " + isNewPunchIconAdded);
            String[] segments = url.getPath().split("/");
            String fileName = segments[segments.length - 1];
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));
            if (isFileExist(loginResponse.getUserDetails().getUsers_id() + fileName)) {
                String completePath = getFilesDir().getAbsolutePath() + "/Pronovos/PDF/" + loginResponse.getUserDetails().getUsers_id() + fileName;
                File file = new File(completePath);
                Log.d(TAG, "reloadFile if: " + file.getPath());
                loadOnlyAnnot(file, true);
            } else {
                Log.d(TAG, "reloadFile else: " + mDrawingDetails);
                getDrawingFile(mDrawingDetails);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mPdfViewCtrl.setLongPressEnabled(true);
    }

    public void getFileAndLoad(boolean loadOnly, boolean isAdded) {
        try {

            URL url = null;
            if (mDrawingDetails.getPdfOrg() != null && !TextUtils.isEmpty(mDrawingDetails.getPdfOrg())) {
                url = new URL(mDrawingDetails.getPdfOrg());
            } else {
                url = new URL(mDrawingDetails.getImageOrg());
            }
                Log.e(TAG, loadOnly+ "  loadOnly   ***********************  getFileAndLoad: " + url.toString()+"    **** isAdded  "+isAdded);

            String[] segments = url.getPath().split("/");
            String fileName = segments[segments.length - 1];
            loginResponse = (new Gson().fromJson(SharedPref.getInstance(this).readPrefs(SharedPref.SESSION_DETAILS), LoginResponse.class));

            if (isFileExist(loginResponse.getUserDetails().getUsers_id() + fileName)) {
                String completePath = getFilesDir().getAbsolutePath() + "/Pronovos/PDF/" + loginResponse.getUserDetails().getUsers_id() + fileName;
//                String completePath = getFilesDir().getAbsolutePath() + "/Pronovos/PDF/sample.pdf";
                File file = new File(completePath);
                if (loadOnly) {
                    //  loadOnlyAnnot(file, isAdded);
                } else {
                    whiteLoaderView.setVisibility(View.VISIBLE);
                    loadPDF(file);
                }
            } else {
                getDrawingFile(mDrawingDetails);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mPdfViewCtrl.setLongPressEnabled(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onSelectDrawingList(DrawingList drawingList) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
         Log.d(TAG, "onSelectDrawingList: ");
        for (int i = 0; i < mDrawingList.size(); i++) {
            if (mDrawingList.get(i).getOriginalDrwId() == drawingList.getOriginalDrwId()) {
//                drawingName = drawingList.getDrawingName();
//                titleTextView.setText(drawingName + " - " + drawingList.getDescriptions());
                LinkUriAction linkUriAction = new LinkUriAction();
                linkUriAction.setAddInBack(true);
                linkUriAction.setLinkUri("/" + drawingList.getOriginalDrwId());
              /*  forwardDrawingList.add(mDrawingDetails.getOriginalDrwId());
                firstDrawingImageView.setImageResource(R.drawable.ic_first_drawing);
              */
                drawingRevisitedNum = drawingList.getRevisitedNum();
                Log.d(TAG, "444444444444 onSelectDrawingList: drawingRevisitedNum " + drawingRevisitedNum + " drawingList.getOriginalDrwId() " + drawingList.getOriginalDrwId());
                onLinkEvent(linkUriAction);
            }
        }
    }

    class LoadAnnotation extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected void onPreExecute() {
//            whiteLoaderView.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Boolean... voids) {
            DrawingXmls drawingXmls = mDrawingAnnotationProvider.getDrawingAnnotation(mDrawingDetails.getDrawingsId());
            Log.d(TAG, "LoadAnnotation  doInBackground: read xml from DB  than mergeXFDFString in mPDFdoc drawingXmls " + drawingXmls);
            if (drawingXmls != null) {
                try {
                    Log.d(TAG, "LoadAnnotation  doInBackground: read xml from DB  than mergeXFDFString in mPDFdoc drawingXmls " + drawingXmls.getAnnotxml());
                    mPdfViewCtrl.setDrawAnnotations(false);
                    String annotXml = convertXML(drawingXmls.getAnnotxml(), voids[0]);
                    Log.d(TAG, "LoadAnnotation 222222222222222 \n******************** LoadAnnotation  doInBackground: read xml from DB  than mergeXFDFString in mPDFdoc Read the xml from DB " + annotXml);
//                    mPdfDoc.mergeXFDFString(annotXml, " ");
                    //TODO: GWL Changed according to new SDK 9.2.3
                    Thread.sleep(2000);
                    mPdfDoc.mergeXFDFString(annotXml, new MergeXFDFOptions().setForce(true));
                    if (!annotXml.equals("")) {
                        Log.d(TAG, "doInBackground: if (!annotXml.equals(\"\"))");
                        AnnotationHighLevelAPIOld(mPdfDoc);
                    }
                    mPdfViewCtrl.setDrawAnnotations(true);
                    Log.d(TAG, "doInBackground: 3232323232323" + mPdfDoc.getFileName());
                    Log.d(TAG, "doInBackground: 12121212121212 " + mPdfViewCtrl);
                    // FDFDoc fdfDoc = mPdfDoc.fdfExtract(PDFDoc.e_annots_only);
                    //  previousAnnotation = fdfDoc.saveAsXFDF();
                } catch (PDFNetException e) {
                    e.printStackTrace();
                    // previousAnnotation = "";
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    //  previousAnnotation = "";
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            enableNavigation();
            isAnnotationsParsed = true;
            if (isDocumentLoaded) {
                whiteLoaderView.setVisibility(View.GONE);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (mPdfViewCtrl != null) {
                        mPdfViewCtrl.setZoom(zoomVal);
                        mPdfViewCtrl.setHScrollPos(hScrollPos);
                        mPdfViewCtrl.setVScrollPos(vScrollPos);
                        mPdfViewCtrl.setLongPressEnabled(true);
                    }
                }
            }, 100);
        }
    }

    /**
     * To hide the annotation tool bar buttons.
     *
     * @param annotationToolbar
     */
    private void hideAnnotationToolBarButtons(AnnotationToolbar annotationToolbar) {
        annotationToolbar.hideButton(AnnotationToolbarButtonId.CLOSE);
//        annotationToolbar.hideButton(AnnotationToolbarButtonId.PAN);
        annotationToolbar.hideButton(AnnotationToolbarButtonId.UNDERLINE);

//        annotationToolbar.hideButton(AnnotationToolbarButtonId.EXT_HIGHLIGHT);
//        annotationToolbar.hideButton(AnnotationToolbarButtonId.TEXT_STRIKEOUT);
//        annotationToolbar.hideButton(AnnotationToolbarButtonId.TEXT_SQUIGGLY);
//        annotationToolbar.hideButton(AnnotationToolbarButtonId.SIGNATURE);
    }
}
