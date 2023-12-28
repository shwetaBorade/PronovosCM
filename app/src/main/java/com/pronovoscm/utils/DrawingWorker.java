package com.pronovoscm.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.pronovoscm.PronovosApplication;
import com.pronovoscm.data.DrawingAnnotationProvider;
import com.pronovoscm.data.ProjectDrawingListProvider;
import com.pronovoscm.data.ProviderResult;
import com.pronovoscm.model.SyncDataEnum;
import com.pronovoscm.model.TransactionLogUpdate;
import com.pronovoscm.model.TransactionModuleEnum;
import com.pronovoscm.model.request.drawingstore.DeletedPunchAnnotation;
import com.pronovoscm.model.request.drawingstore.DrawingStoreRequest;
import com.pronovoscm.persistence.domain.DrawingXmls;
import com.pronovoscm.persistence.domain.PunchlistDb;
import com.pronovoscm.persistence.domain.TransactionLogMobile;
import com.pronovoscm.persistence.domain.TransactionLogMobileDao;
import com.pronovoscm.persistence.repository.DrawingListRepository;
import com.pronovoscm.persistence.repository.PunchListRepository;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.DeleteQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class DrawingWorker extends PronovosWorker {
    public static final String TAG = DrawingWorker.class.getSimpleName();

    @Inject
    DrawingAnnotationProvider mDrawingAnnotationProvider;
    @Inject
    PunchListRepository mPunchListRepository;
    @Inject
    ProjectDrawingListProvider mDrawingListProvider;
    @Inject
    DrawingListRepository mDrawingListRepository;

    TransactionLogMobile transactionLogMobile;
    TransactionLogMobileDao mPronovosSyncData;
    private Context applicationContext;

    public DrawingWorker(TransactionLogMobile transactionLogMobile, TransactionLogMobileDao mPronovosSyncData, Context applicationContext) {
        this.transactionLogMobile = transactionLogMobile;
        this.mPronovosSyncData = mPronovosSyncData;
        this.applicationContext = applicationContext;
    }

    @Override
    public void doTransaction() {
        ((PronovosApplication) (applicationContext)).getDaggerComponent().inject(this);
//        DrawingXmls drawingXmls = mDrawingAnnotationProvider.getDrawingAnnotation(transactionLogMobile.getServerId().intValue());
//        DrawingList drawingList = mDrawingListProvider.getDrawingDetail(drawingXmls.getDrwDrawingsId(), drawingXmls.getUsersId());
//        DrawingFolders drawingFolders = mDrawingListRepository.getDrawingFolder(drawingList.getDrwFoldersId(), drawingXmls.getUsersId());
//        if (!mPunchListRepository.getNonSyncPunchListSize(drawingFolders.getPjProjectsId())) {
        callNotSyncAnnotations(mDrawingAnnotationProvider.getDrawingAnnotation(transactionLogMobile.getServerId().intValue()));
//        }

    }

    private void callNotSyncAnnotations(DrawingXmls drawingXml) {
        boolean callPunchlist = false;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();

            Document doc = db.parse(new InputSource(new StringReader(drawingXml.getAnnotxml())));
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("annots");
            if (nodeList.getLength() > 0) {
                NodeList namedNodeMap = nodeList.item(0).getChildNodes();
                for (int i = 0; i < namedNodeMap.getLength(); i++) {
                    Node node = namedNodeMap.item(i);
                    try {
                        if (node instanceof Element) {

                            if (((Element) node).getTagName().equals("text") || ((Element) node).getTagName().equals("punchitem")) {

                                NodeList nodeList1;

                                String childNodeValue = null;
                                int childN = 0;
                                for (int n = 0; n < (node).getChildNodes().getLength(); n++) {
                                    if ((node).getChildNodes().item(n).getNodeName().equals("contents")) {
                                        childNodeValue = (node).getChildNodes().item(n).getTextContent();
                                        childN = n;
                                    }
                                }
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
                                            if (val.length > 1 && val[1].trim().length() > 0 && punchId == 0) {
                                                punchId = Integer.parseInt(val[1].trim());
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
                                        if (str1.contains("punch_number")) {
                                            String[] val = str1.split("=");
                                            if (val.length > 1 && val[1].trim().length() > 0 && !val[1].trim().equals("-1")) {
                                                punchNumber = val[1].trim();
                                                contentPunchNumber = val[1].trim();
                                            }
                                        }
                                        if (str1.contains("title")) {
                                            String[] val = str1.split("=");
                                            if (val.length > 1 && val[1].trim().length() > 0 && !val[1].trim().equals("-1")) {
                                                punchTitle = val[1].replace(")", "").trim();

//                                                punchTitle = val[1].trim();
                                            }
                                        }
                                        if (str1.contains("punch_id_mobile")) {
                                            String[] val = str1.split("=");
                                            punchIdMobile = val[1].trim();
                                            PunchlistDb punchlistDb = null;

                                            if (punchId == 0 && !punchIdMobile.equals("")) {
                                                punchlistDb = mPunchListRepository.getPunchListDetail(Long.valueOf(punchIdMobile));
                                            } else if (punchId != 0) {
                                                punchlistDb = mPunchListRepository.getPunchListDetail(punchId, true);
                                            }
                                            if (punchlistDb != null) {
                                                punchId = punchlistDb.getPunchlistId();
                                                punchNumber = String.valueOf(punchlistDb.getItemNumber());

                                            }
                                        }
                                    }
                                    Log.e(TAG, "callNotSyncAnnotations: data 2 " + punchStatus);

                                    if (punchId == 0) {
                                        callPunchlist = true;
                                    } else {
                      //                  Log.e(TAG, "callNotSyncAnnotations: setting content values here ");
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

                    String annotations = writer.toString();
                    annotations = annotations.replaceAll("</text>", "</punchitem>");
                    annotations = annotations.replaceAll("<text ", "<punchitem ");

                    mDrawingAnnotationProvider.doUpdateDrawingAnnotationTable(annotations, drawingXml.getDrwDrawingsId(), false, false, false, "");

                    TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                    transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.DRAWING_ANNOTATION);
                    transactionLogUpdate.setDrawingId(drawingXml.getDrwDrawingsId());
                    EventBus.getDefault().post(transactionLogUpdate);

//                    if (!callPunchlist) {
                    saveAnnotations(drawingXml, annotations);
//                    }
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            }
            {

            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * save annotation to server
     *
     * @param drawingXml
     * @param annotations
     */
    private void saveAnnotations(DrawingXmls drawingXml, String annotations) {

        DrawingStoreRequest drawingStoreRequest = new DrawingStoreRequest();
        drawingStoreRequest.setAnnot_xml(annotations);
        drawingStoreRequest.setDrawing_id(drawingXml.getDrwDrawingsId());
        ArrayList<DeletedPunchAnnotation> deletedPunchAnnotations = new ArrayList<>();
        Log.i(TAG, "saveAnnotations: deletedAnnotXml" + drawingXml.getAnnotdeletexml());
        if (!TextUtils.isEmpty(drawingXml.getAnnotdeletexml())) {
            String[] annotDeleteArray = drawingXml.getAnnotdeletexml().split(";");
            for (int i = 0; i <= annotDeleteArray.length - 1; i++) {
                String[] punchlistIdArray = annotDeleteArray[i].split(",");


                int punchId = 0;
                String punchIdMobile = "";
                String punchStatus = "";
                String punchNumber = "";
                String contentPunchNumber = "";
                String punchTitle = "";
                String rect = "";
                boolean isSync = true;
                for (int p = 0; p < punchlistIdArray.length; p++) {
                    String str1 = punchlistIdArray[p];

                    if (str1.contains("punch_id") && !str1.contains("punch_id_mobile")) {
                        String[] val = str1.split("=");
                        if (val.length > 1 && val[1].trim().length() > 0 && punchId == 0) {
                            punchId = Integer.parseInt(val[1].trim());
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
                    if (str1.contains("punch_number")) {
                        String[] val = str1.split("=");
                        if (val.length > 1 && val[1].trim().length() > 0 && !val[1].trim().equals("-1")) {
                            punchNumber = val[1].trim();
                            contentPunchNumber = val[1].trim();
                        }
                    }
                    if (str1.contains("rect")) {
                        String[] val = str1.split("=");
                        rect = val[1].trim().replaceAll(":", ",");
                    }
                    if (str1.contains("title")) {
                        String[] val = str1.split("=");
                        punchTitle = val[1].trim();
                    }
                    if (str1.contains("punch_id_mobile")) {
                        String[] val = str1.split("=");
                        punchIdMobile = val[1].trim();
                        PunchlistDb punchlistDb = null;

                        if (punchId == 0 && !punchIdMobile.equals("")) {
                            punchlistDb = mPunchListRepository.getPunchListDetail(Long.valueOf(punchIdMobile));
                        } else if (punchId != 0) {
                            punchlistDb = mPunchListRepository.getPunchListDetail(punchId, true);

                        }
                        if (punchlistDb != null) {
                            punchId = punchlistDb.getPunchlistId();
                            punchNumber = String.valueOf(punchlistDb.getItemNumber());
                        }
                    }
                }
                DeletedPunchAnnotation deletedPunchAnnotation = new DeletedPunchAnnotation();
                deletedPunchAnnotation.setPunch_id(punchId);
                deletedPunchAnnotation.setPunch_id_mobile(Integer.parseInt(punchIdMobile.replaceAll("\\)", "").trim()));
                ;
                deletedPunchAnnotation.setPunch_status(Integer.parseInt(punchStatus.replaceAll("\\)", "").trim()));
                deletedPunchAnnotation.setPunch_number(Integer.parseInt(punchNumber.replaceAll("\\)", "").trim()));
                deletedPunchAnnotation.setRect(rect);
                deletedPunchAnnotation.setTitle(punchTitle.replaceAll("\\)", "").trim());
                deletedPunchAnnotations.add(deletedPunchAnnotation);
            }
        }
        drawingStoreRequest.setDeletedPunchAnnotation(deletedPunchAnnotations);
        transactionLogMobile.setStatus(SyncDataEnum.PROCESSING.ordinal());
        mPronovosSyncData.update(transactionLogMobile);

        mDrawingAnnotationProvider.getDrawingStoreAnnotations(drawingStoreRequest, new ProviderResult<String>() {
            @Override
            public void success(String result) {
            //    Log.e(TAG, "getDrawingStoreAnnotations    ############         success: ");
                transactionLogMobile.setStatus(SyncDataEnum.SYNC.ordinal());
                DeleteQuery<TransactionLogMobile> pronovosSyncDataDeleteQuery = ((PronovosApplication) applicationContext).getDaoSession().queryBuilder(TransactionLogMobile.class)
                        .where(TransactionLogMobileDao.Properties.SyncId.eq(transactionLogMobile.getSyncId()))
                        .buildDelete();
                pronovosSyncDataDeleteQuery.executeDeleteWithoutDetachingEntities();
             /*   TransactionLogUpdate transactionLogUpdate = new TransactionLogUpdate();
                transactionLogUpdate.setTransactionModuleEnum(TransactionModuleEnum.DRAWING_ANNOTATION);
                transactionLogUpdate.setDrawingId(drawingXml.getDrwDrawingsId());
                EventBus.getDefault().post(transactionLogUpdate);
             */
                ((PronovosApplication) applicationContext).setupAndStartWorkManager();
                 EventBus.getDefault().post(new ReloadPDFEvent());
            }

            @Override
            public void AccessTokenFailure(String message) {
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
                mDrawingAnnotationProvider.accessTokenFailure();
            }

            @Override
            public void failure(String message) {
                Log.i(TAG, "fail drawing worker save annot: setupAndStartWorkManager");
                transactionLogMobile.setStatus(SyncDataEnum.SYNC_FAILED.ordinal());
                mPronovosSyncData.update(transactionLogMobile);
                mDrawingAnnotationProvider.accessTokenFailure();

//                ((PronovosApplication) applicationContext).setupAndStartWorkManager();

//                messageDialog.showMessageAlert(DrawingPDFActivity.this, message, getString(R.string.ok));

            }
        });
    }

}
